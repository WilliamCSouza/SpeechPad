package com.example.william.speechpad;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class Configuracao extends Activity {

    private Button btnSalvarConfiguracao;
    private List<String> ArrayPrioridade = new ArrayList<String>();
    private DatabaseHelper dbh;
    private EditText txtNome;
    private Switch switchLembrete;
    private int estadoLembreteReduzido;
    private Spinner spinnerPrioridade, spinnerCategoria;
    private TextView lblCategoria, lblPrioridade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracao);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//Forçar tela na Vertical
        dbh = new DatabaseHelper(this);
        txtNome = (EditText) findViewById(R.id.txtNomeUsuario);
        switchLembrete = (Switch) findViewById(R.id.swithLembreteVoz);
        spinnerPrioridade = (Spinner) findViewById(R.id.spPrioridadeConfig);
        spinnerCategoria = (Spinner) findViewById(R.id.spCategoriaConfig);
        lblCategoria = (TextView) findViewById(R.id.lblCategoriaConfig);
        lblPrioridade = (TextView) findViewById(R.id.lblPrioridadeConfig);
        String[] arrayCategoria = new String[dbh.getTodasCategorias().size()];
        txtNome.setText(dbh.getNomeUsuario());
        btnSalvarConfiguracao = (Button) findViewById(R.id.btnSalvarConfiguracao);
        btnSalvarConfiguracao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SalvarConfiguracao();
            }
        });
        spinnerCategoria.setVisibility(View.INVISIBLE);
        spinnerPrioridade.setVisibility(View.INVISIBLE);
        lblCategoria.setVisibility(View.INVISIBLE);
        lblPrioridade.setVisibility(View.INVISIBLE);
        ArrayPrioridade.add(dbh.getNomePrioridade(0));
        ArrayPrioridade.add(dbh.getNomePrioridade(1));
        ArrayPrioridade.add(dbh.getNomePrioridade(2));
        estadoLembreteReduzido=Integer.parseInt(dbh.estadoLembreteReduzido());
        ArrayAdapter<String> adapterPrioridade = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ArrayPrioridade);
        spinnerPrioridade.setAdapter(adapterPrioridade);
        for (int i=0;i<dbh.getTodasCategorias().size();i++)
        {
            arrayCategoria[i] = dbh.getTodasCategorias().get(i).getNomeCategoria();
        }

        ArrayAdapter<String> adapterCategoria = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayCategoria);
        spinnerCategoria.setAdapter(adapterCategoria);

        if(estadoLembreteReduzido==1)
        {
            switchLembrete.setChecked(true);
            spinnerPrioridade.setVisibility(View.VISIBLE);
            spinnerCategoria.setVisibility(View.VISIBLE);
            lblCategoria.setVisibility(View.VISIBLE);
            lblPrioridade.setVisibility(View.VISIBLE);

            if(dbh.getPrioridadePreSelecionada().equals("ALTA"))
            {
                spinnerPrioridade.setSelection(0);
            }
            else if (dbh.getPrioridadePreSelecionada().equals("MÉDIA"))
            {
                spinnerPrioridade.setSelection(1);
            }
            else if(dbh.getPrioridadePreSelecionada().equals("BAIXA"))
            {
                spinnerPrioridade.setSelection(2);
            }
            spinnerCategoria.setSelection(adapterCategoria.getPosition(dbh.getCategoriaPreSelecionada()));
        }

        else if (estadoLembreteReduzido==0)
        {
            switchLembrete.setChecked(false);
            spinnerCategoria.setVisibility(View.INVISIBLE);
            spinnerPrioridade.setVisibility(View.INVISIBLE);
            lblCategoria.setVisibility(View.INVISIBLE);
            lblPrioridade.setVisibility(View.INVISIBLE);
        }
        //Switch Visível/Invisível
        switchLembrete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    spinnerPrioridade.setVisibility(View.VISIBLE);
                    spinnerCategoria.setVisibility(View.VISIBLE);
                    lblCategoria.setVisibility(View.VISIBLE);
                    lblPrioridade.setVisibility(View.VISIBLE);
                }
                else
                {
                    spinnerCategoria.setVisibility(View.INVISIBLE);
                    spinnerPrioridade.setVisibility(View.INVISIBLE);
                    lblCategoria.setVisibility(View.INVISIBLE);
                    lblPrioridade.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public void SalvarConfiguracao()
    {
        if(switchLembrete.isChecked())
        {
            dbh.atualizarNomeUsuario(txtNome.getText().toString().trim());;
            dbh.atualizarCategoriaPreSelecionada(spinnerCategoria.getSelectedItem().toString().trim().toUpperCase());
            dbh.atualizarPrioridadePreSelecionada(spinnerPrioridade.getSelectedItem().toString().trim().toUpperCase());
            dbh.atualizarStatusLembreteReduzido(1);
            Toast.makeText(this,"Dados salvos com sucesso!",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Configuracao.this, MenuInicial.class));
        }
        else
        {
            dbh.atualizarNomeUsuario(txtNome.getText().toString().trim());;
            dbh.atualizarStatusLembreteReduzido(0);
            Toast.makeText(this,"Nome salvo com sucesso!",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Configuracao.this, MenuInicial.class));
        }
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event)
    {
        if(keycode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            startActivity(new Intent(this, MenuInicial.class));
            Configuracao.this.finish();
            return true;
        }
        return super.onKeyDown(keycode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_configuracao, menu);
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
