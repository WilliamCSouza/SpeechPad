package com.example.william.speechpad;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.NotificationManager;
import android.content.Context;
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
import java.util.Date;
import java.util.List;

public class EditarLembrete extends Activity {

    private Lembrete lembrete = new Lembrete();
    private List<String> ArrayPrioridade = new ArrayList<String>();
    private Button btnSalvarLembreteTexto;
    private Spinner spinnerPrioridade, spinnerCategoria;
    private int idSelecionado, idCategoriaSelecionada, idPrioridadeSelecionada;
    private String categoriaSelecionada, lembreteSelecionado, horaEscolhida, dataEscolhida;
    private DatabaseHelper dbh;
    private TextView lblDataActivity, lblHoraActivity;
    private EditText txtDescricaoLembrete;
    private String telaAnteriorTabs;
    private int[] arrayIdCategoria;
    private ScheduleCliente schCliente = new ScheduleCliente(this);
    private NotificationManager mNM;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_lembrete);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//Forçar tela na Vertical
        schCliente.doBindService();
        lblHoraActivity = (TextView) findViewById(R.id.lblHora);
        lblDataActivity = (TextView) findViewById(R.id.lblData);
        txtDescricaoLembrete = (EditText) findViewById(R.id.txtEditarLembrete);
        btnSalvarLembreteTexto = (Button) findViewById(R.id.btnSalvarLembretePorTextoEdit);
        spinnerPrioridade = (Spinner) findViewById(R.id.spEditarPrioridadeLembrete);//Faz da View spPrioriodade um tipo Spinner
        spinnerCategoria = (Spinner) findViewById(R.id.spCategoria);
        dbh = new DatabaseHelper(this);
        mNM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        telaAnteriorTabs = getIntent().getStringExtra("telaAnterior");
        idSelecionado = getIntent().getIntExtra("idLembrete", 0);
        idCategoriaSelecionada = getIntent().getIntExtra("idCategoria", 0);
        idPrioridadeSelecionada = getIntent().getIntExtra("idPrioridade", 0);
        categoriaSelecionada = getIntent().getStringExtra("categoria");
        lembreteSelecionado = getIntent().getStringExtra("lembrete");
        dataEscolhida = getIntent().getStringExtra("data");
        horaEscolhida = getIntent().getStringExtra("hora");
        String[] arrayCategoria = new String[dbh.getTodasCategorias().size()];
        arrayIdCategoria = new int[dbh.getTodasCategorias().size()];

        //Adiciona no ArrayList Prioridade
        ArrayPrioridade.add(dbh.getNomePrioridade(0));
        ArrayPrioridade.add(dbh.getNomePrioridade(1));
        ArrayPrioridade.add(dbh.getNomePrioridade(2));
        ArrayAdapter<String> adapterPrioridade = new ArrayAdapter<String>(EditarLembrete.this, android.R.layout.simple_spinner_dropdown_item, ArrayPrioridade);
        spinnerPrioridade.setAdapter(adapterPrioridade);

        for (int i=0;i<dbh.getTodasCategorias().size();i++)
        {
            arrayCategoria[i] = dbh.getTodasCategorias().get(i).getNomeCategoria();
            arrayIdCategoria[i] = dbh.getTodasCategorias().get(i).getIdCategoria();
        }

        ArrayAdapter<String> adapterCategoria = new ArrayAdapter<String>(EditarLembrete.this, android.R.layout.simple_spinner_dropdown_item, arrayCategoria);
        spinnerCategoria.setAdapter(adapterCategoria);
        btnSalvarLembreteTexto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AtualizarLembrete();
            }
        });

        spinnerCategoria.setSelection(adapterCategoria.getPosition(categoriaSelecionada));
        spinnerPrioridade.setSelection(idPrioridadeSelecionada);
        txtDescricaoLembrete.setText(lembreteSelecionado);
        lblDataActivity.setText(dataEscolhida);
        lblHoraActivity.setText(horaEscolhida);
    }

    //Método disparado ao clicar no botão DATA
    public void MostrarData(View v)
    {
        DialogFragment pickerData = new DataPicker(EditarLembrete.this);
        pickerData.show(getFragmentManager(), "datapicker");
    }

    public void MostrarHora(View v) {
        DialogFragment pickerHora = new HoraPicker(EditarLembrete.this);
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
            Toast.makeText(this, new StringBuilder().append("Problema para formatar data e hora. Erro: "+e), Toast.LENGTH_SHORT).show();
        }
        return mili;
    }

    public void AtualizarLembrete()
    {
        if (txtDescricaoLembrete.getText().toString().trim().matches(""))
        {
            Toast.makeText(this, "Campo em branco!", Toast.LENGTH_SHORT).show();
            txtDescricaoLembrete.requestFocus();
        }
        else if(!txtDescricaoLembrete.getText().toString().trim().matches("")) {
            lembrete.setNomeCategoria(spinnerCategoria.getSelectedItem().toString());
            int x = dbh.getIdCategoriaPorNome(lembrete.getNomeCategoria().trim());
            lembrete.setIdCategoria(x);
            lembrete.setIdLembrete(idSelecionado);
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
            mNM.cancel(idSelecionado);
            dbh.atualizarLembrete(lembrete);
            Toast.makeText(this, "Lembrete atualizado!", Toast.LENGTH_SHORT).show();
            schCliente.editAlarmForNotification(StringToMili(lblDataActivity.getText().toString().trim(), lblHoraActivity.getText().toString().trim()), idSelecionado, spinnerCategoria.getSelectedItem().toString(), txtDescricaoLembrete.getText().toString(), "EDITAR");
            onStop();
            Intent intent = new Intent(this, ListarLembretes.class);
            if(telaAnteriorTabs.equals("lembreteTab1"))
            {
                intent.putExtra("telaAnterior", "lembreteTab1");
            }
            else if(telaAnteriorTabs.equals("lembreteTab2"))
            {
                intent.putExtra("telaAnterior", "lembreteTab2");
            }
            else if(telaAnteriorTabs.equals("lembreteTab3"))
            {
                intent.putExtra("telaAnterior", "lembreteTab3");
            }
            else
            {
                intent.putExtra("telaAnterior","menu");
            }
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
    public boolean onKeyDown(int keycode, KeyEvent event)
    {
        if(keycode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            Intent intent = new Intent(this, ListarLembretes.class);
            if(telaAnteriorTabs.equals("lembreteTab1"))
            {
                intent.putExtra("telaAnterior", "lembreteTab1");
            }
            else if(telaAnteriorTabs.equals("lembreteTab2"))
            {
                intent.putExtra("telaAnterior", "lembreteTab2");
            }
            else if(telaAnteriorTabs.equals("lembreteTab3"))
            {
                intent.putExtra("telaAnterior", "lembreteTab3");
            }
            else
            {
                intent.putExtra("telaAnterior","menu");
            }
            startActivity(intent);

        }
        return super.onKeyDown(keycode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_editar_lembrete, menu);
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
