package ee.kirill.locationexample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;


public class LocationActivity extends Activity {

    public static final String TAG = "LocationActivity";
    public static final int MIN_TIME = 5000;
    public static final int MIN_DISTANCE = 1;
    List<Location> locations = new ArrayList<>();
    private EditText editTextShowLocation;
    private Button buttonGetLocation;
    private ProgressBar progress;
    private LocationManager locManager;
    private LocationListener locListener = new MyLocationListener();
    private boolean gps_enabled = false;
    private boolean network_enabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        editTextShowLocation = (EditText) findViewById(R.id.editText);
        progress = (ProgressBar) findViewById(R.id.progressBar);

        locManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    }

    private void startLocationRequests() {
        try {
            Log.v(TAG, "Trying use GPS_PROVIDER");
            gps_enabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            Log.v(TAG, "Trying use NETWORK_PROVIDER");
            network_enabled = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        // don't start listeners if no provider is enabled
        Log.v(TAG, "gps_enabled:" + gps_enabled);
        Log.v(TAG, "network_enabled:" + network_enabled);
        if (!gps_enabled && !network_enabled) {
            Log.e(TAG, "network and gps are DISABLED!!!");
        }

        if (network_enabled) {
            locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, locListener);
        } else if (gps_enabled) {
            locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, locListener);
        }
    }

    public void showLocationsOnMap(View view) {
        startActivity(new Intent(this, MapActivity.class));
    }

    public void recordLocation(View view) {
        Log.v(TAG, "Recording location...");
        locations.clear();
        startLocationRequests();
        progress.setVisibility(View.VISIBLE);
    }

    public void stopRecord(View view) {
        Log.v(TAG, "Stop recording");
        Log.v(TAG, "Number of locations:" + locations.size());
        locManager.removeUpdates(locListener);
        progress.setVisibility(View.GONE);
    }

    class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            Log.v(TAG, "LocationChanged:" + location);
            if (location != null) {
                locations.add(location);

                String londitude = "Longitude: " + location.getLongitude();
                String latitude = "Latitude: " + location.getLatitude();
                String altitiude = "Altitude: " + location.getAltitude();
                String accuracy = "Accuracy: " + location.getAccuracy();
                String time = "Time: " + location.getTime();

                editTextShowLocation.setText(londitude + "\n" + latitude + "\n" + altitiude + "\n" + accuracy + "\n" + time);
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.v(TAG, "onProviderDisabled:" + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.v(TAG, "onProviderEnabled:" + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.v(TAG, "onStatusChanged:" + provider + status);
        }
    }

}