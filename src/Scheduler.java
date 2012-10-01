import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Scheduler {
	public List<List<PCB>> readyList;
	public Map<String, RCB> resourceList;
	public PCB runningProcess;

	private PCB init;
	
	public Scheduler() {
		// initialize ready list
		readyList = new ArrayList<List<PCB>>();
		for (int i = 0; i < 3; i++) {
			readyList.add(new LinkedList<PCB>());
		}
		
		// initialize resource list
		resourceList = new HashMap<String, RCB>();
		resourceList.put("R1", new RCB("R1"));
		resourceList.put("R2", new RCB("R2"));
		resourceList.put("R3", new RCB("R3"));
		resourceList.put("R4", new RCB("R4"));
		
		// create and insert the Init process
		init = new PCB("Init", 0, null);
		init.setStatusList(readyList.get(0));
		readyList.get(0).add(init);
	}

	public String run() {
		int i;

		// find highest priority process p
		PCB p = null;

		for (i = 2; i >= 0; i--) {
			if (!readyList.get(i).isEmpty()){
				p = readyList.get(i).get(0);
				break;
			}
		}

		if (runningProcess == null) {
			runningProcess = p;
		} else if (runningProcess.getPriority() < p.getPriority()
				|| runningProcess.getStatusType() != PCB.RUNNING) {
			runningProcess = preempt(p, runningProcess);
		}

		return (runningProcess.getPID());
	}

	private PCB preempt(PCB p, PCB runningP) {
		if (runningP.getStatusType() == PCB.RUNNING) {
			runningP.setStatusType(PCB.READY);
		}
		runningP.setStatusList(readyList.get(runningP.getPriority()));
		
		p.setStatusType(PCB.RUNNING);

		return p;
	}
}
