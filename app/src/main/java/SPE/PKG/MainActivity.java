package SPE.PKG;
import android.app.Activity;
import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.Intent;
import android.net.Uri;
import android.speech.RecognizerIntent;
import android.widget.Button;
import android.widget.TextView;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.provider.MediaStore;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Vibrator;
import java.text.SimpleDateFormat;
import java.util.Date;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

 public class MainActivity extends AppCompatActivity {

    private static final int RECOGNIZE_SPEECH_ACTIVITY = 1;
    TextView fecha;
    TextView hora;
    TextView recovoz;
    SimpleDateFormat horaFormat = new SimpleDateFormat("HH:mm:ss");
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy"); ;
    Date horaD,horaD1,horaD0;
    Date date;
    String D0,D1;
    String horafinal;
    String turno;
    String fechafinal;
    String habitacion="101";
    String TiempoRES;
    int newaudio=1;
    long difh,difm,difs = 0;
    //String localizacion = "/storage/emulated/0/Pictures/Messenger";
    //Uri uri = Uri.parse(localizacion); //esto se usa para reproducir url de internet

    private String outputFile = null;
    MediaRecorder miGrabacion = null;
    private MediaPlayer player;
    private boolean presionado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tarjetaSd();

        Button ASISTENCIA = (Button) findViewById(R.id.ASISTENCIA);
        Button ASISTENCIAHECHA = (Button) findViewById(R.id.ASISTENCIAHECHA);
        Button EMERGENCIA = (Button) findViewById(R.id.EMERGENCIA);
        fecha = (TextView) findViewById(R.id.fecha);
        hora = (TextView) findViewById(R.id.hora);
        recovoz= (TextView) findViewById(R.id.timer);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat .requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, 1000);
        }

        EMERGENCIA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // WEBSERVICE("https://localhost:44370/WebService1.asmx");/////////////////////////////////////////////////////////PONER UN URL NO USAR POR EL MOMENTO
                //reconocer();
            }
        });

        if (tarjetaSd()== true) {
            ASISTENCIA.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            if (!presionado) {
                                presionado = true;
                                //AsyncTask que ejecuta Tarea.
                                date = new Date();
                                fechafinal = dateFormat.format(date);
                                turno();
                                horaD0 = new Date();
                                fecha.setText(fechafinal+"  "+horafinal+"  "+habitacion+"  " +turno);
                                reco();
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            presionado = false;
                            play();
                            Toast.makeText(getApplicationContext(), "DEJO DE GRABAR", Toast.LENGTH_LONG).show();
                            break;
                    }
                    return true;
                }
            });
        }
        ASISTENCIAHECHA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    horaD1 = new Date();
                    difs = Math.abs(horaD0.getTime() - horaD1.getTime());
                    difh=difs/(60*60*1000);
                    difs= difs%(60*60*1000);
                    difm=difs/(60*1000);
                    difs= difs%(60*1000);
                    difs=difs/1000;



                    TiempoRES = (String.valueOf(difh)+"horas  "+String.valueOf(difm)+"minutos  "+String.valueOf(difs)+"segundos");
                    D0 = String.valueOf(horaD0);
                    D1 = String.valueOf(horaD1);
                    recovoz.setText(TiempoRES+"  "+D0+"  "+D1);
                }catch(Exception e){
                    Toast.makeText(getApplicationContext(), "Error con el tiempo de asistencia: ", Toast.LENGTH_LONG).show();
                }
            }
        });


    }
    ///////////////////////////////////////////////////////grabar audio///////////////////////////////////////////
    public void reco() {
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Fonts/"+ newaudio +"Grabacion.3gp";
        miGrabacion = new MediaRecorder();
        miGrabacion.setAudioSource(MediaRecorder.AudioSource.MIC);
        miGrabacion.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        miGrabacion.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        miGrabacion.setOutputFile(outputFile);
        newaudio=newaudio+1;
        try {
            miGrabacion.prepare();
            miGrabacion.start();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(), "La grabación comenzó", Toast.LENGTH_LONG).show();
    }

    public void play() {
        if (miGrabacion != null) {
            miGrabacion.stop();
            miGrabacion.release();
            miGrabacion = null;
            Toast.makeText(getApplicationContext(), "El audio  grabado con éxito", Toast.LENGTH_LONG).show();
        }
        MediaPlayer m = new MediaPlayer();
        try {
            m.setDataSource(outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            m.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        m.start();
        Toast.makeText(getApplicationContext(), "reproducción de audio", Toast.LENGTH_LONG).show();
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
             Date date1, date2,date3,date4,date5,date6;
             date1 = horaFormat.parse(hora1);
             date2 = horaFormat.parse(hora2);
             date3 = horaFormat.parse(hora3);
             date4 = horaFormat.parse(hora4);
             date5 = horaFormat.parse(hora5);
             date6 = horaFormat.parse(hora6);
             horaD = horaFormat.parse(horafinal);
             if ((date1.compareTo(horaD) <= 0) && (date2.compareTo(horaD) >= 0)){
                 turno = "noche";
             }
             if((date3.compareTo(horaD) <= 0) && (date4.compareTo(horaD) >= 0)){
                 turno ="Mañana";
             }
             if((date5.compareTo(horaD) <= 0) && (date6.compareTo(horaD) >= 0)){
                 turno= "Tarde";
             }
         } catch (ParseException e){
             turno= "Error";
         }
         return false;
     }
    /////////////////////////////////////////////////////////////revisar sd///////////////
    public boolean tarjetaSd() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Toast.makeText(getApplicationContext(), "SD listo", Toast.LENGTH_SHORT).show();
            return true;
        }
        Toast.makeText(getApplicationContext(), "Error SD", Toast.LENGTH_SHORT).show();
        return false;
    }
    /////////////////////////////////////////////////////////////////////////////////// voz to texto //////////////////////////////////////////////
    private void reconocer(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intentActionRecognizeSpeech = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intentActionRecognizeSpeech.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "es-MX");
                try {
                    startActivityForResult(intentActionRecognizeSpeech, RECOGNIZE_SPEECH_ACTIVITY);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(), "Tú dispositivo no soporta el reconocimiento por voz", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RECOGNIZE_SPEECH_ACTIVITY:
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> speech = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String strSpeech2Text = speech.get(0);
                    recovoz.setText(strSpeech2Text);
                }
            default:
                break;
        }
    }
    ////////////////////////////////////////////////////////////webservice///////////////////////////////////////////////////////////////////////////////////
    private void WEBSERVICE (String URL){
        StringRequest request= new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "OPERACION EXITOSA", Toast.LENGTH_SHORT).show();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();;
            }
        }) {
            @Override
            protected Map <String, String> getParams() throws AuthFailureError{
                Map<String, String> parametros= new HashMap<String, String>();
                parametros.put("FECHA",fecha.getText().toString());
                return parametros;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(request);
    }
}