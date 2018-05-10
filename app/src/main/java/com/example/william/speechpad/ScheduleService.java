package com.example.william.speechpad;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class ScheduleService extends Service {

    public class ServiceBinder extends Binder {
        ScheduleService getService() {
            return ScheduleService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("ScheduleService", "Received start id " + startId + ": " + intent);

        // We want this service to continue running until it is explicitly stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients. See
    private final IBinder mBinder = new ServiceBinder();

    /**
     * Show an alarm for a certain date when the alarm is called it will pop up a notification
     */
    public void setAlarm(long milisegundos, int idLembrete, CharSequence categoria, CharSequence lembrete, String classe) {
        // This starts a new thread to set the alarm
        // You want to push off your tasks onto a new thread to free up the UI to carry on responding
        new AlarmTask(this, milisegundos, idLembrete, categoria, lembrete, classe).run();
    }

    public void editAlarmForNotification(long milisegundos, int idLembrete, CharSequence categoria, CharSequence lembrete, String classe) {
        new AlarmTask(this, milisegundos, idLembrete, categoria, lembrete, classe).run();
    }

    public void excluirAlarmForNotification(long milisegundos, int idLembrete, CharSequence categoria, CharSequence lembrete, String classe) {
        new AlarmTask(this, milisegundos, idLembrete, categoria, lembrete, classe).cancel();
    }
}
