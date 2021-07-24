package com.c5durango.alertagenero.Clases;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.c5durango.alertagenero.R;

public class AdaptadorDirectorio extends BaseAdapter {

    private static LayoutInflater inflater = null;
    Context contexto;
    String[][] datos;

    public AdaptadorDirectorio(Context contexto, String[][] datos){
        this.contexto = contexto;
        this.datos = datos;
        inflater = (LayoutInflater) contexto.getSystemService(contexto.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        final View vista = inflater.inflate(R.layout.elemento_directorio, null);
        TextView lblTitulo = (TextView) vista.findViewById(R.id.lblTituloDirectorio);
        TextView lblDatos = (TextView) vista.findViewById(R.id.lblDireccion);
        TextView lblTelefonos = (TextView) vista.findViewById(R.id.lblTelefono);
        //ImageView image = (ImageView) vista.findViewById(R.id.imageDirectorio);

        lblTitulo.setText(datos[i][0]);
        lblDatos.setText(datos[i][1]);
        lblTelefonos.setText(datos[i][2]);
        // image.setImageResource(R.drawable.ic_location);
        return vista;
    }

    @Override
    public int getCount() {
        return datos.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


}
