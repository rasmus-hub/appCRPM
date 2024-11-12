package cl.inacap.appcrpm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ConexionActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_PERMISSIONS = 2;

    private BluetoothAdapter bluetoothAdapter;
    private static BluetoothSocket connectedSocket;
    public static BluetoothSocket getConnectedSocket() {
        return connectedSocket;
    }

    private boolean isReceiverRegistered = false;
    private List<BluetoothDevice> devicesList = new ArrayList<>();
    private List<String> devicesNamesList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private Button buttonCapturaDispositivo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conexion);

        // Verificar y solicitar permisos
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN},
                    REQUEST_PERMISSIONS);
        } else {
            // Permisos concedidos, proceder con la habilitación de Bluetooth
            initializeBluetooth();
        }

        // ListView para mostrar dispositivos
        ListView listView = findViewById(R.id.deviceList);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, devicesNamesList);
        listView.setAdapter(adapter);

        // Manejar la selección de dispositivos
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice selectedDevice = devicesList.get(position);
                connectToDevice(selectedDevice);
            }
        });

        // Iniciar búsqueda de dispositivos Bluetooth
        searchBluetoothDevices();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isReceiverRegistered) {
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(receiver, filter);
            isReceiverRegistered = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isReceiverRegistered) {
            unregisterReceiver(receiver);
            isReceiverRegistered = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Verificar si el receptor está registrado antes de desregistrarlo
        if (isReceiverRegistered) {
            unregisterReceiver(receiver);
            isReceiverRegistered = false;
        }
    }

    @SuppressLint("MissingPermission")
    private void initializeBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth no es soportado en este dispositivo", Toast.LENGTH_SHORT).show();
        } else if (!bluetoothAdapter.isEnabled()) {
            // Pedir al usuario que active Bluetooth
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permisos concedidos, proceder con la habilitación de Bluetooth
                initializeBluetooth();
            } else {
                // Permisos denegados, manejar la situación
                Toast.makeText(this, "Permisos necesarios no concedidos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void searchBluetoothDevices() {
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery();

        // Registrar el receptor para recibir los resultados de la búsqueda
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null) {
                    devicesList.add(device);
                    String deviceName = device.getName() != null ? device.getName() : "Dispositivo sin nombre";
                    devicesNamesList.add(deviceName + "\n" + device.getAddress());
                    adapter.notifyDataSetChanged();  // Actualizar ListView
                }
            }
        }
    };

    @SuppressLint("MissingPermission")
    private void connectToDevice(BluetoothDevice device) {
        try {
            // Cancelar la búsqueda mientras te conectas
            if (bluetoothAdapter.isDiscovering()) {
                bluetoothAdapter.cancelDiscovery();
            }

            // Crear un socket de conexión
            BluetoothSocket socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            socket.connect();
            Toast.makeText(this, "Conectado a " + device.getName(), Toast.LENGTH_SHORT).show();

            // Guardar el socket conectado para su uso en otras clases
            connectedSocket = socket;

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al conectar al dispositivo", Toast.LENGTH_SHORT).show();
        }
    }
}