package com.example.tormentsd.Models.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.tormentsd.Models.PathArquivo;
import com.example.tormentsd.R;

public class PathAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final PathArquivo path;
    private final String operacao;

    public PathAdapter(Context ctx, PathArquivo paths, String operacao){
        super(ctx, R.layout.listview_requisicoes,paths.getPaths());

        this.context = ctx;
        this.path = paths;
        this.operacao = operacao;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.listview_requisicoes, parent, false);

        TextView dispositivo =  rowView.findViewById(R.id.txtIpElemento);
        TextView txtOperacao = rowView.findViewById(R.id.txtOperacao);


        dispositivo.setText(path.getPaths().get(position));
        txtOperacao.setText(operacao);

        return rowView;
    }

}
