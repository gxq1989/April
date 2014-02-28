package com.example.whereami;

import java.util.List;
import java.util.Locale;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

/**
 * @author April
 * 
 */
public class WhereAmIService extends Service implements LocationListener {

    private Context mContext;

    private LocationManager mLocationManager;
    private String mLocationProvider;

    // for test
    private long mStartTime;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;
        mStartTime = System.currentTimeMillis();

        Utils.log("enter onCreate");

        mLocationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        
        // TODO 
        mLocationProvider = LocationManager.NETWORK_PROVIDER;
        mLocationManager.requestLocationUpdates(mLocationProvider, 0, 0, this);

    }

    @Override
    public void onLocationChanged(Location location) {
        Utils.log("enter onLocationChanged");
        Utils.log("it takes about : " + (System.currentTimeMillis() - mStartTime) / 1000);
        Utils.log("latitude: " + location.getLatitude());
        Utils.log("longtitude: " + location.getLongitude());

        String address = getAddressFromLocation(location.getLatitude(), location.getLongitude());
        // get address successfully, send broadcast
        if (null != address && address.length() > 0) {
            Intent intent = new Intent();
            intent.setAction(Utils.ACTION_UPDATE_ADDRESS);
            intent.putExtra(Utils.KEY_ADDRESS, address);
            this.sendBroadcast(intent);
        }

        //remove LocationListener
        mLocationManager.removeUpdates(this);
        // stop  service after got the address
        this.stopSelf();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    private String getAddressFromLocation(double latitude, double longitude) {
        Utils.log("enter getAddressFromLocation");
        try {
            if (!Geocoder.isPresent()) {//api level 9
                return null;
            }
            Geocoder geocoder = new Geocoder(mContext, Locale.CHINA);
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                StringBuilder sb = new StringBuilder();
                /**
                 * getCountryName() // xxx country
                 * getAdminArea() // xxx province
                 * getLocality() // xxx city
                 * getSubLocality() // xxx area
                 * getThoroughfare() // xxx street
                 */
                // 北京 朝阳区 酒仙桥路
                sb.append(address.getLocality()).append(address.getSubLocality()).append(address.getThoroughfare());

                // too slow
                /*for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i));
                }*/

                Utils.log(sb.toString());
                return sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}