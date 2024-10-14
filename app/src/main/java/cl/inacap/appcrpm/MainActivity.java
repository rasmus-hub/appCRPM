package cl.inacap.appcrpm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

public class MainActivity extends AppCompatActivity {

    private Button buttonInicioSesion, buttonRegistro;

    private EditText editTextLoginEmail, editTextLoginPassword;

    private String loginEmail, loginPassword;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        buttonInicioSesion = findViewById(R.id.btn_login);
        buttonRegistro = findViewById(R.id.btn_registro);
        editTextLoginEmail = findViewById(R.id.loginEmail);
        editTextLoginPassword = findViewById(R.id.loginPassword);

        buttonInicioSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginEmail = editTextLoginEmail.getText().toString();
                loginPassword = editTextLoginPassword.getText().toString();

                if (TextUtils.isEmpty(loginEmail)) {
                    Toast.makeText(MainActivity.this, "Por favor, ingrese un correo", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(loginPassword)) {
                    Toast.makeText(MainActivity.this, "Por favor, ingrese una contraseña", Toast.LENGTH_SHORT).show();
                    return;
                }

                signIn(loginEmail, loginPassword);
            }
        });

        buttonRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RegistroActivity.class));
            }
        });
    }

    private void signIn(String loginEmail, String loginPassword) {
        mAuth.signInWithEmailAndPassword(loginEmail, loginPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("LOGIN", "signInWithEmail:success");
                            startActivity(new Intent(MainActivity.this, MenuInicioActivity.class));
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("LOGIN", "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Autenticacion fallida",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}