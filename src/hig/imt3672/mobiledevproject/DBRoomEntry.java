package hig.imt3672.mobiledevproject;

public class DBRoomEntry {
	private long roomID;
	private String roomName;

	public long getId() {
		return roomID;
	}

	public void setId(long id) {
		this.roomID = id;
	}

	public String getName() {
		return roomName;
	}

	public void setName(String newName) {
		this.roomName = newName;
	}

	// Will be used by the ArrayAdapter in the ListView
	@Override
	public String toString() {
		return roomName;
	}
}
