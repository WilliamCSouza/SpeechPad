package com.example.william.speechpad;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SalvarPorTexto extends Activity {

    private List<String> ArrayPrioridade = new ArrayList<String>();
    private Button btnSalvarLembreteTexto;
    private Spinner spinnerPrioridade,spinnerCategoria;
    private DatabaseHelper dbh;
    private TextView lblDataActivity, lblHoraActivity;
    private EditText txtDescricaoLembrete;
    private int[] arrayIdCategoria;
    private int ano, mes, dia, horas, minuto; //, miliDia, miliMes, miliAno, miliHora, miliMinuto;
    //private long milisegundos;
    private Lembrete lembrete = new Lembrete();
    private ScheduleCliente schCliente = new ScheduleCliente(SalvarPorTexto.this);

    public SalvarPorTexto(){}

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salvar_por_texto);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//Forçar tela na Vertical
        schCliente.doBindService();
        lblHoraActivity = (TextView) findViewById(R.id.lblHora);
        lblDataActivity = (TextView) findViewById(R.id.lblData);
        txtDescricaoLembrete = (EditText) findViewById(R.id.txtLembrete);
        btnSalvarLembreteTexto = (Button) findViewById(R.id.btnSalvarLembretePorTexto);
        spinnerPrioridade = (Spinner) findViewById(R.id.spPrioridade);//Faz da View spPrioriodade um tipo Spinner
        spinnerCategoria = (Spinner) findViewById(R.id.spCategoria);
        dbh = new DatabaseHelper(this);
        String[] arrayCategoria = new String[dbh.getTodasCategorias().size()];
        arrayIdCategoria = new int[dbh.getTodasCategorias().size()];
        //Adiciona no ArrayList Prioridade
        ArrayPrioridade.add(dbh.getNomePrioridade(0));
        ArrayPrioridade.add(dbh.getNomePrioridade(1));
        ArrayPrioridade.add(dbh.getNomePrioridade(2));
        ArrayAdapter<String> adapterPrioridade = new ArrayAdapter<String>(SalvarPorTexto.this, android.R.layout.simple_spinner_dropdown_item, ArrayPrioridade);
        spinnerPrioridade.setAdapter(adapterPrioridade);
        for (int i=0;i<dbh.getTodasCategorias().size();i++)
        {
            arrayCategoria[i] = dbh.getTodasCategorias().get(i).getNomeCategoria();
            arrayIdCategoria[i] = dbh.getTodasCategorias().get(i).getIdCategoria();
        }

        ArrayAdapter<String> adapterCategoria = new ArrayAdapter<String>(SalvarPorTexto.this, android.R.layout.simple_spinner_dropdown_item, arrayCategoria);
        spinnerCategoria.setAdapter(adapterCategoria);
        btnSalvarLembreteTexto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { CriarLembrete(); }
        });//Método que chama a Activity Salvar Por Texto

        //Deixando os Pickers com a data e hora atual
        final Calendar calendario = java.util.Calendar.getInstance();
        ano = calendario.get(java.util.Calendar.YEAR);
        mes = calendario.get(java.util.Calendar.MONTH);
        dia = calendario.get(java.util.Calendar.DAY_OF_MONTH);
        horas = calendario.get(Calendar.HOUR_OF_DAY);
        minuto = calendario.get(Calendar.MINUTE);

        lblDataActivity.setText(new StringBuilder().append(getDia()).append("/").append(getMes()+1).append("/").append(getAno()));
        if (minuto<= 9 && minuto >= 0) {
            lblHoraActivity.setText(new StringBuilder().append(getHoras()).append(":").append("0" + getMinuto()));
        }
        else
        {
            lblHoraActivity.setText(new StringBuilder().append(getHoras()).append(":").append(getMinuto()).append(" "));
        }
    }

    //Método disparado ao clicar no botão DATA
    public void MostrarData(View v)
    {
        DialogFragment pickerData = new DataPicker(SalvarPorTexto.this);
        pickerData.show(getFragmentManager(), "datapicker");
    }

    public void MostrarHora(View v) {
        DialogFragment pickerHora = new HoraPicker(SalvarPorTexto.this);
        pickerHora.show(getFragmentManager(), "timepicker");
    }

    public long StringToMili(String data, String hora)
    {
        long mili=0;
        String dataString = data+" "+hora;
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            Date dataCovertida = formato.parse(dataString);
            mili = dataCovertida.getTime();
        }catch (Exception e) {
            Toast.makeText(this, new StringBuilder().append("Problema para formatar data e hora. Erro: " + e), Toast.LENGTH_SHORT).show();
        }
        return mili;
    }

    public long notificarComAntecedecia (long ms)
    {
         ms =  ms * 1000;
         ms =  ms - (60*5);
        return ms;
    }

    public void CriarLembrete() {
        if (txtDescricaoLembrete.getText().toString().trim().matches("")) {
            Toast.makeText(this, "Campo do lembrete está em branco.", Toast.LENGTH_SHORT).show();
            txtDescricaoLembrete.requestFocus();
        } else if (!txtDescricaoLembrete.getText().toString().matches("")) {
            lembrete.setNomeCategoria(spinnerCategoria.getSelectedItem().toString());
            int x = dbh.getIdCategoriaPorNome(lembrete.getNomeCategoria().trim());
            lembrete.setIdCategoria(x);
            lembrete.setNotaLembrete(txtDescricaoLembrete.getText().toString());
            if (spinnerPrioridade.getSelectedItem().toString().trim().toUpperCase().equals("ALTA")) {
                lembrete.setNomePrioridade("ALTA");
                lembrete.setIdPrioridade(0);
            } else if (spinnerPrioridade.getSelectedItem().toString().trim().toUpperCase().equals("MÉDIA")) {
                lembrete.setNomePrioridade("MÉDIA");
                lembrete.setIdPrioridade(1);
            } else if (spinnerPrioridade.getSelectedItem().toString().trim().toUpperCase().equals("BAIXA")) {
                lembrete.setNomePrioridade("BAIXA");
                lembrete.setIdPrioridade(2);
            }
            lembrete.setHoraLembrete(lblHoraActivity.getText().toString());
            lembrete.setDataLembrete(lblDataActivity.getText().toString());
            lembrete.setMiliSegundosLembrete(StringToMili(lblDataActivity.getText().toString().trim(), lblHoraActivity.getText().toString().trim()));
            dbh.criarLembrete(lembrete);
            Toast.makeText(this, "Lembrete salvo!", Toast.LENGTH_SHORT).show();
            schCliente.setAlarmForNotification(StringToMili(lblDataActivity.getText().toString().trim(), lblHoraActivity.getText().toString().trim()), dbh.getIdUltimoLembrete(), spinnerCategoria.getSelectedItem().toString(), txtDescricaoLembrete.getText().toString(), "SALVAR");
            onStop();
            startActivity(new Intent(this, MenuInicial.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_salvar_por_texto, menu);
        return true;
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event)
    {
        if(keycode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            startActivity(new Intent(this, MenuInicial.class));
            SalvarPorTexto.this.finish();
        }
        return super.onKeyDown(keycode, event);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        // When our activity is stopped ensure we also stop the connection to the service
        // this stops us leaking our activity into the system *bad*
        if(schCliente != null)
            schCliente.doUnbindService();
        super.onStop();
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public int getHoras() {
        return horas;
    }

    public void setHoras(int horas) {
        this.horas = horas;
    }

    public int getMinuto() {
        return minuto;
    }

    public void setMinuto(int minuto) {
        this.minuto = minuto;
    }
}