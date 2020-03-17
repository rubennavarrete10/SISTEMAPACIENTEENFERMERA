package SPE.PKG;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class login extends AppCompatDialogFragment {
    private EditText user,pass;
    private Datoslogin listener;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.login, null);
        builder.setView(view).setTitle("INGRESE USUARIO Y CONTRASENA DE SERVICIO").setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).setPositiveButton("INICIAR SESION", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String usuario = user.getText().toString();
                String contraseña = pass.getText().toString();
                listener.applyTexts(usuario,contraseña);
            }
        });
        user=view.findViewById(R.id.usuario);
        pass=view.findViewById(R.id.contra);
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (Datoslogin)context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() +"error");
        }

    }

    public interface Datoslogin{
        void applyTexts (String usuario,String contraseña);

    }
}
