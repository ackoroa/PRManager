import java.util.Scanner;
import java.util.StringTokenizer;

public class PRManager {
	static Scheduler scheduler;

	public static void main(String[] args) {
		String currentlyRunningProcess;

		scheduler = new Scheduler();

		currentlyRunningProcess = scheduler.run();
		output(currentlyRunningProcess);

		Scanner fin = new Scanner(System.in);

		while (true) {
			processCommand(fin.nextLine());
			currentlyRunningProcess = scheduler.run();
			output(currentlyRunningProcess);
		}

	}

	private static void processCommand(String command) {
		StringTokenizer commandTokenizer = new StringTokenizer(command);
		String commandWord = commandTokenizer.nextToken();

		switch (commandWord) {
		case "init":
			resetScheduler();
			break;

		case "cr":
			// get the create parameters
			String newPID = commandTokenizer.nextToken();
			int newPri = Integer.parseInt(commandTokenizer.nextToken());
			// create
			createProcess(newPID, newPri);
			break;

		case "to":
			timeoutProcess();
			break;

		case "de":
			// get delete parameter
			String deletedPID = commandTokenizer.nextToken();
			// delete
			deleteProcess(deletedPID);
			break;

		case "req":
			// get request parameter
			String reqRID = commandTokenizer.nextToken();
			// get resource RCB
			RCB reqR = scheduler.resourceList.get(reqRID);
			// request
			scheduler.runningProcess.requestResource(reqR);
			break;

		case "rel":
			// get request parameter
			String relRID = commandTokenizer.nextToken();
			// get resource RCB
			RCB relR = scheduler.resourceList.get(relRID);
			// release
			PCB blockedP = scheduler.runningProcess.releaseResource(relR);
			if (blockedP != null)
				scheduler.readyList.get(blockedP.getPriority()).add(blockedP);
			break;

		case "quit":
			System.out.println("process terminated");
			System.exit(0);
		}
	}

	private static void resetScheduler() {
		scheduler = new Scheduler();
		System.out.println();
	}

	private static void deleteProcess(String deletedPID) {
		// delete the process tree and obtain the root
		PCB deletedP = scheduler.runningProcess.deleteProcess(deletedPID);

		// if the root is the currently running process
		// set running process to null
		if (deletedP != null && deletedP.equals(scheduler.runningProcess))
			scheduler.runningProcess = null;
	}

	private static void timeoutProcess() {
		// get the running process
		PCB toP = scheduler.runningProcess;

		// remove from ready list
		scheduler.readyList.get(toP.getPriority()).remove(toP);

		// change status from running to ready
		toP.setStatusType(PCB.READY);

		// re-insert to ready list
		scheduler.readyList.get(toP.getPriority()).add(toP);
	}

	private static void createProcess(String newPID, int newPri) {
		// create the process
		PCB p = scheduler.runningProcess.createProcess(newPID, newPri);

		// insert process to ready list
		scheduler.readyList.get(p.getPriority()).add(p);

		// set process' status list to ready list
		p.setStatusList(scheduler.readyList.get(p.getPriority()));
	}

	private static void output(String s) {
		System.out.println(s + " is running");
	}

}
