package cl.inacap.appcrpm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class CapturaActivity extends AppCompatActivity {

    private Button buttonStartRecording;

    private ImageButton imageButtonMovimientos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captura);

        buttonStartRecording = findViewById(R.id.btn_start_recording);
        imageButtonMovimientos = findViewById(R.id.btn_movimientos);

        buttonStartRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CapturaActivity.this, CapturaCamaraActivity.class));
            }
        });

        imageButtonMovimientos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CapturaActivity.this, PruebaMovimientos.class));
            }
        });
    }
}