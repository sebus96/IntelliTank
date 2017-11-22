package controller;

import java.util.Date;

import model.GasStation;

public class PredictionUnit {
	GasStation gs;

	public PredictionUnit(GasStation gs) {
		this.gs = gs;
	}

	/**
	* 
	*
	*/		
	public void start() {
		int epoch = 100;
		Perceptron p = new Perceptron(0.05, epoch);
		System.out.println(p.train(gs));
		double res = p.output(new Date());
		System.out.println(gs.getID() + ") " + gs.getBrand() + " " + gs.getName() + ": " + (res/1000.0) + " €");
	}
	
	public void startNetwork() {

//        Network network = new Network();
//        network.train(gs);
//        int counter = 0;
        /*for (Data d : trainData)
            System.out.println("Klasse: " + counter++ + ": " + classify(trainData, network.feedForward(d)));*/
	}

    /**
     * Mapping from an array to the letter.
     *
     * @param trainData
     * @param floats
     * @return
     */
//    private static String classify(List<Data> trainData, float[] floats) {
//
//        float max = Float.MIN_VALUE;
//        int index = -1;
//        for (int i = 0; i < floats.length; i++)
//            if (floats[i] > max) {
//                index = i;
//                max = floats[i];
//            }
//        return ""+trainData.get(index).getC();
//    }
}
