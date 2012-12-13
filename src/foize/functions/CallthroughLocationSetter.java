package foize.functions;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.linphone.core.Log;

import foize.megamobile.R;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class CallthroughLocationSetter 
{
	public static String CALLTHROUGHLOCATIONSET = "CalthroughLocationSet";
	private Context mContext;
	private LocationManager mLocationManager;
	final private String TAG = "CallthroughLocationSetter";
	List<NameValuePair> mCountryNumberValues;
	private NameValuePair mCountryNameNumberPair = null;
	
	public  CallthroughLocationSetter (Context context, List<NameValuePair> countrynumberValues )
	{
		mContext = context;
		mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationNetworkListener);
		mCountryNumberValues = countrynumberValues;
	}
	
	LocationListener locationNetworkListener = new LocationListener() 
    { 
        public void onLocationChanged(Location location) { 
        	Geocoder geocode = new Geocoder(mContext);
        	try
        	{
        		Address address         = geocode.getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0);
        		String countryCode    	= address.getCountryCode();
        		mCountryNameNumberPair = getCallThroughNumber(countryCode);
        		AnounceUpdate();
        		Log.d( String.format(TAG, "Found networklocation lat:%f ,long:%f", location.getLatitude(), location.getLongitude()));
            	Log.d( String.format(TAG, "Countrycode: %s", countryCode));
        	}
        	catch(Exception e)
        	{
        		
        	}
        	mLocationManager.removeUpdates(this);
        } 
        public void onProviderDisabled(String provider) {} 
        public void onProviderEnabled(String provider) {} 
        public void onStatusChanged(String provider, int status, Bundle extras) {} 
    }; 
    
    public NameValuePair getFoundCountryNameValuePiar()
    {
    	return mCountryNameNumberPair;
    }
    
    private NameValuePair getCallThroughNumber(String countryCode)
    {
		boolean match = false;
		NameValuePair result = null;
		
		for (int i = 0; i< mCountryNumberValues.size() && match == false; i++ )
		{
			NameValuePair nv = mCountryNumberValues.get(i);
			if (nv.getName().equals(countryCode))
			{
				result = nv;
				match = true;
			}
		}
		return result;
    }
    
    private void AnounceUpdate()
    {
    	Intent update = new Intent(CallthroughLocationSetter.CALLTHROUGHLOCATIONSET);
    	mContext.sendBroadcast(update);
    }
    
    public static String getCountryNameFromCode(Context context, String CountryCode)
    {
    	String Location = "Unknown";
		boolean match = false;
		List<NameValuePair> countryplusnumberlist = new ArrayList<NameValuePair>();
		countryplusnumberlist.add(new BasicNameValuePair("FR", context.getString(R.string.callthrough_country_entry_value_france)));
		countryplusnumberlist.add(new BasicNameValuePair("DE", context.getString(R.string.callthrough_country_entry_value_germany)));
		countryplusnumberlist.add(new BasicNameValuePair("IR", context.getString(R.string.callthrough_country_entry_value_ireland)));
		countryplusnumberlist.add(new BasicNameValuePair("NL", context.getString(R.string.callthrough_country_entry_value_netherlands)));
		countryplusnumberlist.add(new BasicNameValuePair("NO", context.getString(R.string.callthrough_country_entry_value_norway)));
		countryplusnumberlist.add(new BasicNameValuePair("PL", context.getString(R.string.callthrough_country_entry_value_poland)));
		countryplusnumberlist.add(new BasicNameValuePair("ES", context.getString(R.string.callthrough_country_entry_value_spain)));
		countryplusnumberlist.add(new BasicNameValuePair("SE", context.getString(R.string.callthrough_country_entry_value_sweden)));
		countryplusnumberlist.add(new BasicNameValuePair("GB", context.getString(R.string.callthrough_country_entry_value_united_kingdom)));
		for (int i = 0; i < countryplusnumberlist.size() && match == false; i++)
		{
		   NameValuePair nv = countryplusnumberlist.get(i);
		   if (nv.getName().equals(CountryCode))
		   {
			   Location = nv.getValue();
			   match = true;
		   }
		}
		return Location;
    }
}
