package com.example.william.speechpad;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class AlarmTask implements Runnable{

    private long date;
    private AlarmManager am;
    private Context context;
    private String classe;
    private CharSequence categoria, lembrete;
    private int idLembrete;

    public AlarmTask(Context context, long date, int idLembrete, CharSequence categoria, CharSequence lembrete, String classe) {
        this.categoria="";
        this.lembrete="";
        this.classe="";
        this.idLembrete=0;
        this.categoria = categoria;
        this.lembrete = lembrete;
        this.context = context;
        this.classe=classe;
        this.idLembrete = idLembrete;
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        this.date = date;
        this.am = am;
    }

    @Override
    public void run() {
        Intent intent = new Intent(context, NotifyService.class);
        intent.putExtra(NotifyService.INTENT_NOTIFY, true);
        intent.putExtra("classe", classe);
        intent.putExtra("categoria",categoria);
        intent.putExtra("lembrete", lembrete);
        intent.putExtra("idLembrete", idLembrete);
        //intent.setAction("teste");
        intent.setAction(Integer.toString(idLembrete));
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_UPDATE_CURRENT);
        am.set(AlarmManager.RTC, date, pendingIntent);
    }

    public void cancel() {
        Intent intent = new Intent(context, NotifyService.class);
        intent.putExtra(NotifyService.INTENT_NOTIFY, true);
        intent.putExtra("classe", classe);
        intent.putExtra("categoria",categoria);
        intent.putExtra("lembrete", lembrete);
        intent.putExtra("idLembrete", idLembrete);
        intent.setAction(Integer.toString(idLembrete));
        PendingIntent displayIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_NO_CREATE);
        am.cancel(displayIntent);
    }
}