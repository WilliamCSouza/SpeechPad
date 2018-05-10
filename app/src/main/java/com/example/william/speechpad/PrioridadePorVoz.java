package com.example.william.speechpad;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;


public class PrioridadePorVoz extends Activity implements TextToSpeech.OnInitListener {

    private TextView txtPrioridadePorVoz;
    protected static final int RESULT_SPEECH = 1;
    private TextToSpeech tts;
    private String prioridadePorVoz;
    private String nomeCategoria;
    public int DATA_CHECK_COD = 0;
    private DatabaseHelper dbh;
    final Handler handler = new Handler();
    private Button btnVoltar;
    private Intent intentPrincipal;
    private float velocidadeVoz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prioridade_por_voz);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//Forçar tela na Vertical
        txtPrioridadePorVoz = (TextView) findViewById(R.id.txtPrioridadePorVoz);
        tts = new TextToSpeech(this, this);
        Toast.makeText(this,"Informe a prioridade", Toast.LENGTH_SHORT).show();
        nomeCategoria = getIntent().getStringExtra("categoriaPorVoz");
        prioridadePorVoz="";
        velocidadeVoz=1.6f;
        intentPrincipal = new Intent(PrioridadePorVoz.this, MenuInicial.class);
        intentPrincipal.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentPrincipal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        dbh = new DatabaseHelper(this);
        btnVoltar = (Button) findViewById(R.id.btnVoltarPrioridade);
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Voltar();
            }
        });
    }

    public void Voltar()
    {
        onDestroy();
        startActivity(intentPrincipal);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        finish();
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event)
    {
        if(keycode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            startActivity(intentPrincipal);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            finish();
        }
        return super.onKeyDown(keycode, event);
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.getDefault());
            tts.setSpeechRate(velocidadeVoz);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    textoParaVozPrioridade(0, 1900);
                }
            }, 1200);
        }
        if (status == TextToSpeech.ERROR) {
            Toast.makeText(this, "Erro na função por voz.", Toast.LENGTH_SHORT).show();
        }
    }

    //Frases específicas para cada ocasião no setor CATEGORIA
    public String listaFrasesTTSPrioridade(int i) {
        String falarPrioridade = "Informe a Prioridade";//Posição 0
        String erroPrioridade = "Prioridade inexistente. Informe uma prioridade ALTA, MÉDIA ou BAIXA";
        String[] frases = {falarPrioridade, erroPrioridade};
        return frases[i];
    }

    public void textoParaVozPrioridade(int posicaoFrase, int tempoHandler) {
        tts.speak(listaFrasesTTSPrioridade(posicaoFrase), TextToSpeech.QUEUE_FLUSH, null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                vozParaTextoPrioridade();
            }
        }, tempoHandler);
    }

    public void vozParaTextoPrioridade() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Informe a Prioridade");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "pt-BR");
        startActivityForResult(intent, RESULT_SPEECH);
        txtPrioridadePorVoz.setText("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_SPEECH) {
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> textoSTT = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                txtPrioridadePorVoz.setText(textoSTT.get(0).toUpperCase());
                prioridadePorVoz = txtPrioridadePorVoz.getText().toString().trim().toUpperCase();

                if (requestCode == DATA_CHECK_COD) {
                    if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                        // Caso tenha o TTS - Cria a referência
                        tts = new TextToSpeech(this, this);
                    } else {
                        // Sem Dados do TTS - Solicita a instalação
                        Intent installTTSIntent = new Intent();
                        installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                        startActivity(installTTSIntent);
                    }
                }
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
        validarPrioridade(prioridadePorVoz);
    }

    public void validarPrioridade(String nome)
    {
        if(dbh.getPrioridadePorNome(nome) == false)
        {
            textoParaVozPrioridade(1, 6200);
            //Primeiro parâmetro = Frase que a API TTS falará
            //Segundo parâmetro = Tempo (em milisegundos) que o handler vai segurar a API STT
        }
        else
        if(dbh.getPrioridadePorNome(nome) == true)
        {
            proximaEtapaLembrete();
        }
    }

    public void proximaEtapaLembrete() {
        onDestroy();
        Intent intent = new Intent(this, LembretePorVoz.class);
        intent.putExtra("nomeCategoria", nomeCategoria);
        intent.putExtra("prioridadePorVoz", prioridadePorVoz);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_prioridade_por_voz, menu);
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
