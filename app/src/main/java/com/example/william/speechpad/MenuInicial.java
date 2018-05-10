package com.example.william.speechpad;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;


public class MenuInicial extends Activity{

    private DatabaseHelper dbh;
    private String fraseInicial;
    private Calendar cal;
    private int hora;
    private String nomeUsuario;
    private TextView lblBoasVindas, lblQuantidadeLembretes;
    private boolean saida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_inicial);//"Ativa" as Views da Activity MenuInicial
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//Forçar tela na Vertical

        final Button btnTexto = (Button) findViewById(R.id.btnTexto);//Define o botão Lembrete em Texto
        final Button btnVoz = (Button) findViewById(R.id.btnVoz);//Define o botão Lembrete em Voz
        final Button btnLembrete = (Button) findViewById(R.id.btnLembrete);//Define o botão visualizar Lembrete
        final Button btnConfiguracao = (Button) findViewById(R.id.btnConfiguracao);//Define o botão Configuração
        final Button btnCategoria = (Button) findViewById(R.id.btnVisualizarCategoria);// Define o botão Categoria
        final Button btnOuvirLembrete = (Button) findViewById(R.id.btnOuvirLembretes);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        dbh = new DatabaseHelper(this);
        cal = Calendar.getInstance();
        hora=0;
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        hora = cal.get(Calendar.HOUR_OF_DAY);
        dbh = new DatabaseHelper(this);//Instância do Banco de Dados passando esta Activity como contexto
        fraseInicial="";
        nomeUsuario = dbh.getNomeUsuario();
        lblBoasVindas = (TextView) findViewById(R.id.lblBoasVindas);
        lblQuantidadeLembretes = (TextView) findViewById(R.id.lblQuantidadeLembreteMenu);
        saida=false;

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

        if(nomeUsuario.equals(""))
        {
            lblBoasVindas.setText(fraseInicial+"!");
        }
        else if (!nomeUsuario.equals(""))
        {
            lblBoasVindas.setText(fraseInicial+", "+nomeUsuario+"!");
        }

        if (dbh.getQuantidadeLembretesHoje()==0 && dbh.getLembretesExpirados().size()==0)
        {
            lblQuantidadeLembretes.setText("Você não possui lembretes para hoje ou expirados.");
        }
        else if (dbh.getQuantidadeLembretesHoje()==0 || dbh.getLembretesExpirados().size()==0)
        {
            if(dbh.getQuantidadeLembretesHoje()>1)
            {
                lblQuantidadeLembretes.setText("Há "+ dbh.getQuantidadeLembretesHoje() +" lembretes para hoje.");
            }
            else if (dbh.getLembretesExpirados().size()>1)
            {
                lblQuantidadeLembretes.setText("Há "+dbh.getLembretesExpirados().size()+" lembretes expirados.");
            }
            else if (dbh.getLembretesExpirados().size()==1)
            {
                lblQuantidadeLembretes.setText("Há "+dbh.getLembretesExpirados().size()+" lembrete expirado.");
            }
            else if(dbh.getQuantidadeLembretesHoje()==1)
            {
                lblQuantidadeLembretes.setText("Há "+ dbh.getQuantidadeLembretesHoje() +" lembrete para hoje.");
            }
        }
        else if (dbh.getQuantidadeLembretesHoje()>1 || dbh.getLembretesExpirados().size()>1)
        {
            if (dbh.getQuantidadeLembretesHoje()>1 && dbh.getLembretesExpirados().size()>1)
            {
                lblQuantidadeLembretes.setText("Há "+ dbh.getQuantidadeLembretesHoje() +" lembretes para hoje e "+dbh.getLembretesExpirados().size()+" expirados.");
            }
            else if (dbh.getQuantidadeLembretesHoje()==1 )
            {
                lblQuantidadeLembretes.setText("Há "+ dbh.getQuantidadeLembretesHoje() +" lembrete para hoje e "+dbh.getLembretesExpirados().size()+" expirados.");
            }
            else if (dbh.getLembretesExpirados().size()==1)
            {
                lblQuantidadeLembretes.setText("Há "+ dbh.getQuantidadeLembretesHoje() +" lembretes para hoje "+dbh.getLembretesExpirados().size()+" expirado.");
            }
        }
        else if (dbh.getQuantidadeLembretesHoje()==1 && dbh.getLembretesExpirados().size()==1)
        {
            lblQuantidadeLembretes.setText("Há "+ dbh.getQuantidadeLembretesHoje() +" lembrete para hoje e "+dbh.getLembretesExpirados().size()+" expirado.");
        }

        //Quando clicar no botão Lembrete em Texto, aciona o método abaixo
        btnTexto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {CarregarLembreteTexto(); }
        });//Método que chama a Activity Salvar Por Texto

        btnLembrete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {CarregarLembretes(); }
        });//Método que chama a Activity Lembretes

        btnVoz.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick (View v)
            { CarregarLembreteVoz();    }
        });//Método que chama a Activity Salvar Por Voz

        btnConfiguracao.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick (View v)
            { CarregarConfiguracao(); }
        });//Método que chama a Activity Configuração

        btnCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { CarregarCategoria();     }
        });//Método que chama a Activity Categoria

        btnOuvirLembrete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {CarregarOuvirLembrete(); }
        });//Método que chama a Activity Salvar Por Texto

    }

    @Override
    public  void onBackPressed()
    {
        if(saida)
        {
            finishAffinity();
        }
        else
        {
            Toast.makeText(this,"Pressione novamente para sair", Toast.LENGTH_SHORT).show();
            saida = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    saida=false;
                }
            }, 3000);
        }
    }

    //Método que muda para a tela Salvar Por Texto
    public void CarregarLembreteTexto(){
        startActivity(new Intent(this, SalvarPorTexto.class));
    }

    public void CarregarLembreteVoz(){
        if(dbh.getStatusLembreteReduzido()==0)
        {
            startActivity(new Intent(this, CategoriaPorVoz.class));
        }
        else if (dbh.getStatusLembreteReduzido()==1)
        {
            Intent intent = new Intent(this,LembretePorVozReduzido.class);
            intent.putExtra("nomeCategoria", dbh.getCategoriaPreSelecionada());
            intent.putExtra("prioridadePorVoz", dbh.getPrioridadePreSelecionada());
            startActivity(intent);
        }
    }

    public void CarregarOuvirLembrete(){    startActivity(new Intent(this, OuvirLembretes.class));}

    public void CarregarCategoria(){    startActivity(new Intent(this, Categoria.class));}

    public void CarregarLembretes()
    {
        Intent intent = new Intent(this, ListarLembretes.class);
        intent.putExtra("telaAnterior", "menu");
        startActivity(intent);
    }

    public void CarregarConfiguracao(){
        startActivity(new Intent(this, Configuracao.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_menu_inicial, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
