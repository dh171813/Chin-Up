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
        link = findViewById(R.id.link);
        link.setMovementMethod(LinkMovementMethod.getInstance());
        buttonYes = findViewById(R.id.buttonYes);
        buttonYes.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        stopService(new Intent(this, SensorService.class));

    }

    @Override
    public void onClick(View view) {
        startService(new Intent(this, SensorService.class));
        finish();
    }
}
