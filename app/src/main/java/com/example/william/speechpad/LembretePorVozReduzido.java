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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LembretePorVozReduzido extends Activity implements TextToSpeech.OnInitListener {

    private TextView txtLembretePorVoz;
    protected static final int RESULT_SPEECH = 1;
    private TextToSpeech tts;
    private String lembretePorVoz;
    public int DATA_CHECK_COD = 0;
    private DatabaseHelper dbh;
    final Handler handler = new Handler();
    private ScheduleCliente schCliente = new ScheduleCliente(this);
    private Button btnVoltar;
    private Intent intentPrincipal;
    private float velocidadeVoz;
    private Calendar cal;
    private String data;
    private String hojeOuAmanha;
    private String hora;
    private String horaPorVoz;
    private String nomeCategoria, nomePrioridade;
    private boolean deuRuim;
    private Pattern pattern;
    private Matcher matcher;
    private String TIME24HOURS_PATTERN = "([01]?[0-9]|2[0-3]):[0-5][0-9]";
    private Lembrete lembrete = new Lembrete();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lembrete_por_voz);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//Forçar tela na Vertical
        Toast.makeText(this, "Lembrete por Voz Rápido", Toast.LENGTH_SHORT).show();
        txtLembretePorVoz = (TextView) findViewById(R.id.txtLembretePorVoz);
        tts = new TextToSpeech(this, this);
        dbh = new DatabaseHelper(this);
        schCliente.doBindService();
        pattern = Pattern.compile(TIME24HOURS_PATTERN);
        deuRuim = false;
        intentPrincipal = new Intent(LembretePorVozReduzido.this, MenuInicial.class);
        intentPrincipal.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentPrincipal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentPrincipal.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        lembretePorVoz = "";
        hojeOuAmanha = "";
        velocidadeVoz = 1.6f;
        cal = Calendar.getInstance();
        nomeCategoria = getIntent().getStringExtra("nomeCategoria");
        nomePrioridade = getIntent().getStringExtra("prioridadePorVoz");
        data = "";
        hora = "";
        horaPorVoz = "";
        btnVoltar = (Button) findViewById(R.id.btnVoltarLembrete);
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Voltar();
            }
        });
    }

    public void Voltar() {
        onDestroy();
        startActivity(intentPrincipal);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        finish();
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            onStop();
            onDestroy();
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
        String erroLembrete1 = "Lembrete está em branco. ";
        String erroLembrete2 = "Não entendi. Repita por favor.";
        String erroLembrete3 = "Não entendi. Repita o lembrete.";
        String[] frases = {falarLembrete, erroLembrete1, erroLembrete2, erroLembrete3};
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
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Informe o Lembrete");
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
                txtLembretePorVoz.setText("");
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

    public void validarLembrete(String lembrete) {
        if (lembrete.trim().equals("")) {
            textoParaVozLembrete(1, 2200);
            //Primeiro parâmetro = Frase que a API TTS falará
            //Segundo parâmetro = Tempo (em milisegundos) que o handler vai segurar a API STT
        } else if (!lembrete.trim().equals("")) {
            validarLembreteEtapa2(lembrete);
        }
    }

    //Valida a parte da DATA (HOJE ou AMANHÃ)
    public void validarLembreteEtapa2(String lembrete) {
        boolean encotrei = false;
        lembrete = lembrete.toUpperCase().trim();
        String[] frase = lembrete.split(" ");
        int posicaoFrase = 0;
        int ano, mes, dia;
        for (int i = 0; i < frase.length; i++) {
            if (frase[i].equals("HOJE")) {
                dia = cal.get(Calendar.DAY_OF_MONTH);
                mes = cal.get(Calendar.MONTH) + 1;
                ano = cal.get(Calendar.YEAR);
                data = dia + "/" + mes + "/" + ano;
                posicaoFrase = i + 1;//Pula HOJE/AMANHÃ
                hojeOuAmanha = "HOJE";
                encotrei = true;
                i = frase.length;
            } else if (frase[i].equals("AMANHÃ")) {
                dia = cal.get(Calendar.DAY_OF_MONTH) + 1;
                mes = cal.get(Calendar.MONTH) + 1;
                ano = cal.get(Calendar.YEAR);
                data = dia + "/" + mes + "/" + ano;
                posicaoFrase = i + 1;//Pula HOJE/AMANHÃ
                hojeOuAmanha = "AMANHÃ";
                encotrei = true;
                i = frase.length;
            }
        }
        if (encotrei == false) {
            tts.speak(listaFrasesTTSLembrete(2), TextToSpeech.QUEUE_FLUSH, null);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    vozParaTextoLembrete();
                }
            }, 2600);
        } else {
            validarLembreteEtapa3(frase, posicaoFrase);
        }
    }

    public void validarLembreteEtapa3(String[] frase, int posicaoFrase) {
        hora = "";
        boolean validaDiaNoite = false;
        int posicaoMeiaMeio = 0;
        int j = 0;//Controle do loop para preencher a String hora
        int k = 0;//Controle do loop para pesquisar "Meio ou Meia". Necessário para não repetir o lembrete
        do {//Procura pela palavra MEIA|MEIO
            if (frase[k].contains("MEIA") || frase[k].contains("MEIO")) {
                validaDiaNoite = true;//Se encontrar vai para a próxima etapa para setar a hora
                posicaoMeiaMeio = k;
                k = frase.length;
            } else if (!frase[k].contains("MEIA") && !frase[k].contains("MEIO")) {
                validaDiaNoite = false;//Se não encontrar vai para o próximo método validador
                k++;
            }
        } while (k != frase.length);
        if (validaDiaNoite == true) {
            for (int i = posicaoMeiaMeio; i < frase.length; i++) {
                j = i;
                do {
                    hora = hora + " " + frase[j];
                    j++;
                } while (j != frase.length);
                hora = hora.trim();
                if (hora.equals("MEIA NOITE") || hora.equals("MEIA-NOITE")) {
                    hora = "00:00";
                    StringToData(hora);
                    if (deuRuim == true) {
                        textoParaVozLembrete(2, 3000);
                    } else if (deuRuim == false) {
                        horaPorVoz = hora;
                        proximaEtapaSalvar();
                    }
                } else if (hora.equals("MEIO DIA") || hora.equals("MEIO-DIA")) {
                    hora = "12:00";
                    StringToData(hora);
                    if (deuRuim == true) {
                        textoParaVozLembrete(2, 3000);
                    } else if (deuRuim == false) {
                        horaPorVoz = hora;
                        proximaEtapaSalvar();
                    }
                }
            }
        } else if (validaDiaNoite == false) {
            if (frase[posicaoFrase].equals("AS") || frase[posicaoFrase].equals("ÀS")) {
                posicaoFrase = posicaoFrase+1;
                for (int i = posicaoFrase; i < frase.length; i++) {
                    hora = hora + " " + frase[i];
                }
            }else if (!frase[posicaoFrase].equals("AS") && !frase[posicaoFrase].equals("ÀS"))
            {
                for (int i = posicaoFrase; i < frase.length; i++) {
                    hora = hora + " " + frase[i];
                }
            }
            if(hora.contains("HORA") || hora.contains("HORAS"))
            {
                validarLembreteEtapa5(hora);
            }
            else if (!hora.contains("HORA") && !hora.contains("HORAS")){
                validarLembreteEtapa4(hora);
            }
        }
    }

            /*for (int i = posicaoFrase; i < frase.length; i++) {
            if (frase[i].equals("ÀS") || frase[i].equals("AS")) {
                j = i + 1;
                do {
                    hora = hora + " " + frase[j];
                    j++;
                } while (j != frase.length);
                i = frase.length;
                hora = hora.trim();
                if (hora.equals("MEIA NOITE") || hora.equals("MEIA-NOITE")) {
                    hora = "00:00";
                    StringToData(hora);
                    if (deuRuim == true) {
                        textoParaVozLembrete(2, 3000);
                    } else if (deuRuim == false) {
                        horaPorVoz = hora;
                        proximaEtapaSalvar();
                        break;
                    }
                } else if (hora.equals("MEIO DIA") || hora.equals("MEIO-DIA")) {
                    hora = "12:00";
                    StringToData(hora);
                    if (deuRuim == true) {
                        textoParaVozLembrete(2, 3000);
                    } else if (deuRuim == false) {
                        horaPorVoz = hora;
                        proximaEtapaSalvar();
                        break;
                    }
                }
            } else if (!frase[i].equals("ÀS") || !frase[i].equals("AS"))//Se não foi dito ÀS. EX.: AMANHÃ Meia Noite
            {
                j = i;
                do {
                    hora = hora + " " + frase[j];
                    j++;
                } while (j != frase.length);
                hora = hora.trim();
                if (hora.equals("MEIA NOITE") || hora.equals("MEIA-NOITE")) {
                    hora = "";
                    hora = "00:00";
                    StringToData(hora);
                    if (deuRuim == true) {
                        textoParaVozLembrete(2, 3000);
                    } else if (deuRuim == false) {
                        horaPorVoz = hora;
                        proximaEtapaSalvar();
                        break;
                    }
                } else if (hora.equals("MEIO DIA") || hora.equals("MEIO-DIA")) {
                    hora = "12:00";
                    StringToData(hora);
                    if (deuRuim == true) {
                        textoParaVozLembrete(2, 3000);
                    } else if (deuRuim == false) {
                        horaPorVoz = hora;
                        proximaEtapaSalvar();
                        break;
                    }
                }
            }
        }
        validarLembreteEtapa4(hora);
    } /*else if (validaDiaNoite == false) {
            int y=0;
            for (int i=posicaoFrase;i<frase.length;i++)
            {
                if(frase[i].equals("AS") ||frase[i].equals("ÀS")) {
                    y = i+1;
                    hora = hora + " " + frase[y];
                    i=frase.length;
                }
                else if (!frase[i].equals("AS") && !frase[i].equals("ÀS"))
                {
                    hora = hora + " " + frase[i];
                }
            }
            this.hora = hora.trim();
            validarLembreteEtapa4(hora);
        }*/

    public void validarLembreteEtapa4(String hora)
    {
        hora = hora.trim();
        if(hora.length()==2)
        {
            String fraseHora[] = hora.trim().split("");
            hora = "0"+fraseHora[1]+":"+"0"+fraseHora[2];
            StringToData(hora);
            if(deuRuim==true){ this.hora=""; textoParaVozLembrete(3, 3000);
            } else if (deuRuim==false) {
                horaPorVoz = hora;
                proximaEtapaSalvar();
            }
        }
        else if(hora.length()==3)
        {   textoParaVozLembrete(3, 3000);   }
        else if(hora.length()==4 || hora.length()==5)
        {
            hora = hora.replaceAll("\\s", "");
            String fraseHora[] = hora.split("");
            hora = fraseHora[1]+fraseHora[2]+":"+fraseHora[4]+fraseHora[5];
            StringToData(hora);
            if(deuRuim==true){ this.hora=""; textoParaVozLembrete(3, 3000);
            } else if (deuRuim==false) {
                horaPorVoz = hora;
                proximaEtapaSalvar();
            }
        }
        else
        {
            validarLembreteEtapa5(hora);
        }
    }

    public void validarLembreteEtapa5(String hora)
    {
        hora = hora.trim();
        String fraseHora[] = hora.split(" ");
        if(fraseHora[1].trim().equals("HORAS") || fraseHora[1].trim().equals("HORA"))
        {
            String novaHora = fraseHora[0]+":00";
            hora = novaHora;
            StringToData(hora);
            if(deuRuim==true){ this.hora=""; textoParaVozLembrete(3, 3000);
            } else if (deuRuim==false) {
                horaPorVoz = hora;
                proximaEtapaSalvar();
            }
        }
        else
        {
            validarLembreteEtapa6(hora);
        }
    }

    public void validarLembreteEtapa6(String hora)
    {
        hora = hora.trim();
        hora = hora.replace("E", ":");
        hora = hora.replace("I", ":");
        hora = hora.replaceAll("\\s", "");
        StringToData(hora);
        if(deuRuim==true){ this.hora=""; textoParaVozLembrete(3, 3000);
        } else if (deuRuim==false) {
            horaPorVoz = hora;
            proximaEtapaSalvar();
        }
    }

    public String StringToData(String hora)
    {
        String horaConvertida = "";
        matcher = pattern.matcher(hora);
            if (matcher.matches() == true) {
                SimpleDateFormat formato = new SimpleDateFormat("HH:mm");
                try {
                    horaConvertida = formato.parse(hora).toString();
                    deuRuim = false;
                    return horaConvertida;
                } catch (Exception e) {
                    Toast.makeText(this, new StringBuilder().append("Hora em formato inválido. Tente novamente.\nErro: " + hora), Toast.LENGTH_SHORT).show();
                    deuRuim = true;
                }
            } else if (matcher.matches() == false) {
                Toast.makeText(this, new StringBuilder().append("Hora em formato inválido. Tente novamente.\nErro: " + hora), Toast.LENGTH_SHORT).show();
                deuRuim = true;
            }
        return horaConvertida;
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
            Toast.makeText(this,new StringBuilder().append("Problema para formatar data e hora. Tente novamente."),Toast.LENGTH_SHORT).show();}
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
        x = ((x / 2.4) * 1000) + 1850;
        contaPalavra = (int) x;
        return contaPalavra;
    }

    public void proximaEtapaSalvar()
    {
        String textoData="";
        String descricaoLembrete="";
        String frase[] = lembretePorVoz.toLowerCase().split(" ");
        String fraseFinal="O lembrete ";
        for (int i=0; i<frase.length; i++)
        {
            if(frase[i].toUpperCase().equals("HOJE") || frase[i].toUpperCase().equals("AMANHÃ"))
            {
                i=frase.length;
            }
            else {
                fraseFinal = fraseFinal + " " + frase[i];
                descricaoLembrete = descricaoLembrete + " " + frase[i];
            }
        }
        descricaoLembrete = descricaoLembrete.substring(0,1).toUpperCase() + descricaoLembrete.substring(1);
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
        lembrete.setNotaLembrete(descricaoLembrete);
        lembrete.setDataLembrete(data);
        lembrete.setHoraLembrete(horaPorVoz);
        lembrete.setMiliSegundosLembrete(StringToMili(data, horaPorVoz));
        dbh.criarLembrete(lembrete);
        schCliente.setAlarmForNotification(StringToMili(data, horaPorVoz), dbh.getIdUltimoLembrete(), nomeCategoria, descricaoLembrete,"SALVAR");
        if(hojeOuAmanha.trim().toUpperCase().equals("HOJE"))
        {
            textoData = "hoje";
        }
        else if(hojeOuAmanha.trim().toUpperCase().equals("AMANHÃ"))
        {
            textoData = "amanhã";
        }
        fraseFinal = fraseFinal+" será notificado "+textoData + " "+horaPorVoz;
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        int temporizadorPrincipal = contaPalavras(fraseFinal);
        tts.setSpeechRate(velocidadeVoz);
        tts.speak(fraseFinal, TextToSpeech.QUEUE_FLUSH, null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onDestroy();
                onStop();
                startActivity(intentPrincipal);
                finish();
            }
        }, temporizadorPrincipal);
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
