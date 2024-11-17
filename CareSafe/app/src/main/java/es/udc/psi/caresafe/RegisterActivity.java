package es.udc.psi.caresafe;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.checkerframework.checker.nullness.qual.NonNull;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText, confirmPasswordEditText;
    private Button registerButton;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Inicializar las vistas
        emailEditText = findViewById(R.id.email_et);
        passwordEditText = findViewById(R.id.password_et);
        confirmPasswordEditText = findViewById(R.id.confirm_password_et);
        registerButton = findViewById(R.id.register_btn);
        progressBar = findViewById(R.id.progress_bar_register);

        // Registrar un nuevo usuario
        registerButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(RegisterActivity.this, "Por favor ingresa un correo electrónico", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(RegisterActivity.this, "Por favor ingresa una contraseña", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(RegisterActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }

            // Mostrar el ProgressBar
            progressBar.setVisibility(ProgressBar.VISIBLE);

            // Crear la cuenta de usuario en Firebase
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(ProgressBar.GONE);

                            if (task.isSuccessful()) {
                                // Registro exitoso
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    // Enviar un correo de verificación al usuario
                                    user.sendEmailVerification();
                                }

                                Toast.makeText(RegisterActivity.this, "Registro exitoso. Verifica tu correo electrónico.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // Error en el registro
                                Toast.makeText(RegisterActivity.this, "Error al registrar: " + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });
    }
}
