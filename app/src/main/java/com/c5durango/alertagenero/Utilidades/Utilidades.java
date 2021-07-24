package com.c5durango.alertagenero.Utilidades;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class Utilidades {

    static String TAG = "Utilidades";

    public static String obtenerFecha(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Log.d(TAG,dateFormat.format(new Date()) );
        return dateFormat.format(new Date()); // Salida:  2019-10-28 15:24:55
    }

    public static String convertirImgString(Bitmap bitmap){
        ByteArrayOutputStream array = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,array);
        byte[] imagenByte = array.toByteArray();
        String imagenString = Base64.encodeToString(imagenByte, Base64.DEFAULT);


        return imagenString;
    }

    public static String convertirAudioString(String pathAudio) {

        String audioString = "";
        byte[] audioBytes;
        try {
            // Log.d(TAG, "El peso del archivo es: " + new File(pathAudio).length());
            // 25 KB aprox para 15 segundos

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            FileInputStream fis = new FileInputStream(new File(pathAudio));
            byte[] buf = new byte[1024];
            int n;
            while (-1 != (n = fis.read(buf)))
                baos.write(buf, 0, n);
            audioBytes = baos.toByteArray();

            audioString = Base64.encodeToString(audioBytes, Base64.DEFAULT);

        } catch (Exception e) {
            Log.d(TAG, "Ocurrió un error al codificar audio a Base64");
        }
        return audioString;
    }

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    //Método para obtener la posición de un ítem del spinner
    public static int obtenerPosicionItem(Spinner spinner, String valor) {
        int posicion = 0;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(valor)) {
                posicion = i;
            }
        }
        return posicion;
    }

    public static String tipoErrorVolley(VolleyError error){
        if (error instanceof TimeoutError) {
            return "Problema con el servidor";
        } else if (error instanceof NoConnectionError) {
            return "Sin conexión";
        } else if (error instanceof AuthFailureError) {
            return "Falló al autenticar";
        } else if (error instanceof ServerError) {
            return "El servidor no responde";
        } else if (error instanceof NetworkError) {
            return "Error de Red";
        } else if (error instanceof ParseError) {
            return "Error de parseo";
        } else {
            return "Error desconocido";
        }
    }

    // Validaciones
    public static boolean validEmail(String email){
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    public static boolean validPhone(String telefono){
        Pattern pattern = Patterns.PHONE;
        return pattern.matcher(telefono).matches();
    }
}
