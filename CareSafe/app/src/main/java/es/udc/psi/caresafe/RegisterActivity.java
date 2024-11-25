package es.udc.psi.caresafe;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.checkerframework.checker.nullness.qual.NonNull;

import es.udc.psi.caresafe.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initilizeViewBinding();

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Registrar un nuevo usuario
        binding.registerBtn.setOnClickListener(v -> {
            String email = binding.emailEt.getText().toString().trim();
            String password = binding.passwordEt.getText().toString().trim();
            String confirmPassword = binding.confirmPasswordEt.getText().toString().trim();

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
            binding.progressBarRegister.setVisibility(ProgressBar.VISIBLE);

            // Crear la cuenta de usuario en Firebase
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            binding.progressBarRegister.setVisibility(ProgressBar.GONE);

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

    private void initilizeViewBinding() {
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
