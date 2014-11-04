package tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import hcs.HCS;
import hcs.Machine;

/**
 * 
 * @author soto190
 *
 */
public class Utilities {

	private static BufferedReader inputReader;
	private static File instances[];
	private static File machinesConfig;

	public Utilities() {
		loadIndex();
		loadFileMachinesConfig("Machines.conf");
	}

	private static void loadFileMachinesConfig(String config) {

		String path = System.getProperty("user.dir") + File.separator
				+ "Instances" + File.separator + "Machines" + File.separator
				+ config;
		machinesConfig = new File(path);
	}

	private static void loadIndex() {

		String workPath = System.getProperty("user.dir") + File.separator
				+ "Instances" + File.separator;

		try {
			BufferedReader br = new BufferedReader(new FileReader(workPath
					+ "Index"));

			int totalInstances = toInt(br.readLine());
			instances = new File[totalInstances];

			for (int i = 0; i < totalInstances; i++)
				instances[i] = new File(workPath + br.readLine());

			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public HCS readInstance(int indexInstance) {

		if (indexInstance >= 0 && indexInstance <= 12)
			return readInstanceBraunt(indexInstance);

		if (indexInstance >= 13 && indexInstance <= 31)
			return readInstanceReferencia(indexInstance);

		System.err.println("No se encontro la instancia " + indexInstance);
		return null;

	}

	private HCS readInstanceBraunt(int indexInstance) {

		HCS hcs = null;

		try {
			hcs = new HCS(512, 16);
			hcs.setName(instances[indexInstance].getName());
			loadFileMachinesConfig("Machines.conf");
			readConfigurations(hcs);

			inputReader = new BufferedReader(new FileReader(
					instances[indexInstance]));

			for (int task = 0; task < 512; task++)
				for (int machine = 0; machine < 16; machine++)
					hcs.addExecTime(task, machine, toDouble(inputReader.readLine()));

			inputReader.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return hcs;
	}

	private HCS readInstanceReferencia(int indexInstance) {
		HCS hcs = null;
		try {
			inputReader = new BufferedReader(new FileReader(
					instances[indexInstance]));
			
			inputReader.readLine();
			String[] dataIn = inputReader.readLine().split("\\s+");
			hcs = new HCS(toInt(dataIn[0]), toInt(dataIn[1]));

			inputReader.readLine();
			for (int task = 0; task < hcs.getTotalTasks(); task++) {
				dataIn = inputReader.readLine().split("\\s+");
				for (int machine = 0; machine < hcs.getTotalMachines(); machine++) 
					hcs.addExecTime(task, machine, toDouble(dataIn[machine]));
			}
			
			inputReader.close();
			readConfigurations(hcs);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return hcs;
	}

	private static void readConfigurations(HCS hcs) {

		try {
			inputReader = new BufferedReader(new FileReader(machinesConfig));

			/** read comments line "Configuración (voltaje/velocidad)". **/
			inputReader.readLine();

			/** Read metadata: Max k Configurations and total machines. **/
			String dataV[];
			String[] dataIn = inputReader.readLine().split("\\s+");
			int maxKconfig = toInt(dataIn[0]);
			int totalMachines = toInt(dataIn[1]);

			/***
			 * Repite el número de configuración de voltaje/velocidad para el
			 * número de máquinas restantes.
			 */
			int baseMachine = 0;
			

			for (int k = 0; k < maxKconfig; k++) {
				dataIn = inputReader.readLine().split("\\s+");

				for (int machine = 0; machine < hcs.getTotalMachines(); machine++) {
					if (machine < totalMachines) {
						dataV = dataIn[machine].split("/");
						double voltaje = toDouble(dataV[0]);
						double speed = toDouble(dataV[1]);

						if (k == 0)
							hcs.addMachine(new Machine(machine, hcs
									.getTotalTasks(), maxKconfig));

						if (voltaje == Double.MAX_VALUE)
							hcs.getMachine(machine).setkConfig(k);
						else
							hcs.getMachine(machine).setKConfigVoltajeAndSpeed(
									k, voltaje, speed);
						baseMachine = 0;

					} else {

						if (k == 0)
							hcs.addMachine(new Machine(machine, hcs
									.getTotalTasks(), maxKconfig));

						if (!hcs.getMachine(baseMachine).lockedConfig())
							hcs.getMachine(machine).copyKConfig(k,
									hcs.getMachine(baseMachine));
						else
							hcs.getMachine(machine).setkConfig(
									hcs.getMachine(baseMachine).getkConfig());

						if (++baseMachine == totalMachines)
							baseMachine = 0;
					}
				}
			}
			inputReader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int toInt(String sn) {
		return Integer.parseInt(sn);
	}

	public static double toDouble(String sn) {
		if (sn.equals("inf"))
			return Double.MAX_VALUE;
		return Double.parseDouble(sn);
	}

}
