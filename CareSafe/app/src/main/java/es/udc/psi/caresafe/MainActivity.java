package es.udc.psi.caresafe;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import es.udc.psi.caresafe.GPS.coords;
import es.udc.psi.caresafe.GPS.serviceGPSmanager;
import es.udc.psi.caresafe.GPS.SettingGPSActivity;

public class MainActivity extends AppCompatActivity {
    private serviceGPSmanager serviceGPSmanager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        serviceGPSmanager = new serviceGPSmanager(getApplicationContext(), this);
        Button openMapButton = findViewById(R.id.openMapButton);
        bindService(serviceGPSmanager.getServicioGPSIntent(), serviceGPSmanager,Context.BIND_AUTO_CREATE);

        openMapButton.setOnClickListener(v -> {
            Intent mapIntent = new Intent(this, MapsActivity.class);
            startActivity(mapIntent);
        });
    }

    @Override
    protected void onDestroy() {
        serviceGPSmanager.stopService();
        unbindService(serviceGPSmanager);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflate = getMenuInflater();
        inflate.inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.item_AjustesMenu){
            Intent settingIntent = new Intent(this, SettingGPSActivity.class);
            startActivity(settingIntent);
        } else {
            Log.d("TAG", "ERRoR");
        }
        return true;
    }
}
