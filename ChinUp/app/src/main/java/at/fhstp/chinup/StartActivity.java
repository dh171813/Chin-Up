package at.fhstp.chinup;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import at.fhstp.chinup.R;

public class StartActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener {

    // private Vibrator vibrator;
    private SensorManager mSensorManager;
    private TextView textView;
    private TextView textView2;
    private PowerManager powerManager;
    private ToggleButton button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // wenn App aufgerufen wird
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
// Button "holen"
        button = findViewById(R.id.button);
        button.setOnClickListener(this);


        //Startet Messung von Gravitationssensor
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor lagesensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mSensorManager.registerListener(this, lagesensor, SensorManager.SENSOR_DELAY_NORMAL);

        //Schaut, ob Bildschirm eingeschalten ist
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);


        // vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

    }

    @Override
    protected void onStart() {
        super.onStart();
        button.setChecked(SensorService.started);
    }

    @Override
    // es gibt kein TextView2 mehr in Start Activity!
    protected void onNewIntent(Intent intent) {
        if (intent.getBooleanExtra("SHOW_IT", false)){
            textView2.setVisibility(View.VISIBLE);
        }
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
       /*
        if ((x < 0.2 && 0.4 < y && y < 0.8) || (y < 0.2 && 0.4 < Math.abs(x) && Math.abs(x) < 0.8)){
            if (start == -1) {
                Log.d("Info", "Info zur Lage");
                start = timestamp;
            }
            long timeTaken = (timestamp - start)/1000000;
            if (timeTaken > 5000){
                start = -1;
                // vibrator.vibrate(1000);
                // Create an explicit intent for an Activity in your app
                Intent intent = new Intent(this, StartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

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

                WindowManager.LayoutParams params = getWindow().getAttributes();
                params.screenBrightness = 0;
                getWindow().setAttributes(params);

                textView2.setVisibility(View.VISIBLE);
            }
        }else {
            start = -1;
        }*/
        textView.setText(x+"|"+y+"|"+z);
    }

    @Override
    //Genauigkeit ändert sich
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onClick(View view) {
        if (button.isChecked()) {
            Toast.makeText(this, "Service started!", Toast.LENGTH_SHORT).show();
            startService(new Intent(this, SensorService.class));
        } else {
            Toast.makeText(this, "Service stopped!", Toast.LENGTH_SHORT).show();
            stopService(new Intent(this, SensorService.class));
        }
    }
}
