package hig.imt3672.knowthisroom;

public class DBCelltowerEntry {
	private long towerID;
	private long roomID;
	private long min;
	private long max;

	public long getTowerId() {
		return towerID;
	}

	public void setTowerId(long id) {
		this.towerID = id;
	}

	public long getMax() {
		return max;
	}

	public long getMin() {
		return min;
	}

	public void setMin(long str) {
		this.min = str;
	}

	public void setMax(long str) {
		this.max = str;
	}

	public long getRoomId() {
		return roomID;
	}

	public void setRoomId(long roomId) {
		this.roomID = roomId;
	}
}
