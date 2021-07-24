package com.c5durango.alertagenero.Servicios;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.c5durango.alertagenero.Broadcast.BotonazoReceiver;
import com.c5durango.alertagenero.Constantes;
import com.c5durango.alertagenero.R;
import com.c5durango.alertagenero.Utilidades.EnviarCoordenadas;
import com.c5durango.alertagenero.Utilidades.EnviarImagenes;
import com.c5durango.alertagenero.Utilidades.Notificaciones;
import com.c5durango.alertagenero.Utilidades.PreferencesCiclo;
import com.c5durango.alertagenero.Utilidades.PreferencesReporte;
import com.c5durango.alertagenero.Utilidades.Utilidades;

import static com.c5durango.alertagenero.Constantes.CHANNEL_ID;
import static com.c5durango.alertagenero.Constantes.ID_SERVICIO_PANICO;
import static com.c5durango.alertagenero.Constantes.ID_SERVICIO_WIDGET_CREAR_REPORTE;
import static com.c5durango.alertagenero.Constantes.ID_SERVICIO_WIDGET_GENERAR_ALERTA;

public class ServicioNotificacion extends Service {

    static String TAG = "Notificacion";


    // VARIABLES PARA USO DE MULTIMEDIA
    ImageTraseraResultReceiver resultTReceiver;
    ImageFrontalResultReceiver resultFReceiver;
    public static final int SUCCESS = 1;
    public static final int ERROR = 0;
    Boolean procesoImagenFrontal = false;
    Boolean procesoImageTrasera = false;
    String IMAGEN_FRONTAL = "Ninguna";
    String IMAGEN_TRASERA = "Ninguna";
    String FECHA_FRONTAL = "";
    String FECHA_TRASERA = "";

    // VARIABLES DEL COMERCIO
    private int idComercio = 0;
    private int idUsuario = 0;

    // VARIABLES PARA LAS IMAGENES
    int ciclo = 1;

    // VARIABLES PARA REPORTE CREADO
    private int reporteCreado = 0;
    BotonazoReceiver botonazoReceiver;

    Intent intentGPS;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        botonazoReceiver = new BotonazoReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        intentFilter.addAction("android.intent.action.SCREEN_ON");

