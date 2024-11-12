package cl.inacap.appcrpm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;

public class AdminBaseDatosActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private GridView gridViewVistaUsers;
    private UsuarioAdapter usuarioAdapter;

    private ImageButton imageButtonAddUser, imageButtonModUser, imageButtonSearchUser,
        imageButtonDelUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_base_datos);

        mAuth = FirebaseAuth.getInstance();
        mAuth.getCurrentUser();

        imageButtonAddUser = findViewById(R.id.addUsuario);
        imageButtonModUser = findViewById(R.id.modUsuario);
        imageButtonSearchUser = findViewById(R.id.searchUsuario);
        imageButtonDelUser = findViewById(R.id.deleteUsuario);

        gridViewVistaUsers = findViewById(R.id.vistaUsuarios);
        usuarioAdapter = new UsuarioAdapter(this);
        gridViewVistaUsers.setAdapter(usuarioAdapter);
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