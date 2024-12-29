package es.udc.psi.caresafe.FallDetection;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class FallDetectionService extends Service implements SensorEventListener {
    private final IBinder binder = new FallDetectionBinder();
    private final long timeSleep = 7000;
    private boolean isRunning = false;
    private volatile boolean fallCaptured = false;

    public class FallDetectionBinder extends Binder {
        public FallDetectionService getService(){
            return FallDetectionService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            Sensor gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

            if (accelerometer != null) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.e("FallDetection", "Acelerómetro no disponible.");
            }

            if (gravitySensor != null) {
                sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_NORMAL);
            } else {
                Log.e("FallDetection", "Sensor de gravedad no disponible.");
            }
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
                        Log.d("FallDetection", "Caída detectada.");
                        fallCaptured = false;
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

            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
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
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}