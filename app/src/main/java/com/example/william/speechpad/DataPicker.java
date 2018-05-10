package com.example.william.speechpad;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TextView;
import java.util.Calendar;

/**
 * Created by William on 26/02/2015.
 */
public class DataPicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private int escolhaAno;
    private int escolhaMes;
    private int escolhaDia;
    private int pickerAno;
    private int pickerMes;
    private int pickerDia;
    public TextView lblDataClasse;
    private Calendar dataAtual ;
    SalvarPorTexto spt = new SalvarPorTexto();
    Context contexto;

    public DataPicker (){}
    public DataPicker (Context contexto) {
        this.contexto = contexto;
    }

     public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Deixa o PickerData com a data atual
        final Calendar calendario = Calendar.getInstance();
        pickerAno = calendario.get(Calendar.YEAR);
        pickerMes = calendario.get(Calendar.MONTH);
        pickerDia = calendario.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dpd = new DatePickerDialog(getActivity(), this, pickerAno, pickerMes, pickerDia);//Cria a caixa de dialogo data picker com as datas do Calendario instânciado
        DatePicker dp = dpd.getDatePicker();//DataPicker recebe o que foi criado, ao invés de mandar direto para a Activity que solicitou
        dp.setMinDate(calendario.getTimeInMillis());//Deixa com uma data mínima que é o dia atual
        return dpd;//retorna para a Activity o DataPickerDialog configurado
    }

    //Seta a data escolhida nas variáveis
    public void onDateSet(DatePicker view, int vAno, int vMes, int vDia) {
            escolhaAno = vAno;
            spt.setAno(vAno);
            escolhaMes = vMes;
            spt.setMes(vMes);
            escolhaDia = vDia;
            spt.setDia(vDia);
            Intent intent = new Intent(contexto,SalvarPorTexto.class);
            intent.putExtra("escolhaAno", escolhaAno);
            intent.putExtra("escolhaMes", escolhaMes);
            intent.putExtra("escolhaDia", escolhaDia);
            //contexto.startActivity(intent);
            AtualizarData();
    }

    public void AtualizarData() {
        lblDataClasse = (TextView) ((Activity) contexto).findViewById(R.id.lblData);
        lblDataClasse.setText(new StringBuilder().append(escolhaDia).append("/").append(escolhaMes + 1).append("/").append(escolhaAno));
    }

    //Método responsável por chamar o objeto
    public int show(FragmentTransaction transaction, String tag) {
        return super.show(transaction, tag);
    }
}

