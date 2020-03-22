package com.example.tormentsd.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tormentsd.Interfaces.Comunicador;
import com.example.tormentsd.Models.Adapter.DownloadArquivoAdapter;
import com.example.tormentsd.Models.Conexao.ConexaoServer;
import com.example.tormentsd.Models.Conexao.ConexaoTorment;
import com.example.tormentsd.Models.Conexao.RecebeConexao;
import com.example.tormentsd.Models.Dispositivo;
import com.example.tormentsd.Models.Adapter.DispositivoAdapter;
import com.example.tormentsd.Models.Adapter.PathAdapter;
import com.example.tormentsd.Models.DownloadArquivo;
import com.example.tormentsd.Models.Downloads;
import com.example.tormentsd.Models.PathArquivo;
import com.example.tormentsd.R;

import java.io.File;
import java.util.ArrayList;
import static com.example.tormentsd.Models.Downloads.downloads;


import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Comunicador {
    private EditText editTextPesquisa;
    private ConexaoServer conexaoServer;
    private CardView layoutLista;
    private ListView listaElementos;
    private LinearLayout layoutPrincipal;
    private ProgressDialog dialog;
    private ImageButton btnPesquisar;
    private ImageButton btnFechar;
    private TextView txtMensagem;
    private TextView txtTitulo;
    private TextView txtDownload;
    private String pesquisa;
    private boolean realizouPesquisa = false;
    private boolean solicitouDispositivosOnLine = false;
    private boolean solicitouArquivosDeDispositvo = false;
    private ArrayList<Dispositivo> dispositivos;
    private PathArquivo path;
    private RecebeConexao recebeConexao;
    private ListView listviewDeDownloads;


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

                solicitouDispositivosOnLine = true;

                conexaoServer.enviarMensagem("dispositivosConectados;dispositivos");
                Toast.makeText(getBaseContext(), "Enviando Mensagem", Toast.LENGTH_LONG).show();

                novoAlertDialog();

                return true;
        }

        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicia Downloads
        Downloads d = new Downloads();

        // Pede permissão ao usuário
        checaPermissao();

        // faz a conexão com o servidor
        conexaoServer = new ConexaoServer(MainActivity.this);
        conexaoServer.start();

        // Permite que o torment receba conexao de outros torments
        recebeConexao = new RecebeConexao(MainActivity.this);
        recebeConexao.start();

        // linka os componentes da tela com o back-end
        editTextPesquisa = findViewById(R.id.editTextPesquisa);
        listaElementos = findViewById(R.id.listDevices);
        layoutLista = findViewById(R.id.cardListaElementos);
        layoutPrincipal = findViewById(R.id.layoutPrincipal);
        btnPesquisar = findViewById(R.id.btnPesquisar);
        btnFechar = findViewById(R.id.btnFechar);
        txtTitulo = findViewById(R.id.txtTitulo);
        txtMensagem = findViewById(R.id.txtMensagem);
        listviewDeDownloads = findViewById(R.id.listDispositivosBaixados);
        txtDownload = findViewById(R.id.txtTituloDownload);

        atualizaListaDeDownloads();

     //  Toast.makeText(getBaseContext(), Environment.getDataDirectory() + " - " +
      //          Environment.getRootDirectory(), Toast.LENGTH_LONG).show();

        listaElementos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                if (realizouPesquisa) {

                    String ip = dispositivos.get(position).getIp()
                            .replace("/", "").split(":")[0];

                    ConexaoTorment conexaoTorment = new ConexaoTorment(ip, "requisicao;" + pesquisa, MainActivity.this);
                    conexaoTorment.start();

                    Toast.makeText(getBaseContext(), "INICIANDO DOWNLOAD",
                            Toast.LENGTH_LONG).show();

                    escondeLayoutLista();

                } else if (solicitouDispositivosOnLine) {

                    String ip = dispositivos.get(position).getIp()
                            .replace("/", "").split(":")[0];

                    novoAlertDialog();

                    ConexaoTorment conexaoTorment = new ConexaoTorment(ip, "todosArquivos;arquivo", MainActivity.this);
                    conexaoTorment.start();

                    path = new PathArquivo(conexaoTorment);

                } else if (solicitouArquivosDeDispositvo) {

                    final String[] arquivo = path.getPaths().get(position).split("/");


                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String req = "requisicao;" + arquivo[arquivo.length - 1];
                            path.getConexaoTorment().enviaMensagem(req);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    escondeLayoutLista();
                                }
                            });
                        }
                    }).start();

                }

                //Toast.makeText(getBaseContext()," --- "+ip,Toast.LENGTH_LONG).show();
            }
        });

        btnPesquisar.setOnClickListener(this);
        btnFechar.setOnClickListener(this);
    }

    public void atualizaListaDeDownloads() {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Toast.makeText(getBaseContext(), "Atualizando lista de downloads",
                            Toast.LENGTH_LONG).show();


                    if (downloads.size() > 0) {

                        txtDownload.setText("Últimos TORMENTS");
                        DownloadArquivoAdapter adt =
                                new DownloadArquivoAdapter(MainActivity.this, downloads);
                        listviewDeDownloads.setAdapter(adt);


                    } else {
                        txtDownload.setText("Nenhum arquivo baixado recentimente");
                    }

                }
            });

    }

    public void mostraCardComResultados(String[] mensagens, String titulo, String operacao) {

        txtTitulo.setText(titulo);
        txtMensagem.setVisibility(View.GONE);

        cancelaAlertDialog();

        dispositivos = criaListaDeDispostivos(mensagens);

        ArrayAdapter adapter = new DispositivoAdapter(this, dispositivos, operacao);
        listaElementos.setAdapter(adapter);

        mostraLayoutLista();
    }

    public void mostraCardComArquivos(String[] mensagens, String titulo, String operacao) {

        txtTitulo.setText(titulo);

        cancelaAlertDialog();

        path.setPaths(criaListaDePaths(mensagens));

        ArrayAdapter adapter = new PathAdapter(this, path, operacao);
        listaElementos.setAdapter(adapter);

        mostraLayoutLista();
    }

    public void mostraCardSemResultados(String titulo, String mensagem) {

        txtTitulo.setText(titulo);
        txtMensagem.setText(mensagem);
        txtMensagem.setVisibility(View.VISIBLE);

        cancelaAlertDialog();

        listaElementos.setAdapter(null);
        mostraLayoutLista();
    }

    public void mostraLayoutLista() {
        layoutPrincipal.setVisibility(GONE);
        layoutLista.setVisibility(View.VISIBLE);
    }

    public void escondeLayoutLista() {
        layoutPrincipal.setVisibility(View.VISIBLE);
        layoutLista.setVisibility(GONE);
    }

    public void novoAlertDialog() {
        dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage("Aguarde um momento...");
        dialog.setCancelable(false);
        dialog.show();
    }

    public void cancelaAlertDialog() {
        if (dialog.isShowing())
            dialog.cancel();

        dialog = null;
    }

    public void checaPermissao() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
        }
    }

    public void criaDiretorioTorment() {
        File folder = new File(Environment.getExternalStorageDirectory().getPath()
                + "/TORMENT/");

        if (!folder.exists())
            folder.mkdir();
    }

    public void pesquisarArquivo(String arquivo) {

        if (verificarSeArquivoExiste(arquivo))
            conexaoServer.enviarMensagem("possuiArquivo;" + arquivo);
        else
            conexaoServer.enviarMensagem("naoPossuiArquivo;" + arquivo);
    }

    private boolean verificarSeArquivoExiste(String arquivo) {
        File folder = new File(Environment.getExternalStorageDirectory() + "/TORMENT/" + arquivo);

        if (folder.exists())
            return true;
        else
            return false;
    }

    private ArrayList<Dispositivo> criaListaDeDispostivos(String[] ips) {

        ArrayList<Dispositivo> d = new ArrayList<>();
        for (int i = 1; i < ips.length; i++)
            d.add(new Dispositivo(ips[i]));

        return d;
    }

    private ArrayList<String> criaListaDePaths(String[] paths) {

        ArrayList<String> path = new ArrayList<>();
        for (int i = 1; i < paths.length; i++)
            path.add(paths[i]);

        return path;
    }

    private String[] corrigiStringResposta(String[] mensagens) {

        if (mensagens.length == 3 && mensagens[2].equals("false")) {

            String[] m = new String[mensagens.length - 1];
            int i = 0;

            for (String item : mensagens) {
                if (!item.equals("falseresultado")) {
                    m[i] = item;
                    i++;
                }
            }

            return m;
        } else
            return mensagens;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1000:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permissão concedida", Toast.LENGTH_SHORT).show();
                    criaDiretorioTorment();
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
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

                Toast.makeText(getBaseContext(), "mensagem: " + mensagem,
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

                            if (corrigiStringResposta(mensagens)[1].equals("false"))
                                mostraCardSemResultados("Pesquisa: " + pesquisa,
                                        "Sua Pesquisa não obteve resultados!");
                            else
                                mostraCardComResultados(mensagens, "Pesquisa: " + pesquisa, "DOWNLOAD");
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
                                mostraCardComResultados(mensagens, "Dispositivos ONLINE", "VER ARQUIVOS");
                        }
                    });
                    break;
            }
        }
    }

    @Override
    public void getArchivesRequested(final String[] archives) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                Toast.makeText(getBaseContext(), "Entrei", Toast.LENGTH_LONG).show();

                if (archives[1].equals("false")) {
                    mostraCardSemResultados("Arquivos DISPONÍVEIS",
                            "Nenhum arquivo está disponível nesse dispositivo");
                } else {
                    solicitouDispositivosOnLine = false;
                    solicitouArquivosDeDispositvo = true;
                    mostraCardComArquivos(archives, "Arquivos DISPONÍVEIS", "DOWNLOAD");
                }
            }

        });

    }

    @Override
    public void informUpdates(boolean update) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

               Toast.makeText(getBaseContext(),"Atualizando os downloads - blablabla",Toast.LENGTH_LONG).show();
            }
        });
            atualizaListaDeDownloads();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnFechar:

                realizouPesquisa = false;
                solicitouDispositivosOnLine = false;
                solicitouArquivosDeDispositvo = false;

                escondeLayoutLista();
                break;

            case R.id.btnPesquisar:

                realizouPesquisa = true;

                novoAlertDialog();

                pesquisa = editTextPesquisa.getText().toString();

                // enviando uma mensagem ao servidor
                conexaoServer.enviarMensagem("pesquisa;" + pesquisa);
                Toast.makeText(getBaseContext(), "Enviando Mensagem",
                        Toast.LENGTH_LONG).show();

                break;
        }
    }

    @Override
    public void onDestroy(){
        System.out.println("Bye");
        conexaoServer.enviarMensagem("desconectar");
       //conexaoServer.desconectarTorment();
        //recebeConexao.desconectarParaReceberConexao();
        super.onDestroy();


    }

    @Override
    public  void onBackPressed(){
        System.out.println("Bye");
        conexaoServer.enviarMensagem("desconectar");
        super.onBackPressed();
    }


}
