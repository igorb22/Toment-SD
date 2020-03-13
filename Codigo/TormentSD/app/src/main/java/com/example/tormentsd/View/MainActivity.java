package com.example.tormentsd.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tormentsd.Interfaces.Comunicacao;
import com.example.tormentsd.Models.Conexao;
import com.example.tormentsd.Models.Dispositivo;
import com.example.tormentsd.Models.DispositivoAdapter;
import com.example.tormentsd.R;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements Comunicacao {
    private ImageButton btnPesquisar;
    private EditText editTextPesquisa;
    private TextView textView;
    private Conexao conexao;
    private CardView layoutLista;
    private ListView listaElementos;
    private CardView layoutPesquisa;
    private ImageButton btnFecharLista;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dispositivos_conectados, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_back:

                //comunicador.layoutIsClosed(true);
                layoutPesquisa.setVisibility(View.GONE);
                layoutLista.setVisibility(View.VISIBLE);



                ArrayList<Dispositivo> elementos = new ArrayList<>();

                elementos.add(new Dispositivo("192.168.9.2.1"));
                elementos.add(new Dispositivo("192.168.9.2.1"));
                elementos.add(new Dispositivo("192.168.9.2.1"));
                elementos.add(new Dispositivo("192.168.9.2.1"));
                elementos.add(new Dispositivo("192.168.9.2.1"));
                elementos.add(new Dispositivo("192.168.9.2.1"));
                elementos.add(new Dispositivo("192.168.9.2.1"));
                elementos.add(new Dispositivo("192.168.9.2.1"));
                elementos.add(new Dispositivo("192.168.9.2.1"));
                elementos.add(new Dispositivo("192.168.9.2.1"));

                ArrayAdapter adapter = new DispositivoAdapter(this, elementos);
                listaElementos.setAdapter(adapter);


                return true;
        }

        return false;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // Pede permissão ao usuário
        checaPermissao();

        // faz a conexão com o servidor
        conexao = new Conexao(MainActivity.this);
        conexao.start();

        // linka os componentes da tela com o back-end
        btnPesquisar = findViewById(R.id.btnPesquisar);
        editTextPesquisa = findViewById(R.id.editTextPesquisa);
        textView = findViewById(R.id.texto);
        listaElementos = findViewById(R.id.listDevices);
        layoutLista = findViewById(R.id.cardListaElementos);
        layoutPesquisa = findViewById(R.id.cardPesquisa);
        btnFecharLista = findViewById(R.id.btnFechar);

        // solicita o status da conexão (se está aberta ou não)
        conexao.solicitarStatusConexao();

        Toast.makeText(this, Environment.getExternalStorageDirectory()+"", Toast.LENGTH_SHORT).show();

        btnPesquisar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 // enviando uma mensagem ao servidor
                 conexao.enviarMensagem("pesquisa;"+editTextPesquisa.getText().toString());
                 Toast.makeText(getBaseContext(), "Enviando Mensagem",Toast.LENGTH_LONG).show();
            }
        });

        btnFecharLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutPesquisa.setVisibility(View.VISIBLE);
                layoutLista.setVisibility(View.GONE);
            }
        });
    }

    public void checaPermissao(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1000);
        }
    }

    public void criaDiretorioTorment(){
        File folder = new File(Environment.getExternalStorageDirectory() + "/TORMENT");

        if(!folder.exists())
            folder.mkdir();
    }

    public void pesquisarArquivo(String arquivo){

        if(verificarSeArquivoExiste(arquivo))
            conexao.enviarMensagem("possuiArquivo;"+arquivo);
        else
            conexao.enviarMensagem("naoPossuiArquivo;"+arquivo);
    }

    public boolean verificarSeArquivoExiste(String arquivo){
        File folder = new File(Environment.getExternalStorageDirectory() + "/TORMENT/"+arquivo);

        if(folder.exists())
            return true;
        else
            return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1000:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permissão concedida", Toast.LENGTH_SHORT).show();
                    criaDiretorioTorment();
                }else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    finishAffinity();
                }
        }
    }

    @Override
    public void isConnected(boolean status) {

    }

    @Override
    public void isClosed(boolean status) {

    }

    @Override
    public void receiveMessage(String mensagem) {

        textView.setText(mensagem);

        String [] mensagens = mensagem.split(";");
        switch (mensagens[0]){

            case "pesquisa":
                pesquisarArquivo(mensagens[1]);

                break;

            case "resultado":

                break;

            case "dispositivosConectados":

                  break;
        }
    }
}
