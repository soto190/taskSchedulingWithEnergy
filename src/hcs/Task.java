package hcs;

/**
 * 
 * This class represents a Task from the HCSP ( Heterogenous Computing System
 * Problem).
 * 
 * @author soto190
 * 
 * 
 * 
 */
public class Task {

	protected int id = -1;
	protected int assignedMachine = -1;
	protected double currentExecTime = -1;
	protected double energyConsumed = -1;
	protected int kConfig = -1;

	/**
	 * Creates a task with the id.
	 * 
	 * @param id
	 *            Integer with the id of the task.
	 */
	public Task(int id) {
		this.id = id;
	}

	/**
	 * 
	 * @return Integer with the task Id.
	 */
	public int getId() {
		return this.id;
	}

	public void setInMachine(Machine machine, int kConfig) {
		this.assignedMachine = machine.getId();
		this.currentExecTime = machine.getExecTime(this)
				/ machine.getKspeed(kConfig);
		this.energyConsumed = currentExecTime * machine.getKvoltaje(kConfig)
				* machine.getKvoltaje(kConfig);
		this.kConfig = kConfig;
	}

	/**
	 * Depends of the machine assigned.
	 * 
	 * @return Double with the current execution time of the task.
	 */
	public double getCurrentExecTime() {
		return this.currentExecTime;
	}

	/**
	 * 
	 * @param machine
	 *            The machine in which will be execute the current task.
	 * @param kConfig
	 *            The configuration to be used to execute the current task in
	 *            the assigend machine.
	 */
	public void setCurrentExecTime(Machine machine, int kConfig) {
		currentExecTime = machine.getExecTime(this)
				/ machine.getKspeed(kConfig);
	}

	/**
	 * 
	 * @return Id of the assigned machine.
	 */
	public int getAssignedMachine() {
		return this.assignedMachine;
	}

	/**
	 * Assigns the machin in which this task will be execute.
	 * 
	 * @param machine
	 */
	public void setAssingedMachine(Machine machine) {
		this.assignedMachine = machine.getId();
	}
	
	/**
	 * 
	 * @return Integer with the k configurations.
	 */

	public int getkConfig() {
		return this.kConfig;
	}

	public void setkConfig(int kConfig) {
		this.kConfig = kConfig;
	}

	public double getCurrentEnergy() {
		return this.energyConsumed;
	}

	@Override
	public String toString() {
		return "Task [id=" + id + ", assignedMachine=" + assignedMachine
				+ ", currentExecTime=" + currentExecTime + ", kConfig="
				+ kConfig + "]";
	}

}
