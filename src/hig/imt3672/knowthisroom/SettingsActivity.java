package hig.imt3672.knowthisroom;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * SettingsActivity
 * <p>
 * This is simply an activity that starts a fragment that interacts with
 * the setting of the app. All it does it to start a fragmentmanager
 * and set the preferenceFragment to be visible.
 * @author PK
 *
 */
public class SettingsActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		
		SettingsFragment settingsFragment = new SettingsFragment();
		
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(android.R.id.content, settingsFragment);
		fragmentTransaction.commit();
		 
		super.onCreate(savedInstanceState);
	}
	
	/**
	 * SettingsFragment
	 * <p>
	 * A simple fragment that launches the settings. It demands to be public
	 * static. Joy to the world - this was easy :)
	 * @author PK
	 *
	 */
	public static class SettingsFragment extends PreferenceFragment {
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
			addPreferencesFromResource(R.xml.preferences);
		}
	}

}
