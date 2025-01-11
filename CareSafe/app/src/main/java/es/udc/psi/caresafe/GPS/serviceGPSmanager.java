package es.udc.psi.caresafe.GPS;

import static android.provider.Settings.System.getString;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import es.udc.psi.caresafe.R;

public class serviceGPSmanager implements ServiceConnection {
    private GPSService servicioGPS;
    private boolean isRunningGPS = false;
    private SharedPreferences sharedPreferences;
    private Context context;
    private coords coordPreferencies = null;

    public serviceGPSmanager(Context context, Activity activity) {
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        checkPermissions(context, activity);
    }

    public void stopService(){
        servicioGPS.stopTracking();
    }
    public Intent getServicioGPSIntent(){
        return new Intent(context, GPSService.class);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        GPSService.GPSBinder binder = (GPSService.GPSBinder) iBinder;
        servicioGPS = binder.getService();
        isRunningGPS = true;

        updateSettings();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        isRunningGPS = false;
    }

    private void updateSettings() {
        int time, radius;
        String inter = ", ";
        String countryPreferencies = sharedPreferences.getString(context.getString(R.string.keyPreferenciesCountry), null);
        String statePreferencies = sharedPreferences.getString(context.getString(R.string.keyPreferenciesState), null);
        String cityPreferencies = sharedPreferences.getString(context.getString(R.string.keyPreferenciesCity), null);
        String roadPreferencies = sharedPreferences.getString(context.getString(R.string.keyPreferenciesRoad), null);
        String timePreferencies = sharedPreferences.getString(context.getString(R.string.keyPreferenciesTime), null);
        String radiusPreferencies = sharedPreferences.getString(context.getString(R.string.keyPreferenciesRadius), null);
        StringBuilder locationBuilder = new StringBuilder();

        if (countryPreferencies != null) {
            locationBuilder.append(countryPreferencies);
        }
        if (statePreferencies != null) {
            if (locationBuilder.length() > 0) locationBuilder.append(inter);
            locationBuilder.append(statePreferencies);
        }
        if (cityPreferencies != null) {
            if (locationBuilder.length() > 0) locationBuilder.append(inter);
            locationBuilder.append(cityPreferencies);
        }
        if (roadPreferencies != null) {
            if (locationBuilder.length() > 0) locationBuilder.append(inter);
            locationBuilder.append(roadPreferencies);
        }

        String locationPreferencies = locationBuilder.toString();

        try {
            if(timePreferencies != null && radiusPreferencies != null){
                time = Integer.parseInt(timePreferencies);
                radius = Integer.parseInt(radiusPreferencies);
                coordPreferencies = getCoordsFromString(locationPreferencies);
                if (coordPreferencies != null){
                    if(isRunningGPS){
                        servicioGPS.stopTracking();
                    }
                    servicioGPS.startService(coordPreferencies, radius, time, context);
                }
            } else {
                Toast.makeText(context,context.getString(R.string.notConfiguredGPS),Toast.LENGTH_SHORT);
            }
        } catch (NumberFormatException e) {
            Toast.makeText(context,context.getString(R.string.errorConfiguredGPS),Toast.LENGTH_SHORT);
        }
    }

    private coords getCoordsFromString(String direction){
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            // Obtiene una lista de direcciones que coincidan con la dirección proporcionada
            List<Address> addresses = geocoder.getFromLocationName(direction, 1);

            if (addresses != null && !addresses.isEmpty()) {
                // Obtener la primera dirección de la lista
                Address address = addresses.get(0);
                double latitude = address.getLatitude();
                double longitude = address.getLongitude();

                return new coords(latitude, longitude);
            } else {
                Toast.makeText(context, context.getString(R.string.notConfiguredGPS), Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, context.getString(R.string.errorConfiguredGPS), Toast.LENGTH_LONG).show();
        }
        return null;
    }

    private void checkPermissions(Context context, Activity activity){
        // Verifica y solicita permisos si no se tienen
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, 1);
        }
    }
}
