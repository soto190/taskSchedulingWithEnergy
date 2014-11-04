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

	public Solution(int totaTasks, int totalMachines) {
		super(totaTasks, totalMachines);
	}

	public int getChromosomaSize() {
		return this.getTotalTasks();
	}

	public void setGen(Task task, Machine machine, int config) {
		this.setTaskInMachine(task, machine, config);
	}

	public double getObjective1() {
		return this.getMakespan();
	}

	public double getObjective2() {
		return this.getEnergy();
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

	public int dominate(Solution sol1, Solution sol2) {
		if ((sol1.getObjective1() > sol2.getObjective1() && sol1
				.getObjective2() <= sol2.getObjective2())
				|| (sol1.getObjective1() >= sol2.getObjective1() && sol1
						.getObjective2() < sol2.getObjective2()))
			return 1;
		else
			return 0;
	}

}
