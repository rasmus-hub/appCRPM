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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private Button buttonInicioSesion, buttonRegistro;

    private EditText editTextLoginEmail, editTextLoginPassword;

    private String loginEmail, loginPassword;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

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

                            FirebaseUser user = mAuth.getCurrentUser();

                            userVerify(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("LOGIN", "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Autenticacion fallida",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void userVerify(FirebaseUser user) {
        String userId = user.getUid();

        db.collection("usuarios").document(userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String tipo = documentSnapshot.getString("tipo");

                            if (tipo.equals("admin")) {
                                startActivity(new Intent(MainActivity.this, MenuInicioAdminActivity.class));
                            } else if (tipo.equals("cliente")) {
                                startActivity(new Intent(MainActivity.this, MenuInicioActivity.class));
                            } else {
                                Toast.makeText(MainActivity.this, "Tipo de usuario no reconocido.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.w("DOCUMENT", "El documento del usuario no existe.");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("DOCUMENT", "Error al obtener documento del usuario.");
                    }
                });
    }
}