package com.example.william.speechpad;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

public class Categoria extends Activity {

    private DatabaseHelper dbh;
    private int idCategoria, tipoCategoria, idExcluir, qtdLembretesSalvos;
    private String nomeCategoria, resposta;
    private List<Categoria> listaCategorias;
    private ListView lvCategoria;
    private Button btnCriarCategoria;
    private Context context = (this);
    private AlertDialog.Builder telaDialogo, telaDialogoCriarCategoria;

    //Construtor
    public Categoria( ) {}

    //Getters
    public int getIdCategoria() { return idCategoria; }
    public String getNomeCategoria() { return nomeCategoria; }
    public int getTipoCategoria(){  return tipoCategoria; }
    public int getQtdLembretesSalvos() { return qtdLembretesSalvos; }

    //Setters
    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }
    public void setNomeCategoria(String nomeCategoria) {
        this.nomeCategoria = nomeCategoria;
    }
    public void setTipoCategoria(int tipoCategoria) { this.tipoCategoria = tipoCategoria; }
    public void setQtdLembretesSalvos(int qtdLembretesSalvos) { this.qtdLembretesSalvos = qtdLembretesSalvos; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//Forçar tela na Vertical
        lvCategoria = (ListView) findViewById(R.id.listviewCategoria);//Identificando a ListView da Categoria
        idExcluir=0;
        resposta="";
        btnCriarCategoria = (Button) findViewById(R.id.btnCriarCategoria);
        telaDialogo = new AlertDialog.Builder(this);//Popup informando se deseja excluir a Categoria
        telaDialogoCriarCategoria = new AlertDialog.Builder(this);
        dbh = new DatabaseHelper(this);//Instância do Banco de Dados passando esta Activity como contexto
        btnCriarCategoria.setOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {criarCategoria();}});
        listaCategorias = dbh.getTodasCategorias();
        lvCategoria.setAdapter(new ViewAdapter());
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent event)
    {
        if(keycode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            startActivity(new Intent(Categoria.this,MenuInicial.class));
            Categoria.this.finish();
            return true;
        }
        return super.onKeyDown(keycode, event);
    }

    public void criarCategoria()
    {
        final EditText campo = new EditText(this);
        int maxLenght = 30;
        campo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLenght)});

        telaDialogoCriarCategoria.setMessage("Informe uma nova categoria");
        telaDialogoCriarCategoria.setTitle("Categoria");
        telaDialogoCriarCategoria.setView(campo);
        telaDialogoCriarCategoria.setCancelable(false).setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                int x = 0;//Controle do loop
                int i = 0;//Controle das categorias
                // Looping para consultar cada categoria e não deixar repetir ou categoria em branco
                do {
                    //Verifica se a categoria já existe no banco
                    if (campo.getText().toString().trim().toUpperCase().equals(dbh.getTodasCategorias().get(i).getNomeCategoria().trim().toUpperCase())) {
                        campo.requestFocus();
                        Toast.makeText(Categoria.this, "Categoria existente!", Toast.LENGTH_LONG).show();
                        i = i + 1;
                        x = dbh.getTodasCategorias().size();
                        break;
                    } else {
                        i = i + 1;//Incrementa para Verificar a próxima Categoria
                        x = x + 1;//Incrementa para controle do Looping
                    }
                }
                while (x != dbh.getTodasCategorias().size());//Até que o controle do Looping seja igual ao número de Categorias no BD

                //Verifica se o looping chegou ao fim (X = QTD de Categorias) E se a categoria anterior (devido o I ser incrementado no looping antes de sair do mesmos) não é igual a esta nova
                if (x == dbh.getTodasCategorias().size() && !campo.getText().toString().trim().toUpperCase().equals(dbh.getTodasCategorias().get(i - 1).getNomeCategoria().trim())) {
                    setNomeCategoria(campo.getText().toString().trim().toUpperCase());
                    setTipoCategoria(1);//Tipo excluível de Categoria
                    dbh.criarCategoria(Categoria.this);
                    visualizarCategoria();
                    Toast.makeText(Categoria.this, "Categoria " + campo.getText().toString().trim() + " salva!", Toast.LENGTH_SHORT).show();
                }

            }
        })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alerta = telaDialogoCriarCategoria.create();
        alerta.show();
        ((AlertDialog) alerta).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        campo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s))
                {
                    ((AlertDialog) alerta).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
                else
                {
                    ((AlertDialog) alerta).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }
            }
        });

    }
    //Carrga a lista de Categorias toda vez que o botão Visualizar Categorias é acionado
    public void visualizarCategoria()
    {
        listaCategorias = dbh.getTodasCategorias();
        lvCategoria.setAdapter(new ViewAdapter());
    }

    //Inner Class para ADAPTADOR utilizado na ListView
    //ListView de OBJETOS precisa de um VIEW ADAPTER para mostrar cada item separadamente
    public class ViewAdapter extends BaseAdapter {

        LayoutInflater mInflater;
        public ViewAdapter() {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return listaCategorias.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.activity_item_listview_categoria, null);//Informa de qual ListView precisa do View Adapter
            }
            //ConvertView pega os valores das VIEWS vinculadas ao LAYOUT informado acima
            final TextView nomeCategoria = (TextView) convertView.findViewById(R.id.txtNomeCategoria);//Variável recebe valores
            nomeCategoria.setText(listaCategorias.get(position).getNomeCategoria());//Seta na lista de Categorias criada
            final TextView qtdLembretes = (TextView) convertView.findViewById(R.id.lblQtdLembretesSalvos);
            //int i = listaCategorias.get(position).getQtdLembretesSalvos();
            long i = dbh.lembretesPorCategoria(listaCategorias.get(position).getIdCategoria());
            if(i > 0)
            {
               qtdLembretes.setText("Quantidade de lembretes salvos: "+i);
            }
            else if(i <=0){
                qtdLembretes.setText("Não existe lembrete salvo");
            }
            final ImageButton excluir = (ImageButton) convertView.findViewById(R.id.btnExcluirCategoria);
            excluir.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {//Inicia o AlertDialog
                    idExcluir = dbh.getIdCategoriaPorNome(dbh.getTodasCategorias().get(position).getNomeCategoria());
                    telaDialogo.setMessage("Excluir categoria "+dbh.getTodasCategorias().get(position).getNomeCategoria()+"?")
                            .setCancelable(false)
                            .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //Verifica se não é a Primeira Categoria com ID = 0
                                    if(dbh.getTipoCategoriaPorID(idExcluir)==0) {
                                        resposta = "Esta categoria é padrão e não pode ser excluída!";
                                    }
                                    //Manda para o BD que analisa e se é possível excluir ou não
                                    //Feito isso, devolve uma String resposta e realiza a devida ação
                                    else if(dbh.getTipoCategoriaPorID(idExcluir)!=0 && !dbh.getCategoriaPreSelecionada().equals(dbh.getTodasCategorias().get(position).getNomeCategoria()))
                                    {
                                        resposta = dbh.deletarCategoria(idExcluir);
                                    }
                                    else if (dbh.getTipoCategoriaPorID(idExcluir)!=0 && dbh.getCategoriaPreSelecionada().equals(dbh.getTodasCategorias().get(position).getNomeCategoria()))
                                    {
                                        Toast.makeText(Categoria.this,
                                                "Categoria pré-selecionada foi alterada para 'Pessoal'",
                                                Toast.LENGTH_LONG).show();
                                        dbh.atualizarCategoriaPreSelecionada("PESSOAL");
                                        resposta = dbh.deletarCategoria(idExcluir);
                                    }
                                    Toast.makeText(Categoria.this, new StringBuilder().append(resposta),Toast.LENGTH_SHORT).show();
                                    visualizarCategoria();
                                }
                            })
                            .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alerta = telaDialogo.create();
                    alerta.show();
                    notifyDataSetChanged();
                    listaCategorias = dbh.getTodasCategorias();//Retorna do BD a lista de Categorias atualizadas
                    lvCategoria.setAdapter(new ViewAdapter());//Solicita o VIEW ADAPTER para a ListView Categoria
                }
            });
            return convertView;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_categoria, menu);
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
