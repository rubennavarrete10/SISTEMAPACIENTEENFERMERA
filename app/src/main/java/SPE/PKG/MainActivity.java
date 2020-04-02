package SPE.PKG;
import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

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

public class MainActivity extends AppCompatActivity implements Response.ErrorListener, Response.Listener<JSONObject>, login.Datoslogin {

    RequestQueue request1;
    JSONArray consulta;
    JsonObjectRequest jsonrequest;
    TextView fecha, hora, error;
    SimpleDateFormat horaFormat = new SimpleDateFormat("HH:mm:ss");
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    Date horaD, horaD1, horaD0, date;
    Date fechaF0,fechaF1;
    String F0,F1,D0, D1, horafinal = "N/A", turno = "N/A", fechafinal = "N/A", AE = "N/A", ip = "N/A", DISPOSITIVO = "N/A";
    String habitacion = "N/A",TiempoRES = "SIN RESPUESTA", enfermera = "N/A", FOLIODIPOSITIVO = "N/A";
    String PACIENTE = "N/A", MEDICO = "N/A", PAGO = "N/A", ESTACION = "N/A", SECCION = "N/A", AUDIO = "N/A", hs = "N/A";
    int e=0;
    long numEvento = 1,folio;
    long dif=0, difh=0, difm=0, difs = 0;

