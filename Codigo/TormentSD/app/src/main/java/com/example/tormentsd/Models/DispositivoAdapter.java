package com.example.tormentsd.Models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.tormentsd.R;

import java.util.ArrayList;

public class DispositivoAdapter extends ArrayAdapter<Dispositivo> {

    private final Context context;
    private final ArrayList<Dispositivo> elementos;
    private final String operacao;

    public DispositivoAdapter(Context ctx,ArrayList<Dispositivo> dispositivos,String operacao){
        super(ctx, R.layout.listview,dispositivos);
        this.context = ctx;
        this.elementos = dispositivos;
        this.operacao = operacao;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.listview, parent, false);

        TextView dispositivo =  rowView.findViewById(R.id.txtIpElemento);
        TextView txtOperacao = rowView.findViewById(R.id.txtOperacao);


        dispositivo.setText(elementos.get(position).getIp());
        txtOperacao.setText(operacao);

        return rowView;
    }

}
