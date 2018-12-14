// bei Java geben ich zu Beginn immer das package an (Pfad)
package at.fhstp.chinup;


// importiert benötigte Klassen (mit anderem Pfad wie package)

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

/* public = alle anderen Klasse innerhalb derselben App können auf diese Klasse zugreifen
   extends Service = der SensorService ist ein Service, der alle Methoden und Variablen von Service übernimmt (von Android vorprogrammiert)
   implements = ein interface (Deklaration von Methoden) wird implementiert mit wenigen Methoden
*/
public class SensorService extends Service implements SensorEventListener{

    // innerhalb der Klasse werden Variablen definiert
    // static = gehört zur Klasse/ nicht static = gehört zur Instanz
    public static boolean started = false;
    public static boolean wasStarted = false;

    // Variablen der Instanz (nicht static) und können nur innerhalb der Klasse verwendet werden (private)
    // SensorManager = Typ(Klasse) der Variable; mSensorManager = Name
    private NotificationManager mNotificationManager;
    private SensorManager mSensorManager;
    private Vibrator vibrator;
    private Ringtone ringtone;

    private long timespan;

    //Konstruktor (von Android ausgeführt)  - macht eine Instanz aus den oben genannten Variablen.
    public SensorService() {
    }


    // Override = in einer niedrigen Stufe der Klasse gibt es inStartCommand bereits
    @Override
    // wird ausgeführt, wenn der Service gestartet wird.
    public int onStartCommand(Intent intent, int flags, int startId) {
        timespan = intent.getLongExtra("timespan", 600) * 1000;
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        final Sensor lagesensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mSensorManager.registerListener(this, lagesensor, SensorManager.SENSOR_DELAY_NORMAL);

        // Notification-Klingelton einrichten
        ringtone = RingtoneManager.getRingtone(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)){
                    if (wasStarted){
                        started = true;
                        wasStarted = false;
                        values.clear();
                        mSensorManager.registerListener(SensorService.this, lagesensor, SensorManager.SENSOR_DELAY_NORMAL);

                    }
                }else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
                    if (started){
                        started = false;
                        wasStarted = true;
                        mSensorManager.unregisterListener(SensorService.this);
                    }
                }
            }
        }, filter);
        started = true;
        return Service.START_NOT_STICKY;
    }

    @Override
    // wird ausgeführt wenn der Service beendet wird.
    public void onDestroy() {
        mSensorManager.unregisterListener(this);
        started = false;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    // Liste, um Zeit und 1/0 zu speichern
    private List<Pair<Long, Boolean>> values = new ArrayList<>();

    // Prozent werden erst berechnet, wenn das Array Values "voll" ist.
    private boolean valuesFull = false;

    // Methoden, der Activity, die vom Sensor aufgerufen werden, wenn sich Sensor-Daten ändern.
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        long timestamp = System.currentTimeMillis();
        float x = (int) (sensorEvent.values[0] * 100 / 9.81) / 100f;
        float y = (int) (sensorEvent.values[1] * 100 / 9.81) / 100f;
        float z = (int) (sensorEvent.values[2] * 100 / 9.81) / 100f;
        if ((x < 0.2 && 0.4 < y && y < 0.8) || (y < 0.2 && 0.4 < Math.abs(x) && Math.abs(x) < 0.8)) {
            values.add(Pair.create(timestamp, true));
        } else {
            values.add(Pair.create(timestamp, false));
        }

        //For-Schleife, die aus dem Array die Werte löscht, die länger als eine bestimmte Zeit vergangen sind.
        for (int i = 0; i < values.size(); i++) {
            Pair<Long, Boolean> pair = values.get(i);
            long timeTaken = timestamp - pair.first;
            if (timeTaken > timespan) {
                valuesFull = true;
                values.remove(i);
                i--;
            } else {
                break;
            }
        }
        int trues = 0;
        for (Pair<Long, Boolean> pair : values){
            if (pair.second){
            trues ++;
            }
        }

        //Prozentsatz der "falschen" Handyposition
       float percentage = trues * 100f / values.size();

        if (valuesFull && percentage > 80) {
            vibrator.vibrate(1000);
            // Create an explicit intent for an Activity in your app
            Intent intent = new Intent(this, ExerciseActivity.class);
            intent.putExtra("SHOW_IT", true);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) (System.currentTimeMillis() & 0xfffffff), intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "ChinUp_0")
                    .setSmallIcon(android.R.drawable.ic_dialog_alert)
                    .setColor(Color.BLUE)
                    .setLights(Color.CYAN, 500, 500)
                    .setContentTitle("Achtung!")
                    .setContentText("Zu lange aufs Handy geschaut :(")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    // Set the intent that will fire when the user taps the notification
                    .setContentIntent(pendingIntent)
                    .setChannelId("ChinUp_0")
                    .setAutoCancel(true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mNotificationManager.createNotificationChannel(new NotificationChannel("ChinUp_0", "ChinUp", NotificationManager.IMPORTANCE_HIGH));
                mNotificationManager.notify(1, mBuilder.build());
            } else{
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

                // notificationId is a unique int for each notification that you must define
                notificationManager.notify(1, mBuilder.build());
            }

            ringtone.play();
            values.clear();
            valuesFull = false;
        }
    }


    @Override
    //Genauigkeit ändert sich
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
