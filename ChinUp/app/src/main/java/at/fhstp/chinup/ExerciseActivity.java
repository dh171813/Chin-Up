package at.fhstp.chinup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import at.fhstp.chinup.R;



public class ExerciseActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView link;
    private Button buttonYes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        // mit findViewById hole ich mir Komponenten, die im Layout definiert sind
        link = findViewById(R.id.link);
        // damit ich den Link klicken kann
        link.setMovementMethod(LinkMovementMethod.getInstance());
        buttonYes = findViewById(R.id.buttonYes);
        // wir können hier this verwenden, weil ExerciseActivity onCLickListener implementiert
        buttonYes.setOnClickListener(this);

    }

    @Override
    // wenn sich diese Activity startet, wird der Service gestoppt
    protected void onStart() {
        super.onStart();
        stopService(new Intent(this, SensorService.class));

    }

    @Override
    // beim Drücken des Buttons beendet sich die Activity und der Service startet wieder.
    public void onClick(View view) {
        startService(new Intent(this, SensorService.class));
        finish();
    }
}
