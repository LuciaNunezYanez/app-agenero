package com.c5durango.alertagenero.Servicios;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.android.volley.toolbox.StringRequest;
import com.androidhiddencamera.CameraConfig;
import com.androidhiddencamera.CameraError;
import com.androidhiddencamera.HiddenCameraService;
import com.androidhiddencamera.HiddenCameraUtils;
import com.androidhiddencamera.config.CameraFacing;
import com.androidhiddencamera.config.CameraFocus;
import com.androidhiddencamera.config.CameraImageFormat;
import com.androidhiddencamera.config.CameraResolution;
import com.androidhiddencamera.config.CameraRotation;
import com.c5durango.alertagenero.R;
import com.c5durango.alertagenero.Utilidades.Utilidades;

import java.io.File;

public class FotografiaService extends HiddenCameraService {

    StringRequest request;
    String tipoCamara;
    Bitmap imagenBitmap;
    int reporteCreado;
    String fecha;
    String imagen = "ninguna";

    static String TAG = "FotografiaService";

    public static ResultReceiver receiver;
    public static final int SUCCESS = 1;
    public static final int ERROR = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG,"Va en 8");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            // Recuperar datos del reporte
            reporteCreado = intent.getIntExtra("reporteCreado", 0);
            tipoCamara = intent.getStringExtra("tipoCamara");

            int tipoFoto = CameraFacing.FRONT_FACING_CAMERA;
            int rotacion = CameraRotation.ROTATION_270;

            if(tipoCamara.equals("frontal")){
                tipoFoto = CameraFacing.FRONT_FACING_CAMERA;
            } else if (tipoCamara.equals("trasera")){
                tipoFoto = CameraFacing.REAR_FACING_CAMERA;
                rotacion = CameraRotation.ROTATION_90;
            } else {
                // No hay foto y termina proceso
            }

            receiver = intent.getParcelableExtra("receiver");
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {

                if (HiddenCameraUtils.canOverDrawOtherApps(getApplicationContext())) {

                    CameraConfig cameraConfig = new CameraConfig()
                            .getBuilder(getApplicationContext())
                            .setCameraFacing(tipoFoto)
                            .setCameraResolution(CameraResolution.MEDIUM_RESOLUTION)
                            .setImageFormat(CameraImageFormat.FORMAT_JPEG)
                            .setCameraFocus(CameraFocus.AUTO)
                            .setImageRotation(rotacion)
                            .build();

                    startCamera(cameraConfig);

                    new android.os.Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Log.d(TAG, "Estoy capturando... "+ tipoCamara);
                            takePicture();
                        }
                    }, 2000L);
                } else {
                    // Solicitar permisos de aparecer encima
                    // HiddenCameraUtils.openDrawOverPermissionSetting(getApplicationContext());
                    responderReceiver(ERROR, imagen, "Sin permiso de aparecer encima");
                }
            } else {
                Log.d(TAG, "Permiso de cámara no disponible");
                responderReceiver(ERROR, imagen,"Permiso de cámara no disponible" );
            }
        } catch(Exception e) {
            Log.d(TAG, "Cayó en el catch");
            responderReceiver(ERROR, imagen, "Cayó en el catch");
            stopSelf();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onImageCapture(@NonNull File imageFile) {
        Log.d(TAG,"Va en onImageCapture");
        fecha = Utilidades.obtenerFecha();

        // Convertir la imagen a bitmap
        String filePath = imageFile.getPath();
        imagenBitmap = BitmapFactory.decodeFile(filePath);
        imagen = Utilidades.convertirImgString(imagenBitmap);
        responderReceiver(SUCCESS, imagen, "Éxito al tomar fotografía");
    }

    @Override
    public void onCameraError(@CameraError.CameraErrorCodes int errorCode) {
        String error = "Desconocido";
        switch (errorCode) {
            case CameraError.ERROR_CAMERA_OPEN_FAILED:
                error = R.string.error_cannot_open + tipoCamara;
                Log.d(TAG, error);
                break;
            case CameraError.ERROR_IMAGE_WRITE_FAILED:
                error = R.string.error_cannot_write + tipoCamara;
                Log.d(TAG, error);
                break;
            case CameraError.ERROR_CAMERA_PERMISSION_NOT_AVAILABLE:
                error = R.string.error_cannot_get_permission + tipoCamara;
                Log.d(TAG, error);
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_OVERDRAW_PERMISSION:
                // Mostrar cuadro de diálogo de información al usuario con pasos para conceder "Dibujar sobre otra aplicación"
                // Permiso para la aplicación.
                HiddenCameraUtils.openDrawOverPermissionSetting(getApplicationContext());
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_FRONT_CAMERA:
                error = R.string.error_not_having_camera + tipoCamara;
                Log.d(TAG, error);
                break;
        }
        responderReceiver( ERROR, imagen, error);
    }

    public void responderReceiver(int estatus , String imagen, String mensaje){
        Bundle bundle = new Bundle();
        bundle.putString("imagen", imagen);
        bundle.putString("tipoCamara", tipoCamara);
        bundle.putString("fecha", fecha);
        bundle.putString("mensaje", mensaje);
        receiver.send(estatus, bundle);
        stopSelf();
    }
}
