package hcs;

/**
 * 
 * @author soto190
 * 
 */
public class Schedule {

	protected Task[] schedule;

	protected double[] execTimeInMachine;
	protected double[] energyInMachine;

	protected int[] machinesWithMakespan;
	protected int machineWithMinTime;
	protected double makespan;
	protected double minTime;
	protected double energy;
	protected int totalMachinesWithMakespan;

	protected int totalTasks;
	protected int totalMachines;

	public Schedule(int totaTasks, int totalMachines) {

		makespan = 0;
		energy = 0;

		this.totalTasks = totaTasks;
		this.totalMachines = totalMachines;

		execTimeInMachine = new double[totalMachines];
		energyInMachine = new double[totalMachines];

		machinesWithMakespan = new int[totaTasks];

		schedule = new Task[totaTasks];

	}

	public void setTaskInMachine(Task task, Machine machine, int kConfig) {
		if (this.schedule[task.getId()] == null) {
			this.schedule[task.getId()] = new Task(task.getId());
			this.schedule[task.getId()].setInMachine(machine, kConfig);

			this.execTimeInMachine[machine.getId()] += this.schedule[task
					.getId()].getCurrentExecTime();

			this.energyInMachine[machine.getId()] += this.schedule[task.getId()]
					.getCurrentEnergy();

			this.energy += this.schedule[task.getId()].getCurrentEnergy();
		} else
			changeTaskToMachine(this.schedule[task.getId()], machine, kConfig);

	}

	public void changeTaskToMachine(Task task, Machine machine, int kConfig) {
		this.execTimeInMachine[task.getAssignedMachine()] -= task
				.getCurrentExecTime();
		this.energyInMachine[task.getAssignedMachine()] -= task
				.getCurrentEnergy();

		this.schedule[task.getId()].setInMachine(machine, kConfig);

		this.execTimeInMachine[task.getAssignedMachine()] -= task
				.getCurrentExecTime();
		this.energyInMachine[task.getAssignedMachine()] -= task
				.getCurrentEnergy();
	}

	public void computeMakespanAndEnergy() {
		makespan = 0;
		energy = 0;
		minTime = Integer.MAX_VALUE;
		totalMachinesWithMakespan = 0;
		machineWithMinTime = 0;

		for (int i = 0; i < totalMachines; i++) {

			if (execTimeInMachine[i] > makespan) {
				makespan = execTimeInMachine[i];
				machinesWithMakespan[totalMachinesWithMakespan++] = i;
			}

			if (execTimeInMachine[i] < minTime) {
				minTime = execTimeInMachine[i];
				machineWithMinTime = i;
			}

			energy += energyInMachine[i];
		}
	}

	public int getTotalTasks() {
		return this.totalTasks;
	}

	public int getTotalMachines() {
		return this.totalMachines;
	}

	public double getMakespan() {
		return this.getMakespan();
	}

	public double getEnergy() {
		return this.getEnergy();
	}
}
