package cl.inacap.appcrpm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MenuInicioAdminActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private CardView buttonAdministracion, buttonConexion, buttonCaptura, buttonAjustes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_inicio_admin);

        mAuth = FirebaseAuth.getInstance();

        buttonAdministracion = findViewById(R.id.botonAdminUsuarios);

        buttonAdministracion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuInicioAdminActivity.this, AdminBaseDatosActivity.class));
            }
        });

        buttonCaptura = findViewById(R.id.botonCaptura);

        buttonCaptura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuInicioAdminActivity.this, CapturaActivity.class));
            }
        });

        buttonConexion = findViewById(R.id.botonConexion);

        buttonConexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuInicioAdminActivity.this, ConexionActivity.class));
            }
        });

        buttonAjustes = findViewById(R.id.botonAjustes);

        buttonAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuInicioAdminActivity.this, AjustesActivity.class));
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