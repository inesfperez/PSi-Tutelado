package es.udc.psi.caresafe.FallDetection;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class FallDetectionService extends Service implements SensorEventListener {
    private final IBinder binder = new FallDetectionBinder();
    private long timeSleep = 7000;
    private boolean isRunning = false, fallCaptured = false;
    public class FallDetectionBinder extends Binder {
        public FallDetectionService getService(){
            return FallDetectionService.this;
        }
    }

    public void startService(){
        isRunning = true;
        new Thread(() -> {
            int count = 0;
            while (isRunning){
                try{
                    Thread.sleep(this.timeSleep);
                    Log.d("COUNT", count++ + "");
                    if(fallCaptured){
                        // Caida
                    }
                }catch (InterruptedException e){
                    isRunning = false;
                    stopSelf();
                }
            }
        }).start();
    }

    public void stopService(){
        isRunning = false;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent != null) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            // Cálculo de la aceleración resultante
            double acceleration = Math.sqrt(x * x + y * y + z * z);

            // Detectar caída (umbral ajustable)
            if (acceleration > 15) {
                fallCaptured = true;
                Log.d("FallDetection", "Posible caída detectada: " + acceleration);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}