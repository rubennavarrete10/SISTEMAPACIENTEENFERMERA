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

public class modificardispositivo extends AppCompatActivity implements Response.ErrorListener, Response.Listener<JSONObject> {

    RequestQueue request1;
    JSONArray consulta;
    JsonObjectRequest jsonrequest;

    EditText host,habitacion,dispositivo;
    String hs="192.168.0.16",hb,dis;
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.modificardispo);

        Button ACTUALIZAR = (Button) findViewById(R.id.ACTUALIZAR);
        Button REGRESAR = (Button) findViewById(R.id.REGRESAR);
        host = (EditText) findViewById(R.id.editText);
        habitacion = (EditText)findViewById(R.id.editText2);
        dispositivo = (EditText) findViewById(R.id.editText3);
        request1 = Volley.newRequestQueue(this);


        ACTUALIZAR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hs=host.getText().toString();
                hb=habitacion.getText().toString();
                dis=dispositivo.getText().toString();
                actualizardisp();
            }
        });
        REGRESAR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }


    public void actualizardisp() {
        String url1 = "http://"+hs+"/BDEJEMPLOS/actualizardisp.php?NODISPOSITIVO="+dis+"&HABITACION="+hb+"&IP="+hs;
        jsonrequest = new JsonObjectRequest(Request.Method.POST, url1, null, this, this);////////////////////////////////////////////////////////////json webservices/////////////////
        request1.add(jsonrequest);
    }
    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getApplicationContext(), "ERROR EN LA ACTUALIZACION ", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject response) {
        Toast.makeText(getApplicationContext(), "ACTUALIZACION LISTA", Toast.LENGTH_LONG).show();
    }
}