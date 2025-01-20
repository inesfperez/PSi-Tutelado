package es.udc.psi.caresafe;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EmailNotifier {
    private Context context;
    // Debería de cargarse estas variables de sharedPreferencies o de la BDA, deben de estar especificadas por el usuario en algún momento
    private final String aliasUser = "aliasExample";
    private final String toEmail = "caresafeapp@gmail.com";

    public EmailNotifier(Context context) {
        this.context = context;
    }

    public void sendPanicButtonAlert(){
        String subject = context.getString(R.string.panicButtonSubject);
        String body = context.getString(R.string.panicButtonBody, aliasUser);

        sendEmail(toEmail, subject, body);
    }

    public void sendEmailGeoposAlert(){
        String subject = context.getString(R.string.geoposAlertSubject);
        String body = context.getString(R.string.geoposAlertBody, aliasUser);

        sendEmail(toEmail, subject, body);
    }

    public void sendEmailFallAlert(){
        String subject = context.getString(R.string.fallAlertSubject);
        String body = context.getString(R.string.fallAlertBody, aliasUser);

        sendEmail(toEmail, subject, body);
    }

    private void sendEmail(String toEmail, String subject, String body) {
        // Debemos de guardar estas credenciales en algún lado (BDA p.e.) y que no queden aquí
        String username = "caresafeapp@gmail.com";
        String password = "uuxw povz bviv seqv";

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                EmailSender.sendEmail(username, password, toEmail, subject, body);
                ((AppCompatActivity) context).runOnUiThread(() -> Toast.makeText(context, context.getString(R.string.toastEmailSend, toEmail), Toast.LENGTH_LONG).show());
            } catch (Exception e) {
                Log.e("EmailNotifier", e.getMessage(), e);
                ((AppCompatActivity) context).runOnUiThread(() -> Toast.makeText(context, context.getString(R.string.toastEmailError, toEmail), Toast.LENGTH_LONG).show());
            }
        });
    }
}