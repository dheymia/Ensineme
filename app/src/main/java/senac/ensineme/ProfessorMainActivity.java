package senac.ensineme;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import senac.ensineme.adapters.CategoriaProfAdapter;
import senac.ensineme.adapters.DemandaAluAdapter;
import senac.ensineme.adapters.DemandaProfAdapter;
import senac.ensineme.adapters.OfertaProfAdapter;
import senac.ensineme.models.Categoria;
import senac.ensineme.models.Demanda;
import senac.ensineme.models.Oferta;
import senac.ensineme.models.Usuario;

import static android.view.View.GONE;

public class ProfessorMainActivity extends AppCompatActivity {

    Usuario usuario,usuariologado;
    public static String idUsuario, nomeUsuario, tipoUsuario;
    private FirebaseDatabase firebase;
    private TextView bemvindo;
    public static boolean alterar = false;
    public static boolean validar = false;
    private androidx.appcompat.app.AlertDialog alertapropostas;
    private androidx.appcompat.app.AlertDialog alerta;
    private ProgressBar progressBar;
    private RecyclerView recyclerOfertas;
    private List<Oferta> ofertaList = new ArrayList<>();
    private RecyclerView recyclerDemandas,recyclerCategorias;
    private List<Categoria> categoriaList = new ArrayList<>();
    private List<Demanda> demandasList = new ArrayList<>();
    public static Demanda demandaSelecionada;
    private String myFormat = "dd/MM/yyyy";
    private String format = "yyyy/MM/dd";
    private SimpleDateFormat formatoData =  new SimpleDateFormat(myFormat, new Locale("pt", "BR"));
    private SimpleDateFormat formatoDataDemanda = new SimpleDateFormat(format, new Locale("pt", "BR"));
    public static String categoria, codCategoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_professor);

        ImageView imagemtoolbar = findViewById(R.id.app_bar_image);
        recyclerDemandas = findViewById(R.id.listDemandas);
        recyclerCategorias = findViewById(R.id.listCategorias);
        progressBar = findViewById(R.id.loading);
        bemvindo = findViewById(R.id.txtSejaBemvindo);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Professor");

        imagemtoolbar.setImageResource(R.drawable.top);

        firebase = FirebaseDatabase.getInstance();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        usuario = new Usuario();
        if (firebaseUser != null) {
            usuario.setId(firebaseUser.getUid());
            idUsuario = usuario.getId();
        }else{
            return;
        }

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference refUsu = database.getReference("usuarios/" + idUsuario);

        refUsu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usuariologado = dataSnapshot.getValue(Usuario.class);
                if (usuariologado.getTipo() != null) {
                    tipoUsuario = usuariologado.getTipo();
                    nomeUsuario = usuariologado.getNome();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference ref = firebase.getReference("categorias");
        DatabaseReference refDem = firebase.getReference("demandas");

        recyclerCategorias.setHasFixedSize(true);
        recyclerCategorias.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerDemandas.setHasFixedSize(true);
        recyclerDemandas.setLayoutManager(new LinearLayoutManager(this));

        progressBar.setFocusable(true);
        openProgressBar();
        ref.limitToFirst(100).orderByChild("nome").addValueEventListener(ListenerGeral);
        refDem.limitToFirst(100).orderByChild("situacao").equalTo("ATIVA").addValueEventListener(ListenerGeralDemandas);


    }

    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();

            Categoria categoriaSelecionada = categoriaList.get(position);
            categoria = categoriaSelecionada.getNome();
            codCategoria = categoriaSelecionada.getCodigo();

            Intent busca = new Intent(ProfessorMainActivity.this, AlunoBuscaActivity.class);
            startActivity(busca);
        }
    };

    private ValueEventListener ListenerGeral = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            categoriaList.clear();

            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                Categoria categoria = ds.getValue(Categoria.class);
                categoriaList.add(categoria);
            }

            CategoriaProfAdapter adapter = new CategoriaProfAdapter(categoriaList, ProfessorMainActivity.this);
            adapter.setOnItemClickListener(onItemClickListener);
            recyclerCategorias.setAdapter(adapter);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            closeProgressBar();
        }
    };

    private final View.OnClickListener onItemClickListenerDemandas = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) v.getTag();
            int position = holder.getAdapterPosition();

            demandaSelecionada = demandasList.get(position);

            LayoutInflater li = getLayoutInflater();
            final View view = li.inflate(R.layout.dialog_demanda_detalhes, null);

            LinearLayout voltar = view.findViewById(R.id.VerPropostas);

            TextView txtDescDemanda = view.findViewById(R.id.txtAluno);
            TextView txtCatDemanda = view.findViewById(R.id.txtCatDemanda);
            TextView txtTurnoDemanda = view.findViewById(R.id.txtTurno);
            TextView txtInicioDemanda = view.findViewById(R.id.txtInicio);
            TextView txtnHorasaulaDemanda = view.findViewById(R.id.txtCH);
            TextView txtLocalidadeDemanda = view.findViewById(R.id.txtCidade);
            TextView txtEstadoDemanda = view.findViewById(R.id.txtEstado);
            TextView txtLogradouroDemanda = view.findViewById(R.id.txtLogradouro);
            TextView txtComplementoDemanda = view.findViewById(R.id.txtComplemento);
            TextView txtNumeroDemanda = view.findViewById(R.id.txtNumero);
            TextView txtCEPDemanda = view.findViewById(R.id.txtCEP);
            TextView txtBairroDemanda = view.findViewById(R.id.txtBairro);
            TextView txtExpiracao = view.findViewById(R.id.textExpiracao);


            txtDescDemanda.setText(String.valueOf(demandaSelecionada.getDescricao()));
            txtCatDemanda.setText(String.valueOf(demandaSelecionada.getCategoria()));
            txtTurnoDemanda.setText(String.valueOf(demandaSelecionada.getTurno()));
            try {
                Date inicioformatado = formatoDataDemanda.parse(demandaSelecionada.getInicio());
                String inicio = formatoData.format(inicioformatado);
                txtInicioDemanda.setText(inicio);

                Date expiracaoformatada = formatoDataDemanda.parse(demandaSelecionada.getExpiracao());
                String expiracao = formatoData.format(expiracaoformatada);
                txtExpiracao.setText(expiracao);

            } catch (ParseException e) {
                e.printStackTrace();
            }
            txtnHorasaulaDemanda.setText(String.valueOf(demandaSelecionada.getHorasaula()) + " horas/aula");
            txtLocalidadeDemanda.setText(String.valueOf(demandaSelecionada.getLocalidade()));
            txtEstadoDemanda.setText(String.valueOf(demandaSelecionada.getEstado()));
            txtLogradouroDemanda.setText(String.valueOf(demandaSelecionada.getLogradouro()));
            txtComplementoDemanda.setText(String.valueOf(demandaSelecionada.getComplemento()));
            txtNumeroDemanda.setText(String.valueOf(demandaSelecionada.getNumero()));
            txtCEPDemanda.setText(String.valueOf(demandaSelecionada.getCEP()));
            txtBairroDemanda.setText(String.valueOf(demandaSelecionada.getBairro()));

            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ProfessorMainActivity.this);
            builder.setTitle(" quer aprender");

            voltar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alerta.cancel();
                }
            });

  /*      builder.setNegativeButton("Voltar", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog,int id) {
                dialog.cancel();
            }
        });*/
            builder.setView(view);
            alerta = builder.create();
            alerta.show();

        }
    };

    private ValueEventListener ListenerGeralDemandas = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            demandasList.clear();

            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                Demanda demanda = ds.getValue(Demanda.class);
                demandasList.add(demanda);

                Collections.sort(demandasList, new Comparator<Demanda>() {
                    @Override
                    public int compare(Demanda demanda, Demanda t1) {
                        if (demanda.getAtualizacao() == null || t1.getAtualizacao() == null)
                            return 0;
                        return t1.getAtualizacao().compareTo(demanda.getAtualizacao());
                    }
                });

                if(demandasList.size() != 0){
                    bemvindo.setVisibility(View.GONE);}
            }

            DemandaProfAdapter adapterDemandas = new DemandaProfAdapter(demandasList, ProfessorMainActivity.this);
            adapterDemandas.setOnItemClickListener(onItemClickListenerDemandas);
            adapterDemandas.setClickConsultaPrposta(clickConsultar);
            recyclerDemandas.setAdapter(adapterDemandas);
            closeProgressBar();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            closeProgressBar();
        }
    };

    private View.OnClickListener clickConsultar = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) v.getTag();
            int position = viewHolder.getAdapterPosition();

            demandaSelecionada = demandasList.get(position);

            LayoutInflater li = getLayoutInflater();
            final View view = li.inflate(R.layout.dialog_ofertas_consultar, null);

            LinearLayout voltar = view.findViewById(R.id.ConsultaPropostas);
            recyclerOfertas = view.findViewById(R.id.ListOfertas);
            final ProgressBar progressBar = view.findViewById(R.id.loading);
            Button btnSelecionaProposta = view.findViewById(R.id.btnEscolherProposta);
            TextView txtTitulo = view.findViewById(R.id.txtTitulo);

            txtTitulo.setText("Propostas enviadas");
            btnSelecionaProposta.setVisibility(View.GONE);


            recyclerOfertas.setHasFixedSize(true);
            recyclerOfertas.setLayoutManager(new LinearLayoutManager(ProfessorMainActivity.this, LinearLayoutManager.VERTICAL, false));

            progressBar.setFocusable(true);
            progressBar.setVisibility(View.VISIBLE);
            DatabaseReference refOfer = firebase.getReference("demandas/" + demandaSelecionada.getCodigo() + "/propostas");
            refOfer.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    ofertaList.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Oferta oferta = ds.getValue(Oferta.class);
                        ofertaList.add(oferta);
                    }
                    OfertaProfAdapter adapterOfertas = new OfertaProfAdapter(ofertaList, ProfessorMainActivity.this);
                    recyclerOfertas.setAdapter(adapterOfertas);
                    progressBar.setVisibility(GONE);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progressBar.setVisibility(GONE);
                }
            });

            voltar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertapropostas.dismiss();
                }
            });

            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ProfessorMainActivity.this);
            builder.setTitle("Ensinar " + demandaSelecionada.getDescricao());
            builder.setView(view);
            alertapropostas = builder.create();
            alertapropostas.show();
        }
    };

    private void openProgressBar(){
        progressBar.setVisibility( View.VISIBLE );
    }

    private void closeProgressBar(){
        progressBar.setVisibility( View.GONE );
    }

    private void showSnackbar(String message){
        Snackbar.make(progressBar,
                message,
                Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    protected void showToast(String message){
        Toast.makeText(this,
                message,
                Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.acaoSair) {
            FirebaseAuth.getInstance().signOut();
            Intent principal = new Intent(getBaseContext(), FullscreenActivity.class);
            startActivity(principal);
            finish();
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.acaoConfigurar) {
            Intent novaConfig = new Intent(getBaseContext(), SettingsActivity.class);
            startActivity(novaConfig);
        }

        if (id == R.id.acaoPesquisar) {
            Intent novaConfig = new Intent(getBaseContext(), ProfessorBuscaActivity.class);
            startActivity(novaConfig);
            categoria = "Todas";
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed()    {

        if (tipoUsuario.equals("administrador")) {
            Intent administrador = new Intent(ProfessorMainActivity.this, AdministradorMainActivity.class);
            startActivity(administrador);
            finish();

        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder
                    .setMessage("Deseja sair?")
                    .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            finishAffinity();

                        }
                    })

                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .show();
        }
    }
}
