/**
 * 
 */
package hig.imt3672.knowthisroom;

import java.util.Observable;

import android.os.Bundle;
import android.telephony.CellLocation;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.gsm.GsmCellLocation;

/**
 * @author Nabby
 * 
 */
public class CellTowerData extends Observable {

	CellLocation mCellLocation;
	SignalStrength mSignalStrength;
	ServiceState mServiceState;

	public Bundle getCellTowerBundle() {
		if (isEmpty())
			return null;

		Bundle towerInfo = new Bundle();
		towerInfo.putInt("CellID", ((GsmCellLocation) mCellLocation).getCid());
		towerInfo
				.putInt("Strength", getSignalStrength().getGsmSignalStrength());
		towerInfo.putInt("cellNoise", getSignalStrength().getGsmBitErrorRate());
		return towerInfo;
	}

	public CellLocation getCellLocation() {
		return mCellLocation;
	}

	public void setCellLocation(CellLocation mCellLocation) {
		this.mCellLocation = mCellLocation;
		setChanged();
		notifyObservers(this);
	}

	public SignalStrength getSignalStrength() {
		return mSignalStrength;
	}

	public void setSignalStrength(SignalStrength mSignalStrength) {
		this.mSignalStrength = mSignalStrength;
		setChanged();
		notifyObservers(this);
	}

	public ServiceState getServiceState() {
		return mServiceState;
	}

	public void setServiceState(ServiceState mServiceState) {
		this.mServiceState = mServiceState;
		setChanged();
		notifyObservers(this);
	}

	public boolean isEmpty() {
		return mSignalStrength == null || mCellLocation == null;
	}
}
