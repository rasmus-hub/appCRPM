package cl.inacap.appcrpm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class AjustesActivity extends AppCompatActivity {

    private ImageButton buttonCalibracion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);

        buttonCalibracion = findViewById(R.id.botonCalibracion);

        buttonCalibracion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AjustesActivity.this, CalibracionActivity.class));
            }
        });
    }
}