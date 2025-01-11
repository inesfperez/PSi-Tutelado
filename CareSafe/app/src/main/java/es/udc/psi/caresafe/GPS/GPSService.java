package es.udc.psi.caresafe.GPS;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.concurrent.Executor;

import es.udc.psi.caresafe.Constantes;

public class GPSService extends Service {
    private final IBinder binder = new GPSBinder();
    public class GPSBinder extends Binder{
        public GPSService getService(){
            return GPSService.this;
        }
    }
    private FusedLocationProviderClient fusedLocationProviderClient;
    private double iLatitude, iLongitude;
    private int radius;
    private long timeSleep;
    private boolean isRunning;

    // Servicio que accederá la ubicación y comprobará si está dentro de los parámetros permitidos
    // Se le pasa el punto inicial del que no quiere que se salga el cuidado y el radio
    public void startService(coords coords, int radius, long timeSleep, Context context){
        this.iLatitude = coords.getAltitude();
        this.iLongitude = coords.getLongitude();
        this.radius = radius;
        // El valor dado está en segundos
        this.timeSleep = timeSleep * 1000;

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        isRunning = true;
        new Thread(() -> {
            while (isRunning) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    isRunning = false;
                    stopSelf();
                    return;
                }

                fusedLocationProviderClient.getLastLocation()
                        .addOnSuccessListener(location -> {
                            if (location != null) {
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                Log.d("hola", "A");
                                if (!checkLocationDistance(latitude, longitude)) {
                                    Log.d("LOCALIZACIÓN", "Paciente fuera de rango");
                                    /*isRunning = false;
                                    stopSelf();*/
                                }
                            } else {
                                isRunning = false;
                                stopSelf();
                            }
                        })
                        .addOnFailureListener(e -> {
                            isRunning = false;
                            stopSelf();
                        });
                try {
                    Thread.sleep(this.timeSleep);
                } catch (InterruptedException e) {
                    isRunning = false;
                    stopSelf();
                }
            }
        }).start();
    }


    public void stopTracking(){
        isRunning = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private boolean checkLocationDistance(double currentLatitude, double currentLongitude) {
        // Convertir las coordenadas de grados a radianes
        double lat1Rad = Math.toRadians(iLatitude);
        double lon1Rad = Math.toRadians(iLongitude);
        double lat2Rad = Math.toRadians(currentLatitude);
        double lon2Rad = Math.toRadians(currentLongitude);
        // Diferencia de las coordenadas
        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        // Fórmula del Haversine
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Distancia en metros
        double distance = Constantes.EARTH_RADIUS * c;

        // Verificar si la distancia está dentro del radio permitido
        return distance <= radius;
    }
}