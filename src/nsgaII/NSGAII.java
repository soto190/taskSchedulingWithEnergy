package nsgaII;

import java.util.Random;

import hcs.HCS;
import hcs.Machine;
import hcs.Task;
import tools.Utilities;

/**
 * 
 * @author soto190
 * 
 */
public class NSGAII {

	private HCS hcs;
	private Random rnd = new Random();

	private int populationSize;
	private Solution[] population;

	public NSGAII(HCS hcs, int populationSize) {
		this.hcs = hcs;
		this.populationSize = populationSize;
	}

	public void start() {
		population = new Solution[populationSize];
		for (int ind = 0; ind < populationSize; ind++)
			population[ind] = generateRandomSolution();
	}

	public Solution generateRandomSolution() {
		Solution sol = new Solution(hcs.getTotalTasks(), hcs.getTotalMachines());
		for (int task = 0; task < sol.getChromosomaSize(); task++) {
			Machine machine = hcs
					.getMachine(rnd.nextInt(hcs.getTotalMachines()));
			int kConfig = rnd.nextInt(machine.getkConfig());
			sol.setGen(new Task(task), machine, kConfig);
		}
		return sol;
	}

	public void sort(Solution[] population){
	}
	
	public static void main(String[] args) {
		Utilities utils = new Utilities();
		NSGAII nsgaII = new NSGAII(utils.readInstance(13), 50);
		nsgaII.start();
	}
}
