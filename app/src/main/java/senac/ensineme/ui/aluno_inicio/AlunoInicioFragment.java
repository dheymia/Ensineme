package senac.ensineme.ui.aluno_inicio;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import senac.ensineme.DemandaActivity;
import senac.ensineme.FullscreenActivity;
import senac.ensineme.R;
import senac.ensineme.SettingsActivity;
import senac.ensineme.adapters.CategoriaProfAdapter;
import senac.ensineme.adapters.DemandaAluAdapter;
import senac.ensineme.models.Categoria;
import senac.ensineme.models.Demanda;

public class AlunoInicioFragment extends Fragment  {

    private AlunoInicioViewModel homeViewModel;
    private FloatingActionButton fab;
    public static boolean alterar = false;
    private AlertDialog alerta;
    private TextView textView, txtExpiracao, txtDescDemanda, txtCatDemanda, txtTurnoDemanda, txtInicioDemanda, txtnHorasaulaDemanda, txtLocalidadeDemanda, txtEstadoDemanda, txtLogradouroDemanda, txtComplementoDemanda, txtNumeroDemanda, txtCEPDemanda, txtBairroDemanda;
    private LinearLayout voltar;
    private ProgressBar progressBar;
    private ImageView imagemtoolbar;
    private Toolbar toolbar;
    private RecyclerView recyclerDemandas,recyclerCategorias;
    private CategoriaProfAdapter adapter;
    public static DemandaAluAdapter adapterDemandas;
    private List<Categoria> categoriaList = new ArrayList<>();
    private List<Demanda> demandasList = new ArrayList<>();
    public static Demanda demandaSelecionada;
    private Demanda demandadetalhe;
    private String codDemanda, inicio, expiracao, aluno;
    private Date inicioformatado, expiracaoformatada;
    private Categoria categoria, categoriaSelecionada;
    private FirebaseDatabase firebase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference ref, refDem;
    private String myFormat = "dd/MM/yyyy";
    private String format = "yyyy/MM/dd";
    private SimpleDateFormat formatoData =  new SimpleDateFormat(myFormat, new Locale("pt", "BR"));
    private SimpleDateFormat formatoDataDemanda = new SimpleDateFormat(format, new Locale("pt", "BR"));

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(AlunoInicioViewModel.class);
        View root = inflater.inflate(R.layout.fragment_aluno_inicio, container, false);
        //final TextView textView = root.findViewById(R.id.text_home);
        fab = root.findViewById(R.id.fab);
        recyclerDemandas = root.findViewById(R.id.listDemandas);
        recyclerCategorias = root.findViewById(R.id.listCategorias);
        progressBar = root.findViewById(R.id.loading);
        imagemtoolbar = root.findViewById(R.id.app_bar_image);
        toolbar = root.findViewById(R.id.toolbar);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
//                textView.setText(s);
            }
        });

        imagemtoolbar.setImageResource(R.drawable.ensinemeprincipal);

        firebase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            aluno = firebaseUser.getUid();
        }

        ref = firebase.getReference("categorias");
        refDem = firebase.getReference("usuarios/" + aluno + "/demandas");

        recyclerCategorias.setHasFixedSize(true);
        recyclerCategorias.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        openProgressBar();
        ref.limitToFirst(100).orderByChild("nome").addValueEventListener(ListenerGeral);

        recyclerDemandas.setHasFixedSize(true);
        recyclerDemandas.setLayoutManager(new LinearLayoutManager(getContext()));
        openProgressBar();
        refDem.limitToFirst(100).addValueEventListener(ListenerGeralDemandas);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alterar = false;
                Intent intent = new Intent(getContext(), DemandaActivity.class);
                startActivity(intent);
            }
        });

        return root;

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();

            categoriaSelecionada = categoriaList.get(position);
            Toast.makeText(getContext(), "Consultando " + categoriaSelecionada.getNome(), Toast.LENGTH_SHORT).show();

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

            adapter = new CategoriaProfAdapter(categoriaList, getContext());
            adapter.setOnItemClickListener(onItemClickListener);
            recyclerCategorias.setAdapter(adapter);

            closeProgressBar();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            closeProgressBar();
        }
    };

    private final View.OnClickListener onItemClickListenerDemandas = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) view.getTag();
            int position = holder.getAdapterPosition();

            demandaSelecionada = demandasList.get(position);
            codDemanda = demandaSelecionada.getCodigo();

            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference refDem = database.getReference("demandas/" + codDemanda);

            refDem.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    demandadetalhe = dataSnapshot.getValue(Demanda.class);

                    dialogDetalhesDemanda();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Snackbar.make(progressBar,
                            "Erro de leitura: " + databaseError.getCode(),
                            Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
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
                        if (demanda.getData() == null || t1.getData() == null)
                            return 0;
                        return t1.getData().compareTo(demanda.getData());
                    }
                });


            }

            adapterDemandas = new DemandaAluAdapter(demandasList, getContext());
            adapterDemandas.setOnItemClickListener(onItemClickListenerDemandas);
            recyclerDemandas.setAdapter(adapterDemandas);
            closeProgressBar();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            closeProgressBar();
        }
    };

    protected void openProgressBar(){
        progressBar.setVisibility( View.VISIBLE );
    }

    protected void closeProgressBar(){
        progressBar.setVisibility( View.GONE );
    }

    protected void showSnackbar(String message){
        Snackbar.make(progressBar,
                message,
                Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    protected void showToast(String message){
        Toast.makeText(getContext(),
                message,
                Toast.LENGTH_LONG)
                .show();
    }

    public void dialogDetalhesDemanda() {
        LayoutInflater li = getLayoutInflater();
        final View view = li.inflate(R.layout.dialog_demanda_detalhes, null);

        voltar = view.findViewById(R.id.VerPropostas);

        txtDescDemanda = view.findViewById(R.id.txtAluno);
        txtCatDemanda = view.findViewById(R.id.txtCatDemanda);
        txtTurnoDemanda = view.findViewById(R.id.txtTurno);
        txtInicioDemanda = view.findViewById(R.id.txtInicio);
        txtnHorasaulaDemanda = view.findViewById(R.id.txtCH);
        txtLocalidadeDemanda = view.findViewById(R.id.txtCidade);
        txtEstadoDemanda = view.findViewById(R.id.txtEstado);
        txtLogradouroDemanda = view.findViewById(R.id.txtLogradouro);
        txtComplementoDemanda = view.findViewById(R.id.txtComplemento);
        txtNumeroDemanda = view.findViewById(R.id.txtNumero);
        txtCEPDemanda = view.findViewById(R.id.txtCEP);
        txtBairroDemanda = view.findViewById(R.id.txtBairro);
        txtExpiracao = view.findViewById(R.id.textExpiracao);


        txtDescDemanda.setText(String.valueOf(demandadetalhe.getDescricao()));
        txtCatDemanda.setText(String.valueOf(demandadetalhe.getCategoria()));
        txtTurnoDemanda.setText(String.valueOf(demandadetalhe.getTurno()));
        try {
            inicioformatado = formatoDataDemanda.parse(demandadetalhe.getInicio());
            inicio = formatoData.format(inicioformatado);
            txtInicioDemanda.setText(inicio);

            expiracaoformatada = formatoDataDemanda.parse(demandadetalhe.getExpiracao());
            expiracao = formatoData.format(expiracaoformatada);
            txtExpiracao.setText(expiracao);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        txtnHorasaulaDemanda.setText(String.valueOf(demandadetalhe.getHorasaula()) + " horas/aula");
        txtLocalidadeDemanda.setText(String.valueOf(demandadetalhe.getLocalidade()));
        txtEstadoDemanda.setText(String.valueOf(demandadetalhe.getEstado()));
        txtLogradouroDemanda.setText(String.valueOf(demandadetalhe.getLogradouro()));
        txtComplementoDemanda.setText(String.valueOf(demandadetalhe.getComplemento()));
        txtNumeroDemanda.setText(String.valueOf(demandadetalhe.getNumero()));
        txtCEPDemanda.setText(String.valueOf(demandadetalhe.getCEP()));
        txtBairroDemanda.setText(String.valueOf(demandadetalhe.getBairro()));

        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alerta.dismiss();
            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Detalhes da demanda");

        builder.setView(view);
        alerta = builder.create();
        alerta.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.acaoSair) {
            FirebaseAuth.getInstance().signOut();
            Intent principal = new Intent(getActivity().getBaseContext(), FullscreenActivity.class);
            startActivity(principal);
            getActivity().finish();
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.acaoConfigurar) {
            Intent novaConfig = new Intent(getActivity().getBaseContext(), SettingsActivity.class);
            startActivity(novaConfig);
        }

        return super.onOptionsItemSelected(item);
    }
}