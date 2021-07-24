package com.c5durango.alertagenero.Utilidades;

import android.content.Context;
import android.util.Log;
import com.c5durango.alertagenero.Constantes;

public class PreferencesReporte {

    // ULTIMO REPORTE
    // Métodos para administrar la información del ultimo reporte generado

    private static String TAG = "PreferencesReporte";
    public static Boolean guardarReporteInicializado(Context context) {
        try {
            android.content.SharedPreferences preferences = context.getSharedPreferences("UltimoReporte", Context.MODE_PRIVATE);
            android.content.SharedPreferences.Editor editor = preferences.edit();

            editor.putLong("fechaUltimoReporte", System.currentTimeMillis());
            editor.putInt("ultimoReporte", 0);
            editor.putBoolean("estatusReporte", false);
            editor.commit();
            return true;
        } catch (Exception e){
            return false;
        }
    }

    // Antes de generar un nuevo reporte es necesario validar
    // si existe uno pendiente para así no duplicar alertas.
    public static Boolean puedeEnviarReporte(Context context, Long fechaHoraActual){
        Boolean enviar;
        android.content.SharedPreferences preferences = context.getSharedPreferences("UltimoReporte", Context.MODE_PRIVATE);

        if (preferences.contains("estatusReporte")){
            Boolean estatusUltimo = preferences.getBoolean("estatusReporte", false);
            Long fechaUltReporte = preferences.getLong("fechaUltimoReporte", 0);
            Long diferenciaMilisegundos = fechaHoraActual - fechaUltReporte;

            // Obtener el ultimo reporte generado
            int ult_reporte_generado = preferences.getInt("ultimoReporte", 0);

            if( estatusUltimo ){
                if(diferenciaMilisegundos >= Constantes.DIFERENCIA_ENTRE_REPORTES){
                    enviar = true;
                } else {
                    enviar = false;
                    // La presión del botón se toma como el mismo reporte y solo se agrega un incremento
                    if(ult_reporte_generado >= 1){
                        EnviarBotonazo enviarBotonazo = new EnviarBotonazo();
                        enviarBotonazo.enviarBotonazo(context, ult_reporte_generado, Utilidades.obtenerFecha());
                    } else {
                        Log.d(TAG, "El ultimo reporte es <=1 por lo cuál no se puede generar incremento");
                    }
                }
            } else {
                // ¿El ultimo reporte que no se generó fue hace mas de 1 minuto?
                if(diferenciaMilisegundos >= Constantes.DIFERENCIA_ENTRE_REPORTES){
                    enviar = true;
                } else {
                    enviar = true;
                }
            }
        } else {
            enviar = true;
        }
        return enviar;
    }

    // Una vez que el reporte se haya generado es necesario
    // modificar el ultimo reporte como enviado.
    public static Boolean actualizarUltimoReporte(Context context, int reporteCreado){
        try{
            android.content.SharedPreferences preferences = context.getSharedPreferences("UltimoReporte", Context.MODE_PRIVATE);
            android.content.SharedPreferences.Editor editor = preferences.edit();

            if (reporteCreado != 0 ){
                editor.putLong("fechaUltimoReporte", System.currentTimeMillis());
                editor.putInt("ultimoReporte", reporteCreado);
                editor.putBoolean("estatusReporte", true);
                editor.commit();
                return true;
            } else {
                return false;
            }
        } catch (Exception e){
            return false;
        }
    }

    public static Boolean puedeCancelarAlerta(Context context, Long fechaHoraActual){
        android.content.SharedPreferences preferences = context.getSharedPreferences("UltimoReporte", Context.MODE_PRIVATE);
        if(preferences.contains("estatusReporte")){
            Boolean estatusUltimo = preferences.getBoolean("estatusReporte", false);
            Long fechaUltReporte = preferences.getLong("fechaUltimoReporte", 0);
            Long diferenciaMilisegundos = fechaHoraActual - fechaUltReporte;

            // Si ha pasado menos de X tiempo y el reporte fue enviado
            if (diferenciaMilisegundos <= Constantes.LAPSO_PARA_CANCELAR_REPORTE && estatusUltimo == true){
                //Puede cancelar
                return true;
            } else {
                //Ya no puede cancelar
                return false;
            }
        } else {
            // No hay ningún reporte que cancelar
            return false;
        }
    }
}
