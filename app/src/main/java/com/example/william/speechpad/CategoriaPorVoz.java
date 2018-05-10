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

public class CategoriaPorVoz extends Activity implements TextToSpeech.OnInitListener  {

    public int DATA_CHECK_COD = 0;
    protected static final int RESULT_SPEECH = 1;
    private TextView txtCategoriaPorVoz;
    private TextToSpeech tts;
    private String categoriaPorVoz;
    private Handler handler = new Handler();
    private DatabaseHelper dbh;
    private Button btnVoltar;
    private Intent intentPrincipal;
    private float velocidadeVoz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria_por_voz);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//Forçar tela na Vertical
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Toast.makeText(this,"Função por voz iniciada\n   Informe a categoria", Toast.LENGTH_SHORT).show();
        btnVoltar = (Button) findViewById(R.id.btnVoltarCategoria);
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Voltar();
            }
        });
        txtCategoriaPorVoz = (TextView) findViewById(R.id.txtCategoriaPorVoz);
        tts = new TextToSpeech(this, this);//Instância a API TTS
        dbh = new DatabaseHelper(this);//Instância do Banco de Dados passando esta Activity como contexto
        categoriaPorVoz="";
        velocidadeVoz = 1.6f;
        intentPrincipal = new Intent(CategoriaPorVoz.this, MenuInicial.class);
        intentPrincipal.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentPrincipal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
            onDestroy();
            onStop();
            startActivity(intentPrincipal);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            finish();
        }
        return super.onKeyDown(keycode, event);
    }

    //Método OBRIGATÓRIO para inicializar a API TTS
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.getDefault());//Pega a Linguagem LOCAL do SMARTPHONE
            tts.setSpeechRate(velocidadeVoz);
            tts.speak("Função por voz iniciada.",TextToSpeech.QUEUE_FLUSH, null);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    textoParaVozCategoria(0, 1800);//Primeiro parâmetro é a posição da frase à ser dita e o segundo parâmetro o delay para inicia-la
                }
            }, 3660);//Delay para chamar o método textoParaVozCategoria
        }
        if (status == TextToSpeech.ERROR) {
            Toast.makeText(this, "Erro na função por voz.", Toast.LENGTH_SHORT).show();
        }
    }

    //Frases específicas para cada ocasião no setor CATEGORIA
    public String listaFrasesTTSCategoria(int i) {
        String falarCategoria = "Informe a categoria";//Posição 0
        String erroCategoria1 = "A categoria não existe. Informe uma categoria já cadastrada";
        String[] frases = {falarCategoria, erroCategoria1 };
        return frases[i];
    }

    //Inicia o TTS com a frase escolhida
    //tempoHandler é o valor de Delay entre as APIs TTS e STT
    public void textoParaVozCategoria(int posicaoFrase, int tempoHandler) {
        tts.speak(listaFrasesTTSCategoria(posicaoFrase), TextToSpeech.QUEUE_FLUSH, null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                vozParaTextoCategoria();
            }
        }, tempoHandler);
    }

    //Inicializa a API STT
    public void vozParaTextoCategoria() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Informe a Categoria");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "pt-BR");
        startActivityForResult(intent, RESULT_SPEECH);//Chama o método abaixo
        txtCategoriaPorVoz.setText("");
    }

    //Método responsável por receber o conteúdo transformado em texto pela API STT
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_SPEECH) {
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> textoSTT = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);//Array com STRING de tudo o que foi dito
                txtCategoriaPorVoz.setText(textoSTT.get(0));//Por ser Array, pegamos o conteúdo pelo "0 - ZERO"
                categoriaPorVoz = txtCategoriaPorVoz.getText().toString().trim().toUpperCase();
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
        validarCategoria(categoriaPorVoz);//Método que verifica conteúdo transformado em texto pela API STT
    }

    public void validarCategoria(String nome)
    {
        //Verifica se existe categoria igual ao que foi dito, utilizando o que foi informado como parâmetro
        //Se retorno for FALSO, então categoria não existe
        //1 = segunda frase do método listaFrasesTTSCategoria
        //5500 = Delay do tempoHandler para dar tempo da API TTS terminar de informar a frase acima
        if(dbh.getCategoriaPorNome(nome) == false)
        {
            textoParaVozCategoria(1, 4700);
        }

        //Se retorno for VERDADEIRO, então a categoria existe e pode-se ir para a próxima etapa
        else
        if(dbh.getCategoriaPorNome(nome) == true)
        {
            proximaEtapaPrioridade();
        }
    }

    //Manda para a próxima Activity (Etapa) as informações recebidas nesta etapa
    public void proximaEtapaPrioridade() {
        Intent intent = new Intent(this, PrioridadePorVoz.class);
        intent.putExtra("categoriaPorVoz", categoriaPorVoz);//Variável com o valor que será passado para a próxima etapa
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        onDestroy();//Encerrar API TTS para não gerar conflitos com a próxima
        startActivity(intent);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        finish();
    }

    //Método para eliminar API que estiver aberto
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
        getMenuInflater().inflate(R.menu.menu_categoria_por_voz, menu);
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
