package cl.inacap.appcrpm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;

public class PruebaMovimientos extends AppCompatActivity {

    private CardView buttonIteracionRapida, buttonIteracionLenta, buttonCombinado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba_movimientos);

        buttonIteracionRapida = findViewById(R.id.botonIteracionRapida);
        buttonIteracionLenta = findViewById(R.id.botonIteracionLenta);
        buttonCombinado = findViewById(R.id.botonCombinado);

        buttonIteracionRapida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarComandoAlEsp32("A");
            }
        });

        buttonIteracionLenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarComandoAlEsp32("B");
            }
        });

        buttonCombinado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enviarComandoAlEsp32("C");
            }
        });
    }

    private void enviarComandoAlEsp32(String comando) {
        BluetoothSocket socket = ConexionActivity.getConnectedSocket();
        if (comando != null) {
            if (socket != null && socket.isConnected()) {
                try {
                    OutputStream outputStream = socket.getOutputStream();
                    outputStream.write(comando.getBytes());
                    outputStream.flush();
                    Toast.makeText(this, "Comando enviado: " + comando, Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error al enviar el comando", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "No hay conexión con el ESP32", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d("COMANDO", "Comando vacío");
        }
    }
}