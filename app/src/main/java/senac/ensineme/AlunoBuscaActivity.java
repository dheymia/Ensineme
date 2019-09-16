package senac.ensineme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

import senac.ensineme.providers.DemandaSuggestionProvider;
import senac.ensineme.adapters.DemandaAluAdapter;
import senac.ensineme.adapters.OfertaProfAdapter;
import senac.ensineme.models.Demanda;
import senac.ensineme.models.Oferta;

import static android.view.View.GONE;

public class AlunoBuscaActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    FirebaseDatabase firebase;
    private String aluno;
    private RecyclerView recyclerDemandas;
    private ProgressBar progressBar;
    private List<Demanda> demandasList = new ArrayList<>();
    public static Demanda demandaSelecionada;
    private String myFormat = "dd/MM/yyyy";
    private String format = "yyyy/MM/dd";
    private SimpleDateFormat formatoData =  new SimpleDateFormat(myFormat, new Locale("pt", "BR"));
    private SimpleDateFormat formatoDataDemanda = new SimpleDateFormat(format, new Locale("pt", "BR"));
    private AlertDialog alertapropostas;
    private AlertDialog alerta;
    private RecyclerView recyclerOfertas;
    private List <Oferta> ofertaList = new ArrayList<>();
    public static boolean alterar = false;
    public static boolean validar = false;
    SearchView searchView;
    DemandaAluAdapter adapterDemandas;
    private ArrayAdapter<CharSequence> statusAdapter;
    private Spinner spConsulta;
    private String consulta;
    DatabaseReference refDem, refCat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busca_aluno);

        recyclerDemandas = findViewById(R.id.ListaBuscaDemandas);
        progressBar = findViewById(R.id.loading);
        spConsulta = findViewById(R.id.spConsulta);

        firebase = FirebaseDatabase.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            aluno = firebaseUser.getUid();
        }

        refDem = firebase.getReference("demandas");
        refCat = firebase.getReference("categorias/" + AlunoMainActivity.codCategoria + "/demandas");



        statusAdapter = ArrayAdapter.createFromResource(this, R.array.statusDemanda, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spConsulta.setAdapter(statusAdapter);
        spConsulta.setSelection(statusAdapter.getPosition("Todos os status"));
        spConsulta.setOnItemSelectedListener(this);
        consulta = spConsulta.getSelectedItem().toString();


        recyclerDemandas.setHasFixedSize(true);
        recyclerDemandas.setLayoutManager(new LinearLayoutManager(this));

        progressBar.setFocusable(true);
        openProgressBar();

        if (AlunoMainActivity.categoria.equals("Todas")){
            getSupportActionBar().setTitle("Todas as demandas");
        } else{
            getSupportActionBar().setTitle(AlunoMainActivity.categoria);
        }

    }

    private ValueEventListener ListenerGeralDemandas = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            demandasList.clear();

            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                Demanda demanda = ds.getValue(Demanda.class);
                if (demanda.getAluno().equals(aluno)){
                    demandasList.add(demanda);
                }

                Collections.sort(demandasList, new Comparator<Demanda>() {
                    @Override
                    public int compare(Demanda demanda, Demanda t1) {
                        if (demanda.getAtualizacao() == null || t1.getAtualizacao() == null)
                            return 0;
                        return t1.getAtualizacao().compareTo(demanda.getAtualizacao());
                    }
                });
            }

            adapterDemandas = new DemandaAluAdapter(demandasList, AlunoBuscaActivity.this);
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

            AlertDialog.Builder builder = new AlertDialog.Builder(AlunoBuscaActivity.this);
            builder.setTitle("Você quer aprender");

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

            txtTitulo.setText("Propostas recebidas");

            if (demandaSelecionada.getStatus().equals("Aguardando validação")){
                btnSelecionaProposta.setVisibility(View.VISIBLE);
            } else {
                btnSelecionaProposta.setVisibility(View.GONE);
            }

            btnSelecionaProposta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    validar = true;
                    Intent oferta = new Intent(AlunoBuscaActivity.this, OfertaValidaActivity.class);
                    startActivity(oferta);
                }
            });

            recyclerOfertas.setHasFixedSize(true);
            recyclerOfertas.setLayoutManager(new LinearLayoutManager(AlunoBuscaActivity.this, LinearLayoutManager.VERTICAL, false));

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
                    OfertaProfAdapter adapterOfertas = new OfertaProfAdapter(ofertaList, AlunoBuscaActivity.this);
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

            AlertDialog.Builder builder = new AlertDialog.Builder(AlunoBuscaActivity.this);
            builder.setTitle("Aprender " + demandaSelecionada.getDescricao());
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();

        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true); // Do not iconify the widget; expand it by default

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String d) {
                adapterDemandas.getFilter().filter(d);
                searchView.clearFocus();
                if(adapterDemandas.getDemandaList().size() > 0){
                    SearchRecentSuggestions suggestions = new SearchRecentSuggestions(AlunoBuscaActivity.this,
                            DemandaSuggestionProvider.AUTHORITY, DemandaSuggestionProvider.MODE);
                    suggestions.saveRecentQuery(d, null);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapterDemandas.getFilter().filter(s);
                return false;
            }
        });

        return true;
    }

    @Override

    public boolean onSearchRequested() {
        return super.onSearchRequested();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.app_bar_search) {
            return true;
        }

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id){
        Object item = parent.getItemAtPosition(pos);

        consulta = item.toString();

        showToast("OnItemSelectedListener : " + item.toString());

        if (consulta.equals("Todos os status")) {
            openProgressBar();
            if (AlunoMainActivity.categoria.equals("Todas")){
                refDem.limitToFirst(100).addValueEventListener(ListenerGeralDemandas);
            } else{
                refCat.limitToFirst(100).addValueEventListener(ListenerGeralDemandas);
            }
        } else {
            openProgressBar();
            if (AlunoMainActivity.categoria.equals("Todas")){
                refDem.limitToFirst(100).orderByChild("status").equalTo(consulta).addValueEventListener(ListenerGeralDemandas);
            } else{
                refCat.limitToFirst(100).orderByChild("status").equalTo(consulta).addValueEventListener(ListenerGeralDemandas);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
