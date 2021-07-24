package com.c5durango.alertagenero;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.c5durango.alertagenero.Utilidades.Notificaciones;

import static com.c5durango.alertagenero.Constantes.CHANNEL_ID;
import static com.c5durango.alertagenero.Constantes.ID_SERVICIO_WIDGET_GENERAR_ALERTA;


public class PerfilActivity extends AppCompatActivity {

    TextView txtCorreo;

    TextView txtNombreUsuario;
    TextView txtSexoUsuario;
    TextView txtTelMovil;
    TextView txtFechaNacimiento;
    TextView txtPadecimientos;
    TextView txtTipoSangre;
    TextView txtAlergias;

    TextView txtCalleNumero;
    TextView txtColonia;
    TextView txtCPLocalidadMunicipio;
    TextView txtEntreCalles;
    TextView txtFachada;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        txtCorreo = findViewById(R.id.txtCorreo);

        txtNombreUsuario = findViewById(R.id.txtNombreUsuario);
        txtSexoUsuario = findViewById(R.id.txtSexoUsuario);
        txtTelMovil = findViewById(R.id.txtTelMovil);
        txtFechaNacimiento = findViewById(R.id.txtFechaNacimiento);
        txtPadecimientos = findViewById(R.id.txtPadecimientos);
        txtTipoSangre = findViewById(R.id.txtTipoSangre);
        txtAlergias = findViewById(R.id.txtAlergias);


        txtCalleNumero = findViewById(R.id.txtCalleNumero);
        txtColonia = findViewById(R.id.txtColonia);
        txtCPLocalidadMunicipio = findViewById(R.id.txtCPLocalidadMunicipio);
        txtEntreCalles = findViewById(R.id.txtCalle1y2);
        txtFachada = findViewById(R.id.txtFachada);


        SharedPreferences preferencesComercioDireccion = getSharedPreferences("ComercioDireccion", Context.MODE_PRIVATE);
        if (preferencesComercioDireccion.contains("id_dir_comercio")){
            txtCalleNumero.setText(
                    (preferencesComercioDireccion.getString("calle","X")).toUpperCase() + " #" +
                            (preferencesComercioDireccion.getString("numero","X")).toUpperCase());
            txtColonia.setText((preferencesComercioDireccion.getString("colonia","X")).toUpperCase());
            txtCPLocalidadMunicipio.setText(
                    (preferencesComercioDireccion.getString("nombre_localidad","X")).toUpperCase() + ", " +
                            (preferencesComercioDireccion.getString("nombre_municipio","X")).toUpperCase() + " " +
                            "C.P. " + preferencesComercioDireccion.getInt("cp",00000));
            txtEntreCalles.setText( "ENTRE " +
                    (preferencesComercioDireccion.getString("entre_calle_1","X")).toUpperCase() + " Y " +
                    (preferencesComercioDireccion.getString("entre_calle_2","X")).toUpperCase() + ".");
            txtFachada.setText((preferencesComercioDireccion.getString("fachada","X")).toUpperCase());
        } else {
            txtSexoUsuario.setText("ID Direccion: 0");
        }


        SharedPreferences preferencesUsuario = getSharedPreferences("Usuario", Context.MODE_PRIVATE);
        if (preferencesUsuario.contains("id_usuarios_app")){
            txtNombreUsuario.setText(
                    (preferencesUsuario.getString("apell_pat","X")).toUpperCase() + " " +
                            (preferencesUsuario.getString("apell_mat","X")).toUpperCase() + " " +
                            (preferencesUsuario.getString("nombres_usuarios_app","X")).toUpperCase());
            String sexo = preferencesUsuario.getString("sexo_app","X");
            if(sexo.equals("F")){
                txtSexoUsuario.setText("FEMENINO");
            } else if (sexo.equals("M")){
                txtSexoUsuario.setText("MASCULINO");
            } else {
                txtSexoUsuario.setText("DESCONOCIDO");
            }


            txtCorreo.setText(preferencesUsuario.getString("correo","X"));
            txtTelMovil.setText(preferencesUsuario.getString("tel_movil","X"));
            txtFechaNacimiento.setText((preferencesUsuario.getString("fecha_nacimiento","X")));
            txtPadecimientos.setText((preferencesUsuario.getString("padecimientos","X")).toUpperCase());
            txtTipoSangre.setText((preferencesUsuario.getString("tipo_sangre","X")).toUpperCase());
            txtAlergias.setText((preferencesUsuario.getString("alergias","X")).toUpperCase());
        } else {
            txtTelMovil.setText("ID Usuario: 0");
        }

    }

    public void cerrarSesion(View view){
        try {
            SharedPreferences.Editor editor1 = getSharedPreferences("Usuario", MODE_PRIVATE).edit();
            editor1.clear().apply();
            SharedPreferences.Editor editor2 = getSharedPreferences("Login", MODE_PRIVATE).edit();
            editor2.clear().apply();
            SharedPreferences.Editor editor3 = getSharedPreferences("Comercio", MODE_PRIVATE).edit();
            editor3.clear().apply();
            SharedPreferences.Editor editor4 = getSharedPreferences("ComercioDireccion", MODE_PRIVATE).edit();
            editor4.clear().apply();
            SharedPreferences.Editor editor5 = getSharedPreferences("UltimoReporte", MODE_PRIVATE).edit();
            editor5.clear().apply();

            startActivity( new Intent(this, MainActivity.class));
        } catch(Exception e){
            Toast.makeText(this, "¡No se puede cerrar sesión, reintente mas tarde!", Toast.LENGTH_LONG).show();
        }
    }
}
