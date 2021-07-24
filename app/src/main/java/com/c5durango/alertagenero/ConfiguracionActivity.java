package com.c5durango.alertagenero;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import com.androidhiddencamera.HiddenCameraUtils;
import com.c5durango.alertagenero.Servicios.ServicioNotificacion;
import com.c5durango.alertagenero.Utilidades.PreferencesCiclo;
import com.c5durango.alertagenero.Utilidades.Utilidades;

public class ConfiguracionActivity extends AppCompatActivity {

    private Switch switchServicioActivo;
    private Boolean isActive = false;
    private ImageButton btnSaveCiclo;
    private EditText txtNoCiclo;
    private String TAG = "Configuracion";

    boolean conf_nueva = false;

    ImageButton btnAlmacWrite, btnCam, btnMicrof, btnUbic, btnEncima;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);
        switchServicioActivo = findViewById(R.id.switchServicioActivo);
        btnSaveCiclo = findViewById(R.id.iBtnGuardarCiclo);
        txtNoCiclo = findViewById(R.id.txtNoCiclo);

        obtenerPreferenciasNotificacion();
        switchServicioActivo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    iniciarServicioPersistente();
                    conf_nueva = true;
                } else {
                    detenerServicioPersistente();
                    conf_nueva = false;
                }
            }
        });

        obtenerPreferenciasCiclo();
        btnSaveCiclo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    if (txtNoCiclo.getText()== null || Integer.parseInt(txtNoCiclo.getText().toString()) <= 0 ){
                        Toast.makeText(getApplication(), "¡Por favor ingrese un valor válido!" , Toast.LENGTH_SHORT).show();
                    } else if ( Integer.parseInt(txtNoCiclo.getText().toString()) > 3  ){
                        Toast.makeText(getApplication(), "¡Por favor ingrese un valor entre 1 y 3!" , Toast.LENGTH_SHORT).show();
                    } else if (Integer.parseInt(txtNoCiclo.getText().toString()) >= 1 && Integer.parseInt(txtNoCiclo.getText().toString()) <= 3){
                        guardarPreferenciasCiclo(Integer.parseInt(txtNoCiclo.getText().toString()));
                    }
                }catch (Exception e){
                    Toast.makeText(getApplication(), "¡Por favor ingrese un valor válido!" , Toast.LENGTH_SHORT).show();
                }
            }
        });

        // PERMISOS
        btnAlmacWrite = findViewById(R.id.btnAlmacenamientoWrite);
        btnCam = findViewById(R.id.btnCamara);
        btnMicrof = findViewById(R.id.btnMicrofono);
        btnEncima = findViewById(R.id.btnAparecerEncima);
        btnUbic = findViewById(R.id.btnUbicacion);

        // Detectar permisos
        permisoAlmacWrite();
        permisoCam();
        permisoMicrof();
        permisoUbic();
        permisoEncima();
    }

    private void obtenerPreferenciasNotificacion(){
        SharedPreferences preferences = getApplication().getSharedPreferences("NotificacionPersistente", Context.MODE_PRIVATE);
        if (preferences.contains("notificacionActiva")){
            isActive = Utilidades.isMyServiceRunning(getApplication(), ServicioNotificacion.class);
            switchServicioActivo.setChecked(isActive);
            conf_nueva = Utilidades.isMyServiceRunning(getApplication(), ServicioNotificacion.class);
        } else {
            isActive = Utilidades.isMyServiceRunning(getApplication(), ServicioNotificacion.class);
            switchServicioActivo.setChecked(isActive);
            conf_nueva = false;
        }
    }

    private void obtenerPreferenciasCiclo(){
        PreferencesCiclo preferencesCiclo = new PreferencesCiclo();
        int ciclos = preferencesCiclo.obtenerCicloFotografias(getApplication());
        txtNoCiclo.setText(String.valueOf(ciclos));
    }

    private void guardarPreferenciasCiclo(int ciclo){
        PreferencesCiclo preferencesCiclo = new PreferencesCiclo();
        Boolean res = preferencesCiclo.guardarCicloFotografias(getApplication(), ciclo);
        if(res){
            Toast.makeText(getApplication(), "¡Número de ciclos guardados con éxito!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplication(), "¡Error al guardar el número de ciclos!", Toast.LENGTH_SHORT).show();
        }
    }

    public void iniciarServicioPersistente(){
        try {
            Intent notificationIntent = new Intent(getApplication(), ServicioNotificacion.class);
            notificationIntent.putExtra("padre", "App");
            getApplication().startService(notificationIntent);

            isActive = Utilidades.isMyServiceRunning(getApplication(), ServicioNotificacion.class);
            actualizarPreferenciasNotificacion(isActive);
        } catch (Exception io){
            Toast.makeText(getApplicationContext(), "¡Error al actualizar los datos locales!", Toast.LENGTH_LONG).show();
        }
    }

    public void detenerServicioPersistente(){
        Intent notificationIntent = new Intent(getApplication(), ServicioNotificacion.class);
        getApplication().stopService(notificationIntent);
        isActive = Utilidades.isMyServiceRunning(getApplication(), ServicioNotificacion.class);
        actualizarPreferenciasNotificacion(isActive);
    }

    private void actualizarPreferenciasNotificacion(boolean nuevoValor){
        SharedPreferences preferences = getApplication().getSharedPreferences("NotificacionPersistente", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("notificacionActiva", nuevoValor);
        editor.commit();
    }

    private void permisoAlmacWrite(){
        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            btnAlmacWrite.setBackgroundTintList(ContextCompat.getColorStateList(ConfiguracionActivity.this, R.color.colorVerdeGob));
        } else {
            btnAlmacWrite.setBackgroundTintList(ContextCompat.getColorStateList(ConfiguracionActivity.this, R.color.colorRojoClaro));
        }
    }

    private void permisoCam(){
        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            btnCam.setBackgroundTintList(ContextCompat.getColorStateList(ConfiguracionActivity.this, R.color.colorVerdeGob));
        } else {
            btnCam.setBackgroundTintList(ContextCompat.getColorStateList(ConfiguracionActivity.this, R.color.colorRojoClaro));
        }
    }

    private void permisoMicrof(){
        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED){
            btnMicrof.setBackgroundTintList(ContextCompat.getColorStateList(ConfiguracionActivity.this, R.color.colorVerdeGob));
        } else {
            btnMicrof.setBackgroundTintList(ContextCompat.getColorStateList(ConfiguracionActivity.this, R.color.colorRojoClaro));
        }
    }

    private void permisoUbic(){
        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            btnUbic.setBackgroundTintList(ContextCompat.getColorStateList(ConfiguracionActivity.this, R.color.colorVerdeGob));
        } else {
            btnUbic.setBackgroundTintList(ContextCompat.getColorStateList(ConfiguracionActivity.this, R.color.colorRojoClaro));
        }
    }

    public void permisoEncima(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            btnEncima.setBackgroundTintList(ContextCompat.getColorStateList(ConfiguracionActivity.this, R.color.colorVerdeGob));
        } else {
            if(!Settings.canDrawOverlays(this))
                btnEncima.setBackgroundTintList(ContextCompat.getColorStateList(ConfiguracionActivity.this, R.color.colorRojoClaro));
            else
                btnEncima.setBackgroundTintList(ContextCompat.getColorStateList(ConfiguracionActivity.this, R.color.colorVerdeGob));
        }
    }


    public void activarPermisoAlmacWrite(View view){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constantes.MY_PERMISSIONS_REQUEST_ALMAC_WRITE);
    }

    public void activarPermisoCam(View view){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, Constantes.MY_PERMISSIONS_REQUEST_CAMERA);
    }

    public void activarPermisoMicrof(View view){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, Constantes.MY_PERMISSIONS_REQUEST_MICROF);
    }

    public void activarPermisoUbic(View view){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constantes.MY_PERMISSIONS_REQUEST_UBICAC);
    }

    public void aparecerEncima(View view){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(!Settings.canDrawOverlays(this))
                HiddenCameraUtils.openDrawOverPermissionSetting(getApplicationContext());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constantes.MY_PERMISSIONS_REQUEST_ALMAC_WRITE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permisoAlmacWrite();
                } else {
                    Toast.makeText(getApplicationContext(), "¡Permiso de escritura denegado!", Toast.LENGTH_LONG).show();
                }
                return;
            }
            case Constantes.MY_PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permisoCam();
                } else {
                    Toast.makeText(getApplicationContext(), "¡Permiso de cámara denegado!", Toast.LENGTH_LONG).show();
                }
                return;
            }
            case Constantes.MY_PERMISSIONS_REQUEST_MICROF: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permisoMicrof();
                } else {
                    Toast.makeText(getApplicationContext(), "¡Permiso de micrófono denegado!", Toast.LENGTH_LONG).show();
                }
                return;
            }
            case Constantes.MY_PERMISSIONS_REQUEST_UBICAC: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permisoUbic();
                } else {
                    Toast.makeText(getApplicationContext(), "¡Permiso de ubicación denegado!", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        permisoEncima();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Reiniciar servicio unicamente para API menor a OREO
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O
                && !Utilidades.isMyServiceRunning(getApplication(), ServicioNotificacion.class)
                && conf_nueva){
            Intent notificationIntent = new Intent(getApplication(), ServicioNotificacion.class);
            notificationIntent.putExtra("padre", "App");
            getApplication().startService(notificationIntent);
        }
    }
}