package cl.inacap.appcrpm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MenuInicioActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private CardView buttonConexion, buttonCaptura, buttonAjustes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_inicio);

        mAuth = FirebaseAuth.getInstance();

        buttonCaptura = findViewById(R.id.botonCaptura);

        buttonCaptura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuInicioActivity.this, CapturaActivity.class));
            }
        });

        buttonConexion = findViewById(R.id.botonConexion);

        buttonConexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuInicioActivity.this, ConexionActivity.class));
            }
        });

        buttonAjustes = findViewById(R.id.botonAjustes);

        buttonAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuInicioActivity.this, AjustesActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Usuario no autorizado, complete el formulario", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}