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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HoraPorVoz extends Activity implements TextToSpeech.OnInitListener {

    private TextView txtHoraPorVoz;
    protected static final int RESULT_SPEECH = 1;
    private TextToSpeech tts;
    private String descicaoLembrete;
    private String nomeCategoria;
    private String nomePrioridade;
    private String dataPorVoz;
    private String horaPorVoz;
    private String fraseFinal;
    private String dataTipo;
    private boolean deuRuim;
    public int DATA_CHECK_COD = 0;
    private DatabaseHelper dbh;
    private Lembrete lembrete = new Lembrete();
    private ScheduleCliente schCliente = new ScheduleCliente(this);
    final Handler handler = new Handler();
    private Button btnVoltar;
    private Intent intentPrincipal;
    private float velocidadeVoz;
    private Pattern pattern;
    private Matcher matcher;
    private String TIME24HOURS_PATTERN = "([01]?[0-9]|2[0-3]):[0-5][0-9]";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hora_por_voz);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//Forçar tela na Vertical
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Toast.makeText(this, "Informe a hora", Toast.LENGTH_SHORT).show();
        dbh = new DatabaseHelper(this);
        schCliente.doBindService();
        txtHoraPorVoz = (TextView) findViewById(R.id.txtHoraPorVoz);
        pattern = Pattern.compile(TIME24HOURS_PATTERN);
        tts = new TextToSpeech(this, this);
        nomeCategoria = getIntent().getStringExtra("nomeCategoria");
        nomePrioridade = getIntent().getStringExtra("nomePrioridade");
        descicaoLembrete = getIntent().getStringExtra("descicaoLembrete");
        dataPorVoz = getIntent().getStringExtra("dataPorVoz");
        dataTipo = getIntent().getStringExtra("dataTipo");
        intentPrincipal = new Intent(HoraPorVoz.this, MenuInicial.class);
        intentPrincipal.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentPrincipal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        velocidadeVoz=1.6f;
        horaPorVoz="";
        deuRuim=false;
        btnVoltar = (Button) findViewById(R.id.btnVoltarHora);
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
                    textoParaVozHora(0, 1300);
                }
            }, 1100);
        }
        if (status == TextToSpeech.ERROR) {
            Toast.makeText(this, "Erro na função por voz!", Toast.LENGTH_SHORT).show();
        }
    }

    //Frases específicas para cada ocasião no setor CATEGORIA
    public String listaFrasesTTSHora(int i) {
        String falarHora = "Informe a hora.";//Posição 0
        String erroHora1 = "Hora inválida. Informe novamente.";
        String erroHora2 = "Hora em branco. Informe a hora.";
        String[] frases = {falarHora, erroHora1, erroHora2};
        return frases[i];
    }

    public void textoParaVozHora(int posicaoFrase, int tempoHandler) {
        tts.speak(listaFrasesTTSHora(posicaoFrase), TextToSpeech.QUEUE_FLUSH, null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                vozParaTextoHora();
            }
        }, tempoHandler);
    }

    public void vozParaTextoHora() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Informe a Hora");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "pt-BR");
        startActivityForResult(intent, RESULT_SPEECH);
        txtHoraPorVoz.setText("");
    }

    public String StringToData(String hora)
    {
        String horaConvertida = "";
        matcher = pattern.matcher(hora);

        if(matcher.matches()==true) {
            SimpleDateFormat formato = new SimpleDateFormat("HH:mm");
            try {
                horaConvertida = formato.parse(hora).toString();
                deuRuim = false;
                return horaConvertida;
            } catch (Exception e) {
                Toast.makeText(this, new StringBuilder().append("Problema para formatar a hora. Informe novamente."), Toast.LENGTH_SHORT).show();
                deuRuim = true;
            }
        }
        else if (matcher.matches()==false)
        {
            Toast.makeText(this, new StringBuilder().append("Problema para formatar a hora. Informe novamente."), Toast.LENGTH_SHORT).show();
            deuRuim = true;
        }
        return horaConvertida;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_SPEECH) {
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> textoSTT = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                txtHoraPorVoz.setText(textoSTT.get(0));
                horaPorVoz = txtHoraPorVoz.getText().toString().toUpperCase().trim();

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
        validarHoraEtapa1(horaPorVoz);
    }

    public void validarHoraEtapa1(String hora)
    {
        if (hora.equals("")) {  textoParaVozHora(2, 3000); }
        else {
            deuRuim = false;
            validarHoraEtapa2(hora);
        }
    }

    public void validarHoraEtapa2(String hora)
    {
        if(hora.equals("MEIA NOITE")||hora.equals("MEIA-NOITE")){
            hora = "00:00";
            StringToData(hora);
            if(deuRuim==true){ textoParaVozHora(1, 3000);
            } else if (deuRuim==false) {
                horaPorVoz = hora;
                proximaEtapaSalvar();
            }
        }
        else if(hora.equals("MEIO DIA")||hora.equals("MEIO-DIA"))
        {
            hora = "12:00";
            StringToData(hora);
            if(deuRuim==true){ textoParaVozHora(1, 3000);
            } else if (deuRuim==false) {
                horaPorVoz = hora;
                proximaEtapaSalvar();
            }
        }
        else
        {
          validarHoraEtapa3(hora);
        }
    }

    public void validarHoraEtapa3 (String hora)
    {
        if(hora.length()==2)
        {
            String fraseHora[] = hora.trim().split("");
            hora = "0"+fraseHora[1]+":"+"0"+fraseHora[2];
            StringToData(hora);
            if(deuRuim==true){ textoParaVozHora(1, 3000);
            } else if (deuRuim==false) {
                horaPorVoz = hora;
                proximaEtapaSalvar();
            }
        }
        else if(hora.length()==3)
        {   textoParaVozHora(3, 3000);   }
        else if(hora.length()==4)
        {
            String fraseHora[] = hora.split("");
            hora = fraseHora[1]+fraseHora[2]+":"+fraseHora[3]+fraseHora[4];
            StringToData(hora);
            if(deuRuim==true){ textoParaVozHora(1, 3000);
            } else if (deuRuim==false) {
                horaPorVoz = hora;
                proximaEtapaSalvar();
            }
        }
        else
        {
            validarHoraEtapa4(hora);
        }
    }

    public void validarHoraEtapa4(String hora)
    {
        String fraseHora[] = hora.split(" ");
        if(fraseHora[1].trim().equals("HORAS") || fraseHora[1].trim().equals("HORA"))
        {
            String novaHora = fraseHora[0]+":00";
            hora = novaHora;
            StringToData(hora);
            if(deuRuim==true){ textoParaVozHora(1, 3000);
            } else if (deuRuim==false) {
                horaPorVoz = hora;
                proximaEtapaSalvar();
            }
        }
        else
        {
            validarHoraEtapa5(hora);
        }
    }

    public void validarHoraEtapa5(String hora)
    {
        hora = hora.trim();
        hora = hora.replace("E", ":");
        hora = hora.replace("I", ":");
        hora = hora.replaceAll("\\s", "");
        Toast.makeText(this,hora,Toast.LENGTH_SHORT).show();
        StringToData(hora);
        if(deuRuim==true){ textoParaVozHora(1, 3000);
        } else if (deuRuim==false) {
            horaPorVoz = hora;
            proximaEtapaSalvar();
        }
    }

    public long StringToMili(String data, String hora)
    {
        long mili=0;
        String dataString = data+" "+hora;
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            Date dataCovertida = formato.parse(dataString);
            mili = dataCovertida.getTime();
        }catch (Exception e){
            Toast.makeText(this,new StringBuilder().append("Problema para formatar data e hora. Informe novamente."),Toast.LENGTH_SHORT).show();}
        return mili;
    }

    public static int contaPalavras(String texto)
    {
        int contaPalavra=0;
        boolean palavra = false;
        int fimDaLinha = texto.length()-1;

        for (int i=0; i < texto.length(); i++)
        {
            if(Character.isLetterOrDigit(texto.charAt(i)) && i != fimDaLinha)
            {
                palavra=true;
            }
            else if(!Character.isLetterOrDigit(texto.charAt(i)) && palavra)
            {
                contaPalavra++;
                palavra = false;
            }
            else if (Character.isLetterOrDigit(texto.charAt(i)) && i == fimDaLinha)
            {
                contaPalavra++;
            }
        }
        double x=0;
        x = contaPalavra;
        x = ((x / 2.4) * 1000) + 1990;
        contaPalavra = (int) x;
        return contaPalavra;
    }

    public void proximaEtapaSalvar()
    {
        int x = dbh.getIdCategoriaPorNome(nomeCategoria);
        lembrete.setNomeCategoria(nomeCategoria);
        lembrete.setIdCategoria(x);
        lembrete.setNomePrioridade(nomePrioridade);
        if (nomePrioridade.equals("ALTA")) {
            lembrete.setIdPrioridade(0);
        } else if (nomePrioridade.equals("MÉDIA")) {
            lembrete.setIdPrioridade(1);
        } else if (nomePrioridade.equals("BAIXA")) {
            lembrete.setIdPrioridade(2);
        }
        lembrete.setNotaLembrete(descicaoLembrete);
        lembrete.setDataLembrete(dataPorVoz);
        lembrete.setHoraLembrete(horaPorVoz);
        lembrete.setMiliSegundosLembrete(StringToMili(dataPorVoz, horaPorVoz));
        dbh.criarLembrete(lembrete);
        schCliente.setAlarmForNotification(StringToMili(dataPorVoz, horaPorVoz), dbh.getIdUltimoLembrete(), nomeCategoria, descicaoLembrete,"SALVAR");
        if(dataTipo.equals("hoje"))
        {
            dataPorVoz="hoje";
        }
        else if(dataTipo.equals("amanhã"))
        {
            dataPorVoz="amanhã";
        }
        fraseFinal = "O lembrete "+descicaoLembrete+" será notificado "+dataPorVoz+" às "+horaPorVoz;
        Toast.makeText(this, new StringBuilder().append(fraseFinal), Toast.LENGTH_SHORT).show();
        int temporizadorPrincipal = contaPalavras(fraseFinal);
        tts.setSpeechRate(velocidadeVoz);
        tts.speak(fraseFinal, TextToSpeech.QUEUE_FLUSH, null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onDestroy();
                onStop();
                Intent intent = new Intent(HoraPorVoz.this, MenuInicial.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }, temporizadorPrincipal);
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
        getMenuInflater().inflate(R.menu.menu_hora_por_voz, menu);
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
