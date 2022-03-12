package com.example.entrega1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    private String error;
    private int duration;
    private Toast toast;

    private MiBD gestorDB;

    private EditText usuarioET;
    private EditText contraseñaET;
    private Button idiomaBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Dejamos el toast preparado para el caso de usuario contraseña incorrectos
        error = getString(R.string.login_error);
        duration = Toast.LENGTH_SHORT;
        toast = Toast.makeText(getApplicationContext(), error, duration);

        //Gestor base de datos
        gestorDB = new MiBD(this, "Users", null, 1);

        //Obtenemos los elementos necesarios
        usuarioET = findViewById(R.id.usuarioET);
        contraseñaET = findViewById(R.id.contraseñaET);
        idiomaBTN = findViewById(R.id.idiomaBTN);

    }

    public void onClickEntrar(View v){

        //Comprobar que son correctos usuario y contraseña
        String usuario = usuarioET.getText().toString();
        String contraseña = contraseñaET.getText().toString();
        Intent i;

        int resultado = gestorDB.existenUsuarioContraseña(usuario,contraseña);

        if(resultado==0){
            //Si es correcto pasamos a la siguiente actividad
            i = new Intent (LoginActivity.this, UsuariosActivity.class);
            i.putExtra("usuario",usuario);
            this.escribirFichero(usuario); //Escribimos en el fichero para saber que ha iniciado sesión
            startActivity(i);
        }else{
            //Si es incorrecto mostramos el toast
            toast.show();
        }
    }

    public void onClickIdioma(View v){

        Button idiomaBTN = (Button)v;
        //Saber a que idioma cambiar
        String idiomaCambiar = idiomaBTN.getText().toString();

        if (idiomaCambiar.equals("ES")){
            idiomaCambiar = "es";
        }
        else{
            idiomaCambiar = "en";
        }

        Locale nuevaloc = new Locale(idiomaCambiar);
        Locale.setDefault(nuevaloc);
        Configuration configuration =
                getBaseContext().getResources().getConfiguration();
        configuration.setLocale(nuevaloc);
        configuration.setLayoutDirection(nuevaloc);

        Context context = getBaseContext().createConfigurationContext(configuration);
        getBaseContext().getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());

        finish();
        startActivity(getIntent());
    }

    public void onClickRegistrate(View v){
        Intent i = new Intent (LoginActivity.this, SignupActivity.class);
        startActivity(i);
    }

    private void escribirFichero(String usuario){
        //En este fichero se escribirán los usuarios que inicien sesión
        // (también la hora a la que lo hicieron)
        OutputStreamWriter ficheroexterno;
        File f;
        File path = this.getExternalFilesDir(null);
        //El fichero estará solo en inglés
        f = new File(path.getAbsolutePath(), "users_login.txt");
        Log.i("FICH","PATH:"+path.getAbsolutePath());
        try {
            Date currentTime = Calendar.getInstance().getTime();
            ficheroexterno = new OutputStreamWriter(new FileOutputStream(f,true));
            ficheroexterno.write(usuario+" has logged-in on "+currentTime+"\n");
            ficheroexterno.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}