package nsgaII;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

//import jmetal.core.SolutionSet;
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
	private String instanceName;
	private Random rnd = new Random();

	private int populationSize;
	private int totalGenerations;
	private int currentGeneration;
	private int chromosomaSize;
	private double probMutation;
	private Solution[] population;
	private int totalInPareto;

	private int mutation = 3;
	private int crossover = 1;

	private String workPath = System.getProperty("user.dir");

	public NSGAII() {
	}

	public NSGAII(HCS hcs) {
		this.instanceName = hcs.getName();
		this.hcs = hcs;
		this.chromosomaSize = hcs.getTotalTasks();
	}

	public Solution[] start(int populationSize, int totalGenerations,
							double probMutation) {

		// String memetic = "memetic";

		String memetic = "No";

		this.populationSize = populationSize;
		this.totalGenerations = totalGenerations;
//		System.out.println("Solving the instance " + hcs.getName() + " with "
//				+ getPopulationSize() + " and " + getTotalGenerations()
//				+ " ...");
		currentGeneration = 0;

		population = generateParents(populationSize);
		population = dominateSort(population);
		population = makeNewPopulation(population);

//		this.savePopulation("/Experiments/Ex16/MS0/MS0.init");


		ArrayList<ArrayList<Solution>> front;
		while (currentGeneration < totalGenerations) {

			front = fastNonDominateSort(population);

			totalInPareto = front.get(0).size();
			Solution[] parents = new Solution[populationSize];
			int parent = 0;

			while (parent < populationSize)

				for (int i = 0; i < front.size() - 1; i++) {
					ArrayList<Solution> f = front.get(i);
					crowdingDistanceAssignment(f);

					for (int index = 0; index < f.size(); index++) {
						parents[parent++] = f.get(index);
						if (parent == populationSize) {
							index = f.size();
							i = front.size() - 1;
						}
					}
				}

			if (memetic.equals("memetic"))
				population = memetic(population);

			//System.out.println(front.get(0).size());

			sort(parents, Solution.CROWDED);
			population = makeNewPopulation(parents);

			currentGeneration++;

		}

		sort(population, Solution.CROWDED);
		// unique(population);

		return population;

	}

	private Solution[] memetic(Solution[] population) {

		for (int i = 0; i < population.length; i++)
			localSearch(population[i]);

		return population;
	}

	private Solution localSearch(Solution solution) {

		int sizeSearch = 50, config, newConfig;
		double newMaE[], localMaE[];
		Machine machine, newMachine;
		Task task;
		localMaE = solution.getObjectives();

		for (int c = 0; c < sizeSearch; c++) {
			task = hcs.getTask(rnd.nextInt(hcs.getTotalTasks()));
			/**
			 * Initianl machine and configuration.
			 */
			machine = hcs.getMachine(solution.getGen(task.getId()).getAssignedMachine());
			config = solution.getGen(task.getId()).getkConfig();

			/**
			 * Random machine and configuration.
			 *
			 */
			newMachine = hcs.getMachine(rnd.nextInt(hcs.getTotalMachines()));
			newConfig = rnd.nextInt(newMachine.getTotalConfig());

			solution.setGen(task, newMachine, newConfig);

			newMaE = hcs.computeMakespanAndEnergy(solution);

			/**
			 * If new is better than local then local is updated.
			 */
			if ((newMaE[0] <= localMaE[0] && newMaE[1] < localMaE[1])
					|| (newMaE[0] < localMaE[0] && newMaE[1] <= localMaE[1])) {
				localMaE[0] = newMaE[0];
				localMaE[1] = newMaE[1];
				config = newConfig;
				machine = newMachine;
			}

			solution.setGen(task, machine, config);
		}
		hcs.computeMakespanAndEnergy(solution);
		return solution;
	}

	@SuppressWarnings("unused")
	private void printSolutions(ArrayList<ArrayList<Solution>> front) {
		for (ArrayList<Solution> f : front)
			for (Solution solution : f)
				System.out.println("\t" + solution);
	}

	@SuppressWarnings("unused")
	private void printSolutions(Solution[] sol) {
		for (int i = 0; i < sol.length; i++)
			System.out.println("\t" + sol[i]);
	}

	private Solution[] makeNewPopulation(Solution[] population) {

		totalInPareto = 0;
		population = selection(population);

		int i = 0;
		for (int ind = population.length / 2; ind < population.length; ind += 2) {

			if (population[i].getRank() == 0)
				totalInPareto++;

			if (population[i + 1].getRank() == 0)
				totalInPareto++;

			Solution[] child = crossover(population[i], population[i + 1],
					getCrossover());

			population[ind] = child[0];
			population[ind + 1] = child[1];

			if (rnd.nextDouble() <= probMutation)
				population[ind] = mutation(child[0], this.mutation);
			if (rnd.nextDouble() <= probMutation)
				population[ind + 1] = mutation(child[1], this.mutation);

			hcs.computeMakespanAndEnergy(population[ind]);
			hcs.computeMakespanAndEnergy(population[ind + 1]);
			// hcs.computeObjectives(population[ind]);
			// hcs.computeObjectives(population[ind + 1]);

			i += 2;
		}

		return population;
	}

	private Solution[] dominateSort(Solution[] population) {
		ArrayList<ArrayList<Solution>> front = fastNonDominateSort(population);
		int index = 0;
		for (ArrayList<Solution> q : front)
			for (Solution p : q)
				population[index++] = p;

		return population;
	}

	private Solution[] generateParents(int populationSize) {
		population = new Solution[populationSize];
		for (int ind = 0; ind < populationSize; ind++) {
			population[ind] = generateRandomSolution();
			// population[ind] = generateSolutionConstructionLS();
			hcs.computeMakespanAndEnergy(population[ind]);
		}
		return population;
	}

	private Solution[] selection(Solution[] population) {

		int totalParents = population.length / 2;

		ArrayList<Solution> P = new ArrayList<Solution>(
				Arrays.asList(population));
		Solution[] parents = new Solution[population.length];

		for (int i = 0; i < totalParents; i++) {

			int a = rnd.nextInt(P.size());
			int b = rnd.nextInt(P.size() - 1);

			while (b == a)
				b = rnd.nextInt(P.size());

			Solution tempA = P.get(a);
			Solution tempB = P.get(b);

			int dominance = tempA.compare(tempB, Solution.DOMINANCE);
			int crowding = tempA.compare(tempB, Solution.CROWDED);

			if (dominance == -1)
				parents[i] = tempA;
			else if (dominance == 0)
				if (crowding == -1)
					parents[i] = tempA;
				else if (crowding == 0)
					parents[i] = tempB;
				else
					parents[i] = tempB;
			else
				parents[i] = tempB;

			if (a < b) {
				P.remove(b);
				P.remove(a);
			} else {
				P.remove(a);
				P.remove(b);
			}
		}
		return parents;
	}

	/**
	 *
	 * @param parent1
	 * @param parent2
	 * @param type
	 * @return
	 */
	private Solution[] crossover(Solution parent1, Solution parent2, int type) {
		if (type == 1)
			return crossoverUniform(parent1, parent2);
		else if (type == 2)
			return crossoverMiddlePoint(parent1, parent2);
		else if (type == 3)
			return crossoverTwoPoints(parent1, parent2);
		else if (type == 4)
			return crossoverMultiPoint(parent1, parent2);
		else if (type == 5)
			return crossoverSinglePoint(parent1, parent2);
		else
			return null;

	}

	/**
	 * Uniform Point Crossover.
	 *
	 * @param parent1
	 * @param parent2
	 * @return
	 */
	private Solution[] crossoverUniform(Solution parent1, Solution parent2) {

		Solution[] child = new Solution[2];

		child[0] = new Solution(currentGeneration, chromosomaSize,
				hcs.getTotalMachines());
		child[1] = new Solution(currentGeneration, chromosomaSize,
				hcs.getTotalMachines());

		for (int gen = 0; gen < chromosomaSize; gen++) {
			Task t1 = parent1.getGen(gen);
			Task t2 = parent2.getGen(gen);

			Machine m1 = hcs.getMachine(t1.getAssignedMachine());
			Machine m2 = hcs.getMachine(t2.getAssignedMachine());

			if (rnd.nextDouble() < 0.5) {
				child[0].setGen(t1, m1);
				child[1].setGen(t2, m2);
			} else {
				child[0].setGen(t2, m2);
				child[1].setGen(t1, m1);
			}
		}

		return child;
	}

	/**
	 *
	 * @param parent1
	 * @param parent2
	 * @return
	 */
	private Solution[] crossoverMiddlePoint(Solution parent1, Solution parent2) {
		Solution[] child = new Solution[2];

		child[0] = new Solution(currentGeneration, chromosomaSize,
				hcs.getTotalMachines());
		child[1] = new Solution(currentGeneration, chromosomaSize,
				hcs.getTotalMachines());

		int middlePoint = parent1.getTotalTasks() / 2;

		for (int gen = 0; gen < middlePoint; gen++) {

			Task t1 = parent1.getGen(gen);
			Task t2 = parent2.getGen(gen);

			Machine m1 = hcs.getMachine(t1.getAssignedMachine());
			Machine m2 = hcs.getMachine(t2.getAssignedMachine());

			child[0].setGen(t1, m1);
			child[1].setGen(t2, m2);
		}

		for (int gen = middlePoint; gen < parent1.getTotalTasks(); gen++) {

			Task t1 = parent1.getGen(gen);
			Task t2 = parent2.getGen(gen);

			Machine m1 = hcs.getMachine(t1.getAssignedMachine());
			Machine m2 = hcs.getMachine(t2.getAssignedMachine());

			child[0].setGen(t2, m2);
			child[1].setGen(t1, m1);
		}

		return child;
	}

	private Solution[] crossoverSinglePoint(Solution parent1, Solution parent2) {
		Solution[] child = new Solution[2];

		child[0] = new Solution(currentGeneration, chromosomaSize,
				hcs.getTotalMachines());
		child[1] = new Solution(currentGeneration, chromosomaSize,
				hcs.getTotalMachines());

		int point = rnd.nextInt(chromosomaSize);

		for (int gen = 0; gen < point; gen++) {

			Task t1 = parent1.getGen(gen);
			Task t2 = parent2.getGen(gen);

			Machine m1 = hcs.getMachine(t1.getAssignedMachine());
			Machine m2 = hcs.getMachine(t2.getAssignedMachine());

			child[0].setGen(t1, m1);
			child[1].setGen(t2, m2);
		}

		for (int gen = point; gen < parent1.getTotalTasks(); gen++) {

			Task t1 = parent1.getGen(gen);
			Task t2 = parent2.getGen(gen);

			Machine m1 = hcs.getMachine(t1.getAssignedMachine());
			Machine m2 = hcs.getMachine(t2.getAssignedMachine());

			child[0].setGen(t2, m2);
			child[1].setGen(t1, m1);
		}

		return child;
	}

	/**
	 *
	 * @param parent1
	 * @param parent2
	 * @return
	 */
	private Solution[] crossoverTwoPoints(Solution parent1, Solution parent2) {
		Solution[] child = new Solution[2];

		child[0] = new Solution(currentGeneration, chromosomaSize,
				hcs.getTotalMachines());
		child[1] = new Solution(currentGeneration, chromosomaSize,
				hcs.getTotalMachines());

		int sizeCut = hcs.getTotalTasks() / 3;

		boolean flag = rnd.nextBoolean();

		for (int gen = 0; gen < hcs.getTotalTasks(); gen++) {
			Task t1 = parent1.getGen(gen);
			Task t2 = parent2.getGen(gen);

			Machine m1 = hcs.getMachine(t1.getAssignedMachine());
			Machine m2 = hcs.getMachine(t2.getAssignedMachine());

			if (gen % sizeCut == 0)
				flag = !flag;

			if (flag) {

				child[0].setGen(t1, m1);
				child[1].setGen(t2, m2);

			} else {

				child[0].setGen(t2, m2);
				child[1].setGen(t1, m1);

			}
		}

		return child;
	}

	/**
	 *
	 * @param parent1
	 * @param parent2
	 * @return
	 */

	private Solution[] crossoverMultiPoint(Solution parent1, Solution parent2) {
		Solution[] child = new Solution[2];

		child[0] = new Solution(currentGeneration, chromosomaSize,
				hcs.getTotalMachines());
		child[1] = new Solution(currentGeneration, chromosomaSize,
				hcs.getTotalMachines());

		int cuts = rnd.nextInt(parent1.getChromosomaSize() - 1) + 1;

		int sizeCut = hcs.getTotalTasks() / cuts;
		boolean flag = rnd.nextBoolean();

		for (int gen = 0; gen < hcs.getTotalTasks(); gen++) {

			Task t1 = parent1.getGen(gen);
			Task t2 = parent2.getGen(gen);

			Machine m1 = hcs.getMachine(t1.getAssignedMachine());
			Machine m2 = hcs.getMachine(t2.getAssignedMachine());

			if (gen % sizeCut == 0)
				flag = !flag;

			if (flag) {

				child[0].setGen(t1, m1);
				child[1].setGen(t2, m2);

			} else {

				child[0].setGen(t2, m2);
				child[1].setGen(t1, m1);

			}
		}

		return child;
	}

	/**
	 * 1: Changed 10% of the chromosome.
	 * 2: Every gen has 5% probability to change.
	 * 3: Load balanced mutation.
	 *
	 * @param sol
	 * @param Type
	 * @return
	 */
	private Solution mutation(Solution sol, int Type) {
		if (Type == 1)
			return mutationType1(sol);
		else if (Type == 2)
			return mutationType2(sol);
		else if (Type == 3)
			return mutationType3(sol);
		else if (Type == 4)
			return mutationType4(sol);
		else
			return null;
	}

	/**
	 * Cambia el 10% de los genes.
	 *
	 * @param sol
	 * @return
	 */
	private Solution mutationType1(Solution sol) {

		int totalGenesToChange = (int) Math.ceil(chromosomaSize * 0.1);
		int changedGenes = 0;
		int genPicked;
		boolean[] picked = new boolean[chromosomaSize];
		while (changedGenes < totalGenesToChange) {

			do {
				genPicked = rnd.nextInt(chromosomaSize);
			} while (picked[genPicked]);

			Task gen = sol.getGen(genPicked);
			Machine m = hcs.getMachine(gen.getAssignedMachine());
			sol.setTaskInMachine(gen, m, rnd.nextInt(m.getTotalConfig()));

			picked[genPicked] = true;
			changedGenes++;
		}
		return sol;
	}

	/**
	 * Cada gen tiene un 0.05 probabilidad de cambiar.
	 *
	 * @param sol
	 * @return
	 */
	private Solution mutationType2(Solution sol) {
		for (int g = 0; g < sol.getChromosomaSize(); g++)
			if (rnd.nextDouble() < 0.05) {
				Task gen = sol.getGen(g);
				Machine m = hcs.getMachine(gen.getAssignedMachine());
				sol.setTaskInMachine(sol.getGen(g), m,
						rnd.nextInt(m.getTotalConfig()));
			}
		return sol;
	}

	/**
	 * Move a task from a machine with makespan to another.
	 *
	 * @param sol
	 * @return
	 */
	private Solution mutationType3(Solution sol) {

		int machineMakespan = sol.getMachineWithMakespan(rnd.nextInt(sol
				.getTotalMachinesWithMakespan()));

		Task taksInM[] = new Task[chromosomaSize];
		int totalTareasInM = 0;
		for (int task = 0; task < chromosomaSize; task++)
			if (sol.getGen(task).getAssignedMachine() == machineMakespan)
				taksInM[totalTareasInM++] = sol.getGen(task);

		sol.setTaskInMachine(taksInM[rnd.nextInt(totalTareasInM)],
				hcs.getMachine(machineMakespan),
				taksInM[rnd.nextInt(totalTareasInM)].getkConfig());

		return sol;
	}

	private Solution mutationType4(Solution sol) {

		int genPicked = rnd.nextInt(chromosomaSize);

		Task gen = sol.getGen(genPicked);
		Machine m = hcs.getMachine(rnd.nextInt(hcs.getTotalMachines()));
		sol.setTaskInMachine(gen, m, rnd.nextInt(m.getTotalConfig()));

		return sol;
	}

	/**
	 *
	 * @param population
	 * @return
	 */
	private ArrayList<ArrayList<Solution>> fastNonDominateSort(
			Solution[] population) {

		List<Solution> l = Arrays.asList(population);
		ArrayList<Solution> P = new ArrayList<Solution>(l);
		ArrayList<ArrayList<Solution>> paretoFront = new ArrayList<ArrayList<Solution>>(10);
		ArrayList<ArrayList<Solution>> IDominatesTo = new ArrayList<ArrayList<Solution>>(10);

		for (Solution p : P) {
			p.dominateBy = 0;
			p.setRank(-1);
		}

		paretoFront.add(new ArrayList<Solution>());

		for (int indexP = 0; indexP < P.size(); indexP++) {
			Solution p = P.get(indexP);
			IDominatesTo.add(new ArrayList<Solution>());
			for (Solution q : P) {
				if (p.dominate(q))
					IDominatesTo.get(indexP).add(q);
				else if (q.dominate(p))
					p.increaseDominateBy();
			}
			if (p.getDominateBy() == 0) {
				p.setRank(0);
				paretoFront.get(0).add(p);
			}
		}
		int i = 0;
		while (!paretoFront.get(i).isEmpty()) {
			ArrayList<Solution> H = new ArrayList<Solution>();
			for (Solution p : paretoFront.get(i))
				for (Solution q : IDominatesTo.get(P.indexOf(p))) {
					q.decreaseDominatedBy();
					if (q.getDominateBy() == 0 && q.getRank() == -1) {
						q.setRank(i + 1);
						H.add(q);
					}
				}
			i++;
			paretoFront.add(i, H);
		}
		return paretoFront;
	}

	/**
	 *
	 * @param I array of solutions.
	 */
	private void crowdingDistanceAssignment(ArrayList<Solution> I) {
		int l = I.size() - 1;

		for (Solution solution : I)
			solution.setDistance(0);

		for (int obj = 1; obj <= 2; obj++) {
			sort(I, obj);
			I.get(0).setDistance(Double.MAX_VALUE);
			I.get(l).setDistance(Double.MAX_VALUE);
			for (int i = 1; i <= l - 1; i++) {
				Solution temp = I.get(i);
				temp.setDistance(temp.getDistance()
						+ (I.get(i + 1).getObjective(obj) - I.get(i - 1)
						.getObjective(obj)));
			}
		}

	}

	private void sort(ArrayList<Solution> I, int objective) {
		if (I.size() > 1)
			quickSort(I, 0, I.size() - 1, objective);
	}

	private void sort(Solution[] I, int objective) {
		if (I.length > 1)
			quickSort(I, 0, I.length - 1, objective);
	}

	public static void quickSort(ArrayList<Solution> I, int low, int high,
								 int TYPE) {
		int i = low, j = high;
		// Get the pivot element from the middle of the list
		Solution pivot = I.get(low + (high - low) / 2);

		// Divide into two lists
		while (i <= j) {
			// If the current value from the left list is smaller then the pivot
			// element then get the next element from the left list

			while (I.get(i).compare(pivot, TYPE) == -1)
				i++;

			// If the current value from the right list is larger then the pivot
			// element then get the next element from the right list
			while (I.get(j).compare(pivot, TYPE) == 1)
				j--;

			// If we have found a values in the left list which is larger then
			// the pivot element and if we have found a value in the right list
			// which is smaller then the pivot element then we exchange the
			// values.
			// As we are done we can increase i and j
			if (i <= j) {
				Solution temp = I.get(i);
				I.set(i, I.get(j));
				I.set(j, temp);
				i++;
				j--;
			}
		}
		// Recursion
		if (low < j)
			quickSort(I, low, j, TYPE);
		if (i < high)
			quickSort(I, i, high, TYPE);
	}

	public static void quickSort(Solution[] I, int low, int high, int TYPE) {
		int i = low, j = high;

		Solution pivot = I[low + (high - low) / 2];

		while (i <= j) {

			while (I[i].compare(pivot, TYPE) == -1)
				i++;

			while (I[j].compare(pivot, TYPE) == 1)
				j--;

			if (i <= j) {
				Solution temp = I[i];
				I[i] = I[j];
				I[j] = temp;
				i++;
				j--;
			}
		}
		if (low < j)
			quickSort(I, low, j, TYPE);
		if (i < high)
			quickSort(I, i, high, TYPE);
	}

	public int getPopulationSize() {
		return populationSize;
	}

	public int getTotalGenerations() {
		return totalGenerations;
	}

	/**
	 * @author Alx Written in Java by soto190@gmail.com
	 */
	private Solution generateSolutionConstructionLS() {
		int newConfig, config, maxSteps = 200, step = 0;
		double newMaE[], localMaE[];

		Machine machine;

		Solution solution = new Solution(this.chromosomaSize,
				this.hcs.getTotalMachines());
		Task task = hcs.getTask(rnd.nextInt(this.chromosomaSize));
		Machine newMachine = hcs
				.getMachine(rnd.nextInt(hcs.getTotalMachines()));

		newConfig = rnd.nextInt(newMachine.getTotalConfig());
		solution.setGen(task, newMachine, newConfig);
		for (int ch = 0; ch < this.chromosomaSize; ch++) {
			step = 0;

			localMaE = hcs.computeMakespanAndEnergy(solution);

			do {
				step++;
				/**
				 * Tarea aleatoria
				 */
				task = hcs.getTask(rnd.nextInt(hcs.getTotalTasks()));

				/**
				 * Si la tarea aletoria no habáa sido asignada se le asigna un
				 * máquina y configuración aleatoria.
				 */
				Task temp = solution.getGen(task.getId());
				if (temp == null) {
					Machine tempMachine = hcs.getMachine(rnd.nextInt(hcs
							.getTotalMachines()));
					int tempConfig = rnd.nextInt(tempMachine.getTotalConfig());

					solution.setGen(task, tempMachine, tempConfig);

					localMaE = hcs.computeMakespanAndEnergy(solution);

				}

				// Initial machine and configuration.
				machine = hcs.getMachine(solution.getGen(task.getId())
						.getAssignedMachine());
				config = solution.getGen(task.getId()).getkConfig();

				 //R andom machine and configuration.
				newMachine = hcs
						.getMachine(rnd.nextInt(hcs.getTotalMachines()));
				newConfig = rnd.nextInt(newMachine.getTotalConfig());

				solution.setGen(task, newMachine, newConfig);

				newMaE = hcs.computeMakespanAndEnergy(solution);

				if ((newMaE[0] <= localMaE[0] && newMaE[1] < localMaE[1])
						|| (newMaE[0] < localMaE[0] && newMaE[1] <= localMaE[1])) {
					localMaE[0] = newMaE[0];
					localMaE[1] = newMaE[1];
					config = newConfig;
					machine = newMachine;
				}

				solution.setGen(task, machine, config);

			} while (step <= maxSteps);

		}

		return solution;
	}

	private Solution generateRandomSolution() {

		Solution sol = new Solution(currentGeneration, hcs.getTotalTasks(),
				hcs.getTotalMachines());

		for (int task = 0; task < sol.getChromosomaSize(); task++) {
			Machine machine = hcs
					.getMachine(rnd.nextInt(hcs.getTotalMachines()));

			int kConfig = rnd.nextInt(machine.getTotalConfig());
			sol.setGen(hcs.getTask(task), machine, kConfig);
		}
		return sol;
	}

	public void savePopulation(String file) {
		try {

			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
					workPath + file), false));
			for (int i = 0; i < population.length; i++)
				bw.write(population[i].getMakespan() + "\t"
						+ population[i].getEnergy() + "\n");
			bw.flush();
			bw.close();
			System.out.println("Output file:" + workPath + file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveParetoFront(String filePath) {
		Solution[] pareto = new Solution[population.length];
		try {

			File front = new File(workPath + filePath);
			BufferedWriter bw = new BufferedWriter(new FileWriter(front, false));

			String[] temp = filePath.split("\\.");
			File f = new File(workPath + temp[0] + "." + temp[1] + ".pf");

			BufferedWriter bwPareto = new BufferedWriter(new FileWriter(f,
					false));

			int k = 0;
			boolean writeIt = true;
			for (int i = 0; i < populationSize; i++) {

				bw.write(population[i].getMakespan() + "\t"
						+ population[i].getEnergy() + "\n");

				writeIt = true;
				for (int j = 0; j < k; j++)
					if (pareto[j].getMakespan() == population[i].getMakespan()
							&& pareto[j].getEnergy() == population[i]
							.getEnergy())
						writeIt = false;

				if (writeIt) {
					pareto[k++] = population[i];

					bwPareto.write(population[i].getMakespan() + "\t"
							+ population[i].getEnergy() + "\n");
				}
			}

			bw.flush();
			bw.close();
			bwPareto.flush();
			bwPareto.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Solution[] saveParetoFront(String pathFile, Solution[] solution) {
		Solution[] pareto = new Solution[totalInPareto];
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
					workPath + pathFile), false));
			// int k =0;
			for (int i = 0; i < totalInPareto; i++) {
				bw.write(solution[i].getMakespan() + "\t"
						+ solution[i].getEnergy() + "\n");
			}

			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return pareto;
	}

	public void saveVariables(String file) {
		try {

			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
					workPath + file), false));
			//for (int i = 0; i < population.length; i++)
			//	System.out.println(population[i]);
			// for (int j = 0; j < this.chromosomaSize; j++) {
			// bw.write(population[i].getMakespan() + "\t"
			// + population[i].getEnergy() + "\n");
			// }

			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setMutation(int mutation) {
		this.mutation = mutation;
	}

	/**
	 *
	 * @param crossover
	 */

	public void setCrossover(int crossover) {
		this.crossover = crossover;
	}

	public int getMutation() {
		return this.mutation;
	}

	public int getCrossover() {
		return this.crossover;
	}

	public String getInstanceName() {
		return this.instanceName;
	}

	public Solution[] getParetoFront(Solution[] solution) {
		Solution[] pareto = new Solution[totalInPareto];
		for (int i = 0; i < totalInPareto; i++)
			pareto[i] = solution[i];
		return pareto.clone();
	}


}
