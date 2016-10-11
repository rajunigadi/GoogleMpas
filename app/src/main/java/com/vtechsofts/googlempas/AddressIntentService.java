package com.vtechsofts.googlempas;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by rajunigadi on 10/11/16.
 */

public class AddressIntentService extends IntentService {
    private String TAG = "AddressIntentService";
    protected ResultReceiver mReceiver;

    public AddressIntentService() {
        super("AddressIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        String errorMessage = "";

        mReceiver = intent.getParcelableExtra(Constants.RECEIVER);
        double latitude = intent.getDoubleExtra(Constants.LATITUDE_DATA_EXTRA, 0);
        double longitude = intent.getDoubleExtra(Constants.LONGITUDE_DATA_EXTRA, 0);

        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException ioException) {
            errorMessage = "Service not available";
            Log.e(TAG, errorMessage, ioException);
        } catch (IllegalArgumentException illegalArgumentException) {
            errorMessage = "Invalid latitude longitude used";
            Log.e(TAG, errorMessage + ". " + "Latitude = " + latitude + ", Longitude = " + longitude, illegalArgumentException);
        }

        // Handle case where no address was found.
        if (addresses == null || addresses.size()  == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = "No address found";
                Log.e(TAG, errorMessage);
            }
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressList = new ArrayList<String>();

            // Fetch the address lines using getAddressLine, join them, and send them to the thread.
            for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressList.add(address.getAddressLine(i));
            }
            Log.e(TAG, "Address found");
            deliverResultToReceiver(Constants.SUCCESS_RESULT, TextUtils.join(System.getProperty("line.separator"), addressList));
        }
    }

    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        if(mReceiver != null) {
            mReceiver.send(resultCode, bundle);
        } else {
            Log.e(TAG, "Receiver not found");
        }
    }
}

