/*
CallButton.java
Copyright (C) 2010  Belledonne Communications, Grenoble, France

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.linphone.ui;

import org.linphone.LinphoneManager;
import org.linphone.LinphonePreferenceManager;
import foize.megamobile.R;
import foize.megamobile.R.string;
import org.linphone.core.LinphoneCoreException;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.InputFilter.LengthFilter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * @author Guillaume Beraudo
 *
 */
public class CallButton extends ImageButton implements OnClickListener, AddressAware {

	private AddressText mAddress;
	public void setAddressWidget(AddressText a) {mAddress = a;}

	private OnClickListener externalClickListener;
	public void setExternalClickListener(OnClickListener e) {externalClickListener = e;}

	public CallButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnClickListener(this);
	}

	public void onClick(View v) {
		try {
			if (!LinphoneManager.getInstance().acceptCallIfIncomingPending()) {
				if (mAddress.getText().length() >0) { 
					if (LinphoneManager.getInstance().getCallMode() == LinphoneManager.SIPCALLMODE)
					{
						LinphoneManager.getInstance().newOutgoingCall(mAddress);
					}
					else
					{
						 try {
							 	SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
							 	String AddressText = mAddress.getText().toString();
							 	
							 	// Get callthrough number, username and password from settings
							 	String Callthroughnumber = pref.getString(getResources().getString(R.string.pref_callthrough_phonenumber_key), getResources().getString(R.string.pref_callthrough_phonenumber_default));
							 	String Password 		 = pref.getString(getResources().getString(R.string.pref_passwd_key), "unknown");
							 	String Account  		 = pref.getString(getResources().getString(R.string.pref_username_key), "unknown");
							 	
							 	if (Account.startsWith("SD"))
							 		Account = Account.substring(2);
							 	
							 	// Clean called number
							 	AddressText = cleanAddress(AddressText);
						        
							 	// Start a call intent and let android OS handle the call
							 	Intent callIntent = new Intent(Intent.ACTION_CALL);
							 	String callString = String.format("tel:%s,%s%s%s",Callthroughnumber,Account, Password, AddressText);
						        callIntent.setData(Uri.parse(callString));
						        getContext().startActivity(callIntent);
						        
						    } catch (ActivityNotFoundException activityException) {
						         Log.e("Error", "Call failed", activityException);
						    }
					}
				}
			}
		} catch (LinphoneCoreException e) {
			LinphoneManager.getInstance().terminateCall();
			onWrongDestinationAddress();
		};

		if (externalClickListener != null) externalClickListener.onClick(v);
	}

	
	protected void onWrongDestinationAddress() {
		Toast toast = Toast.makeText(getContext()
				,String.format(getResources().getString(R.string.warning_wrong_destination_address),mAddress.getText().toString())
				,Toast.LENGTH_LONG);
		toast.show();
	}
	
	private String cleanAddress(String input)
	{
		String output = input;
		
		if (output.startsWith("+"))
		{
			output = output.substring(1, output.length()-1);
		}
		// Remove leading zero´s if present
		if (output.startsWith("00"))
		{
			output = output.substring(2, output.length()-2);
		}
		
		if (output.startsWith("06"))
		{
			String prefix = getCountryPrefix();
		
			output = prefix + output.substring(1);
		}
		
		// Remove any non digit characters
		output = output.replaceAll("[^\\d/g]", "");

		return output;
	}
	
	private String getCountryPrefix()
	{
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
		String selectedCountry = pref.getString(getResources().getString(string.pref_callthrough_country_key), getResources().getString(string.callthrough_country_entry_value_netherlands));
		if (selectedCountry.equals(getResources().getString(R.string.callthrough_country_entry_value_france)))
		{
			return getResources().getString(R.string.prefix_france);
		}
		else if (selectedCountry.equals(getResources().getString(R.string.callthrough_country_entry_value_germany)))
		{
			return getResources().getString(R.string.prefix_germany);
		}
		else if (selectedCountry.equals(getResources().getString(R.string.callthrough_country_entry_value_ireland)))
		{
			return getResources().getString(R.string.prefix_ireland);
		}
		else if (selectedCountry.equals(getResources().getString(R.string.callthrough_country_entry_value_netherlands)))
		{
			return getResources().getString(R.string.prefix_netherlands);
		}
		else if (selectedCountry.equals(getResources().getString(R.string.callthrough_country_entry_value_norway)))
		{
			return getResources().getString(R.string.prefix_norway);
		}
		else if (selectedCountry.equals(getResources().getString(R.string.callthrough_country_entry_value_poland)))
		{
			return getResources().getString(R.string.prefix_poland);
		}
		else if (selectedCountry.equals(getResources().getString(R.string.callthrough_country_entry_value_spain)))
		{
			return getResources().getString(R.string.prefix_spain);
		}
		else if (selectedCountry.equals(getResources().getString(R.string.callthrough_country_entry_value_sweden)))
		{
			return getResources().getString(R.string.prefix_sweden);
		}
		else if (selectedCountry.equals(getResources().getString(R.string.callthrough_country_entry_value_united_kingdom)))
		{
			return getResources().getString(R.string.prefix_united_kingdom);
		}
		else
		{
			return "";
		}
	}
	


}
