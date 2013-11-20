package hig.imt3672.knowthisroom;

public class DBCelltowerEntry {
	private long towerID;
	private long roomID;
	private long min;
	private long max;

	public long getId() {
		return towerID;
	}

	public void setId(long id) {
		this.roomID = id;
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

	public long getRoom() {
		return roomID;
	}

	public void setRoom(long roomId) {
		this.roomID = roomId;
	}
}
