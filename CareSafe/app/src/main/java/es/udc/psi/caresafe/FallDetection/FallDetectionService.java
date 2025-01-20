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

import es.udc.psi.caresafe.EmailNotifier;

public class FallDetectionService extends Service implements SensorEventListener {
    private final IBinder binder = new FallDetectionBinder();
    private final long timeSleep = 7000;
    private boolean isRunning = false;
    private volatile boolean fallCaptured = false;
    private float gravityValues[] = null;

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
                Log.d("FallDetection", "Acelerómetro disponible en este dispositivo.");
            } else {
                Log.e("FallDetection", "Acelerómetro no disponible en este dispositivo.");
            }

            if (gravitySensor != null) {
                sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_NORMAL);
                Log.d("FallDetection", "Sensor gravitatorio disponible en este dispositivo.");
            } else {
                Log.e("FallDetection", "Sensor gravitatorio no disponible en este dispositivo.");
            }
        }
    }

    public void startService(EmailNotifier emailNotifier){
        isRunning = true;
        new Thread(() -> {
            int count = 0;
            while (isRunning){
                try{
                    Thread.sleep(this.timeSleep);
                    Log.d("COUNT", count++ + "");
                    if(fallCaptured){
                        Log.d("FallDetection", "Caída detectada.");
                        emailNotifier.sendEmailFallAlert();
                        fallCaptured = false;
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
            if (sensorEvent.sensor.getType() == Sensor.TYPE_GRAVITY) {
                gravityValues = sensorEvent.values;
            }

            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];

                if (gravityValues != null){
                    //Normalización de los valores de gravedad
                    float gravityNorm = (float) Math.sqrt(Math.pow(gravityValues[0], 2) +
                            Math.pow(gravityValues[1], 2) + Math.pow(gravityValues[2], 2));

                    float gravityX = gravityValues[0] / gravityNorm;
                    float gravityY = gravityValues[1] / gravityNorm;
                    float gravityZ = gravityValues[2] / gravityNorm;

                    //Proyección de la aceleración en la dirección de la gravedad
                    float accelerationInGravityDir = x * gravityX + y * gravityY + z * gravityZ;

                    //Aceleración perpendicular a la gravedad (movimientos laterales)
                    float perpendicularAcceleration = (float) Math.sqrt(Math.pow(x - accelerationInGravityDir * gravityX, 2) +
                            Math.pow(y - accelerationInGravityDir * gravityY, 2) + Math.pow(z - accelerationInGravityDir * gravityZ, 2));

                    // Detectar caída (umbral ajustable)
                    if (accelerationInGravityDir > 14.0 && perpendicularAcceleration < 1.0) {
                        fallCaptured = true;
                        Log.d("FallDetection", "Posible caída detectada: " + accelerationInGravityDir + ", " + perpendicularAcceleration );
                    }
                }
            }
        }
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
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}