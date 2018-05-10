package com.example.william.speechpad;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OuvirLembretes extends Activity implements TextToSpeech.OnInitListener {

    public int DATA_CHECK_COD = 0;
    protected static final int RESULT_SPEECH = 1;
    private TextToSpeech tts;
    private String resposta;
    private Handler handler = new Handler();
    private DatabaseHelper dbh;
    private String fraseInicial;
    private Calendar cal;
    private int hora;
    private long qtdLembretesHoje;
    private long qtdLembretesAmanha;
    private int posicaoFrase;
    private int contadorHoje;
    private int contadorAmanha;
    private int etapa;
    private boolean validaEtapa;
    private boolean ouvirLembretesAmanha;
    private String nomeUsuario;
    private Button btnVoltar;
    private float velocidadeVoz;
    private List<Lembrete> lembreteHoje;
    private List<Lembrete> lembreteAmanha;
    private Intent intentPrincipal;
    private String textoSeraLido;
    private int temporizadorPrincipal, temporizadorHandlerA, temporizadorHandlerB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ouvir_lembretes);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//Forçar tela na Vertical
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        btnVoltar = (Button) findViewById(R.id.btnVoltarOuvirLembrete);
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Voltar();
            }
        });
        cal = Calendar.getInstance();
        hora=0;
        qtdLembretesAmanha=0;
        qtdLembretesHoje=0;
        posicaoFrase=0;
        etapa=0;
        contadorHoje=0;
        contadorAmanha=0;
        velocidadeVoz=1.6f;
        validaEtapa=false;
        ouvirLembretesAmanha=false;
        lembreteHoje = new ArrayList<>();
        lembreteAmanha = new ArrayList<>();
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        hora = cal.get(Calendar.HOUR_OF_DAY);
        tts = new TextToSpeech(this, this);//Instância a API TTS
        dbh = new DatabaseHelper(this);//Instância do Banco de Dados passando esta Activity como contexto
        resposta="";
        fraseInicial="";
        textoSeraLido="";
        temporizadorPrincipal=0;
        temporizadorHandlerA=0;
        temporizadorHandlerB=0;
        nomeUsuario = dbh.getNomeUsuario();
        qtdLembretesHoje = dbh.getQuantidadeLembretesHoje();
        qtdLembretesAmanha = dbh.getQuantidadeLembretesAmanha();
        intentPrincipal = new Intent(OuvirLembretes.this, MenuInicial.class);
        intentPrincipal.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentPrincipal.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentPrincipal.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        if (hora >= 6 && hora < 12)
        {
            fraseInicial="Bom dia";
        }
        else if(hora >=12 && hora < 18)
        {
            fraseInicial="Boa tarde";
        }
        else if(hora >= 18 && hora <= 24)
        {
            fraseInicial = "Boa noite";
        }
        else if (hora >=0 && hora <6)
        {
            fraseInicial = "Boa noite";
        }

        if (qtdLembretesHoje==0 && qtdLembretesAmanha==0)
        {
            posicaoFrase=3;
            etapa=3;
        }
        else if(qtdLembretesHoje==0 || qtdLembretesAmanha==0) {
            if(qtdLembretesHoje>1)
            {
                posicaoFrase = 0;
                etapa=0;
                lembreteHoje = dbh.getLembretesHoje();
            }
            else if(qtdLembretesAmanha>1) {
                posicaoFrase = 1;
                etapa=1;
                lembreteAmanha = dbh.getLembretesAmanha();
            }
            else if (qtdLembretesHoje == 1) {
                posicaoFrase = 5;
                etapa=0;
                lembreteHoje = dbh.getLembretesHoje();
            } else if (qtdLembretesAmanha == 1) {
                posicaoFrase = 6;
                etapa=1;
                lembreteAmanha = dbh.getLembretesAmanha();
            }
        }
        else if(qtdLembretesHoje>1 || qtdLembretesAmanha>1)
        {
            if (qtdLembretesHoje>1 && qtdLembretesAmanha>1)
            {
                posicaoFrase = 2;
                etapa=2;
                lembreteHoje = dbh.getLembretesHoje();
                lembreteAmanha = dbh.getLembretesAmanha();
            }
            else if (qtdLembretesHoje==1)
            {
                posicaoFrase = 7;
                etapa=2;
                lembreteHoje = dbh.getLembretesHoje();
                lembreteAmanha = dbh.getLembretesAmanha();
            }
            else if (qtdLembretesAmanha==1)
            {
                posicaoFrase = 8;
                etapa=2;
                lembreteHoje = dbh.getLembretesHoje();
                lembreteAmanha = dbh.getLembretesAmanha();
            }
        }
        else if(qtdLembretesHoje==1 && qtdLembretesAmanha==1)
        {
            posicaoFrase = 9;
            etapa=2;
            lembreteHoje = dbh.getLembretesHoje();
            lembreteAmanha = dbh.getLembretesAmanha();
        }
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
        x = ((x / 2.4) * 1000) + 2000;
        contaPalavra = (int) x;
        return contaPalavra;
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
            tts.speak(fraseInicial + ", " + nomeUsuario + ".", TextToSpeech.QUEUE_FLUSH, null);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    textoParaVozOuvirLembrete(posicaoFrase);//Primeiro parâmetro é a posição da frase à ser dita e o segundo parâmetro o delay para inicia-la
                }
            }, 3500);//Delay para chamar o método textoParaVozCategoria
        }
        if (status == TextToSpeech.ERROR) {
            Toast.makeText(this, "Erro na inicialização da API Text-To-Speech", Toast.LENGTH_SHORT).show();
        }
    }

    public String listaFrasesTTSOuvirLembrete(int i) {
        String frase1 = " Você possui "+ qtdLembretesHoje+ " lembretes agendados para hoje. Vou ler o primeiro.";
        String frase2 = " Você possui "+ qtdLembretesAmanha+ " lembretes agendados para amanhã. Vou ler o primeiro de amanhã.";
        String frase3 = " Você possui "+ qtdLembretesHoje+ " lembretes agendados para hoje. E "+qtdLembretesAmanha+" lembretes agendados para amanhã. Vou iniciar pelo primeiro de hoje.";
        String frase4 = " Você não possui lembretes agendados para hoje ou amanhã. Vou retornar ao menu.";
        String frase5 = "Responda SIM para ouvir o próximo lembrete.";
        String frase6 = " Você possui "+ qtdLembretesHoje+ " lembrete agendado para hoje. Vou começar a leitura.";
        String frase7 = " Você possui "+ qtdLembretesAmanha+ " lembrete agendado para amanhã. Vou começar a leitura.";
        String frase8 = " Você possui "+ qtdLembretesHoje+ " lembrete agendado para hoje. E "+qtdLembretesAmanha+" lembretes agendados para amanhã. Vou iniciar pelo único de hoje.";
        String frase9 = " Você possui "+ qtdLembretesHoje+ " lembretes agendados para hoje. E "+qtdLembretesAmanha+" lembrete agendado para amanhã. Vou ler o primeiro de hoje.";
        String frase10 = " Você possui "+ qtdLembretesHoje+ " lembrete agendado para hoje. E "+qtdLembretesAmanha+" lembrete agendado para amanhã. Vou iniciar a leitura.";
        String frase11 = "Responda SIM para ouvir o próximo lembrete de amanhã.";
        String frase12 = "Responda SIM para ouvir os lembretes de amanhã.";
        String erro1 = "Informe Sim ou Não como resposta.";
        String[] frases = {frase1, frase2, frase3, frase4, frase5, frase6, frase7, frase8, frase9, frase10, frase11, frase12, erro1};
        return frases[i];
    }

    public void textoParaVozOuvirLembrete(final int posicaoFrase) {
        textoSeraLido = listaFrasesTTSOuvirLembrete(posicaoFrase);
        int tempoHandler = contaPalavras(textoSeraLido);
        tts.speak(listaFrasesTTSOuvirLembrete(posicaoFrase), TextToSpeech.QUEUE_FLUSH, null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (etapa == 3) {
                    onDestroy();
                    startActivity(intentPrincipal);
                    finish();
                } else if (etapa != 3) {
                    escolheEtapa(etapa);
                }
            }
        }, tempoHandler);
    }

    public void vozParaTextoOuvirLembrete() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Responda SIM ou NÃO");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "pt-BR");
        startActivityForResult(intent, RESULT_SPEECH);//Chama o método abaixo
    }

    //Método responsável por receber o conteúdo transformado em texto pela API STT
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_SPEECH) {
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> textoSTT = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);//Array com STRING de tudo o que foi dito
                resposta = textoSTT.get(0).trim().toUpperCase();
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
        validarOuvirLembrete(resposta);//Método que verifica conteúdo transformado em texto pela API STT
    }

    public void validarOuvirLembrete(String resp)
    {
        if(resp.equals("SIM") && validaEtapa==false)
        {
            escolheEtapa(etapa);
        }
        else if (resp.equals("SIM") && validaEtapa==true)
        {
            ouvirLembretesAmanha=true;
            escolheEtapa(etapa);
        }
        else if (resp.equals("NÃO"))
        {
            validaEtapa=false;
            tts.speak("Ok, vou retornar ao menu.", TextToSpeech.QUEUE_FLUSH, null);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onDestroy();
                    startActivity(intentPrincipal);
                    finish();
                }
            }, 3400);
        }
        else if (!resp.equals("SIM") && !resp.equals("NÃO"))
        {
            tts.speak(listaFrasesTTSOuvirLembrete(12), TextToSpeech.QUEUE_FLUSH, null);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    vozParaTextoOuvirLembrete();
                }
            }, 3400);
        }
    }

    //Manda para a próxima Activity (Etapa) as informações recebidas nesta etapa
    public void escolheEtapa(int escolheEtapa) {
        if(escolheEtapa==0)
        {
            etapa1(contadorHoje);
        }
        else if (escolheEtapa==1)
        {
            etapa2(contadorAmanha);
        }
        else if (escolheEtapa==2)
        {
            etapa3(contadorHoje, contadorAmanha);
        }
    }

    public void etapa1(int contadorH)
    {
        if(qtdLembretesHoje>contadorH) {
            textoSeraLido = lembreteHoje.get(contadorH).getNotaLembrete() + " às " + lembreteHoje.get(contadorH).getHoraLembrete() + ".Prioridade " + lembreteHoje.get(contadorH).getNomePrioridade();
            temporizadorPrincipal = contaPalavras(textoSeraLido);
            tts.speak(lembreteHoje.get(contadorH).getNotaLembrete() + " às " + lembreteHoje.get(contadorH).getHoraLembrete() + ".Prioridade " + lembreteHoje.get(contadorH).getNomePrioridade(), TextToSpeech.QUEUE_FLUSH, null);
            contadorHoje = contadorHoje + 1;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (contadorHoje >= qtdLembretesHoje) {
                        textoSeraLido = "Já informei todos os lembretes de hoje. Vou retornar ao menu.";
                        temporizadorHandlerA = contaPalavras(textoSeraLido);
                        tts.speak("Já informei todos os lembretes de hoje. Vou retornar ao menu.", TextToSpeech.QUEUE_FLUSH, null);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                onDestroy();
                                startActivity(intentPrincipal);
                                finish();
                            }
                        }, temporizadorHandlerA);
                    }
                    else if (contadorHoje < qtdLembretesHoje) {
                        textoSeraLido = listaFrasesTTSOuvirLembrete(4);
                        temporizadorHandlerB = contaPalavras(textoSeraLido);
                        tts.speak(listaFrasesTTSOuvirLembrete(4), TextToSpeech.QUEUE_FLUSH, null);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                vozParaTextoOuvirLembrete();
                            }
                        }, temporizadorHandlerB);
                    }
                }
            }, temporizadorPrincipal);
        }
        else if (qtdLembretesHoje<=contadorH)
        {
            textoSeraLido = "Já informei todos os lembretes de hoje. Vou retornar ao menu.";
            temporizadorHandlerA = contaPalavras(textoSeraLido);
            tts.speak("Já informei todos os lembretes de hoje. Vou retornar ao menu.", TextToSpeech.QUEUE_FLUSH, null);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onDestroy();
                    startActivity(new Intent(intentPrincipal));
                    finish();
                }
            }, temporizadorHandlerA);
        }
    }

    public void etapa2 (int contadorA)
    {
        if(qtdLembretesAmanha>contadorA) {
            textoSeraLido = lembreteAmanha.get(contadorA).getNotaLembrete() + " às " + lembreteAmanha.get(contadorA).getHoraLembrete() + ". Prioridade " + lembreteAmanha.get(contadorA).getNomePrioridade();
            temporizadorPrincipal = contaPalavras(textoSeraLido);
            tts.speak(lembreteAmanha.get(contadorA).getNotaLembrete() + " às " + lembreteAmanha.get(contadorA).getHoraLembrete() + ". Prioridade " + lembreteAmanha.get(contadorA).getNomePrioridade(), TextToSpeech.QUEUE_FLUSH, null);
            contadorAmanha = contadorAmanha + 1;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(contadorAmanha>=qtdLembretesAmanha) {
                        textoSeraLido = "Já informei todos os lembretes de amanhã. Vou retornar ao menu.";
                        temporizadorHandlerA = contaPalavras(textoSeraLido);
                        tts.speak("Já informei todos os lembretes de amanhã. Vou retornar ao menu.", TextToSpeech.QUEUE_FLUSH, null);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                onDestroy();
                                startActivity(intentPrincipal);
                                finish();
                            }
                        }, temporizadorHandlerA);
                    }
                    else if (contadorAmanha < qtdLembretesAmanha) {
                        textoSeraLido = listaFrasesTTSOuvirLembrete(10);
                        temporizadorHandlerB = contaPalavras(textoSeraLido);
                        tts.speak(listaFrasesTTSOuvirLembrete(10), TextToSpeech.QUEUE_FLUSH, null);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                vozParaTextoOuvirLembrete();
                            }
                        }, temporizadorHandlerB);
                    }
                }
            }, temporizadorPrincipal);
        }
        else if (qtdLembretesAmanha<=contadorA)
        {
            textoSeraLido = "Já informei todos os lembretes de amanhã. Vou retornar ao menu.";
            temporizadorPrincipal= contaPalavras(textoSeraLido);
            tts.speak("Já informei todos os lembretes de amanhã. Vou retornar ao menu.", TextToSpeech.QUEUE_FLUSH, null);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onDestroy();
                    startActivity(intentPrincipal);
                    finish();
                }
            }, temporizadorPrincipal);
        }
    }

    public void etapa3(final int contadorH, int contadorA) {
        if (qtdLembretesHoje > contadorH) {
            textoSeraLido = lembreteHoje.get(contadorH).getNotaLembrete() + " às " + lembreteHoje.get(contadorH).getHoraLembrete() + ".Prioridade " + lembreteHoje.get(contadorH).getNomePrioridade();
            temporizadorPrincipal = contaPalavras(textoSeraLido);
            tts.speak(lembreteHoje.get(contadorH).getNotaLembrete() + " às " + lembreteHoje.get(contadorH).getHoraLembrete() + ". Prioridade " + lembreteHoje.get(contadorH).getNomePrioridade(), TextToSpeech.QUEUE_FLUSH, null);
            contadorHoje = contadorHoje + 1;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (contadorHoje >= qtdLembretesHoje) {
                        textoSeraLido = listaFrasesTTSOuvirLembrete(11);
                        temporizadorHandlerA = contaPalavras(textoSeraLido);
                        tts.speak(listaFrasesTTSOuvirLembrete(11), TextToSpeech.QUEUE_FLUSH, null);
                        validaEtapa = true;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                vozParaTextoOuvirLembrete();
                            }
                        }, temporizadorHandlerA);
                    } else if (contadorHoje < qtdLembretesHoje) {
                        textoSeraLido = listaFrasesTTSOuvirLembrete(4);
                        temporizadorHandlerB = contaPalavras(textoSeraLido);
                        tts.speak(listaFrasesTTSOuvirLembrete(4), TextToSpeech.QUEUE_FLUSH, null);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                vozParaTextoOuvirLembrete();
                            }
                        }, temporizadorHandlerB);
                    }
                }
            }, temporizadorPrincipal);
        } else if (qtdLembretesHoje <= contadorHoje && ouvirLembretesAmanha == false) {
            textoSeraLido = "Ok, vou retornar ao menu.";
            temporizadorPrincipal = contaPalavras(textoSeraLido);
            tts.speak("Ok, vou retornar ao menu.", TextToSpeech.QUEUE_FLUSH, null);
            validaEtapa = true;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onDestroy();
                    startActivity(intentPrincipal);
                    finish();
                }
            }, temporizadorPrincipal);
        }
        else if (qtdLembretesHoje <= contadorHoje && ouvirLembretesAmanha == true) {
            if (qtdLembretesAmanha == 1) {
                textoSeraLido = lembreteAmanha.get(contadorA).getNotaLembrete() + " às " + lembreteAmanha.get(contadorA).getHoraLembrete() + ". Prioridade " + lembreteAmanha.get(contadorA).getNomePrioridade();
                temporizadorPrincipal = contaPalavras(textoSeraLido);
                tts.speak(lembreteAmanha.get(contadorA).getNotaLembrete() + " às " + lembreteAmanha.get(contadorA).getHoraLembrete() + ". Prioridade " + lembreteAmanha.get(contadorA).getNomePrioridade(), TextToSpeech.QUEUE_FLUSH, null);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        textoSeraLido = "Já informei o único lembrete de amanhã. Vou retornar ao menu.";
                        temporizadorHandlerA = contaPalavras(textoSeraLido);
                        tts.speak("Já informei o único lembrete de amanhã. Vou retornar ao menu.", TextToSpeech.QUEUE_FLUSH, null);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                onDestroy();
                                startActivity(intentPrincipal);
                                finish();
                            }
                        }, temporizadorHandlerA);
                    }
                }, temporizadorPrincipal);
            }
            else if (qtdLembretesAmanha > 1 && qtdLembretesAmanha > contadorA) {
                textoSeraLido = lembreteAmanha.get(contadorA).getNotaLembrete() + " às " + lembreteAmanha.get(contadorA).getHoraLembrete() + ". Prioridade " + lembreteAmanha.get(contadorA).getNomePrioridade();
                temporizadorPrincipal = contaPalavras(textoSeraLido);
                tts.speak(lembreteAmanha.get(contadorA).getNotaLembrete() + " às " + lembreteAmanha.get(contadorA).getHoraLembrete() + ". Prioridade " + lembreteAmanha.get(contadorA).getNomePrioridade(), TextToSpeech.QUEUE_FLUSH, null);
                contadorAmanha = contadorAmanha + 1;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (contadorAmanha >= qtdLembretesAmanha) {
                            textoSeraLido = "Já informei todos os lembretes de amanhã. Vou retornar ao menu.";
                            temporizadorHandlerA = contaPalavras(textoSeraLido);
                            tts.speak("Já informei todos os lembretes de amanhã. Vou retornar ao menu.", TextToSpeech.QUEUE_FLUSH, null);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    onDestroy();
                                    startActivity(intentPrincipal);
                                    finish();
                                }
                            }, temporizadorHandlerA);
                        }
                        else if (contadorAmanha < qtdLembretesAmanha) {
                            textoSeraLido = listaFrasesTTSOuvirLembrete(10);
                            temporizadorHandlerB = contaPalavras(textoSeraLido);
                            tts.speak(listaFrasesTTSOuvirLembrete(10), TextToSpeech.QUEUE_FLUSH, null);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    vozParaTextoOuvirLembrete();
                                }
                            }, temporizadorHandlerB);
                        }
                    }
                }, temporizadorPrincipal);
            }
            else if (contadorA >= qtdLembretesAmanha) {
                textoSeraLido = "Já informei todos os lembretes de amanhã. Vou retornar ao menu.";
                temporizadorPrincipal = contaPalavras(textoSeraLido);
                tts.speak("Já informei todos os lembretes de amanhã. Vou retornar ao menu.", TextToSpeech.QUEUE_FLUSH, null);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onDestroy();
                        startActivity(intentPrincipal);
                        finish();
                    }
                }, temporizadorPrincipal);
            }
        }
    }

    //Método para eliminar API que estiver aberto
    @Override
    public void onDestroy()
    {
        if (tts != null)
        {
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ouvir_lembretes, menu);
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
