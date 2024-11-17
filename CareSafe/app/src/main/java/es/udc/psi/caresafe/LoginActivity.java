package es.udc.psi.caresafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

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
        sharedPreferences = getSharedPreferences("CareSafePrefs", MODE_PRIVATE);

        // Verificamos si hay sesión activa, si la hay vamos directamente a la app
        if (sharedPreferences.getBoolean("rememberMe", false)) {
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

        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            // Comprobamos que los campos no estén vacíos
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, R.string.empty_emailOrPassw_msg, Toast.LENGTH_SHORT).show();
            }

            // Mostramos el ProgressBar
        });
    }
}