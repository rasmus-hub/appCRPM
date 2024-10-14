package cl.inacap.appcrpm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;

import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.core.Mat;
import org.opencv.core.CvType;
import org.opencv.android.OpenCVLoader;
import org.opencv.imgproc.Imgproc;

import java.io.FileInputStream;
import android.util.Base64;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;

public class CapturaCamaraActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private CameraBridgeViewBase cameraBridgeViewBase;
    private Mat frame, processedFrame;  // Declarar Mat globales

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captura_camara);

        ActivityCompat.requestPermissions(CapturaCamaraActivity.this, new String[]{Manifest.permission.CAMERA}, 1);

        // Inicializar el CameraBridgeViewBase
        cameraBridgeViewBase = (CameraBridgeViewBase) findViewById(R.id.camera_preview);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeViewBase.setCameraPermissionGranted();
        cameraBridgeViewBase.setCvCameraViewListener(this);
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
        // Procesar el frame de la cámara en formato RGBA
        frame = inputFrame.rgba(); // Guardar el frame en el Mat global "frame"

        // Convertir frame a Base64 para enviar
        String encodedFrame = matToBase64(frame);

        // Enviar el frame al servidor Python
        sendFrameToServer(encodedFrame);

        // Devolver el frame para mostrarlo en pantalla
        return frame;
    }

    // Convertir Mat a Base64
    private String matToBase64(Mat frame) {
        Mat matJpeg = new Mat();
        Imgproc.cvtColor(frame, matJpeg, Imgproc.COLOR_RGBA2BGR); // Convertir a formato JPEG

        // Convertir a bytes
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] byteArray = new byte[(int) (matJpeg.total() * matJpeg.elemSize())];
        matJpeg.get(0, 0, byteArray);

        // Libera el matJpeg una vez que ya no es necesario
        matJpeg.release();

        return Base64.encodeToString(byteArray, Base64.DEFAULT); // Codificar en Base64
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

                    String jsonInputString = "{\"frame\":\"" + encodedFrame + "\"}";

                    try (OutputStream os = conn.getOutputStream()) {
                        byte[] input = jsonInputString.getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    int code = conn.getResponseCode();
                    if (code == HttpURLConnection.HTTP_OK) {
                        // Procesar la respuesta si es necesario
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
