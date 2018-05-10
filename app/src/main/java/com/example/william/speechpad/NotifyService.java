package com.example.william.speechpad;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class NotifyService extends Service {

    public class ServiceBinder extends Binder {
        NotifyService getService() {
            return NotifyService.this;
        }
    }

    public static final String INTENT_NOTIFY = "com.example.william.speechpad.INTENT_NOTIFY";
    private NotificationManager mNM;
    private String telaAnteriorNotificacao;

    @Override
    public void onCreate() {
        Log.i("NotifyService", "onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        String classe = intent.getStringExtra("classe").toUpperCase().trim();

        if(intent.getBooleanExtra(INTENT_NOTIFY, false))
        {
            CharSequence titulo = "";
            CharSequence texto = "";
            int xId = 0;
            telaAnteriorNotificacao = "notificação";
            titulo = intent.getCharSequenceExtra("categoria");
            texto = intent.getCharSequenceExtra("lembrete");
            xId = intent.getIntExtra("idLembrete", 0);
            showNotification(xId, titulo, texto);
        }
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IBinder mBinder = new ServiceBinder();

    private void showNotification(int idLemb, CharSequence cat, CharSequence lemb) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        Uri som = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mNM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int id = idLemb;
        mBuilder.setContentTitle(cat);
        mBuilder.setContentText(lemb);
        mBuilder.setSmallIcon(R.drawable.ic_launcher);
        mBuilder.setAutoCancel(true);
        mBuilder.setSound(som);
        Intent intent = new Intent(this, VisualizarLembrete.class);
        intent.putExtra("id", id);
        intent.putExtra("telaAnterior",telaAnteriorNotificacao);
        intent.setAction(Long.toString(System.currentTimeMillis()));
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        mNM.notify(id, mBuilder.build());
    }
}