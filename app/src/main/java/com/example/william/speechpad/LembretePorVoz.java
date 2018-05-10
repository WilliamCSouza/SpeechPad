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

public class LembretePorVoz extends Activity implements TextToSpeech.OnInitListener {

    private TextView txtLembretePorVoz;
    protected static final int RESULT_SPEECH = 1;
    private TextToSpeech tts;
    private String lembretePorVoz;
    private String nomeCategoria;
    private String nomePrioridade;
    public int DATA_CHECK_COD = 0;
    final Handler handler = new Handler();
    private Button btnVoltar;
    private Intent intentPrincipal;
    private float velocidadeVoz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lembrete_por_voz);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//Forçar tela na Vertical
        Toast.makeText(this,"Informe o lembrete", Toast.LENGTH_SHORT).show();
        txtLembretePorVoz = (TextView) findViewById(R.id.txtLembretePorVoz);
        tts = new TextToSpeech(this, this);
        nomeCategoria = getIntent().getStringExtra("nomeCategoria");
        nomePrioridade = getIntent().getStringExtra("prioridadePorVoz");
        intentPrincipal = new Intent(LembretePorVoz.this, MenuInicial.class);
        intentPrincipal.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentPrincipal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        lembretePorVoz="";
        velocidadeVoz=1.6f;
        btnVoltar = (Button) findViewById(R.id.btnVoltarLembrete);
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { Voltar();}
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
                    textoParaVozLembrete(0, 1800);
                }
            }, 1200);
        }
        if (status == TextToSpeech.ERROR) {
            Toast.makeText(this, "Erro na função por voz.", Toast.LENGTH_SHORT).show();
        }
    }

    //Frases específicas para cada ocasião no setor CATEGORIA
    public String listaFrasesTTSLembrete(int i) {
        String falarLembrete = "Informe o lembrete.";//Posição 0
        String erroLembrete = "Lembrete está branco. ";
        String[] frases = {falarLembrete, erroLembrete};
        return frases[i];
    }

    public void textoParaVozLembrete(int posicaoFrase, int tempoHandler) {
        tts.speak(listaFrasesTTSLembrete(posicaoFrase), TextToSpeech.QUEUE_FLUSH, null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                vozParaTextoLembrete();
            }
        }, tempoHandler);
    }

    public void vozParaTextoLembrete() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Informe o Lembrete");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "pt-BR");
        startActivityForResult(intent, RESULT_SPEECH);
        txtLembretePorVoz.setText("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_SPEECH) {
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> textoSTT = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                txtLembretePorVoz.setText(textoSTT.get(0));
                lembretePorVoz = txtLembretePorVoz.getText().toString().trim();

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
        validarLembrete(lembretePorVoz);
    }

    public void validarLembrete(String nome)
    {
        if(nome.trim().equals(""))
        {
            textoParaVozLembrete(1, 2200);
            //Primeiro parâmetro = Frase que a API TTS falará
            //Segundo parâmetro = Tempo (em milisegundos) que o handler vai segurar a API STT
        }
        else
        if(!nome.trim().equals(""))
        {
            proximaEtapaData();
        }
    }


    public void proximaEtapaData()
    {
        onDestroy();
        Intent intent = new Intent(this, DataPorVoz.class);
        intent.putExtra("nomeCategoria", nomeCategoria);
        intent.putExtra("nomePrioridade", nomePrioridade);
        intent.putExtra("lembretePorVoz", lembretePorVoz);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        finish();
    }

    @Override
    public void onDestroy()
    {
        if(tts != null)
        {
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lembrete_por_voz, menu);
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
