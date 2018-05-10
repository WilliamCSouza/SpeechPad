package com.example.william.speechpad;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

public class Tab1 extends Fragment{

    private Context context;
    private DatabaseHelper dbh;
    private List<Lembrete> listaLembretes;
    private ListView lvLembrete;
    private AlertDialog.Builder telaDialogo;
    private String telaAnteriorLembrete;
    private ScheduleCliente schCliente;
    private View v;
    private TextView txtExpirados;
    private NotificationManager mNM;


    public Tab1(Context mContext)
    {
        this.context = mContext;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        schCliente = new ScheduleCliente(context);
        schCliente.doBindService();
        telaDialogo = new AlertDialog.Builder(context);
        dbh = new DatabaseHelper(context);
        v = inflater.inflate(R.layout.tab_1, container, false);
        txtExpirados = (TextView) v.findViewById(R.id.txtExpirados);
        txtExpirados.setVisibility(View.INVISIBLE);
        telaAnteriorLembrete="lembreteTab1";
        lvLembrete = (ListView) v.findViewById(R.id.listViewTab1);
        mNM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (dbh.getLembretesExpirados().size() == 0) {
            lvLembrete.setVisibility(View.INVISIBLE);
            txtExpirados.setVisibility(View.VISIBLE);
            txtExpirados.setText("Sem lembretes expirados!");
        } else {
            listaLembretes = dbh.getLembretesExpirados();
            txtExpirados.setVisibility(View.INVISIBLE);
            lvLembrete.setVisibility(View.VISIBLE);
            lvLembrete.setAdapter(new ViewAdapter());
        }
        return v;
    }

    public void visualizarLembrete()
    {
        if (dbh.getLembretesExpirados().size() == 0) {
            lvLembrete.refreshDrawableState();
            txtExpirados.setVisibility(View.VISIBLE);
            txtExpirados.setText("Sem lembretes expirados!");
            lvLembrete.setVisibility(View.INVISIBLE);
        } else {
            txtExpirados.setVisibility(View.INVISIBLE);
            listaLembretes = dbh.getLembretesExpirados();
            lvLembrete.setVisibility(View.VISIBLE);
            lvLembrete.setAdapter(new ViewAdapter());
        }
    }

    public class ViewAdapter extends BaseAdapter {

        LayoutInflater mInflater;

        public ViewAdapter() {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return listaLembretes.size();
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
                convertView = mInflater.inflate(R.layout.activity_item_listview_lembrete, null);
            }

            final TextView nomeCategoria = (TextView) convertView.findViewById(R.id.txtNomeCategoriaLembrete);
            nomeCategoria.setText(listaLembretes.get(position).getNomeCategoria());
            final TextView prioridadeLembrete = (TextView) convertView.findViewById(R.id.txtPrioridadeLembrete);
            prioridadeLembrete.setText("Prioridade: " + listaLembretes.get(position).getNomePrioridade());
            final TextView conteudoLembrete = (TextView) convertView.findViewById(R.id.txtConteudoLembrete);
            conteudoLembrete.setText(listaLembretes.get(position).getNotaLembrete());
            final TextView dataLembrete = (TextView) convertView.findViewById(R.id.txtDataLembrete);
            dataLembrete.setText(listaLembretes.get(position).getDataLembrete());
            final TextView horaLembrete = (TextView) convertView.findViewById(R.id.txtHoraLembrete);
            horaLembrete.setText(listaLembretes.get(position).getHoraLembrete());
            final ImageButton editar = (ImageButton) convertView.findViewById(R.id.btnEditarLembrete);
            editar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dbh = new DatabaseHelper(context);
                    int idLembrete = listaLembretes.get(position).getIdLembrete();
                    int idCategoria = listaLembretes.get(position).getIdCategoria();
                    int idPrioridade = listaLembretes.get(position).getIdPrioridade();
                    String categoria = listaLembretes.get(position).getNomeCategoria().toUpperCase();
                    String lembrete = listaLembretes.get(position).getNotaLembrete();
                    String data = listaLembretes.get(position).getDataLembrete();
                    String hora = listaLembretes.get(position).getHoraLembrete();
                    Intent intent = new Intent(context, EditarLembrete.class);
                    intent.putExtra("telaAnterior", telaAnteriorLembrete);
                    intent.putExtra("idLembrete", idLembrete);
                    intent.putExtra("idCategoria", idCategoria);
                    intent.putExtra("idPrioridade", idPrioridade);
                    intent.putExtra("categoria", categoria);
                    intent.putExtra("lembrete", lembrete);
                    intent.putExtra("data", data);
                    intent.putExtra("hora", hora);
                    startActivity(intent);
                }
            });

            final ImageButton visualizar = (ImageButton) convertView.findViewById(R.id.btnVisualizarLembrete);
            visualizar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dbh = new DatabaseHelper(context);
                    int idLembrete = listaLembretes.get(position).getIdLembrete();
                    Intent intent = new Intent(context, VisualizarLembrete.class);
                    intent.putExtra("id", idLembrete);
                    intent.putExtra("telaAnterior", telaAnteriorLembrete);
                    startActivity(intent);
                }
            });

            final ImageButton excluir = (ImageButton) convertView.findViewById(R.id.btnExcluirLembrete);
            excluir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    telaDialogo.setMessage("Excluir o lembrete?")
                            .setCancelable(false)
                            .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dbh = new DatabaseHelper(context);
                                    schCliente.excluirAlarmForNotification(listaLembretes.get(position).getMiliSegundosLembrete(),listaLembretes.get(position).getIdLembrete(), listaLembretes.get(position).getNomeCategoria(), listaLembretes.get(position).getNotaLembrete() ,"EXCLUIR");
                                    mNM.cancel(listaLembretes.get(position).getIdLembrete());
                                    dbh.deletarLembrete(listaLembretes.get(position).getIdLembrete());
                                    visualizarLembrete();
                                    Toast.makeText(context, "Lembrete excluído!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alerta = telaDialogo.create();
                    alerta.show();
                    visualizarLembrete();
                }
            });
            return convertView;
        }
    }

    @Override
    public void onStop() {
        // When our activity is stopped ensure we also stop the connection to the service
        // this stops us leaking our activity into the system *bad*
        if(schCliente != null)
            schCliente.doUnbindService();
        super.onStop();
    }

}