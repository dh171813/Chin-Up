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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import at.fhstp.chinup.R;

public class StartActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private TextView textView;
    private TextView textView2;
    private PowerManager powerManager;
    private ToggleButton button;
    private SeekBar slider;


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

        slider = findViewById(R.id.slider);
        slider.setOnSeekBarChangeListener(this);
        slider.incrementProgressBy(10);

    }

    @Override
    protected void onStart() {
        super.onStart();
        button.setChecked(SensorService.started);
        slider.setEnabled(!button.isChecked());
    }

    @Override
    // es gibt kein TextView2 mehr in Start Activity!
    protected void onNewIntent(Intent intent) {
        if (intent.getBooleanExtra("SHOW_IT", false)){
            textView2.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onClick(View view) {
        if (button.isChecked()) {
            Toast.makeText(this, "Service started!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, SensorService.class);
            intent.putExtra("timespan",(long)slider.getProgress());
            startService(intent);
        } else {
            Toast.makeText(this, "Service stopped!", Toast.LENGTH_SHORT).show();
            stopService(new Intent(this, SensorService.class));
        }
        slider.setEnabled(!button.isChecked());
    }

    @Override
    public void onProgressChanged(SeekBar slider, int progress, boolean fromUser) {
        if (fromUser) {
            progress = Math.max(10, progress - progress % 10);
            slider.setProgress(progress);

            //HIER ÄNNDERN MIT MINUTEN
            int minuten = progress/60;
            int sekunden = progress%60;

            textView.setText(minuten + " Min " + sekunden + " Sek");
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
