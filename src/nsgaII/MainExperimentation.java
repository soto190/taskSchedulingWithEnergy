package nsgaII;

import tools.Utilities;

public class MainExperimentation {
	public static void exp10() {
		/**
		 * /Experimento10/CxMx/Mi/FUN.X
		 */
		for (int cx = 1; cx <= 4; cx++)
			for (int mt = 1; mt <= 3; mt++)
				for (int instance = 0; instance <= 11; instance++) {
					String problemName = "/MS" + instance;
					String experimentPath = "/Experimento10/C" + cx + "M" + mt
							+ problemName;
					for (int run = 0; run < 10; run++) {

						Utilities utils = new Utilities();

						NSGAII nsgaII = new NSGAII(utils.readInstance(instance));
						nsgaII.setCrossover(cx);
						nsgaII.setMutation(mt);
						nsgaII.start(300, 300, 1.0);
						nsgaII.saveParetoFront(experimentPath + "/FUN." + run);
					}
				}
	}

	public static void exp11_test(String[] args) {
		Utilities utils = new Utilities();
		for (int instance = 0; instance < 52; instance++)
			for (int run = 0; run < 50; run++) {
				String problemName = "/MS" + instance;
				String experimentPath = "/Experiments/Ex12/memetic/"
						+ problemName;
				NSGAII nsgaII = new NSGAII(utils.readInstance(instance));
				nsgaII.setCrossover(5);
				nsgaII.setMutation(1);
				nsgaII.start(200, 300, 1.0);
				nsgaII.saveParetoFront(experimentPath + "/FUN." + run + ".rf");
			}
	}

	public static void exp14(String[] args) {
		Utilities utils = new Utilities();
		for (int instance = 1; instance < 52; instance++)
			for (int run = 0; run < 50; run++) {
				String problemName = "/MS" + instance;
				String experimentPath = "/Experiments/Ex14/nsgaII/"
						+ problemName;
				NSGAII nsgaII = new NSGAII(utils.readInstance(instance));
				nsgaII.setCrossover(1);
				nsgaII.setMutation(1);
				nsgaII.start(200, 300, 1.0);
				nsgaII.savePopulation(experimentPath + "/FUN." + run);
			}
	}

	public static void exp15(String[] args) {
		Utilities utils = new Utilities();
		
		int[] inst = new int[]{12, 13, 14, 15, 17, 26, 29, 30, 31, 34, 36, 38, 48, 49};
		
		for (Integer instance:inst){
			double totalTime = 0;
			for (int run = 0; run < 50; run++) {
				long initTime = System.currentTimeMillis();

				String problemName = "/MS" + instance;
				String experimentPath = "/Experiments/Ex15/"
						+ problemName;
				NSGAII nsgaII = new NSGAII(utils.readInstance(instance));
				nsgaII.setCrossover(1);
				nsgaII.setMutation(1);
				nsgaII.start(200, 300, 1.0);
				nsgaII.savePopulation(experimentPath + "/FUN." + run);
				long estimatedTime = System.currentTimeMillis() - initTime;
				double tot_time = estimatedTime / 1000.0;
				totalTime += tot_time;
				
			}
			System.out.println(totalTime/50);
		}
	}
	
	public static void exp16(String[] args) {
		System.out.println("Starting exp16...");
		Utilities utils = new Utilities();
		for (int instance = 0; instance < 1; instance++)
			for (int run = 0; run < 1; run++) {
				String problemName = "/MS" + instance;
				String experimentPath = "/Experiments/Ex16/"
						+ problemName;
				NSGAII nsgaII = new NSGAII(utils.readInstance(instance));
				nsgaII.setCrossover(1);
				nsgaII.setMutation(3);
				nsgaII.start(200, 600, 1.0);
				nsgaII.savePopulation(experimentPath + "/FUN." + run);
			}
		System.out.println("exp16 has ended ...");

	}

	public static void main(String[] args) {
		exp16(args);
//		NSGAII nsgaII = new NSGAII();

//		nsgaII.test();
	}

}
