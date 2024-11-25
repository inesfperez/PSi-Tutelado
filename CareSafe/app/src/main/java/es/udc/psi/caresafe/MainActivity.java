package es.udc.psi.caresafe;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import es.udc.psi.caresafe.FallDetection.FallDetectionService;
import es.udc.psi.caresafe.FallDetection.serviceFallDetecManager;

public class MainActivity extends AppCompatActivity {
    private serviceFallDetecManager serviceFallDetecManager;
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
        serviceFallDetecManager = new serviceFallDetecManager(getApplicationContext(), this);
        bindService(serviceFallDetecManager.getServicioIntent(), serviceFallDetecManager, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        serviceFallDetecManager.stopService();
        unbindService(serviceFallDetecManager);
        super.onDestroy();
    }
}