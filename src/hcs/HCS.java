package hcs;

/**
 * This class represents an instance of the HCSP. Includes an Task array and
 * Machine array.
 * 
 * @author soto190
 * 
 */
public class HCS {

	protected String name;
	protected int totalTasks;
	protected int totalMachines;
	protected Machine[] machine;
	protected Task[] task;

	protected int[] schedule;

	protected double makespan;
	protected double energy;

	/**
	 * This shouldn't be here, because this information is in the Task and
	 * Machine class, but by the moment.
	 **/
	protected double[][] execTime;
	
	/**
	 * Creates an HCS with the total of tasks and machines specified.
	 * 
	 * @param totalTasks Total of the task in the HCS.
	 * @param totalMachines Total of the machines in the HCS.
	 */

	public HCS(int totalTasks, int totalMachines) {

		this.totalTasks = totalTasks;
		this.totalMachines = totalMachines;
		task = new Task[totalTasks];
		machine = new Machine[totalMachines];
		schedule = new int[totalTasks];
		execTime = new double[totalTasks][totalMachines];
	}

	/**
	 * public void setTaskInMachine(Task task, Machine machine, int kConfig){
	 * this.schedule[task.getId()] = machine.getId();
	 * 
	 * task.setCurrentExectTime(machine, kConfig); }
	 **/
	public void changeTaskToMachine(Task task, Machine machine) {

	}

	/**
	 * Add a new task to the HCS.
	 * 
	 * @param task The task to be added.
	 */
	public void addTask(Task task) {
		this.task[task.getId()] = task;
	}

	/**
	 * Add a new machine to the HCS
	 * 
	 * @param machine The machine to be added.
	 */
	public void addMachine(Machine machine) {

		this.machine[machine.getId()] = machine;

	}

	/**
	 * get the total of tasks in the HCS.
	 * 
	 * @return Integer with total of tasks in the HCS.
	 */
	public int getTotalTasks() {
		return this.task.length;
	}

	/**
	 * Get the total of machines in the HCS.
	 * 
	 * @return integer with the total of machines in the HCS.
	 */
	public int getTotalMachines() {
		return this.machine.length;
	}

	/**
	 * 
	 * @param id
	 *            Id of the machine.
	 * @return Machine with the specified id.
	 */
	public Machine getMachine(int id) {
		return machine[id];
	}

	/**
	 * 
	 * @param id
	 *            Id of the task.
	 * @return Task with the specified id.
	 */
	public Task getTask(int id) {
		return task[id];
	}

	/**
	 * Add the execution time of the task in the machine.
	 * 
	 * @param idTask
	 *            Id of the task.
	 * @param idMachine
	 *            Id of the Machine.
	 * @param execTime
	 *            execution time to process the specified task in the machine.
	 */

	public void addExecTime(int idTask, int idMachine, double execTime) {

		if (this.task[idTask] == null)
			this.task[idTask] = new Task(idTask);
		if (this.machine[idMachine] == null)
			this.machine[idMachine] = new Machine(idMachine, totalTasks);

		this.execTime[idTask][idMachine] = execTime;
		this.machine[idMachine].addTask(idTask, execTime);
	}

	/**
	 * Set the name of the current instance.
	 * 
	 * @param name
	 *            Name of the instance.
	 */

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @return String with the name of the current instance.
	 */
	public String getName() {
		return this.name;
	}

}
