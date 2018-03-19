package at.fhstp.chinup;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

public class SensorService extends Service implements SensorEventListener{
    public static boolean started = false;
    private SensorManager mSensorManager;
    private Vibrator vibrator;

    public SensorService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor lagesensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mSensorManager.registerListener(this, lagesensor, SensorManager.SENSOR_DELAY_NORMAL);
        started = true;
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        mSensorManager.unregisterListener(this);
        started = false;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    // start zurzeit noch nicht gestartet, deshalb -1
    private long start = -1;

    // Methoden, der Activity, die vom Sensor aufgerufen werden, wenn sich Sensor-Daten ändern.
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        long timestamp = sensorEvent.timestamp;
        float x = (int)(sensorEvent.values[0]*100/9.81)/100f;
        float y = (int)(sensorEvent.values[1]*100/9.81)/100f;
        float z = (int)(sensorEvent.values[2]*100/9.81)/100f;
        if ((x < 0.2 && 0.4 < y && y < 0.8) || (y < 0.2 && 0.4 < Math.abs(x) && Math.abs(x) < 0.8)){
            if (start == -1) {
                Log.d("Info", "Info zur Lage");
                start = timestamp;
            }
            long timeTaken = (timestamp - start)/1000000;
            if (timeTaken > 5000){
                start = -1;
                vibrator.vibrate(1000);
                // Create an explicit intent for an Activity in your app
                Intent intent = new Intent(this, ExerciseActivity.class);
                intent.putExtra("SHOW_IT", true);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) (System.currentTimeMillis() & 0xfffffff), intent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "myId")
                        .setSmallIcon(android.R.drawable.ic_dialog_alert)
                        .setColor(Color.BLUE)
                        .setLights(Color.CYAN, 500, 500)
                        .setContentTitle("Achtung!")
                        .setContentText("Zu lange aufs Handy geschaut :(")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        // Set the intent that will fire when the user taps the notification
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

// notificationId is a unique int for each notification that you must define
                notificationManager.notify(1, mBuilder.build());

            }
        }else {
            start = -1;
        }
        Log.d("Lagesensoren", x+"|"+y+"|"+z);
    }

    @Override
    //Genauigkeit ändert sich
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
