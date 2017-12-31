package controller;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import model.GasStation;
import model.Price;

public class Network {

    public int epochs;
	private final int oldPriceNumber = 4;
	private final int inputlength = 7 + 24 + oldPriceNumber;
	//7+24+oldPriceNumber
	private double rate;
	private final int precision = 13;
	private GasStation station;

    private Matrix weightsInputToHidden;
    private Matrix weightsHiddenToOutput;

    public Network(GasStation gs, double rate, int epochs) {
		this.rate = rate;
		this.station = gs;
		this.epochs = epochs;
    }

    /**
     * Runs the backpropagation algorithm.
     * At first it creates two matrices with randomly generated elements.
     * In every iteration it runs over all train data and it feed the net forwardly.
     * Then it determines the error and update the weights after a backpropagation.
     *
     * @param trainData
     */
    public void train(Date until) {
        // trainData: 6 x 144

        this.weightsInputToHidden = new Matrix(inputlength, 10); // 144 x 10 -> 6x10
        this.weightsHiddenToOutput = new Matrix(10, 1); // 10 x 6 -> 6x6

        for (int i = 0; i < epochs; i++) {

        	for(int m = 0; m < station.getPriceListSize(); m++) {
				Price p = station.getPriceListElement(i);
				if(p.getTime().after(until)) break;

                // feed forward
                Matrix inputLayer = new Matrix(1, inputlength); // TODO input matrix füllen
                Matrix hiddenLayer = MatrixOperations.sigmoid(
                        MatrixOperations.matMult(inputLayer, this.weightsInputToHidden));
                Matrix outputLayer = MatrixOperations.sigmoid(
                        MatrixOperations.matMult(hiddenLayer, this.weightsHiddenToOutput));

                // calculate error
                Matrix outputLayerError = calcError(p.getPrice() , outputLayer.getMatrix()[0][0]);

                // backpropagation
                // in what direction is the target value? were we really sure? if so, don't change too much.
                Matrix outputLayerDelta = MatrixOperations.matMult(outputLayerError,
                        MatrixOperations.sigmoidDerivation(outputLayer));

                // how much did each hiddenlayer value contribute to the outputlayer error (according to the weights)?
                Matrix hiddenLayerError = MatrixOperations.matMult(outputLayerDelta.transpose(), this.weightsHiddenToOutput.transpose());

                // in what direction is the target hiddenlayer? were we really sure? if so, don't change too much.
                Matrix hiddenLayerDelta = MatrixOperations.matMult(hiddenLayerError.transpose(),
                        MatrixOperations.sigmoidDerivation(hiddenLayer));

                // update weights
                this.weightsHiddenToOutput = MatrixOperations.matAdd(this.weightsHiddenToOutput,
                        MatrixOperations.matMult(hiddenLayer.transpose(), outputLayerDelta.transpose()));
                this.weightsInputToHidden = MatrixOperations.matAdd(this.weightsInputToHidden,
                        MatrixOperations.matMult(inputLayer.transpose(), hiddenLayerDelta.transpose()));
            }
        }
    }

    /**
     * Returns the difference between the expected result and the predicted result.
     *
     * @param realPrice
     * @param prediction
     * @return
     */
    private Matrix calcError(int realPrice, double prediction) {
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
    public double feedForward(Date input, List<Double> lastPrices){

        Matrix inputLayer = new Matrix(1, 144/*data.getInput().length*/); // 1 x 144 // TODO generate input
        Matrix hiddenLayer = MatrixOperations.sigmoid(
                MatrixOperations.matMult(inputLayer, this.weightsInputToHidden));
        Matrix outputLayer = MatrixOperations.sigmoid(
                MatrixOperations.matMult(hiddenLayer, this.weightsHiddenToOutput)); // 1 x 6

        return outputLayer.getValue(0,0);
    }
    
    private Matrix generateInputMatrix(Date d, List<Double> lastPrices) {
    	Matrix res = new Matrix(1, inputlength);
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		int[] hourVector = getHourVector(c.get(Calendar.HOUR_OF_DAY));
		int[] weekdayVector = getDayVector(c.get(Calendar.DAY_OF_WEEK));
		//int[] lastPrices = getPriceVector(c);
		return res;
    }
	
	private int[] getDayVector(int day) {
		if(day > 7 || day <= 0) return null;
		int[] res = new int[7];
		res[day-1] = 1;
		return res;
	}
	
	private int[] getHourVector(int hour) {
		if(hour >= 24 || hour < 0) return null;
		int[] res = new int[24];
		res[hour] = 1;
		return res;
	}
	
	private int[] getPriceVector(Calendar c) {
		int[] res = new int[oldPriceNumber];
		for(int i = 0; i < oldPriceNumber; i++) {
			c.add(Calendar.HOUR_OF_DAY, -1);
			res[i] = station.getHistoricPrice(c.getTime());
		}
		return res;
	}

//    public Matrix getWeightsInputToHidden() {
//        return weightsInputToHidden;
//    }
//
//    public Matrix getWeightsHiddenToOutput() {
//        return weightsHiddenToOutput;
//    }
//
//    public void setWeightsInputToHidden(Matrix weightsInputToHidden) {
//        this.weightsInputToHidden = weightsInputToHidden;
//    }
//
//    public void setWeightsHiddenToOutput(Matrix weightsHiddenToOutput) {
//        this.weightsHiddenToOutput = weightsHiddenToOutput;
//    }
}
