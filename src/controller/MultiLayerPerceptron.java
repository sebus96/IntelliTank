package controller;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import model.GasStation;
import model.Price;

/**
 * Mehrlagiges Perzeptron
 *
 * @author Sebastian Drath
 *
 */
public class MultiLayerPerceptron extends Perceptron {
	private static final long serialVersionUID = 522119854329442648L;
	private final int inputlength = 7 + 24 + oldPriceNumber + 1;
	//7+24+oldPriceNumber
	private final int hiddenLayerSize = 5;

    private Matrix weightsInputToHidden;
    private Matrix weightsHiddenToOutput;

    public MultiLayerPerceptron(GasStation gs, int epochs, Date until) {
		super(gs, epochs, until);
    }

    @Override
    public boolean train() {
        // trainData: 6 x 144
    	int totalDifference = 0;
		int listCounter = 0;

        this.weightsInputToHidden = new Matrix(inputlength, hiddenLayerSize); // 37x10
        this.weightsHiddenToOutput = new Matrix(hiddenLayerSize, 1); // 10x1

        for (int epoch = 0; epoch < getEpoches(); epoch++) {

			totalDifference = 0;
			listCounter = 0;
			Calendar cal = Calendar.getInstance();
//			for(int i = 0; i < getStation().getPriceListSize(); i++) {
			for(cal.setTime(getStation().getPriceListElement(0).getTime()); cal.getTime().before(getStation().getPriceListElement(getStation().getPriceListSize()-1).getTime()); cal.add(Calendar.HOUR_OF_DAY, 1)) {
//				Price p = getStation().getPriceListElement(i);
				Price p = new Price(cal.getTime(),getStation().getHistoricPrice(cal.getTime()));
				
				if(p.getTime().after(getUntil())) break;

                // feed forward
                Matrix inputLayer = generateTrainingInputMatrix(p.getTime()); // 1x37
                if(inputLayer == null) continue;
                Matrix hiddenLayer = MatrixOperations.sigmoid( MatrixOperations.matMult(inputLayer, this.weightsInputToHidden)); //1x10
                Matrix outputLayer = /*MatrixOperations.sigmoid(*/ MatrixOperations.matMult(hiddenLayer, this.weightsHiddenToOutput)/*)*/; //1x1

                // calculate error
                Matrix outputLayerError = calcError(convertPrice(p.getPrice()) , outputLayer.getMatrix()[0][0]); //1x1
                totalDifference += Math.abs(outputLayerError.getValue(0, 0));
                listCounter++;

                // backpropagation
                Matrix outputLayerDelta = MatrixOperations.matMult(outputLayerError, /*MatrixOperations.sigmoidDerivation(*/outputLayer/*)*/); // 1x1

                Matrix hiddenLayerError = MatrixOperations.matMult(outputLayerDelta.transpose(), this.weightsHiddenToOutput.transpose()); // 1x10

                Matrix hiddenLayerDelta = MatrixOperations.matMult(hiddenLayerError.transpose(), MatrixOperations.sigmoidDerivation(hiddenLayer)); //10x1

                // update weights
                Matrix updateWeightsHiddenToOutput = MatrixOperations.matMult(hiddenLayer.transpose(), outputLayerDelta.transpose()); // 10x1
                Matrix updateWeightsInputToHidden = MatrixOperations.matMult(inputLayer.transpose(), hiddenLayerDelta.transpose()); // 37x1 * 1x10 = 37x10

                this.weightsHiddenToOutput = MatrixOperations.matAdd(this.weightsHiddenToOutput, updateWeightsHiddenToOutput);
                this.weightsInputToHidden = MatrixOperations.matAdd(this.weightsInputToHidden, updateWeightsInputToHidden);
            }
//        	if(totalDifference/listCounter < precision) break;
        }
//        MatrixOperations.matOut(weightsHiddenToOutput);
//        MatrixOperations.matOut(weightsInputToHidden);
        this.setTrained();
        return totalDifference/listCounter <= precision;
    }

    /**
     * Returns the difference between the expected result and the predicted result.
     *
     * @param realPrice echter Preis
     * @param prediction vorhergesagter Preis
     * @return Abweichung
     */
    private Matrix calcError(double realPrice, double prediction) {
        Matrix error = new Matrix(1, 1);
        error.setValue(0, 0, realPrice - prediction);
        return error;
    }

    @Override
    public double feedForward(Date date, List<Double> lastPrices){
    	if(lastPrices.size() != oldPriceNumber) throw new IllegalArgumentException("wrong input number of old prices");
        Matrix inputLayer = new Matrix(1, inputlength);
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int[] hourVector = getHourVector(c.get(Calendar.HOUR_OF_DAY));
		int[] weekdayVector = getDayVector(c.get(Calendar.DAY_OF_WEEK));
		double isHoliday = getHoliday(date, getStation().getState());
		int ctr = 0;
		for(int hour: hourVector) inputLayer.setValue(0, ctr++, hour);
		for(int weekday: weekdayVector) inputLayer.setValue(0, ctr++, weekday);
		for(double price: lastPrices) inputLayer.setValue(0, ctr++, convertPrice(price));
		inputLayer.setValue(0, ctr++, isHoliday);

        Matrix hiddenLayer = MatrixOperations.sigmoid( MatrixOperations.matMult(inputLayer, this.weightsInputToHidden));
        Matrix outputLayer = /*MatrixOperations.sigmoid(*/ MatrixOperations.matMult(hiddenLayer, this.weightsHiddenToOutput/*)*/);

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
		for(int price: lastPrices) res.setValue(0, ctr++, convertPrice(price));
		res.setValue(0, ctr++, isHoliday);
		assert ctr == inputlength;
		return res;
    }
    
    private double reconvertPrice(double price) {
//    	return price;
    	return (price*1000)+1000;
    }
    
    private double convertPrice(double price) {
//    	return price/1000;
    	if(price < 1000 || price > 2000) System.err.println("Price not in range: " + price);
    	return (price-1000)/1000;
    }
}
