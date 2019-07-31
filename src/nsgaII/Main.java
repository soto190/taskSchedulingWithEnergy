package nsgaII;

import hcs.HCS;
import tools.Utilities;

public class Main {
	
	public static void main(String[] args){
		System.out.println("Starting exp16...");
		Utilities utils = new Utilities();
		for (int instance = 12; instance < 13; instance++)
			for (int run = 0; run < 1; run++) {
				HCS hcs = utils.readInstance(instance);

				String problemName = hcs.getName().split("\\.")[0];
				String experimentPath = "/Experiments/Test/"
						+ problemName;
				NSGAII nsgaII = new NSGAII(hcs);
				nsgaII.setCrossover(1);
				nsgaII.setMutation(3);
				nsgaII.start(200, 600, 1.0);
				nsgaII.savePopulation(experimentPath + "/"+problemName + "-r"+ run+".tsv");
			}
		System.out.println("exp16 has ended ...");

	}

}
