package cl.inacap.appcrpm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Output;
import android.os.Bundle;

import org.json.JSONObject;
import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.CvType;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class CapturaCamaraActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private CameraBridgeViewBase cameraBridgeViewBase;
    private Mat frame, processedFrame;  // Declarar Mat globales

    private long lastFrameTime = 0;

    private static final int FRAME_INTERVAL_MS = 100;

    private TextView textViewCommand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captura_camara);

        ActivityCompat.requestPermissions(CapturaCamaraActivity.this, new String[]{Manifest.permission.CAMERA}, 1);

        textViewCommand = findViewById(R.id.command);

        // Inicializar el CameraBridgeViewBase
        cameraBridgeViewBase = (CameraBridgeViewBase) findViewById(R.id.camera_preview);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeViewBase.setCameraPermissionGranted();
        cameraBridgeViewBase.setCvCameraViewListener(this);
        cameraBridgeViewBase.setMaxFrameSize(480, 854);
        cameraBridgeViewBase.setCameraIndex(1);

        if (OpenCVLoader.initDebug()) {
            Toast.makeText(this, "Captura Iniciada Correctamente", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Captura Fallida", Toast.LENGTH_SHORT).show();
            System.loadLibrary("opencv_java3");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cameraBridgeViewBase.setCameraPermissionGranted();
            } else {
                // permission denied
            }
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        // Inicializar los Mat cuando se inicia la cámara
        frame = new Mat();
        processedFrame = new Mat();
    }

    @Override
    public void onCameraViewStopped() {
        // Liberar los recursos al detener la vista de la cámara
        if (frame != null) {
            frame.release();
        }
        if (processedFrame != null) {
            processedFrame.release();
        }
        cameraBridgeViewBase.disableView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cameraBridgeViewBase != null) {
            cameraBridgeViewBase.enableView();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Libera los recursos de OpenCV cuando la app se pausa
        if (frame != null) {
            frame.release();
        }
        if (processedFrame != null) {
            processedFrame.release();
        }

        if (cameraBridgeViewBase != null) {
            cameraBridgeViewBase.disableView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Asegurarse de desactivar la vista de cámara al destruir la actividad
        if (cameraBridgeViewBase != null) {
            cameraBridgeViewBase.disableView();
        }
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        long currentTime = System.currentTimeMillis();

        frame = inputFrame.rgba(); // Guardar el frame en el Mat global "frame"

        // Devolver el frame para mostrarlo en pantalla
        if (currentTime - lastFrameTime > FRAME_INTERVAL_MS) {
            if (frame == null || frame.empty()) {
                Log.e("FRAME_ERROR", "El frame de la cámara está vacío.");
                return frame; // No hacer nada si el frame es inválido
            }

            // Convertir frame a Base64 para enviar
            String encodedFrame = matToBase64(frame);

            // Enviar el frame al servidor Python solo si es válido
            if (isValidBase64(encodedFrame)) {
                sendFrameToServer(encodedFrame);
            } else {
                Log.e("ENCODED_FRAME", "Frame codificado es inválido. No se enviará al servidor.");
            }

            lastFrameTime = currentTime;

            Core.flip(frame, frame, 1);

            return frame;
        } else {
            return null;
        }
    }

    // Convertir Mat a Base64
    private String matToBase64(Mat frame) {
        Mat matJpeg = new Mat();
        Imgproc.cvtColor(frame, matJpeg, Imgproc.COLOR_RGBA2BGR); // Convertir a formato BGR para JPEG

        // Codificar Mat a JPEG
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", matJpeg, matOfByte); // Codificar a JPEG en lugar de extraer directamente los bytes

        // Convertir a Base64
        byte[] byteArray = matOfByte.toArray();
        String encodedFrame = Base64.encodeToString(byteArray, Base64.DEFAULT);

        // Liberar el Mat temporal
        matJpeg.release();

        return encodedFrame;
    }

    // Verificación de decodificación de Base64
    private boolean isValidBase64(String encodedFrame) {
        try {
            // Intentar decodificar el Base64
            byte[] decodedBytes = Base64.decode(encodedFrame, Base64.DEFAULT);

            // Crear un Bitmap temporal para verificar si los bytes forman una imagen válida
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true; // Solo obtener las dimensiones
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length, options);

            // Log de las dimensiones de la imagen
            Log.d("BASE64_DECODE", "Ancho: " + options.outWidth + ", Alto: " + options.outHeight);

            // Si las dimensiones de la imagen son válidas, la decodificación fue exitosa
            return (options.outWidth > 0 && options.outHeight > 0);
        } catch (Exception e) {
            // Si ocurre un error en la decodificación, retornar falso y loguear el error
            Log.e("BASE64_ERROR", "Error verificando Base64: " + e.getMessage());
            return false;
        }
    }

    // Enviar frame al servidor Python
    private void sendFrameToServer(final String encodedFrame) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://192.168.4.27:5000/process_frame");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);

                    //Log.d("ENCODED_FRAME", encodedFrame);

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("frame", encodedFrame);
                    String jsonInputString = jsonObject.toString();
                    Log.d("ENCODED_FRAME_SIZE", String.valueOf(encodedFrame.length()));

                    // Enviar el frame al servidor
                    try (OutputStream os = conn.getOutputStream()) {
                        byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }

                    int code = conn.getResponseCode();
                    if (code == HttpURLConnection.HTTP_OK) {
                        Log.d("SUCCESS", "Paquete enviado correctamente");
                    } else {
                        Log.w("FAILED", "Envío de paquete fallido, código: " + code);

                        // Capturar el mensaje de error del servidor
                        InputStream errorStream = conn.getErrorStream();
                        if (errorStream != null) {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream));
                            StringBuilder errorResponse = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                errorResponse.append(line);
                            }
                            Log.e("SERVER_ERROR", errorResponse.toString());
                        }
                    }

                    // Leer la respuesta del servidor
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }

                    // Convertir respuesta a JSON
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    String command = jsonResponse.getString("finger_count");

                    // Mostrar el número de dedos levantados en la UI (hilo principal)
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textViewCommand.setText("Dedos levantados: " + command);
                            Toast.makeText(CapturaCamaraActivity.this, "Dedos levantados: " + command, Toast.LENGTH_SHORT).show();
                        }
                    });

                    // Enviar comando a ESP32
                    enviarComandoESP32(command);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void enviarComandoESP32(String command) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Dirección IP y puerto del ESP32
                    Socket socket = new Socket("192.168.4.1", 80); // Cambiar IP y puerto de ESP32

                    // Enviar comando al ESP32
                    OutputStream out = socket.getOutputStream();
                    out.write(command.getBytes());
                    out.flush();

                    // Cerrar la conexión
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
