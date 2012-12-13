package foize.functions;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.linphone.core.Log;

import foize.megamobile.R;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class CallthroughLocationDetector 
{
	public static String CALLTHROUGHLOCATIONSET = "CalthroughLocationSet";
	private Context mContext;
	private LocationManager mLocationManager;
	final private String TAG = "CallthroughLocationSetter";
	private String   	    mDetectedCountry;
	List<NameValuePair>   	mCountryCode_Phonenumer     = new ArrayList<NameValuePair>();
	List<NameValuePair>  	mCountryCode_Country 		= new ArrayList<NameValuePair>();
	
	public  CallthroughLocationDetector (Context context)
	{
		mContext = context;
		// Set countrycode - country call trough number list 
		mCountryCode_Phonenumer = new ArrayList<NameValuePair>();
		mCountryCode_Phonenumer.add(new BasicNameValuePair("FR", context.getString(R.string.callthrough_number_france)));
		mCountryCode_Phonenumer.add(new BasicNameValuePair("DE", context.getString(R.string.callthrough_number_germany)));
		mCountryCode_Phonenumer.add(new BasicNameValuePair("IR", context.getString(R.string.callthrough_number_ireland)));
		mCountryCode_Phonenumer.add(new BasicNameValuePair("NL", context.getString(R.string.callthrough_number_netherlands)));
		mCountryCode_Phonenumer.add(new BasicNameValuePair("NO", context.getString(R.string.callthrough_number_norway)));
		mCountryCode_Phonenumer.add(new BasicNameValuePair("PL", context.getString(R.string.callthrough_number_poland)));
		mCountryCode_Phonenumer.add(new BasicNameValuePair("ES", context.getString(R.string.callthrough_number_spain)));
		mCountryCode_Phonenumer.add(new BasicNameValuePair("SE", context.getString(R.string.callthrough_number_sweden)));
		mCountryCode_Phonenumer.add(new BasicNameValuePair("GB", context.getString(R.string.callthrough_number_united_kingdom)));
		
		// Create a list of countrycodes and their corresponding countryname
		mCountryCode_Country.add(new BasicNameValuePair("FR", context.getString(R.string.callthrough_country_entry_value_france)));
		mCountryCode_Country.add(new BasicNameValuePair("DE", context.getString(R.string.callthrough_country_entry_value_germany)));
		mCountryCode_Country.add(new BasicNameValuePair("IR", context.getString(R.string.callthrough_country_entry_value_ireland)));
		mCountryCode_Country.add(new BasicNameValuePair("NL", context.getString(R.string.callthrough_country_entry_value_netherlands)));
		mCountryCode_Country.add(new BasicNameValuePair("NO", context.getString(R.string.callthrough_country_entry_value_norway)));
		mCountryCode_Country.add(new BasicNameValuePair("PL", context.getString(R.string.callthrough_country_entry_value_poland)));
		mCountryCode_Country.add(new BasicNameValuePair("ES", context.getString(R.string.callthrough_country_entry_value_spain)));
		mCountryCode_Country.add(new BasicNameValuePair("SE", context.getString(R.string.callthrough_country_entry_value_sweden)));
		mCountryCode_Country.add(new BasicNameValuePair("GB", context.getString(R.string.callthrough_country_entry_value_united_kingdom)));
		
		mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
	
		
	}
	
	public void startLocationDetection()
	{
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationNetworkListener);
	}
	
	LocationListener locationNetworkListener = new LocationListener() 
    { 
        public void onLocationChanged(Location location) { 
        	Geocoder geocode = new Geocoder(mContext);
        	try
        	{
        		Address address         = geocode.getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0);
        		String countryCode    	= address.getCountryCode();
        		NameValuePair countryNameNumberPair = getCallThroughNumber(countryCode);
				SharedPreferences prefs         = PreferenceManager.getDefaultSharedPreferences(mContext);
				SharedPreferences.Editor editor = prefs.edit();
				String CountryName = getCountryNameFromCode(countryNameNumberPair.getName());
				mDetectedCountry = CountryName;
				editor.putString(mContext.getString(R.string.pref_callthrough_country_key), CountryName);
				editor.putString(mContext.getString(R.string.pref_callthrough_phonenumber_key), countryNameNumberPair.getValue());
				editor.commit();
        		Log.d( String.format(TAG, "Found networklocation lat:%f ,long:%f", location.getLatitude(), location.getLongitude()));
            	Log.d( String.format(TAG, "Countrycode: %s", countryCode));
            	AnounceUpdate();
            	mLocationManager.removeUpdates(this);
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
    
    public String getDetectedCountryName()
    {
    	return mDetectedCountry;
    }
    private NameValuePair getCallThroughNumber(String countryCode)
    {
		boolean match = false;
		NameValuePair result = null;
		
		for (int i = 0; i< mCountryCode_Phonenumer.size() && match == false; i++ )
		{
			NameValuePair nv = mCountryCode_Phonenumer.get(i);
			if (nv.getName().equals(countryCode))
			{
				result = nv;
				match = true;
			}
		}
		return result;
    }
    
    
    private  String getCountryNameFromCode( String CountryCode)
    {
    	String Location = "Unknown";
		boolean match = false;
		
		for (int i = 0; i < mCountryCode_Country.size() && match == false; i++)
		{
		   NameValuePair nv = mCountryCode_Country.get(i);
		   if (nv.getName().equals(CountryCode))
		   {
			   Location = nv.getValue();
			   match = true;
		   }
		}
		return Location;
    }
    
    private void AnounceUpdate()
    {
    	Intent update = new Intent(CallthroughLocationSetter.CALLTHROUGHLOCATIONSET);
    	mContext.sendBroadcast(update);
    }
    
}