        // Registrar el BroadCast para escuchar los tres botonazos
        registerReceiver(botonazoReceiver, intentFilter);
        registrarEscuchadorBotonazos();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            crearNotificacionPersistente();
        }
        //return super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    /*********************************************************************************************
     *                     BROADCAST RECEIVER - ESCUCHA LOS 3 CLICKS                              *
     *********************************************************************************************/

    private BroadcastReceiver broadcastReceiverBotonazo = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle parametros = intent.getExtras();
            Boolean respuesta = parametros.getBoolean("Activado", false);

            if(respuesta){
                //Log.d(TAG, "Dice el broadcast que detecto tres botonazos... " + contador);
                comenzarProceso();
            }
        }
    };

    public void comenzarProceso(){
        Boolean puedeEnviar = PreferencesReporte.puedeEnviarReporte(getApplicationContext(), System.currentTimeMillis());
        if(puedeEnviar){
            PreferencesReporte.guardarReporteInicializado(getApplicationContext());
            registrarReceiverGnerarAlerta();
            iniciarServicioGenerarAlerta();

        } else{
            deboTerminar();
        }
    }

    public void detenerServicioAlerta(){
        Intent intent = new Intent(getApplicationContext(), GenerarAlertaService.class);
        getApplicationContext().stopService(intent);
    }


    /**********************************************************************************************
     *                                 GENERAR  ALERTA                                             *
     **********************************************************************************************/

    public void registrarReceiverGnerarAlerta(){
        LocalBroadcastManager.getInstance(getApplication()).registerReceiver(broadcastReceiverGenerarAlerta, new IntentFilter("generarAlertaService"));
    }

    private void iniciarServicioGenerarAlerta(){
        SharedPreferences preferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        if (preferences.contains("comercio") && preferences.contains("usuario")){
            idComercio = preferences.getInt("comercio" ,0);
            idUsuario = preferences.getInt("usuario", 0);
        } else {
            // No se puede agregar la notificación
        }

        Intent intent = new Intent(getApplicationContext(), GenerarAlertaService.class);
        intent.putExtra("comercio", idComercio);
        intent.putExtra("usuario", idUsuario);
        intent.putExtra("sala", "Genero");
        intent.putExtra("fecha", Utilidades.obtenerFecha());
        intent.setPackage("com.c5durango.appalertagenero");
        getApplicationContext().startService(intent);
    }

    /**********************************************************************************************
     *                                      FOTOGRAFIAS                                            *
     **********************************************************************************************/

    public void iniciarProcesoFotografias(){
        if( getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT) ){
            // INICIAR PROCESO CAMARA FRONTAL
            resultFReceiver = new ImageFrontalResultReceiver(new Handler());
            Intent intentFrontal = new Intent(getApplicationContext(), FotografiaService.class);
            intentFrontal.putExtra("reporteCreado", reporteCreado);
            intentFrontal.putExtra("tipoCamara", "frontal");
            intentFrontal.putExtra("receiver", resultFReceiver);
            getApplicationContext().startService(intentFrontal);

        } else if ( getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA) ){
            // INICIAR PROCESO CAMARA TRAERA
            resultTReceiver = new ImageTraseraResultReceiver(new Handler());
            Intent intentTrasera = new Intent(getApplicationContext(), FotografiaService.class);
            intentTrasera.putExtra("reporteCreado", reporteCreado);
            intentTrasera.putExtra("tipoCamara", "trasera");
            intentTrasera.putExtra("receiver", resultTReceiver);
            getApplicationContext().startService(intentTrasera);

        } else {
            if( procesoImagenFrontal ){
                // ENVIA LA IMAGEN FRONTAL (2)
                Boolean res = EnviarImagenes.enviarImagenFrontal(getApplicationContext(), IMAGEN_FRONTAL, FECHA_FRONTAL, reporteCreado);
                calcularNuevoEnvioFotografias();
            } else{
                // TERMINA PROCESO Y NO ENVIA NADA (3)
                finProcesoFotografias();
            }
        }
    }

    public void calcularNuevoEnvioFotografias(){
        ciclo--;
        if(ciclo != 0){
            // Iniciar nuevamente el proceso de fotografias
            iniciarProcesoFotografias();
        } else if (ciclo == 0){
            terminarServicioFotografias();
        }
    }

    public void terminarServicioFotografias(){
        Intent intent = new Intent(getApplicationContext(), FotografiaService.class);
        getApplicationContext().stopService(intent);
    }

    public void finProcesoFotografias(){
        Log.d(TAG, "Termina proceso vacio de fotografias");
        // Al menos guardar las fotografía en el dispositivo
    }


    /*********************************************************************************************
     *                                        GPS                                                 *
     *********************************************************************************************/

    public void comenzarGPS(){
        intentGPS = new Intent(getApplicationContext(), GPSService.class);
        intentGPS.putExtra("reporteGenerado", reporteCreado);
        intentGPS.putExtra("padre", "ServicioNotificacion");
        startService(intentGPS);
    }

    public void terminarServicioGPS(){
        eliminarEscuchadorGPS();
        Intent intent = new Intent(getApplicationContext(), GPSService.class);
        getApplicationContext().stopService(intent);
    }


    /**********************************************************************************************
     *                                         AUDIO                                               *
     **********************************************************************************************/
    public void comenzarGrabacionAudio(){
        registrarEscuchadorAudio();
        Intent intent = new Intent(getApplicationContext(), AudioService.class);
        intent.putExtra("nombreAudio", "GrabacionBotonDePanico");
        intent.putExtra("reporteCreado", reporteCreado);
        getApplicationContext().startService(intent);
    }

    public void terminarGrabacionAudio(){
        eliminarEscuchadorAudio();
        Intent intent = new Intent(getApplicationContext(), AudioService.class);
        getApplicationContext().stopService(intent);
    }



    /*********************************************************************************************
     *                     INICIAN RESULT RECEIVER DE FOTOGRAFIAS                                 *
     *********************************************************************************************/

    private class ImageTraseraResultReceiver extends ResultReceiver {
        public ImageTraseraResultReceiver(Handler handler) {
            super(handler);
        }
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            procesoImageTrasera = true;
            FECHA_TRASERA = resultData.getString("fecha");
            // GUARDAR LA IMAGEN DE RETORNO
            String imagenTrasera = resultData.getString("imagen");
            if(!imagenTrasera.equals("Ninguna")){
                IMAGEN_TRASERA = imagenTrasera;
            }

            switch (resultCode) {
                case ERROR:
                    Log.d(TAG, "Ocurrió un error al devolver camara trasera");
                    break;

                case SUCCESS:
                    // ENVIAR MULTIMEDIA (1)
                    if(procesoImagenFrontal){
                        Boolean res = EnviarImagenes.enviarImagenFrontal(getApplicationContext(), IMAGEN_FRONTAL, FECHA_FRONTAL, reporteCreado);
                    }
                    if(procesoImageTrasera){
                        Boolean res = EnviarImagenes.enviarImagenTrasera(getApplicationContext(), IMAGEN_TRASERA, FECHA_TRASERA, reporteCreado);
                    }
                    calcularNuevoEnvioFotografias();
                    break;
            }
            super.onReceiveResult(resultCode, resultData);
        }
    }

    private class ImageFrontalResultReceiver extends ResultReceiver {
        public ImageFrontalResultReceiver(Handler handler) {
            super(handler);
        }
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            procesoImagenFrontal = true;
            FECHA_FRONTAL = resultData.getString("fecha");

            // GUARDAR LA IMAGEN DE RETORNO
            String imagenFrontal = resultData.getString("imagen");
            if(!imagenFrontal.equals("Ninguna")){
                IMAGEN_FRONTAL = imagenFrontal;
            }

            switch (resultCode) {
                case ERROR:
                    Log.d(TAG, "Ocurrió un error al devolver cámara frontal");
                    // SE SIGUE CON LA TRASERA
                    break;
                case SUCCESS:

                    if(getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
                        // INICIAR PROCESO CAMARA TRAERA
                        resultTReceiver = new ImageTraseraResultReceiver(new Handler());
                        Intent intentTrasera = new Intent(getApplicationContext(), FotografiaService.class);
                        intentTrasera.putExtra("reporteCreado", reporteCreado);
                        intentTrasera.putExtra("tipoCamara", "trasera");
                        intentTrasera.putExtra("receiver", resultTReceiver);
                        startService(intentTrasera);
                    }else {
                        if(procesoImagenFrontal){
                            // ENVIAR LA IMAGEN FRONTAL (5)
                            Boolean res = EnviarImagenes.enviarImagenFrontal(getApplicationContext(), IMAGEN_FRONTAL, FECHA_FRONTAL, reporteCreado);
                            calcularNuevoEnvioFotografias();
                        } else{
                            // FIN DEL PROCESO (6)
                            finProcesoFotografias();
                        }
                    }
                    break;
            }
            super.onReceiveResult(resultCode, resultData);
        }
    }

    /*********************************************************************************************
     *                       BROADCAST RECEIVERS - ESCUCHADORES                                   *
     *********************************************************************************************/

    public void registrarEscuchadorGPS(){
        LocalBroadcastManager.getInstance(getApplication()).registerReceiver(broadcastReceiverGPSService, new IntentFilter("GPSService"));
    }

    public void eliminarEscuchadorGPS(){
        LocalBroadcastManager.getInstance(getApplication()).unregisterReceiver(broadcastReceiverGPSService);
    }

    public void registrarEscuchadorBotonazos(){
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiverBotonazo, new IntentFilter("botonActivado"));
    }

    public void eliminarEscuchadorBotonazos(){
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiverBotonazo);
    }

    public void registrarEscuchadorAudio(){
        LocalBroadcastManager.getInstance(getApplication()).registerReceiver(broadcastReceiverAudio, new IntentFilter("AudioBroadcast"));
    }

    public void eliminarEscuchadorAudio(){
        LocalBroadcastManager.getInstance(getApplication()).unregisterReceiver(broadcastReceiverAudio);
    }

    private BroadcastReceiver broadcastReceiverGenerarAlerta = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intentGenerarAlerta) {

            try{
                Bundle parametros = intentGenerarAlerta.getExtras();
                reporteCreado = parametros.getInt("reporteCreado", 0);

                detenerServicioAlerta();

                if (reporteCreado != 0) {
                    // Guardar la información del ultimo reporte generado
                    PreferencesReporte.actualizarUltimoReporte(getApplicationContext(), reporteCreado);
                    Notificaciones notificaciones = new Notificaciones();
                    notificaciones.crearNotificacionNormal(context, CHANNEL_ID,  R.drawable.ic_color_success, "¡Alerta enviada!", "Se generó alerta con folio #" + reporteCreado, ID_SERVICIO_WIDGET_GENERAR_ALERTA);

                    // Comenzar con el envio de multimedia unicamente para API +23
                    if( Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED){
                            // Validar si el servicio está activo
                            if(!Utilidades.isMyServiceRunning(getApplication(), AudioService.class)){
                                // Invertí la condición y meti comenzar
                                // terminarGrabacionAudio();
                                // Thread.sleep(3000);
                                comenzarGrabacionAudio();
                            }

                        }
                    }

                    // GPS
                    terminarServicioGPS();
                    registrarEscuchadorGPS();
                    comenzarGPS();

                    // Obtener el numero de ciclos para comenzar a tomar las fotografias
                    PreferencesCiclo preferencesCiclo = new PreferencesCiclo();
                    ciclo = preferencesCiclo.obtenerCicloFotografias(getApplicationContext());
                    if(ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                        iniciarProcesoFotografias();
                    } else {
                        Log.e(TAG, "No tiene permisos de camara");
                    }

                } else {
                    Notificaciones notificaciones = new Notificaciones();
                    notificaciones.crearNotificacionNormal(getApplicationContext(), CHANNEL_ID, R.drawable.ic_color_error, "¡No se pudo generar la alerta de pánico!", parametros.getString("message", "Sin mensaje"), ID_SERVICIO_WIDGET_CREAR_REPORTE);
                }

            } catch (Exception e){
                Log.e(TAG, "Catch broadscast receiver generar alerta: " + e.getMessage());
            }
        }
    };

    private BroadcastReceiver broadcastReceiverGPSService = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle parametros = intent.getExtras();

            double latitud = parametros.getDouble("latitud", 0.0);
            double longitud = parametros.getDouble("longitud", 0.0);
            String fecha = parametros.getString("fecha", "");
            String padre = parametros.getString("padre");
            int reporte = parametros.getInt("reporteCreado", 0);

            if (latitud != 0.0 && longitud != 0.0 && reporte != 0 && !fecha.equals("")){
                EnviarCoordenadas enviarCoordenadas = new EnviarCoordenadas();
                Boolean hasGPS = enviarCoordenadas.enviarCoordenadas(getApplicationContext(), latitud, longitud, fecha, reporte);
                terminarServicioGPS();
                deboTerminar();
            }
        }
    };

    private BroadcastReceiver broadcastReceiverAudio = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getExtras().getBoolean("termino")){
                terminarGrabacionAudio();
                Log.d(TAG, "TERMINÓ AUDIO SERVICE? " + Utilidades.isMyServiceRunning(getApplicationContext(), AudioService.class));
            }
            deboTerminar();
        }
    };


    public void deboTerminar(){
        Boolean runAudio = Utilidades.isMyServiceRunning(getApplicationContext(), AudioService.class);
        Boolean runGPS = Utilidades.isMyServiceRunning(getApplicationContext(), GPSService.class);
        Boolean runFotograf = Utilidades.isMyServiceRunning(getApplicationContext(), FotografiaService.class);

        if(!runAudio && !runGPS && !runFotograf){
            //stopSelf();
            Log.d(TAG, "Ya terminaron todos los procesos");
        } else {
            Log.d(TAG, "Aún no debo terminar. Audio: " + runAudio + " GPS: " + runGPS + " Fotograf: " + runFotograf);
        }
    }

    public void crearNotificacionPersistente(){ // Crear notificación de servicio activo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent notificationIntent = new Intent(getApplicationContext(), ServicioNotificacion.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

            Notification notification =
                    new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                            .setColor(Color.WHITE)
                            .setContentText("Alerta al presionar el botón de encendido.")
                            .setSmallIcon(R.drawable.ic_siren_chica)
                            .setColor(Color.GRAY)
                            .setContentIntent(pendingIntent)
                            .build();
            // Crear Canal de notificaciones
            String descripcion = "El uso de este servicio le permite detectar cuando se presiona tres veces el botón de bloqueo y genera la alerta de pánico"; //
            int importancia = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, Constantes.NOMBRE_APP, importancia);
            notificationChannel.setDescription(descripcion);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);

            startForeground(ID_SERVICIO_PANICO, notification);
            Toast.makeText(getApplicationContext(), "¡Se creó servicio para detectar alerta a traves del botón de bloqueo!", Toast.LENGTH_LONG).show();
        } else{
            Toast.makeText(getApplicationContext(), "¡Se creó servicio para detectar alerta a traves del botón de bloqueo!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Cancela el registro del BroadCast Creo que debo cancelar todas las suscripciones
        unregisterReceiver(botonazoReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiverBotonazo);
    }
}