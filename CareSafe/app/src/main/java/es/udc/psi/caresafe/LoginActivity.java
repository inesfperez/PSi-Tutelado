package es.udc.psi.caresafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final String REMEMBER_KEY = "rememberMe";
    private static final String SHARED_PREFS_KEY = "CareSafePrefs";

    private EditText emailEditText;
    private EditText passwordEditText;
    private CheckBox rememberMeCheckBox;
    private Button loginButton;
    private TextView registerTextView;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar Firebase y SharedPreferences
        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences(SHARED_PREFS_KEY, MODE_PRIVATE);

        // Verificamos si hay sesión activa, si la hay vamos directamente a la app
        if (sharedPreferences.getBoolean(REMEMBER_KEY, false)) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        onPressedInitSesion();

    }

    // Método para manejar el evento de inicio de sesión
    private void onPressedInitSesion() {
        // Inicializar vistas
        emailEditText = findViewById(R.id.email_et);
        passwordEditText = findViewById(R.id.password_et);
        rememberMeCheckBox = findViewById(R.id.remember_me_cb);
        loginButton = findViewById(R.id.login_btn);
        registerTextView = findViewById(R.id.register_tv);
        progressBar = findViewById(R.id.progress_bar_login);

        registerTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            // Comprobamos que los campos no estén vacíos
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, R.string.empty_emailOrPassw_msg, Toast.LENGTH_SHORT).show();
                return;
            }

            // Mostramos el ProgressBar
            progressBar.setVisibility(ProgressBar.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                // Inicio de sesión exitoso
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null){
                                    user.sendEmailVerification();
                                    if (rememberMeCheckBox.isChecked()) {
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