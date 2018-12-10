package cn.lanyj.cehelper;

public class CEProcess implements Comparable<CEProcess> {
	private int pid;
	private String name;
	
	public CEProcess() {
	}
	
	public CEProcess(int pid, String name) {
		super();
		this.pid = pid;
		this.name = name;
	}
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public int compareTo(CEProcess o) {
		if(o == null) {
			return -1;
		}
		return this.pid - o.pid;
	}
	
	@Override
	public String toString() {
		return "[pid = " + pid + ", name = \"" + name + "\"]";
	}
	
}
