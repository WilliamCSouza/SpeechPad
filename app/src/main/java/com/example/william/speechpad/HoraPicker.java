package com.example.william.speechpad;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;
import java.util.Calendar;

/**
 Classe que implementa o HoraPicker e recebe a HORA e MINUTO escolhida pelo usuário
 DialogFragment: Gerencia o "ciclo de vida" do picker HORA
 TimePickerDialog.OnTimeSetListener: Implementa o picker HORA

 */
public class HoraPicker extends DialogFragment implements TimePickerDialog.OnTimeSetListener
{
    private int pickerHora, pickerMinuto, horaSelecionada, minutoSelecionado;
    private TextView lblHoraClasse;
    SalvarPorTexto spt = new SalvarPorTexto();
    Context contexto;

    //Construtor que recebe uma variável do tipo CONTEXT. Serve para indicar qual a Activity está instânciando os pickers
    public HoraPicker(Context contexto)
    {
        this.contexto = contexto;
    }

    public HoraPicker(){}

    //Cria o picker HORA com a hora e minuto atuais
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final Calendar calendario = Calendar.getInstance();//Intânciando classe Calendar
        pickerHora = calendario.get(Calendar.HOUR_OF_DAY);//Definindo a HORA ATUAL para a variável do picker
        pickerMinuto = calendario.get(Calendar.MINUTE);//Definindo o MINUTO ATUAL para a variável do picker
        return new TimePickerDialog(getActivity(), this, pickerHora, pickerMinuto, DateFormat.is24HourFormat(getActivity()));//Definindo o picker com a HORA e MINUTO ATUAIS
    }

    //Recebe a HORA e MINUTO selecionado pelo usuário
    public void onTimeSet(TimePicker view, int hourOfDay, int minute)
    {
        spt.setHoras(hourOfDay);
        spt.setMinuto(minute);
        horaSelecionada = hourOfDay;
        minutoSelecionado = minute;
        Intent intent = new Intent(contexto,SalvarPorTexto.class);
        intent.putExtra("horaSelecionada", horaSelecionada);
        intent.putExtra("minutoSelecionado", minutoSelecionado);
        //contexto.startActivity(intent);
        AtualizarHora();//Após receber as horas e minutos selecionados, chama o método AtualizarHora para o mesmo setar as variáveis recebidas. Seta no TextView da Activity que acionou esta classe
    }

    //Atualiza a TextView da Activity que acionou esta classe
    public void AtualizarHora()
    {
        //Define que a TextView desta classe vai setar o texto abaixo na TextView da Activity que acionou esta classe.
        //A variável "contexto" será utilizada agora para mostrar qual Activity acionou a classe
        lblHoraClasse = (TextView) ((Activity) contexto).findViewById(R.id.lblHora);
        //Condição para formatar o minuto para dois digítos
        if (minutoSelecionado <= 9 && minutoSelecionado >= 0) {
            lblHoraClasse.setText(new StringBuilder().append(horaSelecionada).append(":").append("0" + minutoSelecionado).append(" "));
        }
        else
        {
            lblHoraClasse.setText(new StringBuilder().append(horaSelecionada).append(":").append(minutoSelecionado).append(" "));
        }
    }

    //Chama o objeto HoraPicker
    @Override
    public int show(FragmentTransaction transaction, String tag) {
        return super.show(transaction, tag);
    }
}
