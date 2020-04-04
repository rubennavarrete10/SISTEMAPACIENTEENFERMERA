package SPE.PKG;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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

public class modificardispositivo extends AppCompatActivity{

    RequestQueue request1;
    EditText host,habitacion,dispositivo;
    String hs,hb,dis;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "text";
    public static final String TEXT2 = "text2";
    public static final String TEXT3 = "text3";

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.modificardispo);

        Button ACTUALIZAR = (Button) findViewById(R.id.ACTUALIZAR);
        Button REGRESAR = (Button) findViewById(R.id.REGRESAR);
        host = (EditText) findViewById(R.id.editText);
        habitacion = (EditText)findViewById(R.id.editText2);
        dispositivo = (EditText)findViewById(R.id.editText3);
        request1 = Volley.newRequestQueue(this);


        ACTUALIZAR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hs=host.getText().toString();
                hb=habitacion.getText().toString();
                dis=dispositivo.getText().toString();
                actualizar();
                saveData();
            }
        });
        REGRESAR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("hb", habitacion.getText().toString());
                intent.putExtra("hs", host.getText().toString());
                intent.putExtra("dis", dispositivo.getText().toString());
                startActivity(intent);
            }
        });
    }

    private void actualizar() {
        RequestQueue MyRequestQueue = Volley.newRequestQueue(this);
        String url1= "http://" + hs + "/BDEJEMPLOS/CONSULTAGENERAL.php?HABITACION="+ hb +"&IP="+hs+"&NODISPOSITIVO="+dis+"&TIEMPORESPUESTA=N/A&E=0";
        url1 = url1.replace(" ", "%20");
        StringRequest MyStringRequest = new StringRequest(Request.Method.POST, url1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "ACTUALIZACION EXITOSA", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "NO SE ACTUALIZO", Toast.LENGTH_SHORT).show();
            }
        }) {
        };
        MyRequestQueue.add(MyStringRequest);
    }
    public void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TEXT, hb);
        editor.putString(TEXT2, hs);
        editor.putString(TEXT3, dis);
        editor.apply();
        Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT).show();
    }
}