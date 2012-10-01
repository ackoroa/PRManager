import java.util.LinkedList;
import java.util.List;

public class PCB {
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
	}
	
	public PCB createProcess(String id, int priority) {
		PCB newProcess = new PCB(id, priority, this);
		creationTreeChildren.add(newProcess);
		return newProcess;
	}

	public PCB deleteProcess(String pid) {
		PCB p;
		
		if (pid.equals(PID))
			p = this;
		else
			p = findProcess(pid, creationTreeChildren);
		
		if(p!=null) p.killProcessTree(p);
		
		return p;
	}
	
	private void killProcessTree(PCB p) {
		// kill tree recursively
		for(PCB pc : p.creationTreeChildren) killProcessTree(pc);
		
		//release all resources
		for(RCB r : p.otherResources) p.releaseResource(r); // <-- problem
		
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

	public void requestResource(RCB resource) {
		if (resource.getStatus() == RCB.FREE) {
			resource.setStatus(RCB.ALLOCATED);
			otherResources.add(resource);
		} else {
			statusType = BLOCKED;
			statusList.remove(this);
			statusList = resource.getWaitingList();
			resource.enqueueProcess(this);
		}
	}

	public PCB releaseResource(RCB resource) {
		otherResources.remove(resource);

		if (resource.getWaitingList().isEmpty()) {
			resource.setStatus(RCB.FREE);
		} else {
			PCB q = resource.dequeueProcess();
			q.statusType = READY;
			
			return q;
		}
		
		return null;
	}

	public String getPID() {
		return PID;
	}

	public List<RCB> getOtherResources() {
		return otherResources;
	}

	public int getStatusType() {
		return statusType;
	}

	public void setStatusType(int statusType) {
		this.statusType = statusType;
	}

	public List<PCB> getStatusList() {
		return statusList;
	}

	public void setStatusList(List<PCB> statusList) {
		this.statusList = statusList;
	}

	public PCB getCreationTreeParent() {
		return creationTreeParent;
	}

	public List<PCB> getCreationTreeChildren() {
		return creationTreeChildren;
	}

	public int getPriority() {
		return priority;
	}
	
	public String toString(){
		return PID;
	}
}
