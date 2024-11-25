package es.udc.psi.caresafe.services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

public interface serviceManager extends ServiceConnection {
    public Intent getServiceIntent();
    public void stopService();
}
