package es.udc.psi.caresafe.services.FallDetection;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import es.udc.psi.caresafe.services.serviceManager;

public class serviceFallDetecManager implements ServiceConnection, serviceManager {
    private FallDetectionService servicioFallDetec;
    private Context context;
    private boolean isRunning = false;

    public serviceFallDetecManager(Context context, Activity activity){
        this.context = context;
        checkPermission(context, activity);
    }

    @Override
    public Intent getServiceIntent() {
        return new Intent(context, FallDetectionService.class);
    }

    @Override
    public void stopService() {
        servicioFallDetec.stopService();
        isRunning = false;
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        FallDetectionService.FallDetectionBinder binder = (FallDetectionService.FallDetectionBinder) iBinder;
        servicioFallDetec = binder.getService();
        isRunning = true;
        servicioFallDetec.startService();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        isRunning = false;
    }

    private void checkPermission(Context context, Activity activity){
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.BODY_SENSORS) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(context, Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity, new String[]{
                    Manifest.permission.BODY_SENSORS,
                    Manifest.permission.WAKE_LOCK
            }, 1);
        }
    }
}
