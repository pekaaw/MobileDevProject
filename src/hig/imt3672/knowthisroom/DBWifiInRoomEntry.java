package hig.imt3672.knowthisroom;

public class DBWifiInRoomEntry {
	private String bsID;
	private long roomID;
	private long connectionStrength;

	DBWifiInRoomEntry() {
	}

	DBWifiInRoomEntry(String pbsID, long proomID, long pconnectionStrength) {
		bsID = pbsID;
		roomID = proomID;
		connectionStrength = pconnectionStrength;
	}

	public String getId() {
		return bsID;
	}

	public void setId(String id) {
		this.bsID = id;
	}

	public long getStr() {
		return connectionStrength;
	}

	public void setStr(long str) {
		this.connectionStrength = str;
	}

	public long getRoom() {
		return roomID;
	}

	public void setRoom(long roomId) {
		this.roomID = roomId;
	}

}
