package controller;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import model.GasStation;
import model.Price;

public class MultiLayerPerceptron extends Perceptron {

	private final int inputlength = 7 + 24 + oldPriceNumber + 1;
	//7+24+oldPriceNumber
	private final int hiddenLayerSize = 10;

    private Matrix weightsInputToHidden;
    private Matrix weightsHiddenToOutput;

    public MultiLayerPerceptron(GasStation gs, int epochs) {
		super(gs, epochs);
    }

    /**
     * Runs the backpropagation algorithm.
     * At first it creates two matrices with randomly generated elements.
     * In every iteration it runs over all train data and it feed the net forwardly.
     * Then it determines the error and update the weights after a backpropagation.
     *
     * @param trainData
     */
    @Override
    public boolean train(Date until) {
        // trainData: 6 x 144
    	int totalDifference = 0;
		int listCounter = 0;

        this.weightsInputToHidden = new Matrix(inputlength, hiddenLayerSize);
        this.weightsHiddenToOutput = new Matrix(hiddenLayerSize, 1);

        for (int i = 0; i < getEpoches(); i++) {

			totalDifference = 0;
			listCounter = 0;
			
        	for(int m = 0; m < getStation().getPriceListSize(); m++) {
				Price p = getStation().getPriceListElement(i);
				if(p.getTime().after(until)) break;

                // feed forward
                Matrix inputLayer = generateTrainingInputMatrix(p.getTime());
                if(inputLayer == null) continue;
                Matrix hiddenLayer = MatrixOperations.sigmoid( MatrixOperations.matMult(inputLayer, this.weightsInputToHidden));
                Matrix outputLayer = MatrixOperations.sigmoid( MatrixOperations.matMult(hiddenLayer, this.weightsHiddenToOutput));
//                Matrix hiddenLayer = MatrixOperations.matMult(inputLayer, this.weightsInputToHidden);
//                Matrix outputLayer = MatrixOperations.matMult(hiddenLayer, this.weightsHiddenToOutput);

                // calculate error
                Matrix outputLayerError = calcError(convertPrice(p.getPrice(), "A") , outputLayer.getMatrix()[0][0]);
                totalDifference += Math.abs(outputLayerError.getValue(0, 0));
                listCounter++;

                // backpropagation
                Matrix outputLayerDelta = MatrixOperations.matMult(outputLayerError, MatrixOperations.sigmoidDerivation(outputLayer));
//                Matrix outputLayerDelta = MatrixOperations.matMult(outputLayerError, outputLayer);

                Matrix hiddenLayerError = MatrixOperations.matMult(outputLayerDelta.transpose(), this.weightsHiddenToOutput.transpose());

                Matrix hiddenLayerDelta = MatrixOperations.matMult(hiddenLayerError.transpose(), MatrixOperations.sigmoidDerivation(hiddenLayer));
//                Matrix hiddenLayerDelta = MatrixOperations.matMult(hiddenLayerError.transpose(), hiddenLayer);

                // update weights
                Matrix updateWeightsHiddenToOutput = MatrixOperations.matMult(1, MatrixOperations.matMult(hiddenLayer.transpose(), outputLayerDelta.transpose()));
                Matrix updateWeightsInputToHidden = MatrixOperations.matMult(1, MatrixOperations.matMult(inputLayer.transpose(), hiddenLayerDelta.transpose()));
                
                this.weightsHiddenToOutput = MatrixOperations.matAdd(this.weightsHiddenToOutput, updateWeightsHiddenToOutput);
                this.weightsInputToHidden = MatrixOperations.matAdd(this.weightsInputToHidden, updateWeightsInputToHidden);
            }
//        	if(totalDifference/listCounter < precision) break;
        }
//        MatrixOperations.matOut(weightsHiddenToOutput);
//        MatrixOperations.matOut(weightsInputToHidden);
        return totalDifference/listCounter <= precision;
    }

    /**
     * Returns the difference between the expected result and the predicted result.
     *
     * @param realPrice
     * @param prediction
     * @return
     */
    private Matrix calcError(double realPrice, double prediction) {
        Matrix error = new Matrix(1, 1);
        error.setValue(0, 0, realPrice - prediction);
        return error;
    }

    /**
     * Predict the class for an input.
     *
     * @param input
     * @return
     */
    @Override
    public double feedForward(Date date, List<Double> lastPrices){
    	if(lastPrices.size() != this.oldPriceNumber) System.err.println("wrong input number of old prices");
        Matrix inputLayer = new Matrix(1, inputlength);
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int[] hourVector = getHourVector(c.get(Calendar.HOUR_OF_DAY));
		int[] weekdayVector = getDayVector(c.get(Calendar.DAY_OF_WEEK));
		double isHoliday = getHoliday(date, getStation().getState());
		int ctr = 0;
		for(int hour: hourVector) inputLayer.setValue(0, ctr++, hour);
		for(int weekday: weekdayVector) inputLayer.setValue(0, ctr++, weekday);
		for(double price: lastPrices) inputLayer.setValue(0, ctr++, convertPrice(price, "B"));
		inputLayer.setValue(0, ctr++, isHoliday);

        Matrix hiddenLayer = MatrixOperations.sigmoid( MatrixOperations.matMult(inputLayer, this.weightsInputToHidden));
        Matrix outputLayer = MatrixOperations.sigmoid( MatrixOperations.matMult(hiddenLayer, this.weightsHiddenToOutput));
//        Matrix hiddenLayer = MatrixOperations.matMult(inputLayer, this.weightsInputToHidden);
//        Matrix outputLayer =MatrixOperations.matMult(hiddenLayer, this.weightsHiddenToOutput);

        return reconvertPrice(outputLayer.getValue(0,0));
    }
    
    private Matrix generateTrainingInputMatrix(Date d) {
    	Matrix res = new Matrix(1, inputlength);
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		int[] hourVector = getHourVector(c.get(Calendar.HOUR_OF_DAY));
		int[] weekdayVector = getDayVector(c.get(Calendar.DAY_OF_WEEK));
		int[] lastPrices = getPriceVector(c);
		double isHoliday = getHoliday(d, getStation().getState());
		if(lastPrices == null) return null;
		int ctr = 0;
		for(int hour: hourVector) res.setValue(0, ctr++, hour);
		for(int weekday: weekdayVector) res.setValue(0, ctr++, weekday);
		for(int price: lastPrices) res.setValue(0, ctr++, convertPrice(price, "C"));
		res.setValue(0, ctr++, isHoliday);
		assert ctr == inputlength;
		return res;
    }
    
    private double reconvertPrice(double price) {
    	return (price*1000)+1000;
    }
    
    private double convertPrice(double price, String pos) {
    	if(price < 1000 || price > 2000) System.err.println("Price not in range: " + price + "(" + pos + ")");
    	return (price-1000)/1000;
    }
}
