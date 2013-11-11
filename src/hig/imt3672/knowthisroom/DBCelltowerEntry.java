package hig.imt3672.knowthisroom;

public class DBCelltowerEntry {
	private long towerID;
	private long strength;
	private long roomID;

	public long getId() {
		return towerID;
	}

	public void setId(long id) {
		this.roomID = id;
	}

	public long getStr() {
		return strength;
	}

	public void setStr(long str) {
		this.strength = str;
	}

	public long getRoom() {
		return roomID;
	}

	public void setRoom(long roomId) {
		this.roomID = roomId;
	}
}
