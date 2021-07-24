package com.c5durango.alertagenero.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class BotonazoReceiver extends BroadcastReceiver {

    Context context;
    private long botonazos[] = {0, 0, 0}; // A B C

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        //Log.d(TAG, "Entro a Botonazo Receiver");
        if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            botonazos[0] = botonazos[1];
            botonazos[1] = botonazos[2];
            botonazos[2] = System.currentTimeMillis();

            if (calcularDiferenciaBC()) {
                if (calcularDiferenciaAB()) {
                    responerAServicio();
                    inicializarBotonazos();
                    //Log.d(TAG, "Tengo tres botonazos");
                }
            }
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            botonazos[0] = botonazos[1];
            botonazos[1] = botonazos[2];
            botonazos[2] = System.currentTimeMillis();

            if (calcularDiferenciaBC()) {
                if (calcularDiferenciaAB()) {
                    responerAServicio();
                    inicializarBotonazos();
                    //Log.d(TAG, "Tengo tres botonazos");
                }
            }

        }
    }


    private void responerAServicio() {
        Intent intent = new Intent("botonActivado");
        intent.putExtra("Activado", true);
        LocalBroadcastManager.getInstance(this.context).sendBroadcast(intent);
    }

    private boolean calcularDiferenciaBC() {
        if ((botonazos[2] - botonazos[1] >= 1) && (botonazos[2] - botonazos[1] <= 800)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean calcularDiferenciaAB() {
        if ((botonazos[1] - botonazos[0] >= 1) && (botonazos[1] - botonazos[0] <= 800)) {
            return true;
        } else {
            return false;
        }
    }

    private void inicializarBotonazos() {
        botonazos[0] = 0;
        botonazos[1] = 0;
        botonazos[2] = 0;
    }
}