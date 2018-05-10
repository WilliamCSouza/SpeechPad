package com.example.william.speechpad;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class VisualizarLembrete extends Activity {

    private int idLembrete, idCategoria, idPrioridade;
    private DatabaseHelper dbh;
    private TextView lblDataActivity, lblHoraActivity, lblCategoriaActivity, lblPrioridadeActivity, lblLembreteActivity;
    private Button btnEditar, btnMenu, btnExcluir;
    private String categoria, lembrete, data, hora, prioridade;
    private long milisegundos;
    final Handler handler = new Handler();
    private ScheduleCliente schCliente = new ScheduleCliente(this);
    private String telaAnterior;
    Intent intent;
    NotificationManager nmNotificacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_lembrete);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//Forçar tela na Vertical
        schCliente.doBindService();
        idLembrete = getIntent().getIntExtra("id", 0);
        telaAnterior = getIntent().getStringExtra("telaAnterior");
        dbh = new DatabaseHelper(this);
        lblCategoriaActivity = (TextView) findViewById(R.id.lblCategoriaVisualizacao);
        lblPrioridadeActivity = (TextView) findViewById(R.id.lblPrioridadeVisualizacao);
        lblLembreteActivity = (TextView) findViewById(R.id.lblLembreteVisualizacao);
        lblDataActivity = (TextView) findViewById(R.id.lblData);
        lblHoraActivity = (TextView) findViewById(R.id.lblHora);
        btnEditar = (Button) findViewById(R.id.btnEditarLembreteVisualizacao);
        btnMenu = (Button) findViewById(R.id.btnVoltar);
        btnExcluir = (Button) findViewById(R.id.btnExcluirLembreteVisualizacao);
        nmNotificacao = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nmNotificacao.cancel(idLembrete);
        idCategoria = dbh.getLembrete(idLembrete).getIdCategoria();
        categoria = dbh.getLembrete(idLembrete).getNomeCategoria();
        idPrioridade = dbh.getLembrete(idLembrete).getIdPrioridade();
        prioridade = dbh.getLembrete(idLembrete).getNomePrioridade();
        milisegundos = dbh.getLembrete(idLembrete).getMiliSegundosLembrete();
        lembrete = dbh.getLembrete(idLembrete).getNotaLembrete();
        data = dbh.getLembrete(idLembrete).getDataLembrete();
        hora = dbh.getLembrete(idLembrete).getHoraLembrete();

        btnExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {excluirLembrete(); }
        });

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {editarLembrete(); }
        });

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {startActivity( new Intent(VisualizarLembrete.this, MenuInicial.class)); }
        });

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                carregarTela();
            }
        }, 1500);
    }

    public void carregarTela()
    {
        lblCategoriaActivity.setText(categoria);
        lblPrioridadeActivity.setText(prioridade);
        lblLembreteActivity.setText(lembrete);
        lblDataActivity.setText(data);
        lblHoraActivity.setText(hora);
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event)
    {
        if(keycode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            if(telaAnterior.equals("notificação"))
            {
                onDestroy();
                finishAffinity();
            }
            else if(telaAnterior.equals("lembrete"))
            {
                intent = new Intent(this, ListarLembretes.class);
                onDestroy();
                startActivity(intent);
                finish();
            }

        }
        return super.onKeyDown(keycode, event);
    }

    public void editarLembrete()
    {
        Intent intent = new Intent(VisualizarLembrete.this, EditarLembrete.class);
        intent.putExtra("idLembrete", idLembrete);
        intent.putExtra("idCategoria", idCategoria);
        intent.putExtra("idPrioridade", idPrioridade);
        intent.putExtra("categoria", categoria);
        intent.putExtra("prioridade", prioridade);
        intent.putExtra("lembrete", lembrete);
        intent.putExtra("data", data);
        intent.putExtra("hora", hora);
        intent.putExtra("milisegundos", milisegundos);
        intent.putExtra("telaAnterior", "lembreteTab1");
        startActivity(intent);
    }

    public void excluirLembrete()
    {
        if(telaAnterior.equals("notificação"))
        {
            schCliente.excluirAlarmForNotification(milisegundos, idLembrete, categoria, lembrete ,"EXCLUIR");
            dbh.deletarLembrete(idLembrete);
            Toast.makeText(this,"Lembrete excluído!",Toast.LENGTH_SHORT).show();
            finishAffinity();
        }
        else if(telaAnterior.equals("lembrete"))
        {
            intent = new Intent(this, ListarLembretes.class);
            schCliente.excluirAlarmForNotification(milisegundos, idLembrete, categoria, lembrete ,"EXCLUIR");
            dbh.deletarLembrete(idLembrete);
            Toast.makeText(this,"Lembrete excluído!",Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
        else
        {
            intent = new Intent(this, MenuInicial.class);
            schCliente.excluirAlarmForNotification(milisegundos, idLembrete, categoria, lembrete ,"EXCLUIR");
            dbh.deletarLembrete(idLembrete);
            Toast.makeText(this,"Lembrete excluído!",Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
    }

    @Override
    protected void onStop() {
        // When our activity is stopped ensure we also stop the connection to the service
        // this stops us leaking our activity into the system *bad*
        if(schCliente != null)
            schCliente.doUnbindService();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_visualizar_lembrete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
