package app.savetalk.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import app.savetalk.R;
import app.savetalk.recorder.ManagerRecorder;

public class MainActivity extends AppCompatActivity {
    ManagerRecorder manRec = ManagerRecorder.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        //Solicitamos permisos si no los tiene
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 10);

        //Desactivamos los botones que no hagan nada
        if(ManagerRecorder.daemonEnEjecucion(this)){
            Button btn = findViewById(R.id.btnIniciarGrabacion);
            btn.setEnabled(false);
        }else{
            Button btn = findViewById(R.id.btnDetenerGrabacion);
            btn.setEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //Abrimos ajustes
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void iniciarGrabacion(View view) {
        manRec.iniciarGrabacion(this);
        Button btn = findViewById(R.id.btnIniciarGrabacion);
        btn.setEnabled(false);
        btn = findViewById(R.id.btnDetenerGrabacion);
        btn.setEnabled(true);
    }

    public void detenerGrabacion(View view) {
        manRec.pararGrabacion(this);
        Button btn = findViewById(R.id.btnIniciarGrabacion);
        btn.setEnabled(true);
        btn = findViewById(R.id.btnDetenerGrabacion);
        btn.setEnabled(false);
    }
}