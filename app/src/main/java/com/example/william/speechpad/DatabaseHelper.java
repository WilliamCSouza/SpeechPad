package com.example.william.speechpad;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper
{
    private SQLiteDatabase database;

    // Database Versão
    private static final int DATABASE_VERSION = 2;

    // Database Nome
    private static final String DATABASE_NAME = "Speechpad";

    // Nomes das Tabelas
    private static final String TABELA_LEMBRETE = "Lembrete";
    private static final String TABELA_CATEGORIA = "Categoria";
    private static final String TABELA_PRIORIDADE = "Prioridade";
    private static final String TABELA_CONFIGURACAO = "Configuração";

    // Tabela da Categoria - Nome das Colunas
    private static final String KEY_NOME_DA_CATEGORIA = "Nome_Categoria";
    private static final String KEY_ID_CATEGORIA = "ID_Categoria";
    private static final String KEY_QUANTIDADE_LEMBRETES_SALVOS = "Quantidade_Lembretes";
    private static final String KEY_TIPO_CATEGORIA = "Tipo_Categoria";
    //Categoria Tipo Fixa = 0
    //Categoria Tipo_NãoFixa = 1

    // Tabela do Lembrete - Nomes das Colunas
    public static final String KEY_ID_LEMBRETE = "ID_Lembrete";
    public static final String KEY_CATEGORIA_DO_LEMBRETE = "Categoria_Lembrete";
    public static final String KEY_CONTEUDO_DO_LEMBRETE = "Conteudo_Lembrete";
    public static final String KEY_DATA_LEMBRETE = "Data_Lembrete";
    public static final String KEY_HORA_LEMBRETE = "Hora_Lembrete";
    public static final String KEY_TEMPO_MILISEGUNDOS_LEMBRETE = "Milisegundos_Lembrete";
    public static final String KEY_ID_CATEGORIA_LEMBRETE = "ID_Categoria_Lembrete";
    public static final String KEY_ID_PRIORIDADE_LEMBRETE = "ID_Prioridade_Lembrete";
    public static final String KEY_NOME_PRIORIDADE_LEMBRETE = "Nome_Prioridade_Lembrete";

    // Tabela de Prioridade - Nomes das Colunas
    private static final String KEY_ID_PRIORIDADE = "ID_Prioridade";
    private static final String KEY_NOME_PRIORIDADE = "Nome_Prioridade";
    private static final String KEY_TIPO_PRIORIDADE = "Tipo_Prioridade";
    //Tipo Prioridade
    //0 = Alta
    //1 = Média
    //2 = Baixa

    private static final String KEY_ID = "ID";
    private static final String KEY_NOME_USUARIO = "Nome_Usuario";
    private static final String KEY_CATEGORIA_PRE_SELECIONADA = "Categoria_Pre_Selecionada";
    private static final String KEY_PRIORIDADE_PRE_SELECIONADA = "Prioridade_Pre_Selecionada";
    private static final String KEY_LEMBRETE_REDUZIDO = "Lembrete_Reduzido";

    // Criação do Statement Tabela Categoria
    private static final String CRIAR_TABELA_CATEGORIA = "CREATE TABLE "
            + TABELA_CATEGORIA + "(" + KEY_ID_CATEGORIA + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_NOME_DA_CATEGORIA + " TEXT NOT NULL, "
            + KEY_QUANTIDADE_LEMBRETES_SALVOS + " INTEGER NOT NULL, "
            + KEY_TIPO_CATEGORIA + " INTEGER NOT NULL)";

    //Categorias Padrões
    private static final String CATEGORIA_PADRAO_01 = "INSERT INTO "+ TABELA_CATEGORIA + " ("+KEY_NOME_DA_CATEGORIA+", "+KEY_QUANTIDADE_LEMBRETES_SALVOS+", "
            + KEY_TIPO_CATEGORIA+") VALUES('PESSOAL',0,0);";
    private static final String CATEGORIA_PADRAO_02 = "INSERT INTO "+ TABELA_CATEGORIA + " ("+KEY_NOME_DA_CATEGORIA+", "+KEY_QUANTIDADE_LEMBRETES_SALVOS+", "
            +KEY_TIPO_CATEGORIA+") VALUES('TRABALHO',0,0);";

    // Criação do Statement Tabela Lembrete
    private static final String CRIAR_TABELA_LEMBRETE = "CREATE TABLE "
            + TABELA_LEMBRETE + "(" + KEY_ID_LEMBRETE + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_ID_CATEGORIA_LEMBRETE +  " INTEGER NOT NULL, "
            + KEY_CATEGORIA_DO_LEMBRETE + " TEXT NOT NULL, "
            + KEY_ID_PRIORIDADE_LEMBRETE + " INTEGER NOT NULL, "
            + KEY_NOME_PRIORIDADE_LEMBRETE + " TEXT NOT NULL, "
            + KEY_CONTEUDO_DO_LEMBRETE + " TEXT NOT NULL, "
            + KEY_DATA_LEMBRETE  + " TEXT NOT NULL, "
            + KEY_HORA_LEMBRETE + " TEXT NOT NULL, "
            + KEY_TEMPO_MILISEGUNDOS_LEMBRETE + " INTEGER NOT NULL)";

    // Criação do Statement Tabela Prioridade
    private static final String CRIAR_TABELA_PRIORIDADE = "CREATE TABLE " + TABELA_PRIORIDADE
            + "(" + KEY_ID_PRIORIDADE + " INTEGER PRIMARY KEY, "
            + KEY_NOME_PRIORIDADE + " TEXT NOT NULL, "
            + KEY_TIPO_PRIORIDADE +" INTEGER NOT NULL)";

    private static final String PRIORIDADE_PADRAO_01 = "INSERT INTO " + TABELA_PRIORIDADE +
            " (" + KEY_ID_PRIORIDADE + ", "
                 + KEY_NOME_PRIORIDADE + ", "
                 + KEY_TIPO_PRIORIDADE + ") VALUES (0, 'ALTA', 0);";

    private static final String PRIORIDADE_PADRAO_02 = "INSERT INTO " + TABELA_PRIORIDADE +
            " (" + KEY_ID_PRIORIDADE + ", "
            + KEY_NOME_PRIORIDADE + ", "
            + KEY_TIPO_PRIORIDADE + ") VALUES (1, 'MÉDIA', 1);";

    private static final String PRIORIDADE_PADRAO_03 = "INSERT INTO " + TABELA_PRIORIDADE +
            " (" + KEY_ID_PRIORIDADE + ", "
            + KEY_NOME_PRIORIDADE + ", "
            + KEY_TIPO_PRIORIDADE + ") VALUES (2, 'BAIXA', 2);";

    //Criação do Statement Tabela Configuraçao
    private static final String CRIAR_TABELA_CONFIGURACAO = "CREATE TABLE "+TABELA_CONFIGURACAO+
            " ("+KEY_ID +" INTEGER PRIMARY KEY NOT NULL, "+KEY_NOME_USUARIO + " TEXT, " + KEY_LEMBRETE_REDUZIDO + " INTEGER NOT NULL, "
            +KEY_CATEGORIA_PRE_SELECIONADA+" TEXT, "+KEY_PRIORIDADE_PRE_SELECIONADA+" TEXT);";

    private static final String CONFIGURACAO_PADRAO = "INSERT INTO "+TABELA_CONFIGURACAO+
            " ("+KEY_ID+", "
            +KEY_NOME_USUARIO+", "
            +KEY_CATEGORIA_PRE_SELECIONADA+", "
            +KEY_PRIORIDADE_PRE_SELECIONADA+", "
            +KEY_LEMBRETE_REDUZIDO+") VALUES (0,'','','', 0);";
            //0 = False
            //1 = True

////////////////////////////////////////////////////////////////    MÉTODOS     //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Criando tabelas e inserindo linhas
        db.execSQL(CRIAR_TABELA_CATEGORIA);
        db.execSQL(CRIAR_TABELA_LEMBRETE);
        db.execSQL(CRIAR_TABELA_PRIORIDADE);
        db.execSQL(CRIAR_TABELA_CONFIGURACAO);
        db.execSQL(CATEGORIA_PADRAO_01);
        db.execSQL(CATEGORIA_PADRAO_02);
        db.execSQL(PRIORIDADE_PADRAO_01);
        db.execSQL(PRIORIDADE_PADRAO_02);
        db.execSQL(PRIORIDADE_PADRAO_03);
        db.execSQL(CONFIGURACAO_PADRAO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Upgrade nas tabelas, deletandos arquivos anteriores
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_CATEGORIA);
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_LEMBRETE);
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_PRIORIDADE);

        // Cria novas Tabelas
        onCreate(db);
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////                                                                                                                                    ////
    ////                                                                                                                                    ////
    ////                                                                                                                                    ////
    ////                                                                                                                                    ////
    ////                                                                                                                                    ////
    ////                                            Setor CATEGORIA                                                                         ////
    ////                                                                                                                                    ////
    ////                                                                                                                                    ////
    ////                                                                                                                                    ////
    ////                                                                                                                                    ////
    ////                                                                                                                                    ////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //Criar Categoria
    public boolean criarCategoria(Categoria categoria) {

        database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        if(!categoria.getNomeCategoria().trim().toUpperCase().matches(""))//Não deixa salvar uma categoria em branco
        {
            values.put(KEY_NOME_DA_CATEGORIA, categoria.getNomeCategoria());//Pega o nome da categoria e salva em Values, assim podemos salvar no banco
            values.put(KEY_TIPO_CATEGORIA, categoria.getTipoCategoria());
            values.put(KEY_QUANTIDADE_LEMBRETES_SALVOS, 0);
            // insere a nova categoria. Values está com a nova categoria
            database.insert(TABELA_CATEGORIA, null, values);
            return true;
        }
        else
        {
            return false;
        }
    }

    //Listar Todas as Categorias em um ArrayList
    public List<Categoria> getTodasCategorias()
    {
        List<Categoria> categorias = new ArrayList<Categoria>();
        String queryCategoria = "SELECT * FROM " + TABELA_CATEGORIA;
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(queryCategoria,null);
        if(cursor.moveToFirst())
        {
            do{
                Categoria categoria = new Categoria();
                categoria.setIdCategoria(cursor.getInt((cursor.getColumnIndex(KEY_ID_CATEGORIA))));
                categoria.setNomeCategoria((cursor.getString(cursor.getColumnIndex(KEY_NOME_DA_CATEGORIA))));
                categoria.setTipoCategoria(cursor.getInt(cursor.getColumnIndex(KEY_TIPO_CATEGORIA)));
                categoria.setQtdLembretesSalvos(cursor.getInt(cursor.getColumnIndex(KEY_QUANTIDADE_LEMBRETES_SALVOS)));
                categorias.add(categoria);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return categorias;
    }

    //Pegar o ID da Categoria pelo nome informado - Texto
    public int getIdCategoriaPorNome(String nome)
    {
        String queryCategoria = "SELECT * FROM " + TABELA_CATEGORIA + " WHERE "+KEY_NOME_DA_CATEGORIA + " = '"+nome+"'";//Deixar query únicas no começo do código para facilitar na manutenção do mesmo
        database = this.getReadableDatabase();
        int id=0;
        Cursor cursor = database.rawQuery(queryCategoria,null);
        if(cursor.moveToFirst()) {
            do {
                Categoria categoria = new Categoria();
                categoria.setIdCategoria(cursor.getInt(cursor.getColumnIndex(KEY_ID_CATEGORIA)));
                id = categoria.getIdCategoria();
            }while (cursor.moveToNext());
        }
        cursor.close();
        return id;
    }

    public int getTipoCategoriaPorID(int categoria_id)
    {
        database = this.getReadableDatabase();
        int tipo=0;
        Categoria categoria = new Categoria();
        String query = "SELECT * FROM " +TABELA_CATEGORIA +" WHERE "+KEY_ID_CATEGORIA+" = "+ categoria_id;
        Cursor cursor = database.rawQuery(query,null);
        if(cursor.moveToFirst())
        {
            do {
                categoria.setTipoCategoria(cursor.getInt(cursor.getColumnIndex(KEY_TIPO_CATEGORIA)));
                tipo = categoria.getTipoCategoria();
            }while (cursor.moveToNext());
        }
        return tipo;
    }

    public long lembretesPorCategoria(int categoria_id)
    {
        database = this.getReadableDatabase();
        SQLiteStatement sqls = database.compileStatement(
        "SELECT COUNT("+KEY_ID_CATEGORIA_LEMBRETE+") FROM "+TABELA_LEMBRETE+
                " WHERE "+KEY_ID_CATEGORIA_LEMBRETE+" = "+categoria_id);
        return sqls.simpleQueryForLong();
    }

    //Deletar Categoria
    public String deletarCategoria (int categoria_id) {
            String resposta="";

            if(getLembretePorCategoria(categoria_id) == true)
            {
                database.close();
                resposta = "Categoria possui lembrete e não é possível exclui-la!";
            }
            if(getLembretePorCategoria(categoria_id) == false){
                   database = this.getReadableDatabase();
                   database.delete(TABELA_CATEGORIA, KEY_ID_CATEGORIA + " = ?", new String[]{String.valueOf(categoria_id)});
                   resposta = "Categoria excluída!";
               }
        return resposta;
    }

    ///////////////////////////////////////////  Métodos para utilizar na função Por Voz //////////////////////////////////////////////////////

    public boolean getCategoriaPorNome(String nome)
    {
        boolean resposta=false;
        String queryCategoria = "SELECT * FROM " + TABELA_CATEGORIA + " WHERE "+KEY_NOME_DA_CATEGORIA + " = '"+nome+"'";//Deixar query únicas no começo do código para facilitar na manutenção do mesmo
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(queryCategoria,null);
        if(cursor.moveToNext()) {
            do {
                if (!nome.equals(cursor.getString(cursor.getColumnIndex(KEY_NOME_DA_CATEGORIA)).trim())) {
                    resposta = false;
                    database.close();
                } else if (nome.equals(cursor.getString(cursor.getColumnIndex(KEY_NOME_DA_CATEGORIA)))) {
                    resposta = true;
                    database.close();
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return resposta;
    }


    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////                                                                                                                                    ////
    ////                                                                                                                                    ////
    ////                                                                                                                                    ////
    ////                                                                                                                                    ////
    ////                                                                                                                                    ////
    ////                                            Setor LEMBRETE                                                                         ////
    ////                                                                                                                                    ////
    ////                                                                                                                                    ////
    ////                                                                                                                                    ////
    ////                                                                                                                                    ////
    ////                                                                                                                                    ////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public void criarLembrete(Lembrete lembrete) {
        database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID_CATEGORIA_LEMBRETE, lembrete.getIdCategoria());
        values.put(KEY_CATEGORIA_DO_LEMBRETE, lembrete.getNomeCategoria());
        values.put(KEY_ID_PRIORIDADE_LEMBRETE, lembrete.getIdPrioridade());
        values.put(KEY_NOME_PRIORIDADE_LEMBRETE, lembrete.getNomePrioridade());
        values.put(KEY_CONTEUDO_DO_LEMBRETE, lembrete.getNotaLembrete());
        values.put(KEY_DATA_LEMBRETE, lembrete.getDataLembrete());
        values.put(KEY_HORA_LEMBRETE, lembrete.getHoraLembrete());
        values.put(KEY_TEMPO_MILISEGUNDOS_LEMBRETE,lembrete.getMiliSegundosLembrete());

        // Insere Novo Lembrete
        database.insert(TABELA_LEMBRETE, null, values);
    }

    //Deletar Lembrete
    public void deletarLembrete (int lembrete_id)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(TABELA_LEMBRETE, KEY_ID_LEMBRETE + " = ?", new String[]{String.valueOf(lembrete_id)});
    }

    public boolean getLembretePorCategoria(int idCategoria) {
        boolean resposta=false;
        int i;
        for(i=0; i<getTodosLembretes().size();i++)
        {
            if(getTodosLembretes().get(i).getIdCategoria() == idCategoria)
            {
                resposta = true;
                break;
            }
            if(getTodosLembretes().get(i).getIdCategoria() != idCategoria)
            {
                resposta = false;
            }
        }
        return resposta;
    }

    public Lembrete getLembrete (int id)
    {
        String selectQuery = "SELECT * FROM " + TABELA_LEMBRETE + " WHERE "+KEY_ID_LEMBRETE +"="+id;
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        Lembrete lembrete = new Lembrete();
        if (cursor.moveToFirst()) {
            do {
                lembrete.setIdLembrete(cursor.getInt(cursor.getColumnIndex(KEY_ID_LEMBRETE)));
                lembrete.setIdCategoria(cursor.getInt(cursor.getColumnIndex(KEY_ID_CATEGORIA_LEMBRETE)));
                lembrete.setNomeCategoria(cursor.getString(cursor.getColumnIndex(KEY_CATEGORIA_DO_LEMBRETE)));
                lembrete.setIdPrioridade(cursor.getInt(cursor.getColumnIndex(KEY_ID_PRIORIDADE_LEMBRETE)));
                lembrete.setNomePrioridade(cursor.getString(cursor.getColumnIndex(KEY_NOME_PRIORIDADE_LEMBRETE)));
                lembrete.setNotaLembrete((cursor.getString(cursor.getColumnIndex(KEY_CONTEUDO_DO_LEMBRETE))));
                lembrete.setDataLembrete(cursor.getString(cursor.getColumnIndex(KEY_DATA_LEMBRETE)));
                lembrete.setHoraLembrete(cursor.getString(cursor.getColumnIndex(KEY_HORA_LEMBRETE)));
                lembrete.setMiliSegundosLembrete(cursor.getInt(cursor.getColumnIndex(KEY_TEMPO_MILISEGUNDOS_LEMBRETE)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lembrete;
    }

    public List<Lembrete> getTodosLembretes() {
        List<Lembrete> lembretes = new ArrayList<Lembrete>();
        String selectQuery = "SELECT  * FROM " + TABELA_LEMBRETE + " ORDER BY DATETIME("+KEY_TEMPO_MILISEGUNDOS_LEMBRETE+"/1000, 'unixepoch', 'localtime'), "+KEY_ID_PRIORIDADE_LEMBRETE+";";
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Lembrete lembrete = new Lembrete();
                lembrete.setIdLembrete(cursor.getInt(cursor.getColumnIndex(KEY_ID_LEMBRETE)));
                lembrete.setIdCategoria(cursor.getInt(cursor.getColumnIndex(KEY_ID_CATEGORIA_LEMBRETE)));
                lembrete.setNomeCategoria(cursor.getString(cursor.getColumnIndex(KEY_CATEGORIA_DO_LEMBRETE)));
                lembrete.setIdPrioridade(cursor.getInt(cursor.getColumnIndex(KEY_ID_PRIORIDADE_LEMBRETE)));
                lembrete.setNomePrioridade(cursor.getString(cursor.getColumnIndex(KEY_NOME_PRIORIDADE_LEMBRETE)));
                lembrete.setNotaLembrete((cursor.getString(cursor.getColumnIndex(KEY_CONTEUDO_DO_LEMBRETE))));
                lembrete.setDataLembrete(cursor.getString(cursor.getColumnIndex(KEY_DATA_LEMBRETE)));
                lembrete.setHoraLembrete(cursor.getString(cursor.getColumnIndex(KEY_HORA_LEMBRETE)));
                lembrete.setMiliSegundosLembrete(cursor.getInt(cursor.getColumnIndex(KEY_TEMPO_MILISEGUNDOS_LEMBRETE)));
                // adicionando Lembretes
                lembretes.add(lembrete);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lembretes;
    }

    public List<Lembrete> getLembretesExpirados()
    {
        List<Lembrete> lembretes = new ArrayList<Lembrete>();
        String selectQuery = "SELECT * FROM " + TABELA_LEMBRETE +" WHERE datetime("+KEY_TEMPO_MILISEGUNDOS_LEMBRETE+"/1000, 'unixepoch', 'localtime') " +
                " <= datetime('now','localtime') ORDER BY DATETIME("+KEY_TEMPO_MILISEGUNDOS_LEMBRETE+"/1000, 'unixepoch', 'localtime'), "+KEY_ID_PRIORIDADE_LEMBRETE+";";
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Lembrete lembrete = new Lembrete();
                lembrete.setIdLembrete(cursor.getInt(cursor.getColumnIndex(KEY_ID_LEMBRETE)));
                lembrete.setIdCategoria(cursor.getInt(cursor.getColumnIndex(KEY_ID_CATEGORIA_LEMBRETE)));
                lembrete.setNomeCategoria(cursor.getString(cursor.getColumnIndex(KEY_CATEGORIA_DO_LEMBRETE)));
                lembrete.setIdPrioridade(cursor.getInt(cursor.getColumnIndex(KEY_ID_PRIORIDADE_LEMBRETE)));
                lembrete.setNomePrioridade(cursor.getString(cursor.getColumnIndex(KEY_NOME_PRIORIDADE_LEMBRETE)));
                lembrete.setNotaLembrete((cursor.getString(cursor.getColumnIndex(KEY_CONTEUDO_DO_LEMBRETE))));
                lembrete.setDataLembrete(cursor.getString(cursor.getColumnIndex(KEY_DATA_LEMBRETE)));
                lembrete.setHoraLembrete(cursor.getString(cursor.getColumnIndex(KEY_HORA_LEMBRETE)));
                lembrete.setMiliSegundosLembrete(cursor.getInt(cursor.getColumnIndex(KEY_TEMPO_MILISEGUNDOS_LEMBRETE)));
                // adicionando Lembretes
                lembretes.add(lembrete);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lembretes;
    }

    public List<Lembrete> getLembretesHoje ()
    {
        List<Lembrete> lembretes = new ArrayList<Lembrete>();
        String selectQuery = "SELECT * FROM " + TABELA_LEMBRETE +" WHERE datetime("+KEY_TEMPO_MILISEGUNDOS_LEMBRETE+"/1000, 'unixepoch', 'localtime') >= datetime('now','localtime') " +
                    " AND " +
                    " date(datetime("+KEY_TEMPO_MILISEGUNDOS_LEMBRETE+"/1000, 'unixepoch', 'localtime'))" +
                    " < " +
                    " date(datetime('now','+1 day','localtime')) " +
                    " ORDER BY DATETIME("+KEY_TEMPO_MILISEGUNDOS_LEMBRETE+"/1000, 'unixepoch', 'localtime'), "+KEY_ID_PRIORIDADE_LEMBRETE+";";
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Lembrete lembrete = new Lembrete();
                lembrete.setIdLembrete(cursor.getInt(cursor.getColumnIndex(KEY_ID_LEMBRETE)));
                lembrete.setIdCategoria(cursor.getInt(cursor.getColumnIndex(KEY_ID_CATEGORIA_LEMBRETE)));
                lembrete.setNomeCategoria(cursor.getString(cursor.getColumnIndex(KEY_CATEGORIA_DO_LEMBRETE)));
                lembrete.setIdPrioridade(cursor.getInt(cursor.getColumnIndex(KEY_ID_PRIORIDADE_LEMBRETE)));
                lembrete.setNomePrioridade(cursor.getString(cursor.getColumnIndex(KEY_NOME_PRIORIDADE_LEMBRETE)));
                lembrete.setNotaLembrete((cursor.getString(cursor.getColumnIndex(KEY_CONTEUDO_DO_LEMBRETE))));
                lembrete.setDataLembrete(cursor.getString(cursor.getColumnIndex(KEY_DATA_LEMBRETE)));
                lembrete.setHoraLembrete(cursor.getString(cursor.getColumnIndex(KEY_HORA_LEMBRETE)));
                lembrete.setMiliSegundosLembrete(cursor.getInt(cursor.getColumnIndex(KEY_TEMPO_MILISEGUNDOS_LEMBRETE)));
                // adicionando Lembretes
                lembretes.add(lembrete);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lembretes;
    }

    public List<Lembrete> getLembretesFuturos()
    {
        List<Lembrete> lembretes = new ArrayList<Lembrete>();
        String selectQuery = "SELECT  * FROM " + TABELA_LEMBRETE +" WHERE " +
                "date(dateTime("+KEY_TEMPO_MILISEGUNDOS_LEMBRETE+"/1000, 'unixepoch', 'localtime'))" +
                ">= date('now','+1 day', 'localtime') ORDER BY DATETIME("+KEY_TEMPO_MILISEGUNDOS_LEMBRETE+"/1000, 'unixepoch', 'localtime'), "
                +KEY_ID_PRIORIDADE_LEMBRETE+";";
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Lembrete lembrete = new Lembrete();
                lembrete.setIdLembrete(cursor.getInt(cursor.getColumnIndex(KEY_ID_LEMBRETE)));
                lembrete.setIdCategoria(cursor.getInt(cursor.getColumnIndex(KEY_ID_CATEGORIA_LEMBRETE)));
                lembrete.setNomeCategoria(cursor.getString(cursor.getColumnIndex(KEY_CATEGORIA_DO_LEMBRETE)));
                lembrete.setIdPrioridade(cursor.getInt(cursor.getColumnIndex(KEY_ID_PRIORIDADE_LEMBRETE)));
                lembrete.setNomePrioridade(cursor.getString(cursor.getColumnIndex(KEY_NOME_PRIORIDADE_LEMBRETE)));
                lembrete.setNotaLembrete((cursor.getString(cursor.getColumnIndex(KEY_CONTEUDO_DO_LEMBRETE))));
                lembrete.setDataLembrete(cursor.getString(cursor.getColumnIndex(KEY_DATA_LEMBRETE)));
                lembrete.setHoraLembrete(cursor.getString(cursor.getColumnIndex(KEY_HORA_LEMBRETE)));
                lembrete.setMiliSegundosLembrete(cursor.getInt(cursor.getColumnIndex(KEY_TEMPO_MILISEGUNDOS_LEMBRETE)));
                // adicionando Lembretes
                lembretes.add(lembrete);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lembretes;
    }


    public List<Lembrete> getLembretesAmanha()
    {
        List<Lembrete> lembretes = new ArrayList<Lembrete>();
        String selectQuery = "SELECT  * FROM " + TABELA_LEMBRETE +" WHERE date(dateTime("+KEY_TEMPO_MILISEGUNDOS_LEMBRETE+"/1000, 'unixepoch', 'localtime'))= date('now','+1 day', 'localtime') ORDER BY DATETIME("+KEY_TEMPO_MILISEGUNDOS_LEMBRETE+"/1000, 'unixepoch', 'localtime'), "+KEY_ID_PRIORIDADE_LEMBRETE+";";
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Lembrete lembrete = new Lembrete();
                lembrete.setNomePrioridade(cursor.getString(cursor.getColumnIndex(KEY_NOME_PRIORIDADE_LEMBRETE)));
                lembrete.setNotaLembrete((cursor.getString(cursor.getColumnIndex(KEY_CONTEUDO_DO_LEMBRETE))));
                lembrete.setHoraLembrete(cursor.getString(cursor.getColumnIndex(KEY_HORA_LEMBRETE)));
                // adicionando Lembretes
                lembretes.add(lembrete);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lembretes;
    }

    public long getQuantidadeLembretesHoje()
    {
        database = this.getReadableDatabase();
        SQLiteStatement sqls = database.compileStatement(
                "SELECT COUNT(*) FROM " + TABELA_LEMBRETE +" WHERE datetime("+KEY_TEMPO_MILISEGUNDOS_LEMBRETE+"/1000, 'unixepoch', 'localtime') >= datetime('now','localtime') " +
                " AND " +
                " date(datetime("+KEY_TEMPO_MILISEGUNDOS_LEMBRETE+"/1000, 'unixepoch', 'localtime'))" +
                " < " +
                " date(datetime('now','+1 day','localtime'));");
        return sqls.simpleQueryForLong();
    }

    public long getQuantidadeLembretesAmanha()
    {
        database = this.getReadableDatabase();
        SQLiteStatement sqls = database.compileStatement("SELECT COUNT(*) FROM "+TABELA_LEMBRETE+" WHERE date(dateTime("+KEY_TEMPO_MILISEGUNDOS_LEMBRETE+"/1000, 'unixepoch', 'localtime'))= date('now','+1 day', 'localtime');");
        return sqls.simpleQueryForLong();
    }

    //Atualizar Lembrete
    public int atualizarLembrete (Lembrete lembrete)
    {
        database = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID_CATEGORIA_LEMBRETE, lembrete.getIdCategoria());
        values.put(KEY_CATEGORIA_DO_LEMBRETE, lembrete.getNomeCategoria());
        values.put(KEY_ID_PRIORIDADE_LEMBRETE, lembrete.getIdPrioridade());
        values.put(KEY_NOME_PRIORIDADE_LEMBRETE, lembrete.getNomePrioridade());
        values.put(KEY_CONTEUDO_DO_LEMBRETE, lembrete.getNotaLembrete());
        values.put(KEY_DATA_LEMBRETE, lembrete.getDataLembrete());
        values.put(KEY_HORA_LEMBRETE, lembrete.getHoraLembrete());
        values.put(KEY_TEMPO_MILISEGUNDOS_LEMBRETE, lembrete.getMiliSegundosLembrete());

        //Atualizar linhas
        return database.update(TABELA_LEMBRETE, values, KEY_ID_LEMBRETE + " = ?", new String[] {String.valueOf(lembrete.getIdLembrete())});
    }

    //Método utilizado para pegar o ID do último lembrete salvo, para utilizar na classe SalvarPorTexto
    //É necessário o ID do lembrete criado para utilizar na Notificaçao/EditarLembrete
    public int getIdUltimoLembrete()
    {
        String selectQuery = "SELECT "+KEY_ID_LEMBRETE +" FROM " + TABELA_LEMBRETE;
        database = this.getReadableDatabase();
        int id=0;
        Cursor cursor = database.rawQuery(selectQuery, null);
        do {
            if(cursor.moveToLast())
            {
                id = cursor.getInt(cursor.getColumnIndex(KEY_ID_LEMBRETE));
            }
        }while (cursor.moveToNext());

        return id;
                /*
        int posicao = getTodosLembretes().size();//Posicao do lembrete criado recentemente, no caso o último.
        return getTodosLembretes().get(posicao).getIdLembrete();*/

    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////                                                                                                                                    ////
    ////                                                                                                                                    ////
    ////                                                                                                                                    ////
    ////                                                                                                                                    ////
    ////                                                                                                                                    ////
    ////                                            Setor PRIORIDADE                                                                         ////
    ////                                                                                                                                    ////
    ////                                                                                                                                    ////
    ////                                                                                                                                    ////
    ////                                                                                                                                    ////
    ////                                                                                                                                    ////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public String getNomePrioridade(int id)
    {
        String selectQuery = "SELECT * FROM " + TABELA_PRIORIDADE + " WHERE " +KEY_ID_PRIORIDADE+ " = "+id;
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        String nomePrioridade="";
        if(cursor.moveToFirst()) {
            do {
                nomePrioridade = cursor.getString(cursor.getColumnIndex(KEY_NOME_PRIORIDADE));
            }while(cursor.moveToNext());
        }
        cursor.close();
        return nomePrioridade;
    }


    ///////////////////////////////////////////  Métodos para utilizar na função Por Voz //////////////////////////////////////////////////////

    public boolean getPrioridadePorNome(String nome)
    {
        boolean resposta=false;
        String queryCategoria = "SELECT * FROM " + TABELA_PRIORIDADE + " WHERE "+ KEY_NOME_PRIORIDADE + " = '"+nome+"'";//Deixar query únicas no começo do código para facilitar na manutenção do mesmo
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(queryCategoria,null);
        if(cursor.moveToNext()) {
            do {
                if (!nome.equals(cursor.getString(cursor.getColumnIndex(KEY_NOME_PRIORIDADE)).trim())) {
                    resposta = false;
                    database.close();
                } else if (nome.equals(cursor.getString(cursor.getColumnIndex(KEY_NOME_PRIORIDADE)))) {
                    resposta = true;
                    database.close();
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return resposta;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////                                                                                                                                    ////
    ////                                                                                                                                    ////
    ////                                                                                                                                    ////
    ////                                                                                                                                    ////
    ////                                                                                                                                    ////
    ////                                            Setor CONFIGURAÇÃO                                                                      ////
    ////                                                                                                                                    ////
    ////                                                                                                                                    ////
    ////                                                                                                                                    ////
    ////                                                                                                                                    ////
    ////                                                                                                                                    ////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //Atualizar Lembrete
    public void atualizarNomeUsuario (String nome)
    {
        database = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NOME_USUARIO, nome);
        values.put(KEY_LEMBRETE_REDUZIDO, 0);
        //Atualizar linhas
        database.update(TABELA_CONFIGURACAO, values, KEY_ID + " = ?", new String[]{String.valueOf(0)});
    }

    public String estadoLembreteReduzido()
    {
        database = this.getReadableDatabase();
        SQLiteStatement sqls = database.compileStatement("SELECT "+ KEY_LEMBRETE_REDUZIDO +" FROM "+TABELA_CONFIGURACAO);
        return sqls.simpleQueryForString();
    }

    public void atualizarStatusLembreteReduzido(int status)
    {
        database = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_LEMBRETE_REDUZIDO, status);
        //Atualizar linhas
        database.update(TABELA_CONFIGURACAO, values, KEY_ID + " = ?", new String[]{String.valueOf(0)});
    }

    public void atualizarCategoriaPreSelecionada(String categoria)
    {
        database = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CATEGORIA_PRE_SELECIONADA, categoria);
        //Atualizar linhas
        database.update(TABELA_CONFIGURACAO, values, KEY_ID + " = ?", new String[]{String.valueOf(0)});
    }

    public void atualizarPrioridadePreSelecionada(String prioridade)
    {
        database = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PRIORIDADE_PRE_SELECIONADA, prioridade);
        //Atualizar linhas
        database.update(TABELA_CONFIGURACAO, values, KEY_ID + " = ?", new String[]{String.valueOf(0)});
    }


    public String getNomeUsuario()
    {
        database = this.getReadableDatabase();
        String nome="";
        String query = "SELECT * FROM " +TABELA_CONFIGURACAO+" WHERE "+KEY_ID+" = "+ 0;
        Cursor cursor = database.rawQuery(query,null);
        if(cursor.moveToFirst())
        {
            do {
                nome = cursor.getString(cursor.getColumnIndex(KEY_NOME_USUARIO));
            }while (cursor.moveToNext());
        }
        return nome;
    }

    public String getCategoriaPreSelecionada()
    {
        database = this.getReadableDatabase();
        String nome="";
        String query = "SELECT * FROM " +TABELA_CONFIGURACAO+" WHERE "+KEY_ID+" = "+ 0;
        Cursor cursor = database.rawQuery(query,null);
        if(cursor.moveToFirst())
        {
            do {
                nome = cursor.getString(cursor.getColumnIndex(KEY_CATEGORIA_PRE_SELECIONADA));
            }while (cursor.moveToNext());
        }
        return nome;
    }

    public String getPrioridadePreSelecionada()
    {
        database = this.getReadableDatabase();
        String nome="";
        String query = "SELECT * FROM " +TABELA_CONFIGURACAO+" WHERE "+KEY_ID+" = "+ 0;
        Cursor cursor = database.rawQuery(query,null);
        if(cursor.moveToFirst())
        {
            do {
                nome = cursor.getString(cursor.getColumnIndex(KEY_PRIORIDADE_PRE_SELECIONADA));
            }while (cursor.moveToNext());
        }
        return nome;
    }

    public int getStatusLembreteReduzido()
    {
        database = this.getReadableDatabase();
        int status=0;
        String query = "SELECT * FROM " +TABELA_CONFIGURACAO+" WHERE "+KEY_ID+" = "+ 0;
        Cursor cursor = database.rawQuery(query,null);
        if(cursor.moveToFirst())
        {
            do {
                status = cursor.getInt(cursor.getColumnIndex(KEY_LEMBRETE_REDUZIDO));
            }while (cursor.moveToNext());
        }
        return status;
    }
}
