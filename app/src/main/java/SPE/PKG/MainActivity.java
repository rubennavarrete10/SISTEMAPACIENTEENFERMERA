package SPE.PKG;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.widget.Button;
import android.widget.TextView;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity<ca> extends AppCompatActivity implements Response.ErrorListener, Response.Listener<JSONObject>, login.Datoslogin {

    RequestQueue request1;
    JSONArray consulta;
    JsonObjectRequest jsonrequest;

    TextView fecha, hora;
    SimpleDateFormat horaFormat = new SimpleDateFormat("HH:mm:ss");
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    Date horaD, horaD1, horaD0, date;
    String D0, D1, horafinal, turno, fechafinal, AE, ip="192.168.0.16", DISPOSITIVO = "N/A";
    String habitacion = "N/A";
    String TiempoRES = "SIN RESPUESTA";
    String enfermera = "N/A";
    long numEvento = 1;
    long idEorA, difh, difm, difs = 0;
    private String outputFile = null;
    MediaRecorder miGrabacion = null;
    private boolean presionado = false;


    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        Button ASISTENCIA = (Button) findViewById(R.id.ASISTENCIA);
        Button ASISTENCIAHECHA = (Button) findViewById(R.id.ASISTENCIAHECHA);
        Button EMERGENCIA = (Button) findViewById(R.id.EMERGENCIA);
        Button MOD = (Button) findViewById(R.id.configuracion);

        fecha = (TextView) findViewById(R.id.fecha);
        hora = (TextView) findViewById(R.id.hora);
        request1 = Volley.newRequestQueue(this);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 1000);
        }
        leerdisp();

        AE = "";
        EMERGENCIA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idEorA = 1;
                date = new Date();
                fechafinal = dateFormat.format(date);
                turno();
                horaD0 = new Date();
                sendHTTPRequestE();
                fecha.setText("EMERGENCIA\nFECHA: " + fechafinal + "\nHORA: " + horafinal + "\nHABITACION: " + habitacion + "\nTURNO: " + turno + "\nFOLIO: " + numEvento + "\nENFEREMERA:" + enfermera + "\nDISPOSITIVO:" + DISPOSITIVO);
                numEvento = numEvento + 1;
                AE = "EMERGENCIA";
            }
        });
        ASISTENCIA.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (!presionado) {
                            presionado = true;
                            date = new Date();
                            fechafinal = dateFormat.format(date);
                            turno();
                            horaD0 = new Date();
                            //reco();
                            fecha.setText("ASISTENCIA\nFECHA: " + fechafinal + "\nHORA: " + horafinal + "\nHABITACION: " + habitacion + "\nTURNO: " + turno + "\nFOLIO: " + numEvento + "\nENFEREMERA:" + enfermera + "\nDISPOSITIVO:" + DISPOSITIVO);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        presionado = false;
                        //play();
                        AE = "ASISTENCIA";
                        sendHTTPRequest();
                        numEvento = numEvento + 1;
                        Toast.makeText(getApplicationContext(), "DEJO DE GRABAR", Toast.LENGTH_LONG).show();
                        break;
                }
                return true;
            }
        });
        ASISTENCIAHECHA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TasisEnf();
                fecha.setText("");
                if (AE.compareTo("EMERGENCIA") == 0) {
                    updateHTTPRequestE();
                }
                if (AE.compareTo("ASISTENCIA") == 0) {
                    updateHTTPRequest();
                }
                idEorA = 0;
                TiempoRES = "SIN RESPUESTA";
            }
        });
        MOD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }
    ///////////////////////////////////////////////////////grabar audio///////////////////////////////////////////
    public void reco() {
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Fonts/" + numEvento + "Grabacion.mp3";
        miGrabacion = new MediaRecorder();
        miGrabacion.setAudioSource(MediaRecorder.AudioSource.MIC);
        miGrabacion.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        miGrabacion.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        miGrabacion.setOutputFile(outputFile);
        try {
            miGrabacion.prepare();
            miGrabacion.start();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            alertaRECO();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            alertaRECO2();
        }
        Toast.makeText(getApplicationContext(), "GRABANDO", Toast.LENGTH_LONG).show();
    }

    //////////////////////////////////////////////reproducir audio///////////////////////////////////////////////////////
    public void play() {
        if (miGrabacion != null) {
            miGrabacion.stop();
            miGrabacion.release();
            miGrabacion = null;
            Toast.makeText(getApplicationContext(), "AUDIO GRABADO", Toast.LENGTH_LONG).show();
        }
        MediaPlayer m = new MediaPlayer();
        try {
            m.setDataSource(outputFile);
        } catch (IOException e) {
            alertaPLAY();
        }
        try {
            m.prepare();
        } catch (IOException e) {
            alertaPLAY2();
        }
        m.start();
        Toast.makeText(getApplicationContext(), "REPRODUCCION EVENTO", Toast.LENGTH_LONG).show();
    }

    //////////////////////////////////////////////////////identificar turno////////////////////////////////////
    public boolean turno() {
        try {
            horaD = new Date();
            horafinal = horaFormat.format(horaD);
            String hora1 = "00:00:00";
            String hora2 = "07:59:59";
            String hora3 = "08:00:00";
            String hora4 = "15:59:59";
            String hora5 = "16:00:00";
            String hora6 = "23:59:59";
            Date date1, date2, date3, date4, date5, date6;
            date1 = horaFormat.parse(hora1);
            date2 = horaFormat.parse(hora2);
            date3 = horaFormat.parse(hora3);
            date4 = horaFormat.parse(hora4);
            date5 = horaFormat.parse(hora5);
            date6 = horaFormat.parse(hora6);
            horaD = horaFormat.parse(horafinal);
            if ((date1.compareTo(horaD) <= 0) && (date2.compareTo(horaD) >= 0)) {
                turno = "NOCHE";
            }
            if ((date3.compareTo(horaD) <= 0) && (date4.compareTo(horaD) >= 0)) {
                turno = "MANANA";
            }
            if ((date5.compareTo(horaD) <= 0) && (date6.compareTo(horaD) >= 0)) {
                turno = "TARDE";
            }
        } catch (ParseException e) {
            alertaturno();
        }
        return false;
    }

    ///////////////////////////////////////////////tiempo de respuesta////////////////////////////////////////////////////////
    public void TasisEnf() {
        try {
            horaD1 = new Date();
            difs = Math.abs(horaD0.getTime() - horaD1.getTime());
            difh = difs / (60 * 60 * 1000);
            difs = difs % (60 * 60 * 1000);
            difm = difs / (60 * 1000);
            difs = difs % (60 * 1000);
            difs = difs / 1000;
            D0 = String.valueOf(horaD0);
            D1 = String.valueOf(horaD1);
            TiempoRES = (String.valueOf(difh) + "HORAS " + String.valueOf(difm) + "MINUTOS " + String.valueOf(difs) + "SEGUNDOS " + String.valueOf(D0) + "   " + String.valueOf(D1) + ".");
            Toast.makeText(getApplicationContext(), TiempoRES, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            alertaTasis();
        }
    }

    ////////////////////////////////////////////WEB SERVICES ESCRIBIR EN BASE DE DATOS/////////////////
    public void sendHTTPRequest() {
        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        String url1 = "http://192.168.0.16/BDEJEMPLOS/REGISTROEVENTOS.php?FOLIODISPOSITIVO=" + numEvento + "&TIPODELLAMADO=ASISTENCIA&FECHA=" + fechafinal + "&HORA=" + horafinal + "&TURNO=" + turno + "&HABITACION=" + habitacion + "&ENFERMERA=" + enfermera + "&TIEMPORESPUESTA=" + TiempoRES;
        url1 = url1.replace(" ", "%20");
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "REGISTRO EXITOSO ASISTENCIA", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hora.setText(error.toString());
                Toast.makeText(getApplicationContext(), "NO SE REGISTRO  ASISTENCIA", Toast.LENGTH_SHORT).show();
            }
        }) {
        };
        MyRequestQueue.add(MyStringRequest);
    }

    /////////////////////////////////WEB SERVICE MODIFICAR TIEMPORESPUESTA////////////////////////
    public void updateHTTPRequest() {
        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        String url1 = "http://192.168.0.16/BDEJEMPLOS/UPDATETIEMPOASISTENCIA.php?FOLIODISPOSITIVO=" + (numEvento - 1) + "&TIPODELLAMADO=ASISTENCIA&FECHA=" + fechafinal + "&HORA=" + horafinal + "&HABITACION=" + habitacion + "&TIEMPORESPUESTA=" + TiempoRES;
        url1 = url1.replace(" ", "%20");
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                Toast.makeText(getApplicationContext(), "TIEMPO DE RESPUESTA ACTUALIZADO", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                hora.setText(error.toString());
                Toast.makeText(getApplicationContext(), "NO SE ACTUALIZO TIEMPO DE RESPUESTA", Toast.LENGTH_SHORT).show();
            }
        }) {
        };
        MyRequestQueue.add(MyStringRequest);
    }
    public void sendHTTPRequestE() {
        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        String url1 = "http://192.168.0.16/BDEJEMPLOS/REGISTROEVENTOS.php?FOLIODISPOSITIVO=" + numEvento + "&TIPODELLAMADO=EMERGENCIA&FECHA=" + fechafinal + "&HORA=" + horafinal + "&TURNO=" + turno + "&HABITACION=" + habitacion + "&ENFERMERA=" + enfermera + "&TIEMPORESPUESTA=" + TiempoRES;
        url1 = url1.replace(" ", "%20");
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                Toast.makeText(getApplicationContext(), "REGISTRO EXITOSO EMERGENCIA", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                hora.setText(error.toString());
                Toast.makeText(getApplicationContext(), "NO SE REGISTRO  EMERGENCIA", Toast.LENGTH_SHORT).show();
            }
        }) {
        };
        MyRequestQueue.add(MyStringRequest);
    }
    public void updateHTTPRequestE() {
        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        String url1 = "http://192.168.0.16/BDEJEMPLOS/UPDATETIEMPOASISTENCIAE.php?FOLIODISPOSITIVO=" + (numEvento - 1) + "&TIPODELLAMADO=EMERGENCIA&FECHA=" + fechafinal + "&HORA=" + horafinal + "&HABITACION=" + habitacion + "&TIEMPORESPUESTA=" + TiempoRES;
        url1 = url1.replace(" ", "%20");
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                Toast.makeText(getApplicationContext(), "TIEMPO DE RESPUESTA ACTUALIZADO", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                hora.setText(error.toString());
                Toast.makeText(getApplicationContext(), "NO SE ACTUALIZO TIEMPO DE RESPUESTA", Toast.LENGTH_SHORT).show();
            }
        }) {
        };
        MyRequestQueue.add(MyStringRequest);
    }
    public void alertaturno() {
        AlertDialog.Builder noeventos = new AlertDialog.Builder(this);
        noeventos.setTitle("ERROR!");
        noeventos.setMessage("NO SE PUDO OBTENER TURNO");
        final AlertDialog noeventosB = noeventos.create();
        noeventosB.setCanceledOnTouchOutside(true);
        noeventosB.show();

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (noeventosB.isShowing()) {
                    noeventosB.dismiss();
                }
            }
        };
        noeventosB.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
            }
        });
        handler.postDelayed(runnable, 3000);
    }
    public void alertaTasis() {
        AlertDialog.Builder noeventos = new AlertDialog.Builder(this);
        noeventos.setTitle("ERROR!");
        noeventos.setMessage("NO SE PUEDO OBTENER TIEMPO DE RESPUESTA EN LA ASISTENCIA");
        final AlertDialog noeventosB = noeventos.create();
        noeventosB.setCanceledOnTouchOutside(true);
        noeventosB.show();

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (noeventosB.isShowing()) {
                    noeventosB.dismiss();
                }
            }
        };
        noeventosB.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
            }
        });
        handler.postDelayed(runnable, 3000);
    }
    public void alertaRECO() {
        AlertDialog.Builder noeventos = new AlertDialog.Builder(this);
        noeventos.setTitle("ERROR!");
        noeventos.setMessage("ERROR AL GRABAR AUDIO");
        final AlertDialog noeventosB = noeventos.create();
        noeventosB.setCanceledOnTouchOutside(true);
        noeventosB.show();

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (noeventosB.isShowing()) {
                    noeventosB.dismiss();
                }
            }
        };
        noeventosB.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
            }
        });
        handler.postDelayed(runnable, 3000);
    }
    public void alertaRECO2() {
        AlertDialog.Builder noeventos = new AlertDialog.Builder(this);
        noeventos.setTitle("ERROR!");
        noeventos.setMessage("ERROR AL GRABAR AUDIO 2");
        final AlertDialog noeventosB = noeventos.create();
        noeventosB.setCanceledOnTouchOutside(true);
        noeventosB.show();

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (noeventosB.isShowing()) {
                    noeventosB.dismiss();
                }
            }
        };
        noeventosB.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
            }
        });
        handler.postDelayed(runnable, 3000);
    }
    public void alertaPLAY() {
        AlertDialog.Builder noeventos = new AlertDialog.Builder(this);
        noeventos.setTitle("ERROR!");
        noeventos.setMessage("ERROR AL OBTENER EL ARCHIVO DE AUDIO");
        final AlertDialog noeventosB = noeventos.create();
        noeventosB.setCanceledOnTouchOutside(true);
        noeventosB.show();

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (noeventosB.isShowing()) {
                    noeventosB.dismiss();
                }
            }
        };
        noeventosB.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
            }
        });
        handler.postDelayed(runnable, 3000);
    }
    public void alertaPLAY2() {
        AlertDialog.Builder noeventos = new AlertDialog.Builder(this);
        noeventos.setTitle("ERROR!");
        noeventos.setMessage("ERROR AL PROCESAR ARCHIVO DE AUDIO");
        final AlertDialog noeventosB = noeventos.create();
        noeventosB.setCanceledOnTouchOutside(true);
        noeventosB.show();

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (noeventosB.isShowing()) {
                    noeventosB.dismiss();
                }
            }
        };
        noeventosB.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
            }
        });
        handler.postDelayed(runnable, 3000);
    }




    public void login() {
        login login = new login();
        login.show(getSupportFragmentManager(), "INICIO SESION");
    }
    @Override
    public void applyTexts(String usuario, String contraseña) {
        if (usuario.equals("USER") == true && contraseña.equals("1234") == true) {
            Toast.makeText(getApplicationContext(), "SESION INICIADA", Toast.LENGTH_SHORT).show();
            Intent ListFruta = new Intent(getApplicationContext(), modificardispositivo.class);
            startActivity(ListFruta);
        } else {
            Toast.makeText(getApplicationContext(), "USUARIO O CONTRASEÑA INCORRECTA", Toast.LENGTH_SHORT).show();
        }
    }
    public void leerdisp() {
        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        String url1 = "http://"+ip+"/BDEJEMPLOS/leerdispositivo.php";
        jsonrequest = new JsonObjectRequest(Request.Method.POST, url1, null, this, this);////////////////////////////////////////////////////////////json webservices/////////////////
        request1.add(jsonrequest);
    }
    @Override
    public void onErrorResponse(VolleyError error) {

    }
    @Override
    public void onResponse(JSONObject response) {
        consulta = response.optJSONArray("usuario");
        try {
            for (int i = 0; i < consulta.length(); i++) {
                Usuarios consultaUsuario;
                consultaUsuario = new Usuarios();
                JSONObject jsonconsulta = null;
                jsonconsulta = consulta.getJSONObject(i);
                consultaUsuario.setIpbd(jsonconsulta.optString("IP"));
                consultaUsuario.setdisbd(jsonconsulta.optString("NODISPOSITIVO"));
                consultaUsuario.sethbbd(jsonconsulta.optString("HABITACION"));

                ip = consultaUsuario.getIpbd();
                habitacion = consultaUsuario.gethbbd();
                DISPOSITIVO = consultaUsuario.getdisbd();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
