package com.c5durango.alertagenero.Servicios;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.c5durango.alertagenero.Utilidades.Utilidades;

public class GPSService extends Service {

    private LocationManager mlocManager;
    Intent intentG;
    String TAG = "GPSService";
    Localizacion Local;

    // VARIABLES
    int reporteGenerado;
    String nombrePadre;
    String fechaActual;

    public GPSService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("onBind no implementado");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent!=null){
            intentG = intent;
            reporteGenerado = intent.getIntExtra("reporteGenerado",0);
            nombrePadre = intent.getStringExtra("padre");
            if(reporteGenerado!=0){
                locationStart();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void locationStart() {
        mlocManager = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);
        Local = new Localizacion();
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!gpsEnabled) {
            // GPS Desactivado
            darResultados(getApplicationContext(),0.0, 0.0, "GPS Deshabilitado");
        } else {
            try{
                mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, Local, Looper.myLooper());
            }catch (java.lang.SecurityException ex) {
                Log.i(TAG, "Network "+ "no solicitar la actualización de ubicación: " + ex.getMessage());
            } catch (IllegalArgumentException ex) {
                Log.d(TAG, "Network "+ "proveedor de red no existe, " + ex.getMessage());
            }
            try{
                mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, Local, Looper.myLooper());
            }catch (java.lang.SecurityException ex) {
                Log.i(TAG, "GPS "+ "no solicitar la actualización de ubicación: " + ex.getMessage());
                darResultados(getApplicationContext(),0.0, 0.0, "No se pudo obtener la ubicación actual");
            } catch (IllegalArgumentException ex) {
                Log.d(TAG, "GPS "+ "proveedor de red no existe, " + ex.getMessage());
                darResultados(getApplicationContext(),0.0, 0.0, "El proveedor GPS no existe");
            }
        }
    }


    public class Localizacion implements LocationListener {
        @Override
        public void onLocationChanged(Location loc) {
            Log.d(TAG, "GPS "+ loc.getLatitude());
            Log.d(TAG, "GPS "+ loc.getLongitude());
            fechaActual = Utilidades.obtenerFecha();
            mlocManager.removeUpdates(Local);
            try {
                Local.finalize();
            } catch (Throwable throwable) {
                Log.d(TAG, "GPS "+ "Error al finalizar");
                throwable.printStackTrace();
            }
            darResultados(getApplicationContext(), loc.getLatitude(), loc.getLongitude(), "Se localizó ubicación");
            stopSelf();
        }

        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            Log.d(TAG, "GPS Desactivado");
        }
        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            Log.d(TAG, "GPS Activado");
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(TAG, "GPS "+ "El estatus cambió");
        }
    }

    private void darResultados(Context context, Double latitud, Double longitud, String mensaje){
        Intent intent = new Intent("GPSService");
        intent.putExtra("fecha", fechaActual);
        intent.putExtra("latitud", latitud);
        intent.putExtra("longitud",  longitud);
        intent.putExtra("padre", nombrePadre);
        intent.putExtra("reporteCreado", reporteGenerado);
        intent.putExtra("mensaje", mensaje);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        stopSelf();
    }
}