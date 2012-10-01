import java.util.Scanner;
import java.util.StringTokenizer;

public class PRManager {
	static Scheduler scheduler;

	public static void main(String[] args) {
		String currentlyRunningProcess;

		scheduler = new Scheduler();

		currentlyRunningProcess = scheduler.run();
		outputRunningProcess(currentlyRunningProcess);

		Scanner fin = new Scanner(System.in);

		while (true) {
			processCommand(fin.nextLine());
			currentlyRunningProcess = scheduler.run();
			outputRunningProcess(currentlyRunningProcess);
		}

	}

	private static void processCommand(String command) {
		StringTokenizer commandTokenizer = new StringTokenizer(command);
		String commandWord = commandTokenizer.nextToken();

		switch (commandWord) {
		case "init":
			scheduler = new Scheduler();
			System.out.println();
			break;

		case "cr":
			// get the create parameters
			String newPID = commandTokenizer.nextToken();
			int newPri = Integer.parseInt(commandTokenizer.nextToken());
			// create
			scheduler.runningProcess.createProcess(newPID, newPri);
			break;

		case "to":
			scheduler.runningProcess.timeout();
			break;

		case "de":
			// get delete parameter
			String deletedPID = commandTokenizer.nextToken();
			// delete
			scheduler.runningProcess.deleteProcess(deletedPID);
			break;

		case "req":
			// get request parameter
			String reqRID = commandTokenizer.nextToken();
			// request
			scheduler.runningProcess.requestResource(reqRID);
			break;

		case "rel":
			// get request parameter
			String relRID = commandTokenizer.nextToken();
			// release
			scheduler.runningProcess.releaseResource(relRID);
			break;

		case "quit":
			System.out.println("process terminated");
			System.exit(0);
		}
	}

	private static void outputRunningProcess(String s) {
		System.out.println(s + " is running");
	}

}
