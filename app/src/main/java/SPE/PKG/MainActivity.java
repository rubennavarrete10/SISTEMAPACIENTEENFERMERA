package SPE.PKG;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import android.os.Environment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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

    String localizacion = "/storage/emulated/0/Pictures/Messenger";
    //String localizacion = "Tarjeta SD/Pictures/Screenshots/file.mp3";
    //String localizacion = "https://sampleswap.org/samples-ghost/MELODIC%20SAMPLES/GUITARS/146[kb]badmetal2.aif.mp3";///funciona
    Uri uri = Uri.parse(localizacion);

    MediaRecorder grabacion = null;
    private MediaPlayer player;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tarjetaSd();

        Button ASISTENCIA = (Button) findViewById(R.id.ASISTENCIA);
        Button EMERGENCIA = (Button) findViewById(R.id.EMERGENCIA);
        fecha = (TextView) findViewById(R.id.fecha);
        hora = (TextView) findViewById(R.id.hora);
        recovoz= (TextView) findViewById(R.id.timer);

        EMERGENCIA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Date date = new Date();
                String fechafinal = dateFormat.format(date);
                fecha.setText(fechafinal);
               // WEBSERVICE("https://localhost:44370/WebService1.asmx");/////////////////////////////////////////////////////////PONER UN URL NO USAR POR EL MOMENTO
               // reconocer();
                reproducir();

            }
        });

        if (tarjetaSd()== true) {
            ASISTENCIA.setOnClickListener(new View.OnClickListener() {
                @Override

                public void onClick(View v) {

                    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
                    Date date = new Date();
                    String horafinal = dateFormat.format(date);
                    hora.setText(horafinal);
                    grabar();

                }
            });
        }
    }
/////////////////////////////////////////////////////////////////////////////////// voz to texto //////////////////////////////////////////////
    private void reconocer(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intentActionRecognizeSpeech = new Intent(
                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intentActionRecognizeSpeech.putExtra(
                        RecognizerIntent.EXTRA_LANGUAGE_MODEL, "es-MX");
                try {
                    startActivityForResult(intentActionRecognizeSpeech,
                            RECOGNIZE_SPEECH_ACTIVITY);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(),
                            "Tú dispositivo no soporta el reconocimiento por voz",
                            Toast.LENGTH_SHORT).show();
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
    ///////////////////////////////////////////////////////grabar audio///////////////////////////////////////////
    public void grabar (){
        if(grabacion == null){

            grabacion = new MediaRecorder();
            grabacion.setAudioSource(MediaRecorder.AudioSource.MIC);
            grabacion.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            grabacion.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
            grabacion.setOutputFile(localizacion);//////////////////////////////////////////////////////////////////error al sacar el archivo de audio
            Toast.makeText(getApplicationContext(), "empezaste a grabar", Toast.LENGTH_SHORT).show();
            try {
                grabacion.prepare();
                grabacion.start();
            }
            catch (IOException e){

            }
        }else if (grabacion != null){
            grabacion.stop();
            grabacion.release();
            grabacion = null;
            Toast.makeText(getApplicationContext(), "dejaste de grabar", Toast.LENGTH_SHORT).show();
        }
    }
    ///////////////////////////////////////////////////////////reproducir audio/////////////////////
    public void reproducir (){
        if(grabacion == null){
            player = new MediaPlayer();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                player.setDataSource(getApplicationContext(),uri);
                player.prepareAsync();
                player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        Toast.makeText(getApplicationContext(), "Reproduciendo audio", Toast.LENGTH_SHORT).show();
                        if (player.isPlaying()){
                            player.pause();
                        }else{
                            player.start();
                        }
                    }
                });
            }
            catch (IOException e){

            }
            player = MediaPlayer.create(this, uri);
            Toast.makeText(getApplicationContext(), "No se puede reproducir", Toast.LENGTH_SHORT).show();
        }
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
    ///////////////////////////////////////////crear archivos ///////////////////////////////

}

