package nsgaII;

import java.util.Comparator;

import hcs.Machine;
import hcs.Schedule;
import hcs.Task;

/**
 *
 * @author soto190
 *
 */
public class Solution extends Schedule implements Comparator<Solution> {

	int id;
	int dominateBy = Integer.MAX_VALUE;
	int rank = Integer.MAX_VALUE;
	double distance = 0;
	int fitness = 0;

	int generation = -1;
	public static int totalSolutions = 0;
	public static final int CROWDED = 0, MAKESPAN = 1, ENERGY = 2,
			DOMINANCE = 3;


	public Solution(int totaTasks, int totalMachines) {
		super(totaTasks, totalMachines);
		id = totalSolutions++;
	}

	public Solution(int generation, int totaTasks, int totalMachines) {
		super(totaTasks, totalMachines);
		this.generation = generation;
		id = totalSolutions++;
	}

	public int getId() {
		return this.id;
	}

	public int getChromosomaSize() {
		return this.getTotalTasks();
	}

	public void setGen(Task gen) {
		this.schedule[gen.getId()].copyThis(gen);
	}

	public void setGen(Task task, Machine machine) {
		this.setTaskInMachine(task, machine, task.getkConfig());
	}

	public void setGen(Task task, Machine machine, int config) {
		this.setTaskInMachine(task, machine, config);
	}

	public Task getGen(int gen) {
		return this.schedule[gen];
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public int getRank() {
		return this.rank;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public double getDistance() {
		return this.distance;
	}

	public void setFitness(int fitness) {
		this.fitness = fitness;
	}

	public int getFitness() {
		return this.fitness;
	}

	public void copyThis(Solution s) {

	}

	/**
	 * public void setGen(int task, int machine, int kConfig){
	 * this.setTaskInMachine(task, machine, kConfig); }
	 **/
	public double getObjective1() {
		return this.getMakespan();
	}

	public double getObjective2() {
		return this.getEnergy();
	}

	public double getObjective(int objective) {
		if (objective == MAKESPAN)
			return getObjective1();
		else if (objective == ENERGY)
			return getObjective2();

		return -1;
	}

	public int getDominateBy() {
		return this.dominateBy;
	}

	public void increaseDominateBy() {
		this.dominateBy++;
	}

	public void decreaseDominatedBy() {
		this.dominateBy--;
	}

	@Override
	public int compare(Solution sol1, Solution sol2) {
		if ((sol1.getObjective1() <= sol2.getObjective1() && sol1
				.getObjective2() < sol2.getObjective2())
				|| (sol1.getObjective1() < sol2.getObjective1() && sol1
				.getObjective2() <= sol2.getObjective2()))
			return -1;

		else if (sol1.getObjective1() <= sol2.getObjective1()
				&& sol1.getObjective2() >= sol2.getObjective2()
				|| (sol1.getObjective1() <= sol2.getObjective1() && sol1
				.getObjective2() >= sol2.getObjective2()))
			return 0;

		else if (sol1.getObjective1() >= sol2.getObjective1()
				&& sol1.getObjective2() > sol2.getObjective2()
				|| (sol1.getObjective1() > sol2.getObjective1() && sol1
				.getObjective2() >= sol2.getObjective2()))
			return 1;

		return 0;
	}

	public int compare(Solution sol2, int objective) {

		if (objective == MAKESPAN)
			if (this.getObjective1() < sol2.getObjective1())
				return -1;
			else if (this.getObjective1() > sol2.getObjective1())
				return 1;
			else
				return 0;

		else if (objective == ENERGY)
			if (this.getObjective2() < sol2.getObjective2())
				return -1;
			else if (this.getObjective2() > sol2.getObjective2())
				return 1;
			else
				return 0;

		else if (objective == CROWDED)
			if (this.getRank() < sol2.getRank()
					|| (this.getRank() == sol2.getRank() && this.getDistance() > sol2
					.getDistance()))
				return -1;
			else if (this.getRank() == sol2.getRank()
					&& this.getDistance() == sol2.getDistance())
				return 0;
			else
				return 1;

		else if (objective == DOMINANCE)
			if ((this.getObjective1() < sol2.getObjective1() && this
					.getObjective2() <= sol2.getObjective2())
					|| (this.getObjective1() <= sol2.getObjective1() && this
					.getObjective2() < sol2.getObjective2()))
				return -1;

			else if (this.getObjective1() <= sol2.getObjective1()
					&& this.getObjective2() >= sol2.getObjective2()
					|| (this.getObjective1() <= sol2.getObjective1() && this
					.getObjective2() >= sol2.getObjective2()))
				return 0;

			else if (this.getObjective1() >= sol2.getObjective1()
					&& this.getObjective2() > sol2.getObjective2()
					|| (this.getObjective1() > sol2.getObjective1() && this
					.getObjective2() >= sol2.getObjective2()))
				return 1;

		return 0;
	}

	public boolean dominate(Solution sol2) {
		if ((this.getObjective1() < sol2.getObjective1() && this
				.getObjective2() <= sol2.getObjective2())
				|| (this.getObjective1() <= sol2.getObjective1() && this
				.getObjective2() < sol2.getObjective2()))
			return true;
		else
			return false;
	}

	public void print() {
		for (int j = 0; j < getChromosomaSize(); j++)
			System.out.print(this.getGen(j)+" | ");
	}

	public String getTasksAsArray() {
		String result = "[";
		for (int j = 0; j < getChromosomaSize(); j++)
			result += String.format("%2d, %1d|", this.getGen(j)
					.getAssignedMachine(), this.getGen(j).getkConfig());
		return result + "]";
	}

	public int[][] getTaskAssignments(){

		int[][] assignments = new int[this.totalTasks][this.totalTasks];

		for (int i = 0; i < assignments.length; i++) {
			assignments[i][0] = this.getGen(i).getAssignedMachine();
			assignments[i][1] = this.getGen(i).getkConfig();
		}

		return assignments;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String
				.format("Solution [id= %4d, rank= %2d, distance= %8.6g, makespan= %,12.6f, energy= %,6.6f] %s",
						id, rank, distance, makespan, energy, getTasksAsArray());
	}

}
