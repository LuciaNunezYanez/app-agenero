package com.c5durango.alertagenero.Utilidades;

import android.content.Context;

public class PreferencesCiclo {

    /*
    * Clase utilizada para manejar el número de veces
    * que se enviaran las fotografias.
    * */

    public static Boolean guardarCicloFotografias(Context context, int cicloFotografias){
        try {
            android.content.SharedPreferences preferencesCiclo = context.getSharedPreferences("CicloFotografias", Context.MODE_PRIVATE);
            android.content.SharedPreferences.Editor editorCiclo = preferencesCiclo.edit();
            editorCiclo.putInt("ciclo", cicloFotografias);
            editorCiclo.commit();
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public static int obtenerCicloFotografias(Context context){
        int ciclo = 1;
        android.content.SharedPreferences preferences = context.getSharedPreferences("CicloFotografias", Context.MODE_PRIVATE);
        if (preferences.contains("ciclo")) {
            ciclo = preferences.getInt("ciclo", ciclo);
        }
        return ciclo;
    }

}
