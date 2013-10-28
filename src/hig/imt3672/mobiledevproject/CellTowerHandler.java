package hig.imt3672.mobiledevproject;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.SignalStrength;
import android.telephony.gsm.GsmCellLocation;

public class CellTowerHandler extends Service {
	GsmCellLocation gsmCellLocation;
	SignalStrength gsmCellStrength;
	int cellID;
	int cellStrength;
	int cellNoise;

	@Override
	public void onCreate() {
		super.onCreate();
		setTowerInfo();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		setTowerInfo();
		return Service.START_STICKY;
	}

	private void setTowerInfo() {
		cellID = gsmCellLocation.getCid();
		cellStrength = gsmCellStrength.getGsmSignalStrength();
		cellNoise = gsmCellStrength.getEvdoSnr();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public int[] getTowerInfo() {
		// Sets the tower info before returning to make sure they are updated
		setTowerInfo();
		int[] towerInformation = { cellID, cellStrength, cellNoise };
		return towerInformation;
	}
}