    private String outputFile = null;
    MediaRecorder miGrabacion = null;
    MediaPlayer m = new MediaPlayer();
    private boolean presionado = false;
    final Handler handler = new Handler();
   /* private BluetoothAdapter BTAdapter;
    private ArrayList<BluetoothDevice> btDeviceArray = new ArrayList<BluetoothDevice>();
    private ArrayAdapter<String> mArrayAdapter;
    private BluetoothSocket btSocket;
    private InputStream btin;
    private OutputStream btout;
    byte[] buffer = new byte[1024];  // buffer (our data)
    int bytesCount; // amount of read bytes
    private Thread CICLO;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");*/

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);

        Button ASISTENCIA = (Button) findViewById(R.id.ASISTENCIA);
        Button ASISTENCIAHECHA = (Button) findViewById(R.id.ASISTENCIAHECHA);
        Button EMERGENCIA = (Button) findViewById(R.id.EMERGENCIA);
        Button MOD = (Button) findViewById(R.id.configuracion);
        fecha = (TextView) findViewById(R.id.fecha);
        error = (TextView) findViewById(R.id.Errores);
        hora = (TextView) findViewById(R.id.hora);
        request1 = Volley.newRequestQueue(this);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 1000);
        }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        /*Button BTH = (Button) findViewById(R.id.BT);
        BTAdapter = BluetoothAdapter.getDefaultAdapter();


        if(BTAdapter == null){
            Toast.makeText(getApplicationContext(), "NO SOPORTA BLUETOOTH", Toast.LENGTH_SHORT).show();
            finish();
        }
        if(!BTAdapter.isEnabled()){
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }
        else
        {
            Toast.makeText(getApplicationContext(), "BLUETOOTH LISTO", Toast.LENGTH_SHORT).show();
        }

        BTH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<BluetoothDevice> pariedDevices = BTAdapter.getBondedDevices();
                if(pariedDevices.size() > 0){
                    for(BluetoothDevice device : pariedDevices){
                        mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                        btDeviceArray.add(device);
                    }
                }

            }
        });*/

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        EMERGENCIA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    habitacion = extras.getString("hb");
                    hs = extras.getString("hs");
                    DISPOSITIVO = extras.getString("dis");
                }
                habitacion="105";
                hs="192.168.0.16";
                DISPOSITIVO="5";
                e=0;
                consultageneral();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms
                        AE = "EMERGENCIA";
                        //idEorA = 1;
                        date = new Date();
                        fechafinal = dateFormat.format(date);
                        turno();
                        horaD0 = new Date();
                        sendHTTPRequest();
                        fecha.setText("\nHABITACION: " + habitacion + "\nPACIENTE=" + PACIENTE + "\nMEDICO=" + MEDICO);
                        numEvento = numEvento + 1;
                        folio=numEvento;
                        e=0;
                    }
                }, 500);

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
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        presionado = false;
                        Bundle extras = getIntent().getExtras();
                        if (extras != null) {
                            habitacion = extras.getString("hb");
                            hs = extras.getString("hs");
                            DISPOSITIVO = extras.getString("dis");
                        }
                        e=0;
                        consultageneral();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Do something after 5s = 5000ms
                                AE = "ASISTENCIA";
                                sendHTTPRequest();
                                fecha.setText("\nHABITACION: " + habitacion + "\nPACIENTE=" + PACIENTE + "\nMEDICO=" + MEDICO);
                                numEvento = numEvento + 1;
                                folio=numEvento;
                                Toast.makeText(getApplicationContext(), "DEJO DE GRABAR", Toast.LENGTH_LONG).show();
                                e=0;
                            }
                        }, 500);

                        break;
                }
                return true;
            }
        });
        ASISTENCIAHECHA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    habitacion = extras.getString("hb");
                    hs = extras.getString("hs");
                    DISPOSITIVO = extras.getString("dis");
                }
                habitacion="105";
                hs="192.168.0.16";
                DISPOSITIVO="5";

                e=1;
                consultageneral();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms
                        TasisEnf();
                        e=0;
                        consultageneral();
                        updateHTTPRequest();
                        //idEorA = 0;
                        TiempoRES = "SIN RESPUESTA";
                        numEvento=0;
                        fechafinal="N/A";
                        horafinal="N/A";
                    }
                }, 500);
            }
        });
        MOD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void reco() {
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Fonts/" + habitacion + "Grabacion.mp3";
        miGrabacion = new MediaRecorder();
        miGrabacion.setAudioSource(MediaRecorder.AudioSource.MIC);
        miGrabacion.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        miGrabacion.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        miGrabacion.setOutputFile(outputFile);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
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
        }, 200);

    }

    public void play() {
        if (miGrabacion != null) {
            miGrabacion.stop();
            miGrabacion.release();
            miGrabacion = null;
            Toast.makeText(getApplicationContext(), "AUDIO GRABADO", Toast.LENGTH_LONG).show();
        }
        try {
            m.setDataSource(outputFile);


            ////////////////////////////////////////////////////////////////////////////////


            /////////////////////////////////////////////////////////////////////////////////////////////
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

    public void TasisEnf() {
        if(e==1){
            try {
                date = new Date();
                F1 =dateFormat.format(date);
                fechaF1 = dateFormat.parse(F1);
                fechaF0 = dateFormat.parse(fechafinal);
                F0 = dateFormat.format(fechaF0);
                dif = Math.abs(fechaF0.getTime() - fechaF1.getTime());
                dif=dif/ (60 * 60 * 1000);

                date = new Date();
                D1 = horaFormat.format(date);
                horaD1 = horaFormat.parse(D1);
                horaD0 = horaFormat.parse(horafinal);
                difs = Math.abs(horaD0.getTime() - horaD1.getTime());
                difh = difs / (60 * 60 * 1000)+dif;
                difs = difs % (60 * 60 * 1000);
                difm = difs / (60 * 1000);
                difs = difs % (60 * 1000);
                difs = difs / 1000;
                TiempoRES = (String.valueOf(difh) + "HORAS " + String.valueOf(difm) + "MINUTOS " + String.valueOf(difs) + "SEGUNDOS.");
            } catch (Exception e) {
                alertaTasis();
            }
        }
        if(e==0){
            try {
                horaD1 = new Date();
                difs = Math.abs(horaD0.getTime() - horaD1.getTime());
                difh = difs / (60 * 60 * 1000);
                difs = difs % (60 * 60 * 1000);
                difm = difs / (60 * 1000);
                difs = difs % (60 * 1000);
                difs = difs / 1000;
                TiempoRES = (String.valueOf(difh) + "HORAS " + String.valueOf(difm) + "MINUTOS " + String.valueOf(difs) + "SEGUNDOS.");
            } catch (Exception e) {
                alertaTasis();
            }
        }
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
        handler.postDelayed(runnable, 2000);
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

    public void applyTexts(String usuario, String contraseña) {
        if (usuario.equals("USER") == true && contraseña.equals("1234") == true) {
            Toast.makeText(getApplicationContext(), "SESION INICIADA", Toast.LENGTH_SHORT).show();
            Intent ListFruta = new Intent(getApplicationContext(), modificardispositivo.class);
            startActivity(ListFruta);
        } else {
            Toast.makeText(getApplicationContext(), "USUARIO O CONTRASEÑA INCORRECTA", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendHTTPRequest() {
        numEvento=folio;
        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        String url1 = "http://" + hs + "/BDEJEMPLOS/REGISTROEVENTOS.php?FOLIODISPOSITIVO=" + (numEvento) + "&TIPODELLAMADO=" + AE + "&FECHA=" + fechafinal + "&HORA=" + horafinal + "&TURNO=" + turno + "&HABITACION=" + habitacion + "&ENFERMERA=" + enfermera + "&TIEMPORESPUESTA=" + TiempoRES + "&PACIENTE="
                + PACIENTE + "&MEDICO=" + MEDICO + "&PAGO=" + PAGO + "&ESTACION=" + ESTACION + "&SECCION=" + SECCION + "&AUDIO=" + AUDIO;
        url1 = url1.replace(" ", "%20");
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "REGISTRO EXITOSO", Toast.LENGTH_SHORT).show();
                fecha.setText("\nHABITACION: " + habitacion + "\nPACIENTE: " + PACIENTE + "\nMEDICO: " + MEDICO);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "NO SE REGISTRO", Toast.LENGTH_SHORT).show();
            }
        }) {
        };
        MyRequestQueue.add(MyStringRequest);
    }

    public void updateHTTPRequest() {
        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        String url1 = "http://" + hs + "/BDEJEMPLOS/UPDATETIEMPO.php?FOLIODISPOSITIVO=" + (numEvento - 1) + "&TIPODELLAMADO=" + AE + "&FECHA=" + fechafinal + "&HORA=" + horafinal + "&HABITACION=" + habitacion + "&TIEMPORESPUESTA=" + TiempoRES;
        url1 = url1.replace(" ", "%20");
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "REGISTRO EXITOSO", Toast.LENGTH_SHORT).show();
                AE="OCUPADO";
                fecha.setText("\nHABITACION: " + habitacion + "\nPACIENTE: " + PACIENTE + "\nMEDICO: " + MEDICO);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "NO SE REGISTRO", Toast.LENGTH_SHORT).show();
            }
        }) {
        };
        MyRequestQueue.add(MyStringRequest);
    }

    public void consultageneral() {
        String url1 = "http://" + hs + "/BDEJEMPLOS/CONSULTAGENERAL.php?HABITACION=" + habitacion + "&IP=" + hs + "&NODISPOSITIVO=" + DISPOSITIVO+"&TIEMPORESPUESTA=" + TiempoRES+"&E="+e;
        jsonrequest = new JsonObjectRequest(Request.Method.POST, url1, null, this, this);////////////////////////////////////////////////////////////json webservices/////////////////
        request1.add(jsonrequest);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
    }

    @Override
    public void onResponse(JSONObject response) {
        consulta = response.optJSONArray("usuario");
        if (e == 0) {
            try {
                for (int i = 0; i < consulta.length(); i++) {
                    Usuarios consultaUsuario;
                    consultaUsuario = new Usuarios();
                    JSONObject jsonconsulta = null;
                    jsonconsulta = consulta.getJSONObject(i);
                    consultaUsuario.setIP(jsonconsulta.optString("IP"));
                    consultaUsuario.setESTATUS(jsonconsulta.optString("ESTATUS"));
                    consultaUsuario.setNODISPOSITIVO(jsonconsulta.optString("NODISPOSITIVO"));
                    consultaUsuario.setHABITACION(jsonconsulta.optString("NOHABITACION"));
                    consultaUsuario.setPACIENTE(jsonconsulta.optString("PACIENTE"));
                    consultaUsuario.setMEDICO(jsonconsulta.optString("MEDICO"));
                    consultaUsuario.setPAGO(jsonconsulta.optString("PAGO"));
                    consultaUsuario.setNOESTACION(jsonconsulta.optString("NOESTACION"));
                    consultaUsuario.setNOSECCION(jsonconsulta.optString("NOSECCION"));
                    ip = consultaUsuario.getIP();
                    habitacion = consultaUsuario.getHABITACION();
                    DISPOSITIVO = consultaUsuario.getNODISPOSITIVO();
                    PACIENTE = consultaUsuario.getPACIENTE();
                    PAGO = consultaUsuario.getPAGO();
                    MEDICO = consultaUsuario.getMEDICO();
                    ESTACION = consultaUsuario.getNOESTACION();
                    SECCION = consultaUsuario.getNOSECCION();
                    AE= consultaUsuario.getESTATUS();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            fecha.setText("\nHABITACION: " + habitacion + "\nPACIENTE: " + PACIENTE + "\nMEDICO: " + MEDICO);

    }
        if (e == 1) {
            try {
                for (int i = 0; i < consulta.length(); i++) {
                    Usuarios consultaUsuario;
                    consultaUsuario = new Usuarios();
                    JSONObject jsonconsulta = null;
                    jsonconsulta = consulta.getJSONObject(i);
                    consultaUsuario.setTIPODELLAMDO(jsonconsulta.optString("TIPODELLAMADO"));
                    consultaUsuario.setFECHA(jsonconsulta.optString("FECHA"));
                    consultaUsuario.setHORA(jsonconsulta.optString("HORA"));
                    consultaUsuario.setFOLIODISPOSITIVO(jsonconsulta.optString("FOLIODISPOSITIVO"));
                    AE = consultaUsuario.getTIPODELLAMDO();
                    fechafinal = consultaUsuario.getFECHA();
                    horafinal = consultaUsuario.getHORA();
                    FOLIODIPOSITIVO = consultaUsuario.getFOLIODISPOSITIVO();
                    numEvento = Integer.parseInt(FOLIODIPOSITIVO);
                    numEvento=numEvento+1;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
/*
handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms

                    }
                }, 200);
 */