package es.udc.psi.caresafe;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.PreferenceManager;

import es.udc.psi.caresafe.GPS.coords;
import es.udc.psi.caresafe.GPS.serviceGPSmanager;
import es.udc.psi.caresafe.GPS.SettingGPSActivity;
import es.udc.psi.caresafe.databinding.ActivityGpsmainBinding;
import es.udc.psi.caresafe.databinding.ActivityRegisterBinding;

public class GPSMainActivity extends AppCompatActivity {
    private serviceGPSmanager serviceGPSmanager;
    private ActivityGpsmainBinding binding;
    private SharedPreferences sharedPreferences;
    private EmailNotifier notifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityGpsmainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String toEmail = sharedPreferences.getString(getString(R.string.keyPreferenciesToEmail), null);
        String alias = sharedPreferences.getString(getString(R.string.keyPreferenciesAlias), null);

        if(toEmail == null || alias == null){
            Toast.makeText(getApplicationContext(), getString(R.string.settingsError), Toast.LENGTH_SHORT).show();
        } else {
            notifier = new EmailNotifier(getApplicationContext(), alias, toEmail);
            serviceGPSmanager = new serviceGPSmanager(getApplicationContext(), this, notifier);
            bindService(serviceGPSmanager.getServicioGPSIntent(), serviceGPSmanager, Context.BIND_AUTO_CREATE);
        }
        Button openMapButton = binding.openMapButton;
        bindService(serviceGPSmanager.getServicioGPSIntent(), serviceGPSmanager,Context.BIND_AUTO_CREATE);

        openMapButton.setOnClickListener(v -> {
            Intent mapIntent = new Intent(this, MapsActivity.class);
            coords coordinates = serviceGPSmanager.getCoordinates();
            int radius = serviceGPSmanager.getRadius();

            if (coordinates!=null){
                Bundle bundle = new Bundle();
                bundle.putDouble("latitude", coordinates.getAltitude()); // Altitude como latitud
                bundle.putDouble("longitude", coordinates.getLongitude()); // Longitude como longitud
                bundle.putInt("radius", radius);
                mapIntent.putExtras(bundle);
            }
            startActivity(mapIntent);
        });
    }

    @Override
    protected void onDestroy() {
        serviceGPSmanager.stopService();
        if(notifier != null){
            unbindService(serviceGPSmanager);
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

    @Override
    public void onBackPressed() {
        // Regresa a MainActivity sin cerrar la aplicación
        super.onBackPressed();
        Intent intent = new Intent(GPSMainActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Finaliza la actividad actual
    }
}
