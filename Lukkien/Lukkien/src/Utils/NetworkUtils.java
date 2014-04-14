package Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Utility class for Network Information.
 * 
 * @author Okan SAYILGAN
 */
public class NetworkUtils {
	
	/**
	 * Checks If the device is Connected to a Network
	 * 
	 * @param context		Activity Context
	 * @return				True if the Devices connected to a Network and have internet connection, False otherwise
	 */
	public static boolean isConnected(Context context) {
		
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		NetworkInfo ni = cm.getActiveNetworkInfo();
		
		return ((ni != null) && (ni.isAvailable()) && (ni.isConnected()));
	}
}
