package com.example.tormentsd.Models.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.tormentsd.Models.DownloadArquivo;
import com.example.tormentsd.R;

import java.util.ArrayList;


public class DownloadArquivoAdapter extends ArrayAdapter<DownloadArquivo> {

    private final Context context;
    private final ArrayList<DownloadArquivo> elementos;

    public DownloadArquivoAdapter(Context ctx, ArrayList<DownloadArquivo> elementos){
        super(ctx, R.layout.listview_arquivos_baixados,elementos);

        this.context = ctx;
        this.elementos = elementos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.listview_arquivos_baixados, parent, false);

        TextView path =  rowView.findViewById(R.id.txtPathArquivo);
        TextView status = rowView.findViewById(R.id.txtStatus);
        ProgressBar progrees = rowView.findViewById(R.id.progressDownload);


        path.setText(elementos.get(position).getPath());
        status.setText(elementos.get(position).getStatus());
        progrees.setProgress(elementos.get(position).getDownload());

        return rowView;
    }

}
