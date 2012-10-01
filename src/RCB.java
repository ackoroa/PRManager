import java.util.LinkedList;
import java.util.List;


public class RCB {
	public final static int FREE = 0;
	public final static int ALLOCATED = 1;
	
	private String RID;
	private int status;
	private List<PCB> waitingList;

	public RCB(String id){
		RID = id;
		status = FREE;
		waitingList = new LinkedList<PCB>();
	}
	
	public PCB dequeueProcess(){
		 return waitingList.remove(0);
	}
	
	public void enqueueProcess(PCB p){
		waitingList.add(p);
	}
	
	public List<PCB> getWaitingList(){
		return waitingList;
	}

	public String getRID() {
		return RID;
	}

	public int getStatus() {
		return status;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
	public String toString(){
		return RID;
	}
}
