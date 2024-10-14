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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private FirebaseFirestore db;

    private Button buttonRegistroCuenta;

    private EditText editTextRegistroEmail, editTextRegistroPassword;

    private String registroEmail, registroPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
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

                            // Crear nuevo usuario con email y contraseña
                            Map<String, Object> usuario = new HashMap<>();
                            usuario.put("correo", registroEmail);

                            // Añadir documento a una coleccion con ID
                            db.collection("usuarios")
                                    .add(usuario)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Log.d("REGISTER SUCCESS", "Documento añadido con ID: " + documentReference.getId());
                                            Toast.makeText(RegistroActivity.this, "Cuenta registrada con correo: " + registroEmail, Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d("REGISTER FAILED", "Error al añadir documento", e);
                                        }
                                    });
                            finish();
                        } else {
                            // Si el registro falla, muestra un mensaje
                            Log.w("CREACION FALLIDA", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegistroActivity.this, "El registro ha fallado, intentelo nuevamente", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}