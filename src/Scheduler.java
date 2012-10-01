import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Scheduler {
	public PCB runningProcess;

	private List<List<PCB>> readyList;
	private Map<String, RCB> resourceList;

	class PCB {
		// status types definitions
		public static final int RUNNING = 1;
		public static final int READY = 0;
		public static final int BLOCKED = -1;

		// PCB attributes
		private String PID;
		private List<RCB> otherResources;

		private int statusType;
		private List<PCB> statusList;

		private PCB creationTreeParent;
		private List<PCB> creationTreeChildren;

		private int priority;

		public PCB(String id, int pri, PCB parent) {
			PID = id;
			priority = pri;
			creationTreeParent = parent;

			otherResources = new LinkedList<RCB>();
			statusType = READY;
			creationTreeChildren = new LinkedList<PCB>();

			readyList.get(priority).add(this);
			statusList = readyList.get(priority);
		}

		public void createProcess(String id, int priority) {
			PCB newProcess = new PCB(id, priority, this);
			creationTreeChildren.add(newProcess);
		}

		public void deleteProcess(String pid) {
			PCB p;

			if (pid.equals(PID)) {
				p = this;
				runningProcess = null;
			} else
				p = findProcess(pid, creationTreeChildren);

			if (p != null) {
				p.killProcessTree(p);
			}
		}

		private void killProcessTree(PCB p) {
			// kill tree recursively
			for (PCB pc : p.creationTreeChildren)
				pc.killProcessTree(pc);

			// release all resources
			for (RCB r : p.otherResources)
				p.releaseResource(r.RID);

			// remove references
			p.statusList.remove(p);
			p.creationTreeParent.creationTreeChildren.remove(p);
		}

		private PCB findProcess(String id, List<PCB> tree) {
			PCB returnProcess = null;

			for (PCB p : tree) {
				if (p.PID.equals(id))
					return p;

				returnProcess = findProcess(id, p.creationTreeChildren);

				if (returnProcess != null)
					break;
			}
			return returnProcess;
		}

		public void requestResource(String RID) {
			RCB resource = resourceList.get(RID);

			// already allocated
			if (otherResources.contains(resource))
				return;

			if (resource.status == RCB.FREE) {
				resource.status = RCB.ALLOCATED;
				otherResources.add(resource);
			} else {
				statusType = BLOCKED;
				statusList.remove(this);
				resource.waitingList.add(this);
				statusList = resource.waitingList;
			}
		}

		public void releaseResource(String RID) {
			RCB resource = resourceList.get(RID);

			otherResources.remove(resource);
			
			if (resource.waitingList.isEmpty()) {
				resource.status = RCB.FREE;
			} else {
				PCB q = resource.waitingList.remove(0);
				
				q.statusType = READY;
				readyList.get(q.priority).add(q);
				q.statusList = readyList.get(q.priority);
				
				q.otherResources.add(resource);
			}
		}

		public void timeout() {
			readyList.get(priority).remove(this);
			statusType = READY;
			readyList.get(priority).add(this);
		}

		public String toString() {
			return PID;
		}
	}

	class RCB {
		// status type definitions
		public final static int FREE = 0;
		public final static int ALLOCATED = 1;

		// RCB attributes
		private String RID;
		private int status;
		private List<PCB> waitingList;

		public RCB(String id) {
			RID = id;
			status = FREE;
			waitingList = new LinkedList<PCB>();
		}

		public String toString() {
			return RID;
		}

	}

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
		new PCB("Init", 0, null);
	}

	public String run() {
		int i;

		// find highest priority process p
		PCB p = null;
		for (i = 2; i >= 0; i--) {
			if (!readyList.get(i).isEmpty()) {
				p = readyList.get(i).get(0);
				break;
			}
		}

		// if currently running process is null or
		// its priority < p's, preempt
		if (runningProcess == null) {
			runningProcess = p;
		} else if (runningProcess.priority < p.priority
				|| runningProcess.statusType != PCB.RUNNING) {
			preempt(p, runningProcess);
		}

		// return the name of the currently running process for shell
		return runningProcess.PID;
	}

	private void preempt(PCB p, PCB runningP) {
		if(runningP.statusType == PCB.RUNNING)
			runningP.statusType = PCB.READY;
		
		p.statusType = PCB.RUNNING;
		runningProcess = p;
	}

}