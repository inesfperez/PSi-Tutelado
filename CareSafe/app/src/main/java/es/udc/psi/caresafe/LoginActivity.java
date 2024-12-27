package es.udc.psi.caresafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import es.udc.psi.caresafe.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private static final String TAG = "LoginActivity";
    private static final String REMEMBER_KEY = "rememberMe";
    private static final String SHARED_PREFS_KEY = "CareSafePrefs";

    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;

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

        // Inicializar Firebase y SharedPreferences
        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences(SHARED_PREFS_KEY, MODE_PRIVATE);

        // Verificamos si hay sesión activa, si la hay vamos directamente a la app
        if (isUserLoggedIn()) {
            redirectToMainActivity();
            return; // Finalizamos aquí para evitar cargar el resto del código
        }

        onPressedInitSesion();
    }

    private void initilizeViewBinding() {
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    private boolean isUserLoggedIn() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        boolean rememberMe = sharedPreferences.getBoolean(REMEMBER_KEY, false);

        // Verificar si hay un usuario autenticado y si "Recordar sesión" está activado
        return currentUser != null && rememberMe;
    }

    private void redirectToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // Método para manejar el evento de inicio de sesión
    private void onPressedInitSesion() {

        binding.registerTv.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        binding.loginBtn.setOnClickListener(v -> {
            String email = binding.emailEt.getText().toString();
            String password = binding.passwordEt.getText().toString();

            // Comprobamos que los campos no estén vacíos
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, R.string.empty_emailOrPassw_msg, Toast.LENGTH_SHORT).show();
                return;
            }

            // Mostramos el ProgressBar
            binding.progressBarLogin.setVisibility(ProgressBar.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            binding.progressBarLogin.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                // Inicio de sesión exitoso
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null){
                                    user.sendEmailVerification();
                                    if (binding.rememberMeCb.isChecked()) {
                                        sharedPreferences.edit().putBoolean(REMEMBER_KEY, true).apply();
                                    }
                                    Toast.makeText(LoginActivity.this, getText(R.string.succesful_login), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            } else {
                                // Error en inicio de sesión
                                Log.e(TAG, "Error en inicio de sesión: " + task.getException());
                                Toast.makeText(LoginActivity.this, getText(R.string.error_authentication), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

        });
    }
}