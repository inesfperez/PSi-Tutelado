package es.udc.psi.caresafe;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

import es.udc.psi.caresafe.databinding.ActivityMainBinding;
import es.udc.psi.caresafe.FallDetection.serviceFallDetecManager;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private serviceFallDetecManager serviceFallDetecManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        initilizeViewBinding();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        logout();
    }

    private void initilizeViewBinding() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Si el usuario no está autenticado, redirige a LoginActivity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    public void logout() {

        binding.logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut(); // Cierra la sesión
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Limpia el stack de actividades
                startActivity(intent);
                finish(); // Finaliza la actividad actual
            }
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
