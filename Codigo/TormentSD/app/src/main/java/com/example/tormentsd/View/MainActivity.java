package com.example.tormentsd.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tormentsd.Interfaces.Comunicacao;
import com.example.tormentsd.Models.ConexaoServer;
import com.example.tormentsd.Models.Dispositivo;
import com.example.tormentsd.Models.DispositivoAdapter;
import com.example.tormentsd.R;
import java.io.File;
import java.util.ArrayList;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,Comunicacao {
    private EditText editTextPesquisa;
    private TextView textView;
    private ConexaoServer conexao;
    private CardView layoutLista;
    private ListView listaElementos;
    private CardView layoutPesquisa;
    private ProgressDialog dialog;
    private ImageButton btnPesquisar;
    private ImageButton btnFechar;
    private TextView txtMensagem;
    private TextView txtTitulo;
    private String pesquisa;
    private boolean realizouPesquisa = false;
    private ArrayList<Dispositivo> dispositivos;

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

                conexao.enviarMensagem("dispositivosConectados;dispositivos");
                Toast.makeText(getBaseContext(), "Enviando Mensagem",Toast.LENGTH_LONG).show();

                novoAlertDialog();

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
        conexao = new ConexaoServer(MainActivity.this);
        conexao.start();

        // linka os componentes da tela com o back-end
        editTextPesquisa = findViewById(R.id.editTextPesquisa);
        textView = findViewById(R.id.texto);
        listaElementos = findViewById(R.id.listDevices);
        layoutLista = findViewById(R.id.cardListaElementos);
        layoutPesquisa = findViewById(R.id.cardPesquisa);
        btnPesquisar = findViewById(R.id.btnPesquisar);
        btnFechar = findViewById(R.id.btnFechar);
        txtTitulo = findViewById(R.id.txtTitulo);
        txtMensagem = findViewById(R.id.txtMensagem);


        listaElementos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(realizouPesquisa){

                    dispositivos.get(position).getIp();

                } else{


                }
            }
        });

        btnPesquisar.setOnClickListener(this);
        btnFechar.setOnClickListener(this);
    }

    public void mostraCardComResultados(String[] mensagens, String titulo,String operacao){

        txtTitulo.setText(titulo);
        txtMensagem.setVisibility(View.GONE);

        cancelaAlertDialog();

        dispositivos = criaListaDeDispostivos(mensagens);

        ArrayAdapter adapter = new DispositivoAdapter(this,dispositivos,operacao);
        listaElementos.setAdapter(adapter);

        mostraLayoutLista();
    }

    public void mostraCardSemResultados(String titulo, String mensagem){

        txtTitulo.setText(titulo);
        txtMensagem.setText(mensagem);
        txtMensagem.setVisibility(View.VISIBLE);

        cancelaAlertDialog();

        listaElementos.setAdapter(null);
        mostraLayoutLista();
    }


    public void mostraLayoutLista(){
        layoutPesquisa.setVisibility(GONE);
        layoutLista.setVisibility(View.VISIBLE);
    }

    public void escondeLayoutLista(){
        layoutPesquisa.setVisibility(View.VISIBLE);
        layoutLista.setVisibility(GONE);
    }

    public void novoAlertDialog(){
        dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage("Aguarde um momento...");
        dialog.setCancelable(false);
        dialog.show();
    }

    public void cancelaAlertDialog(){
        if (dialog.isShowing())
            dialog.cancel();

        dialog = null;
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
            conexao.enviarMensagem("naoPossuiArquivolalalalal;"+arquivo);
    }

    private boolean verificarSeArquivoExiste(String arquivo){
        File folder = new File(Environment.getExternalStorageDirectory() + "/TORMENT/"+arquivo);

        if(folder.exists())
            return true;
        else
            return false;
    }

    private ArrayList<Dispositivo> criaListaDeDispostivos(String [] ips){

        ArrayList<Dispositivo> d = new ArrayList<>();
        for (int i = 1; i < ips.length;i++)
            d.add(new Dispositivo(ips[i]));

        return d;
    }

    private String[] corrigiStringResposta(String[] mensagens){

        if (mensagens.length == 3 && mensagens[2].equals("false")) {

            String[] m = new String[mensagens.length-1];
            int i = 0;

            for (String item : mensagens) {
                if (!item.equals("falseresultado")) {
                    m[i] = item;
                    i++;
                }
            }

            return m;
        }else{
            return mensagens;
        }
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
    public void receiveMessage(final String mensagem) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Toast.makeText(getBaseContext(), "mensagem: "+mensagem,
                        Toast.LENGTH_LONG).show();

            }
        });

        if (mensagem != null) {

            final String[] mensagens = mensagem.split(";");
            switch (mensagens[0]) {

                case "pesquisa":
                    pesquisarArquivo(mensagens[1]);

                    break;

                case "resultado":

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Toast.makeText(getBaseContext(), "tem original "+mensagens[1]+
                                    "\nitem: "+
                                    corrigiStringResposta(mensagens)[1],Toast.LENGTH_LONG).show();
                            if (corrigiStringResposta(mensagens)[1].equals("false"))
                                mostraCardSemResultados("Pesquisa: "+pesquisa,
                                        "Sua Pesquisa não obteve resultados!");
                            else
                                mostraCardComResultados(mensagens,"Pesquisa: "+pesquisa,"DOWNLOAD");
                        }
                    });

                    break;

                case "dispositivosConectados":

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mensagens[1].equals("false"))
                                mostraCardSemResultados("Dispositivos ONLINE",
                                        "Nenhum dispositivo além de você \nestá conectado");
                            else
                                mostraCardComResultados(mensagens,"Dispositivos ONLINE","VER ARQUIVOS");
                        }
                    });
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnFechar:

                    if (realizouPesquisa)
                        realizouPesquisa = false;

                    escondeLayoutLista();
                break;

            case R.id.btnPesquisar:

                    realizouPesquisa = true;

                    novoAlertDialog();

                    pesquisa = editTextPesquisa.getText().toString();

                    // enviando uma mensagem ao servidor
                    conexao.enviarMensagem("pesquisa;"+pesquisa);
                    Toast.makeText(getBaseContext(), "Enviando Mensagem",
                                    Toast.LENGTH_LONG).show();

                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        conexao.desconectarTorment();
    }


}
