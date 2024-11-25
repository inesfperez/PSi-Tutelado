package es.udc.psi.caresafe;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import es.udc.psi.caresafe.services.FallDetection.serviceFallDetecManager;
import es.udc.psi.caresafe.services.GPS.SettingGPSActivity;
import es.udc.psi.caresafe.services.GPS.serviceGPSmanager;
import es.udc.psi.caresafe.services.serviceManager;

public class MainActivity extends AppCompatActivity {
    private serviceManager[] services;
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
        services = new serviceManager[] {
                new serviceGPSmanager(getApplicationContext(), this),
                new serviceFallDetecManager(getApplicationContext(), this)
        };

        for(serviceManager service: services){
            bindService(service.getServiceIntent(), service, Context.BIND_AUTO_CREATE);
        }

    }

    @Override
    protected void onDestroy() {
        for (serviceManager service: services){
            service.stopService();
            unbindService(service);
        }
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