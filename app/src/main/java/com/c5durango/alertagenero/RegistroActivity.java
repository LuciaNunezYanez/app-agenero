package com.c5durango.alertagenero;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidhiddencamera.HiddenCameraUtils;
import com.c5durango.alertagenero.Utilidades.Utilidades;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RegistroActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    // Variables principales
    private Boolean tieneAcceso = false;

    // Components Layout Personales
    LinearLayout linearPersonales;
    EditText txtCorreo, txtNombres, txtPaterno, txtMaterno, dateNacimiento, areaPadecimientos, txtTelefonoM, areaAlergias;
    TextView lblNuevaContrasena;
    Spinner spSexo, spSangre;
    Button btnSiguiente, btnFechaNac;
    TextView lblPersonales;

    // Components Layout Domicilio
    LinearLayout linearDomicilio;
    EditText txtCalle, txtNumeroExt, txtColonia, txtCodigoP, txtCalle1, txtCalle2, areaReferencia, txtContrasena;
    static Spinner spEstado, spMunicipio, spLocalidad;
    Button btnFinalizar, btnRegresar;
    int id_direccion = 0;

    String stCorreo = "", stNombres = "", stPaterno = "", stMaterno = "", stNacimiento = "", stPadecimientos = "", stTelefonoM = "", stAlergias = "", stSexo = "", stSangre = "", stContrasena = "";
    String stCalle = "", stNumeroExt = "", stColonia = "", stCalle1 = "", stCalle2 = "", stReferencia = "", stEstado = "", stMunicipio = "", stLocalidad = "";
    int codigoP = 0;

    String sexo [] = { "Seleccionar", "Femenino", "Masculino", "Desconocido" };
    String sangre [] = { "Seleccionar", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-", "No sé" };
    String estado [] = { "Durango" };

    // Apoyo a Spinners Dirección
    static int id_loc_select = 0;
    static List<String> misIDsMunicipios = new ArrayList<>();
    static List<String> misMunicipios = new ArrayList<>();
    static List<String> misIDsLocalidades = new ArrayList<>();
    static List<String> misLocalidades = new ArrayList<>();

    static String TAG = "RegistroActivity";
    static Boolean editar = false;

    // Variables para edicion
    int id_usuario = 0;
    static String nombre_municipio = "";
    static String nombre_localidad = "";
    static String accion = "";
    Boolean actUsuario, actDir;

    // GET Usuario completo
    String nombre_grupo = "";
    int id_grupo = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        linearPersonales = findViewById(R.id.linearPersonales);
        linearDomicilio = findViewById(R.id.linearDomiclio);

        // Componentes
        lblPersonales = findViewById(R.id.lblPersonales);
        txtCorreo = findViewById(R.id.txtCorreo);
        txtNombres = findViewById(R.id.txtNombres);
        txtPaterno = findViewById(R.id.txtPaterno);
        txtMaterno = findViewById(R.id.txtMaterno);
        dateNacimiento = findViewById(R.id.dateNacimiento);
        areaPadecimientos = findViewById(R.id.areaPadecimientos);
        txtTelefonoM = findViewById(R.id.txtTelefono);
        areaAlergias = findViewById(R.id.areaAlergias);
        spSexo = findViewById(R.id.spinnerSexo);
        spSexo.setAdapter( new ArrayAdapter<>(getApplicationContext(), R.layout.custom_spinner_item, sexo));
        spSangre = findViewById(R.id.spinnerTipoSangre);
        spSangre.setAdapter( new ArrayAdapter<>(getApplicationContext(), R.layout.custom_spinner_item, sangre));

        lblNuevaContrasena = findViewById(R.id.lblNuevaContrasena);
        txtContrasena = findViewById(R.id.txtContrasenaReg);

        txtCalle = findViewById(R.id.txtCalle);
        txtNumeroExt = findViewById(R.id.txtNumeroExt);
        txtColonia = findViewById(R.id.txtColonia);
        txtCodigoP = findViewById(R.id.txtCodigoP);
        txtCalle1 = findViewById(R.id.txtCalle1);
        txtCalle2 = findViewById(R.id.txtCalle2);
        areaReferencia = findViewById(R.id.areaReferencia);
        spEstado = findViewById(R.id.spinnerEstado);
        spEstado.setAdapter( new ArrayAdapter<>(getApplicationContext(), R.layout.custom_spinner_item, estado));
        spMunicipio = findViewById(R.id.spinnerMunicipio);
        spMunicipio.setAdapter( new ArrayAdapter<>(getApplicationContext(), R.layout.custom_spinner_item, misMunicipios));
        spLocalidad = findViewById(R.id.spinnerLocalidad);
        spLocalidad.setAdapter( new ArrayAdapter<>(getApplicationContext(), R.layout.custom_spinner_item, misLocalidades));

        // Botones
        btnSiguiente = findViewById(R.id.btnSiguiente);
        btnFinalizar = findViewById(R.id.btnFinalizar);
        btnRegresar = findViewById(R.id.btnRegresar);
        btnFechaNac = findViewById(R.id.btnFechaNac);

        btnSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    stCorreo = txtCorreo.getText().toString();
                    stNombres = txtNombres.getText().toString();
                    stPaterno = txtPaterno.getText().toString();
                    stMaterno = txtMaterno.getText().toString();
                    stNacimiento = dateNacimiento.getText().toString();
                    stPadecimientos = areaPadecimientos.getText().toString();
                    stTelefonoM = txtTelefonoM.getText().toString();
                    stAlergias = areaAlergias.getText().toString();
                    String s = spSexo.getSelectedItem().toString();
                    if(s.equals("Femenino"))
                        stSexo = "F";
                    if(s.equals("Masculino"))
                        stSexo = "M";
                    if(s.equals("Desconocido"))
                        stSexo = "D";
                    if(s.equals("Seleccionar"))
                        stSexo = "";

                    stSangre = spSangre.getSelectedItem().toString();
                    if(accion.equals("nuevo") || accion.equals("edicion"))
                        stContrasena = txtContrasena.getText().toString();

                    if(stSangre.equals("Seleccionar"))
                        stSangre = "";

                    if(!Utilidades.validEmail(stCorreo)) {
                        Toast.makeText(getApplicationContext(), "Correo electrónico inválido", Toast.LENGTH_LONG).show();
                    } else if(stContrasena.length() < 8){
                        Toast.makeText(getApplicationContext(), "¡Introducir contraseña con al menos 8 caractéres!", Toast.LENGTH_LONG).show();
                    } else if( stNombres.length() < 3 || stPaterno.length() < 3) {
                        Toast.makeText(getApplicationContext(), "Nombre(s) o apellido paterno inválido", Toast.LENGTH_LONG).show();
                    } else if(stSexo.equals("")) {
                        Toast.makeText(getApplicationContext(), "Seleccione sexo por favor", Toast.LENGTH_LONG).show();
                    } else if(stTelefonoM.length() != 10 || !Utilidades.validPhone(stTelefonoM)) {
                        try {
                            int tel = Integer.parseInt(stTelefonoM);
                            Toast.makeText(getApplicationContext(), "Teléfono inválido, solo 10 digitos", Toast.LENGTH_LONG).show();
                        } catch (Exception e){
                            Toast.makeText(getApplicationContext(), "¡El teléfono debe contener solo números!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        if(editar){
                            actualizarUsuario(id_usuario);
                        } else {
                            linearPersonales.setVisibility(View.GONE);
                            linearDomicilio.setVisibility(View.VISIBLE);
                        }
                    }}
                catch(Exception e){
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearPersonales.setVisibility(View.VISIBLE);
                linearDomicilio.setVisibility(View.GONE);
            }
        });

        btnFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    stCalle = txtCalle.getText().toString();
                    stNumeroExt = txtNumeroExt.getText().toString();
                    stColonia = txtColonia.getText().toString();
                    String cp = txtCodigoP.getText().toString();

                    stCalle1 = txtCalle1.getText().toString();
                    stCalle2 = txtCalle2.getText().toString();
                    stReferencia = areaReferencia.getText().toString();

                    if(stCalle.length() < 3){
                        Toast.makeText(getApplicationContext(), "¡La calle es incorrecta!", Toast.LENGTH_LONG).show();
                    } else if( stNumeroExt.length() == 0){
                        Toast.makeText(getApplicationContext(), "¡El número exterior es incorrecto!", Toast.LENGTH_LONG).show();
                    } else if( stColonia.length() < 3){
                        Toast.makeText(getApplicationContext(), "¡La colonia es incorrecta!", Toast.LENGTH_LONG).show();
                    } else if( cp.length() > 0 && cp.length() < 5){
                        Toast.makeText(getApplicationContext(), "¡El código postal debe ser de 5 dígitos!", Toast.LENGTH_LONG).show();
                    } else if( spMunicipio.getSelectedItem() == null || spLocalidad.getSelectedItem() == null){
                        Toast.makeText(getApplicationContext(), "¡Municipio y/o localidad inválida!", Toast.LENGTH_LONG).show();
                    } else if( stReferencia.length() < 10 || areaReferencia.getText() == null || areaReferencia.getText().toString().length() < 10){
                        Toast.makeText(getApplicationContext(), "¡La eferencia es muy corta, por favor sea mas especifico!", Toast.LENGTH_LONG).show();
                    } else {
                        if(cp.length() == 5 || cp.length() == 0){
                            try {
                                if(cp.length() > 0)
                                    codigoP = Integer.parseInt(cp);
                                stEstado = spEstado.getSelectedItem().toString();
                                stMunicipio = spMunicipio.getSelectedItem().toString();
                                stLocalidad = spLocalidad.getSelectedItem().toString();
                                // Registrar usuario
                                if (!editar)
                                    crearUsuario();
                                else {
                                    actualizarDireccion(id_direccion);
                                }

                            } catch (Exception e){
                                Toast.makeText(getApplicationContext(), "¡El código postal debe tener solo números!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }

            }
        });

        btnFechaNac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarDatePicker();
            }
        });

        spMunicipio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?>arg0, View view, int arg2, long arg3) {
                misLocalidades.clear();
                misIDsLocalidades.clear();
                spLocalidad.setAdapter( new ArrayAdapter<>(getApplicationContext(), R.layout.custom_spinner_item, misLocalidades));
                getLocalidades(getApplicationContext(), Integer.parseInt(misIDsMunicipios.get(arg2)));

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                id_loc_select = 0;
            }
        });


        spLocalidad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                id_loc_select = Integer.parseInt(misIDsLocalidades.get(i));
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                id_loc_select = 0;
            }
        });

        // Para editar la información
        if(getIntent().getExtras() != null){
            if(getIntent().getExtras().containsKey("id")) {
                id_usuario = getIntent().getExtras().getInt("id");
                if (id_usuario > 0){
                    editar = true;
                    btnSiguiente.setText("Actualizar mis datos");
                    btnFinalizar.setText("Actualizar mi dirección");
                    btnRegresar.setVisibility(View.GONE);
                    lblPersonales.setText("ACTUALIZA TUS DATOS\nPERSONALES");
                    txtCorreo.setText(getIntent().getExtras().getString("correo"));
                    txtCorreo.setEnabled(false);
                    txtCorreo.setFocusable(false);

                    // Obtener los datos del usuario
                    getUsuario(id_usuario);
                } else {
                    editar = false;
                }
            }
            if (getIntent().getExtras().containsKey("accion")){
                accion = getIntent().getExtras().getString("accion");
                if (accion.equals("recuperacion")){
                    stContrasena = getIntent().getExtras().getString("contrasena");
                    txtContrasena.setVisibility(View.GONE);
                    lblNuevaContrasena.setVisibility(View.GONE);

                }
                if(accion.equals("login")){
                    txtContrasena.setText(getIntent().getExtras().getString("contrasena"));
                    stContrasena = txtContrasena.getText().toString();
                }
                if(accion.equals("edicion")){
                    // Tomar contraseña de las preferencias
                    // Guardar contraseña en las preferencias
                }
            }
        }
        getMunicipios(getApplicationContext(), 10); // 10: Durango
    }

    public void getUsuario(int id_usuario) { // GET http://localhost:8888/usuariocomercio/completo/104
        StringRequest requestGetUsuario;
        String URL = Constantes.URL + "/usuariocomercio/completo/" + id_usuario;

        final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        requestGetUsuario = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject json = new JSONObject(response);
                    Log.d(TAG, response.toString());
                    Boolean OK = json.getBoolean("ok");
                    if(OK){
                        JSONObject json_u = json.getJSONObject("resp");
                        txtNombres.setText(json_u.getString("nombres_usuarios_app"));
                        txtPaterno.setText(json_u.getString("apell_pat"));
                        txtMaterno.setText(json_u.getString("apell_mat"));
                        String ff[] = json_u.getString("fecha_nacimiento").substring(0, 10).split("-");
                        dateNacimiento.setText(ff[2]+"/"+ff[1]+"/"+ff[0]);
                        String s = "";
                        if(json_u.getString("sexo_app").equals("F")) s = "Femenino";
                        if(json_u.getString("sexo_app").equals("M")) s = "Masculino";
                        if(json_u.getString("sexo_app").equals("D")) s = "Desconocido";
                        if(json_u.getString("sexo_app").equals("")) s = "Seleccionar";
                        spSexo.setSelection(Utilidades.obtenerPosicionItem(spSexo, s));
                        areaPadecimientos.setText(json_u.getString("padecimientos"));
                        txtTelefonoM.setText(json_u.getString("tel_movil"));
                        areaAlergias.setText(json_u.getString("alergias"));
                        spSangre.setSelection(Utilidades.obtenerPosicionItem(spSangre, json_u.getString("tipo_sangre")));

                        id_direccion = json_u.getInt("id_direccion");
                        txtCalle.setText(json_u.getString("calle"));
                        txtNumeroExt.setText(json_u.getString("numero"));
                        txtColonia.setText(json_u.getString("colonia"));
                        txtCodigoP.setText(json_u.getString("cp"));
                        nombre_municipio = json_u.getString("nombre_municipio");
                        nombre_localidad = json_u.getString("nombre_localidad");
                        txtCalle1.setText(json_u.getString("entre_calle_1"));
                        txtCalle2.setText(json_u.getString("entre_calle_2"));
                        areaReferencia.setText(json_u.getString("fachada"));

                        nombre_grupo = json_u.getString("nombre_comercio");
                        id_grupo = json_u.getInt("id_comercio");

                    }
                } catch (Exception e){

                }
                requestQueue.stop();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),  Utilidades.tipoErrorVolley(error), Toast.LENGTH_SHORT).show();
                requestQueue.stop();
            }
        });
        requestQueue.add(requestGetUsuario);
    }


    public void actualizarDireccion(final int idDireccion){
        final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String URL = Constantes.URL + "/direccion/" + idDireccion;

        JSONObject jsonObjectBody = new JSONObject();
        try {
            jsonObjectBody.put("calle", stCalle);
            jsonObjectBody.put("numero", stNumeroExt);
            jsonObjectBody.put("colonia", stColonia);
            jsonObjectBody.put("cp", codigoP);
            jsonObjectBody.put("entre1", stCalle1);
            jsonObjectBody.put("entre2", stCalle2);
            jsonObjectBody.put("referencia", stReferencia);
            jsonObjectBody.put("idLocalidad", id_loc_select);
            jsonObjectBody.put("lat", 0);
            jsonObjectBody.put("lg", 0);
        } catch (JSONException e) {
            e.printStackTrace();
            // Log.e(TAG, e.toString());
        }

        final String requestBody = jsonObjectBody.toString();
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject object) {
                        if(object == null){
                            Toast.makeText(getApplicationContext(), "¡Respuesta vacia al actualizar dirección!", Toast.LENGTH_LONG).show();
                            return;
                        }
                        try {
                            Toast.makeText(getApplicationContext(), object.getString("resp"), Toast.LENGTH_LONG).show();
                            if (object.getBoolean("ok")) {
                                Boolean res = com.c5durango.alertagenero.Utilidades.PreferencesComercio.actualizarDireccion(getApplicationContext(),
                                        stCalle, stNumeroExt, stColonia, codigoP, stCalle1, stCalle2, stReferencia, id_loc_select, nombre_localidad, nombre_municipio, stEstado, idDireccion);
                                actDir = res ? true : false;
                                if(!res)
                                    Toast.makeText(getApplicationContext(), "¡Ocurrió un error al actualizar los datos locales de la dirección!", Toast.LENGTH_LONG).show();
                                else {
                                    // Guardar datos de login y grupo/sala
                                    com.c5durango.alertagenero.Utilidades.PreferencesComercio.actualizarLogin(getApplicationContext(), id_grupo, id_usuario, "Genero", "");
                                    com.c5durango.alertagenero.Utilidades.PreferencesComercio.actualizarSala(getApplicationContext(), id_grupo, 0, nombre_grupo, "", "", "", "");
                                    activarPermisoAlmacWrite();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        requestQueue.stop();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "E#11A " + Utilidades.tipoErrorVolley(error), Toast.LENGTH_SHORT).show();
                        requestQueue.stop();
                    }
                }
        ) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Codificación no compatible al intentar obtener los bytes de% s usando %s", requestBody, "utf-8");
                    return null;
                }
            }
        };
        requestQueue.add(getRequest);
    }

    public void actualizarUsuario(final int idusuario){
        final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String URL = Constantes.URL + "/usuariocomercio/" + idusuario;

        JSONObject jsonObjectBody = new JSONObject();
        try {
            jsonObjectBody.put("nombre", stNombres);
            jsonObjectBody.put("paterno", stPaterno);
            jsonObjectBody.put("materno", stMaterno);
            jsonObjectBody.put("nacimiento", stNacimiento);
            jsonObjectBody.put("sexo", stSexo);
            jsonObjectBody.put("padecimientos", stPadecimientos);
            jsonObjectBody.put("telefono", stTelefonoM);
            jsonObjectBody.put("alergias", stAlergias);
            jsonObjectBody.put("sangre", stSangre);
            jsonObjectBody.put("pass", stContrasena);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }

        final String requestBody = jsonObjectBody.toString();
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject object) {
                        Log.d(TAG, "Respuesta:" + object.toString());

                        try {
                            Toast.makeText(getApplicationContext(), object.getString("resp"), Toast.LENGTH_LONG).show();
                            if (object.getBoolean("ok")) {
                                Boolean res = com.c5durango.alertagenero.Utilidades.PreferencesComercio.actualizarUsuario(getApplicationContext(),
                                        stNombres, stPaterno, stMaterno, stNacimiento, stSexo,
                                        stPadecimientos, stTelefonoM, stAlergias, stSangre, stCorreo, idusuario);
                                actUsuario = res ? true : false;
                                if(!res)
                                    Toast.makeText(getApplicationContext(), "¡Ocurrió un error al actualizar los datos locales del usuario! Reintente.", Toast.LENGTH_LONG).show();
                                else {
                                    linearPersonales.setVisibility(View.GONE);
                                    linearDomicilio.setVisibility(View.VISIBLE);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        requestQueue.stop();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "E#11 " + Utilidades.tipoErrorVolley(error), Toast.LENGTH_SHORT).show();
                        requestQueue.stop();
                    }
                }
        ) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Codificación no compatible al intentar obtener los bytes de% s usando %s", requestBody, "utf-8");
                    return null;
                }
            }
        };
        requestQueue.add(getRequest);
    }

    public void crearUsuario(){
        final RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String URL = Constantes.URL + "/registroalertagenero";

        JSONObject jsonObjectBody = new JSONObject();
        try {
            // Enviar todos los datos del usuario y dirección
            jsonObjectBody.put("calle", stCalle);
            jsonObjectBody.put("numero", stNumeroExt);
            jsonObjectBody.put("colonia", stColonia);
            jsonObjectBody.put("cp", codigoP);
            jsonObjectBody.put("entre_calle_1", stCalle1);
            jsonObjectBody.put("entre_calle_2", stCalle2);
            jsonObjectBody.put("fachada", stReferencia);
            jsonObjectBody.put("id_localidad", id_loc_select);
            jsonObjectBody.put("lat_dir", 0);
            jsonObjectBody.put("lgn_dir", 0);

            jsonObjectBody.put("nombres_usuarios_app", stNombres);
            jsonObjectBody.put("apell_pat", stPaterno);
            jsonObjectBody.put("apell_mat", stMaterno);
            jsonObjectBody.put("fecha_nacimiento", stNacimiento);
            jsonObjectBody.put("sexo_app", stSexo);
            jsonObjectBody.put("padecimientos", stPadecimientos);
            jsonObjectBody.put("tel_movil", stTelefonoM);
            jsonObjectBody.put("alergias", stAlergias);
            jsonObjectBody.put("tipo_sangre", stSangre);
            jsonObjectBody.put("estatus_usuario", 1);
            jsonObjectBody.put("correo_usuario", stCorreo);
            jsonObjectBody.put("id_grupo", 2); // 2 = Alerta de genero
            jsonObjectBody.put("contrasena", stContrasena);

            jsonObjectBody.put("id_comercio", 1); // 1 = Comercio con nombre alerta de género

        } catch (JSONException e) {
            e.printStackTrace();
            // Log.e(TAG, e.toString());
        }
        Log.d(TAG, URL);

        final String requestBody = jsonObjectBody.toString();
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject object) {

                        try {
                            Boolean ok = object.getBoolean("ok");
                            Log.d(TAG, "OK: " + ok.toString());
                            if (ok) {
                                if (object.has("resultado")) {
                                    Log.d(TAG, "Resultado = TRUE");
                                    // Obtener cada dato de comercio
                                    JSONObject object_resultado = object.optJSONObject("resultado");


                                    if(object_resultado.has("resultado") || object_resultado == null){
                                        if(object_resultado.getInt("resultado") == 1){
                                            Log.d(TAG, "Resultado válido");
                                            int id_comercio = object_resultado.getInt("idComercio");
                                            int id_usuario = object_resultado.getInt("idUsuario");
                                            int id_direccion = object_resultado.getInt("idDireccion");
                                            Log.d(TAG, id_comercio + "/" + id_usuario + "/" + id_direccion);
                                            Toast.makeText(getApplicationContext(), object_resultado.getString("mensage"), Toast.LENGTH_LONG).show();

                                            // Guardar la informacion modo local
                                            if (id_comercio >= 1) {
                                                Boolean resp = com.c5durango.alertagenero.Utilidades.PreferencesComercio.guardarDatosComercio(getApplicationContext(),
                                                        id_comercio,
                                                        id_direccion,
                                                        0,
                                                        Constantes.NOMBRE_APP,
                                                        "",
                                                        "",
                                                        "",
                                                        "",
                                                        stCalle,
                                                        stNumeroExt,
                                                        stColonia,
                                                        codigoP,
                                                        stCalle1,
                                                        stCalle2,
                                                        stReferencia,
                                                        id_loc_select,
                                                        stLocalidad,
                                                        stMunicipio,
                                                        stEstado,
                                                        id_usuario,
                                                        stNombres,
                                                        stPaterno,
                                                        stMaterno,
                                                        stNacimiento,
                                                        stSexo,
                                                        stPadecimientos,
                                                        stTelefonoM,
                                                        stAlergias,
                                                        stSangre,
                                                        stCorreo,
                                                        "");

                                                if (resp) {
                                                    tieneAcceso = true;
                                                    activarPermisoAlmacWrite();
                                                } else {
                                                    Toast.makeText(getApplicationContext(), "¡Error al guardar la información local!", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Log.d(TAG, "No se pudieron obtener los datos");
                                                Toast.makeText(getApplicationContext(), "Error al obtener la información", Toast.LENGTH_SHORT).show();
                                            }

                                        } else {
                                            Log.d(TAG, "Resultado inválido #1");
                                            Toast.makeText(getApplicationContext(), object_resultado.getString("mensage"), Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        Log.d(TAG, "Resultado cero #2");
                                        Toast.makeText(getApplicationContext(), "Error al procesar su solicitud, reintente más tarde y valide el acceso a internet", Toast.LENGTH_LONG).show();
                                    }

                                } else {
                                    // No viene comercio por que el codigo está mal
                                    Toast.makeText(getApplicationContext(), "Error al procesar solicitud de registro", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Log.d(TAG, "Error al traer los datos" + object.getString("error"));
                                Toast.makeText(getApplicationContext(), "¡Error al procesar registro!\n" + object.getString("error"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        } catch (Exception e ){
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();                        }
                            requestQueue.stop();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "E#11A " + Utilidades.tipoErrorVolley(error), Toast.LENGTH_SHORT).show();
                        requestQueue.stop();
                    }
                }
        ) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Codificación no compatible al intentar obtener los bytes de% s usando %s", requestBody, "utf-8");
                    return null;
                }
            }
        };
        requestQueue.add(getRequest);
    }

    // SPINNERS

    public static void getMunicipios(final Context context, int id_estado){

        try {
            StringRequest requestGetMunicipios;
            final String URL = Constantes.URL + "/municipios/" + id_estado;

            final RequestQueue requestQueue = Volley.newRequestQueue(context);

            requestGetMunicipios = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i(TAG, "La respuesta al obtener " + URL);
                    Log.i(TAG, "La respuesta al obtener los municipios es: " + response);

                    try {
                        JSONObject jsonResponse = new JSONObject(response);

                        JSONArray ids_municipios = jsonResponse.optJSONArray("id_municipios");
                        JSONArray nombre_municipios = jsonResponse.optJSONArray("nombre_municipio");

                        misMunicipios = new ArrayList<>();
                        misIDsMunicipios = new ArrayList<>();

                        for (int i = 0; i < nombre_municipios.length(); i++) {
                            misMunicipios.add(nombre_municipios.getString(i));
                            misIDsMunicipios.add(ids_municipios.getString(i));
                        }
                        spMunicipio.setAdapter(new ArrayAdapter<>(context, R.layout.custom_spinner_item, misMunicipios));
                        if(editar){
                            Log.d(TAG, nombre_municipio);
                            spMunicipio.setSelection(Utilidades.obtenerPosicionItem(spMunicipio, nombre_municipio));
                        } else
                            spMunicipio.setSelection(Utilidades.obtenerPosicionItem(spMunicipio, "Durango"));

                    } catch (JSONException e) {
                        Toast.makeText(context, "¡Error al obtener los municipios!" + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                    requestQueue.stop();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, "E#11 " + Utilidades.tipoErrorVolley(error), Toast.LENGTH_SHORT).show();
                    requestQueue.stop();
                }
            });
            requestQueue.add(requestGetMunicipios);
        } catch (Exception e){
            Toast.makeText(context, "Excepción", Toast.LENGTH_LONG).show();
        }
    }

    public static void getLocalidades(final Context context, int id_municipio){

        StringRequest requestGetLocalidades;
        String URL = Constantes.URL + "/localidades/" + id_municipio;

        final RequestQueue requestQueue = Volley.newRequestQueue(context);

        requestGetLocalidades = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse = new JSONObject(response);

                    JSONArray ids_localidades = jsonResponse.optJSONArray("id_localidades");
                    JSONArray nombre_localidades = jsonResponse.optJSONArray("nombre_localidad");

                    misLocalidades.clear();
                    misIDsLocalidades.clear();

                    for (int i = 0; i < nombre_localidades.length(); i++){
                        misLocalidades.add(nombre_localidades.getString(i));
                        misIDsLocalidades.add(ids_localidades.getString(i));
                    }
                    spLocalidad.setAdapter( new ArrayAdapter<>(context, R.layout.custom_spinner_item, misLocalidades));
                    if(editar){
                        spLocalidad.setSelection(Utilidades.obtenerPosicionItem(spLocalidad, nombre_localidad));
                    } else
                        spLocalidad.setSelection(Utilidades.obtenerPosicionItem(spLocalidad, "Victoria de Durango"));
                }
                catch(JSONException e){
                    Toast.makeText(context, "Error en municipios"+e.toString(), Toast.LENGTH_SHORT).show();
                }
                requestQueue.stop();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "E#12 " + Utilidades.tipoErrorVolley(error), Toast.LENGTH_SHORT).show();
                requestQueue.stop();
            }
        });

        requestQueue.add(requestGetLocalidades);
    }

    public void mostrarDatePicker(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                RegistroActivity.this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    // PERMISOS

    @Override
    public void onDateSet(DatePicker datePicker, int anio, int mes, int dia) {
        dateNacimiento.setText(dia + "/" + (mes +1) + "/" + anio);
    }

    private void iniciarMain(){
        Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
        startActivity(intent);
        RegistroActivity.this.finish();
    }

    private void activarPermisoAlmacWrite(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constantes.MY_PERMISSIONS_REQUEST_ALMAC_WRITE);
    }

    private void activarPermisoCam(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, Constantes.MY_PERMISSIONS_REQUEST_CAMERA);
    }

    private void activarPermisoMicrof(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, Constantes.MY_PERMISSIONS_REQUEST_MICROF);
    }

    private void activarPermisoUbic(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constantes.MY_PERMISSIONS_REQUEST_UBICAC);
    }

    /*private void permisoParaAparecerEncima(){
        iniciarMain();
        // Comprobar permiso para acceder encima..
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(!Settings.canDrawOverlays(this))
                HiddenCameraUtils.openDrawOverPermissionSetting(getApplicationContext());
        }
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constantes.MY_PERMISSIONS_REQUEST_ALMAC_WRITE: {
                activarPermisoCam();
                return;
            }
            case Constantes.MY_PERMISSIONS_REQUEST_CAMERA: {
                activarPermisoMicrof();
                return;
            }
            case Constantes.MY_PERMISSIONS_REQUEST_MICROF: {
                activarPermisoUbic();
                return;
            }
            case Constantes.MY_PERMISSIONS_REQUEST_UBICAC: {
                iniciarMain();
                //permisoParaAparecerEncima();
                return;
            }
        }
    }
}