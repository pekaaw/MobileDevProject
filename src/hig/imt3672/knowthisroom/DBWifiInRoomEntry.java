package hig.imt3672.knowthisroom;

public class DBWifiInRoomEntry {
	private String bsID;
	private long roomID;
	private long min;
	private long max;

	DBWifiInRoomEntry() {
	}

	DBWifiInRoomEntry(String pbsID, long proomID, long pconnectionStrength) {
		bsID = pbsID;
		roomID = proomID;
		min = pconnectionStrength;
		max = pconnectionStrength;
	}

	public String getId() {
		return bsID;
	}

	public long getMin() {
		return min;
	}

	public long getMax() {
		return max;
	}

	public void setId(String id) {
		this.bsID = id;
	}

	public void setMax(long str) {
		this.max = str;
	}

	public void setMin(long str) {
		this.min = str;
	}

	public long getRoom() {
		return roomID;
	}

	public void setRoom(long roomId) {
		this.roomID = roomId;
	}

}
