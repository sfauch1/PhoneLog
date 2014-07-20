package faucher.stephane.phonelog;

public class PhoneLog {

	private int id;
	private String phoneNumber;
	private long startTime;
	private long endTime;
	private int phoneState;

	public PhoneLog(int id, String phoneNumber, long startTime, long endTime,int phoneState) {
		super();
		this.id = id;
		this.phoneNumber = phoneNumber;
		this.startTime = startTime;
		this.endTime = endTime;
		this.phoneState = phoneState;
	}

	public int getId() {
		return id;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	
	public int getPhoneState() {
		return phoneState;
	}

	public void setPhoneState(int phoneState) {
		this.phoneState = phoneState;
	}

	public static PhoneLog get(int position) {
		// TODO Auto-generated method stub
		return null;
	}

}
