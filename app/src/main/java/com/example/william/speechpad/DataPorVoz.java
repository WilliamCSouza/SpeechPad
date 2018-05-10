package com.example.william.speechpad;

import android.app.Activity;
import android.content.Intent;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class DataPorVoz extends Activity implements TextToSpeech.OnInitListener {

    private TextView txtDataPorVoz;
    protected static final int RESULT_SPEECH = 1;
    private TextToSpeech tts;
    private String descicaoLembrete;
    private String nomeCategoria;
    private String nomePrioridade;
    private String dataPorVoz;
    private String dataTipo;
    public int DATA_CHECK_COD = 0;
    private boolean deuRuim;
    private String[] StringMeses = {"JANEIRO", "FEVEREIRO", "MARÇO", "ABRIL", "MAIO", "JUNHO", "JULHO", "AGOSTO", "SETEMBRO", "OUTUBRO", "NOVEMBRO", "DEZEMBRO"};
    private String[] intMeses = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
    final Handler handler = new Handler();
    private Calendar cal;
    private Button btnVoltar;
    private Intent intentPrincipal;
    private float velocidadeVoz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_por_voz);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Toast.makeText(this,"Informe a data", Toast.LENGTH_SHORT).show();
        txtDataPorVoz = (TextView) findViewById(R.id.txtDataPorVoz);
        tts = new TextToSpeech(this, this);
        nomeCategoria = getIntent().getStringExtra("nomeCategoria");
        nomePrioridade = getIntent().getStringExtra("nomePrioridade");
        descicaoLembrete = getIntent().getStringExtra("lembretePorVoz");
        intentPrincipal = new Intent(DataPorVoz.this, MenuInicial.class);
        intentPrincipal.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentPrincipal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        velocidadeVoz=1.6f;
        dataPorVoz="";
        dataTipo="";
        btnVoltar = (Button) findViewById(R.id.btnVoltarData);
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { Voltar(); }
        });
        deuRuim = false;
        cal = Calendar.getInstance();
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
                    textoParaVozData(0, 1400);
                }
            }, 1000);
        }
        if (status == TextToSpeech.ERROR) {
            Toast.makeText(this, "Erro na função por voz.", Toast.LENGTH_SHORT).show();
        }
    }

    //Frases específicas para cada ocasião no setor CATEGORIA
    public String listaFrasesTTSData(int i) {
        String falarData = "Informe a data";//Posição 0
        String erroData1 = "Data em branco. Informe uma data";
        String erroData2 = "Data inválida. Informe novamente";
        String[] frases = {falarData, erroData2, erroData1};
        return frases[i];
    }

    public void textoParaVozData(int posicaoFrase, int tempoHandler) {
        tts.speak(listaFrasesTTSData(posicaoFrase), TextToSpeech.QUEUE_FLUSH, null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                vozParaTextoData();
            }
        }, tempoHandler);
    }

    public void vozParaTextoData() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Informe a Data");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "pt-BR");
        startActivityForResult(intent, RESULT_SPEECH);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_SPEECH) {
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> textoSTT = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                txtDataPorVoz.clearComposingText();
                txtDataPorVoz.setText(textoSTT.get(0));
                dataPorVoz = txtDataPorVoz.getText().toString().trim().toUpperCase();
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
        validarDataEtapa1(dataPorVoz);
    }

    public String StringToData(String data)
    {
        String dataConvertida="";
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");

        try {
            dataConvertida = formato.parse(data).toString();
                deuRuim = false;

        }catch (Exception e){
            Toast.makeText(this,new StringBuilder().append("Problema para formatar a data. Tente novamente."),Toast.LENGTH_SHORT).show();
            deuRuim = true;
        }
        return dataConvertida;
    }

    public void validarDataEtapa1(String data)
    {
        if (data.equals("")) {  textoParaVozData(1, 3000);}
        else {
            deuRuim = false;
            validarDataEtapa2(data);
        }
    }

    public void validarDataEtapa2(String data) {

        String textoData = data;
        if (textoData.trim().equals("HOJE")) {
            int i = 0;
            boolean fimLooping = false;
            int ano, mes, dia;
            String[] frase = textoData.split(" ");
            do {
                if (frase[i].equals("HOJE")) {
                    dia = cal.get(Calendar.DAY_OF_MONTH);
                    mes = cal.get(Calendar.MONTH) + 1;
                    ano = cal.get(Calendar.YEAR);
                    textoData = dia + "/" + mes + "/" + ano;
                    i=frase.length;
                    fimLooping = true;
                    dataTipo="hoje";
                }
                else{
                    i++;
                }
            } while (i != frase.length);
            if (fimLooping == true)
            {
                StringToData(textoData);
                if (deuRuim == true) {
                    textoParaVozData(2, 3100);
                } else if (deuRuim == false) {
                    dataPorVoz = textoData;
                    proximaEtapaData();
                }
            }
        }
        else if (!textoData.trim().equals("HOJE"))
        {
            validarDataEtapa3(textoData);
        }
    }

    public void validarDataEtapa3(String data)
    {
        String textoData = data;
        if (textoData.equals("AMANHÃ")) {
            int i = 0;
            boolean fimLooping = false;
            String[] frase = data.split(" ");
            do {
                if (frase[i].equals("AMANHÃ")) {
                    int ano, mes, dia;
                    dia = cal.get(Calendar.DAY_OF_MONTH)+ 1;
                    mes = cal.get(Calendar.MONTH)+ 1;
                    ano = cal.get(Calendar.YEAR);
                    i=frase.length;
                    textoData = dia + "/" + mes + "/" + ano;
                    fimLooping = true;
                    dataTipo="amanhã";
                }
                else {
                    i++;
                }
            } while (i != frase.length);
            if (fimLooping == true) {
                StringToData(textoData);
                if (deuRuim == true) {
                    textoParaVozData(1, 3100);
                }
                else if (deuRuim == false) {
                    dataPorVoz = textoData;
                    proximaEtapaData();
                }
            }
        }
        else if (!textoData.equals("AMANHÃ"))
        {
            validarDataEtapa4(textoData);
        }
    }

    public void validarDataEtapa4(String data) {
        String textoData = data;
        String[] frase = textoData.split(" ");
        if (frase[2].length() >= 4) {
            String textoNovo = "";
            boolean fimLooping = false;
            for (int j = 0; j < StringMeses.length; j++) {
                if (frase[2].equals(StringMeses[j])) {
                    frase[2] = intMeses[j];
                    fimLooping = true;
                    j = frase.length;
                    for (int p = 0; p < frase.length; p++) {
                        textoNovo = textoNovo + "" + frase[p];
                    }
                }
                textoNovo = textoNovo.trim();
                textoNovo = textoNovo.replace("DE", "/");
                textoNovo = textoNovo.replace("DO", "/");
                textoData = textoNovo;
            }
            if (fimLooping == true) {
                StringToData(textoData);
               if (deuRuim == true)
                {
                    textoParaVozData(2,3100);
                }
                else if (deuRuim == false) {
                    dataPorVoz = textoData;
                    proximaEtapaData();
                    Toast.makeText(this, textoData, Toast.LENGTH_SHORT).show();
                }
            }
        }
        else if (frase[2].length() <4 )
        {
            validarDataEtapa5(textoData);
        }
        else
        {
            deuRuim=true;
            textoParaVozData(2, 3100);
        }
    }

    public void validarDataEtapa5 (String data)
    {
        data = data.trim();
        data = data.replace("DE", "/");
        data= data.replace("DO", "/");
        data= data.replaceAll("\\s", "");
        Toast.makeText(this, data, Toast.LENGTH_SHORT).show();
        StringToData(data);
        if (deuRuim == true) {
            textoParaVozData(1, 3100);
        } else if (deuRuim == false) {
            dataPorVoz = data.trim();
            proximaEtapaData();
        }
    }

    public void proximaEtapaData()
    {
        onDestroy();
        Intent intent = new Intent(this, HoraPorVoz.class);
        intent.putExtra("nomeCategoria", nomeCategoria);
        intent.putExtra("nomePrioridade", nomePrioridade);
        intent.putExtra("descicaoLembrete",descicaoLembrete);
        intent.putExtra("dataPorVoz",dataPorVoz);
        intent.putExtra("dataTipo",dataTipo);
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
        getMenuInflater().inflate(R.menu.menu_data_por_voz, menu);
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
