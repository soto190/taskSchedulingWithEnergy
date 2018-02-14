package hcs;

/**
 * 
 * @author soto190
 * 
 */
public class Machine {

	protected int id;
	protected int numberOfConfigurations;
	/**
	 * protected double totalExecTime = 0; protected double totalEnergy = 0;
	 **/
	protected double[] voltaje;
	protected double[] speeds;

	protected double[] taskExecutionTime;
	
	protected boolean lockKConfig = false;

	/**
	 * protected int totalTask = 0;
	 * 
	 * protected Task[] assignedTask = new Task[512];
	 **/

	public Machine(int id) {
		this.id = id;
	}

	public Machine(int id, int totalTasks) {
		this.id = id;
		this.taskExecutionTime = new double[totalTasks];
	}

	public Machine(int id, Machine machine) {
		this.id = id;
		this.numberOfConfigurations = machine.getkConfig();

		for (int k = 0; k < machine.getkConfig(); k++)
			this.setKConfigVoltajeAndSpeed(k, machine.getKvoltaje(k),
					machine.getKspeed(k));

	}

	public Machine(int id, int totalTasks, int kConfig) {
		this.id = id;
		this.taskExecutionTime = new double[totalTasks];
		this.numberOfConfigurations = kConfig;
		this.voltaje = new double[kConfig];
		this.speeds = new double[kConfig];
	}

	/**
	 * public void addTask(Task task) { totalTask++; assignedTask[task.getId()]
	 * = task; }
	 **/
	/**
	 * public void removeTask(Task task) { if (assignedTask[task.getId()] !=
	 * null) { totalTask--; assignedTask[task.getId()] = null; totalExecTime -=
	 * task.getCurrentExecTime(); } }
	 **/
	public int getId() {
		return id;
	}

	public int getkConfig() {
		return numberOfConfigurations;
	}

	public void setkConfig(int kConfig) {
		if (lockKConfig == false) {
			this.numberOfConfigurations = kConfig;
			lockKConfig = true;
		}
	}

	public void addTask(int taskId, double execTime) {
		this.taskExecutionTime[taskId] = execTime;
	}

	/**
	 * public double getTotalExecTime() { return totalExecTime; }
	 * 
	 * public void setTotalExecTime(double totalExecTime) { this.totalExecTime =
	 * totalExecTime; }
	 * 
	 * public void increaseExecTime(double execTime) { this.totalExecTime +=
	 * execTime; }
	 * 
	 * public double getTotalEnergy() { return totalEnergy; }
	 * 
	 * public void setTotalEnergy(double totalEnergy) { this.totalEnergy =
	 * totalEnergy; }
	 * 
	 * public void increaseEnergy(double energy) { this.totalEnergy += energy; }
	 **/

	public double getExecTime(Task task) {
		return taskExecutionTime[task.getId()];
	}

	public double[] getVoltaje() {
		return voltaje;
	}

	public void setVoltaje(double[] voltaje) {
		for (int i = 0; i < voltaje.length; i++)
			this.voltaje[i] = voltaje[i];
	}

	public double[] getSpeeds() {
		return speeds;
	}

	public double getKspeed(int k) {
		return speeds[k];
	}

	public void setKspeed(int k, double speed) {
		this.speeds[k] = speed;
	}

	public double getKvoltaje(int k) {
		return voltaje[k];
	}

	public void setKvoltaje(int k, double voltaje) {
		this.voltaje[k] = voltaje;
	}

	public void setSpeeds(double[] speeds) {
		for (int i = 0; i < speeds.length; i++)
			this.speeds[i] = speeds[i];
	}

	public void setKConfigVoltajeAndSpeed(int k, double voltaje, double speed) {
		this.speeds[k] = speed;
		this.voltaje[k] = voltaje;

	}

	public void copyKConfig(int k, Machine machine) {
		this.setKConfigVoltajeAndSpeed(k, machine.getKvoltaje(k),
				machine.getKspeed(k));
	}
	
	public boolean lockedConfig(){
		return this.lockKConfig;
	}

	/**
	 * public Task[] getAssignedTask() { return assignedTask; }
	 * 
	 * public void setAssignedTask(Task[] assignedTask) { for (int i = 0; i <
	 * assignedTask.length; i++) this.assignedTask[i] = assignedTask[i]; }
	 **/

	@Override
	public String toString() {
		return "Machine [id=" + id + ", kConfig=" + numberOfConfigurations + "]";
	}

}
