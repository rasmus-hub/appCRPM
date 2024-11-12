package cl.inacap.appcrpm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UsuarioAdapter extends BaseAdapter {

    private Context context;
    private List<Usuario> usuarios;

    public UsuarioAdapter(Context context) {
        this.context = context;
        this.usuarios = new ArrayList<>();

        // Obtener datos de Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usuariosRef = db.collection("usuarios");
        usuariosRef.addSnapshotListener((value, error) -> {
            if (error != null) {
                // Manejar errores
                return;
            }

            usuarios.clear();
            for (QueryDocumentSnapshot document : value) {
                Usuario usuario = document.toObject(Usuario.class);
                usuarios.add(usuario);
            }
            notifyDataSetChanged();
        });
    }

    @Override
    public int getCount() {
        return usuarios.size();
    }

    @Override
    public Usuario getItem(int position) {
        return usuarios.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_usuario, parent, false);
        }

        TextView nombreTextView = convertView.findViewById(R.id.nombreTextView);
        TextView correoTextView = convertView.findViewById(R.id.correoTextView);
        TextView tipoTextView = convertView.findViewById(R.id.tipoTextView);

        Usuario usuario = getItem(position);
        nombreTextView.setText(usuario.getNombre());
        correoTextView.setText(usuario.getCorreo());
        tipoTextView.setText(usuario.getTipo());

        return convertView;
    }
}
