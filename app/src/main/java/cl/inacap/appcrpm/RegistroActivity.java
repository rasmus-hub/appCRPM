package cl.inacap.appcrpm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegistroActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private Button buttonRegistroCuenta;

    private EditText editTextRegistroEmail, editTextRegistroPassword;

    private String registroEmail, registroPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        mAuth = FirebaseAuth.getInstance();
        editTextRegistroEmail = findViewById(R.id.registro_email);
        editTextRegistroPassword = findViewById(R.id.registro_password);
        buttonRegistroCuenta = findViewById(R.id.btn_registroCuenta);

        buttonRegistroCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registroEmail = String.valueOf(editTextRegistroEmail.getText().toString());
                registroPassword = String.valueOf(editTextRegistroPassword.getText().toString());

                if (TextUtils.isEmpty(registroEmail)) {
                    Toast.makeText(RegistroActivity.this, "Por favor, ingrese un correo", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(registroPassword)) {
                    Toast.makeText(RegistroActivity.this, "Por favor, ingrese una contraseña", Toast.LENGTH_SHORT).show();
                    return;
                }

                createAccount(registroEmail, registroPassword);
            }
        });
    }

    private void createAccount(String registroEmail, String registroPassword) {
        mAuth.createUserWithEmailAndPassword(registroEmail, registroPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Si el registro es correcto, se muestra la UI con la información del usuario
                            Log.d("CREACION CORREO", "createUserWithEmail:success");
                            Toast.makeText(RegistroActivity.this, "Cuenta registrada con correo: " + registroEmail, Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            // Si el registro falla, muestra un mensaje
                            Log.w("CREACION FALLIDA", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegistroActivity.this, "El registro ha fallado, intentelo nuevamente", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Toast.makeText(this, "Usuario no autorizado, complete el formulario", Toast.LENGTH_SHORT).show();
        }
    }
}