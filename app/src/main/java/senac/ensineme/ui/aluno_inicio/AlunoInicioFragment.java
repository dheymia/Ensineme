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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
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
import senac.ensineme.OfertaActivity;
import senac.ensineme.R;
import senac.ensineme.SettingsActivity;
import senac.ensineme.adapters.CategoriaProfAdapter;
import senac.ensineme.adapters.DemandaAluAdapter;
import senac.ensineme.adapters.OfertaProfAdapter;
import senac.ensineme.models.Categoria;
import senac.ensineme.models.Demanda;
import senac.ensineme.models.Oferta;

import static android.view.View.GONE;

public class AlunoInicioFragment extends Fragment  {

    public static boolean alterar = false;
    private AlertDialog alertapropostas;
    private AlertDialog alerta;
    private ProgressBar progressBar;
    private RecyclerView recyclerOfertas;
    private List <Oferta> ofertaList = new ArrayList<>();
    private RecyclerView recyclerDemandas,recyclerCategorias;
    private List<Categoria> categoriaList = new ArrayList<>();
    private List<Demanda> demandasList = new ArrayList<>();
    public static Demanda demandaSelecionada;
    private Demanda demandadetalhe;
    private String codDemanda;
    private String aluno;
    private String myFormat = "dd/MM/yyyy";
    private String format = "yyyy/MM/dd";
    private SimpleDateFormat formatoData =  new SimpleDateFormat(myFormat, new Locale("pt", "BR"));
    private SimpleDateFormat formatoDataDemanda = new SimpleDateFormat(format, new Locale("pt", "BR"));
    private String descDemanda;
    private String statusDemanda;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_aluno_inicio, container, false);
        FloatingActionButton fab = root.findViewById(R.id.fab);
        ImageView imagemtoolbar = root.findViewById(R.id.app_bar_image);
        recyclerDemandas = root.findViewById(R.id.listDemandas);
        recyclerCategorias = root.findViewById(R.id.listCategorias);
        progressBar = root.findViewById(R.id.loading);
        Toolbar toolbar = root.findViewById(R.id.toolbar);

        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Aluno");


        imagemtoolbar.setImageResource(R.drawable.ensinemeprincipal);

        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            aluno = firebaseUser.getUid();
        }

        DatabaseReference ref = firebase.getReference("categorias");
        DatabaseReference refDem = firebase.getReference("usuarios/" + aluno + "/demandas");

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
    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();

            Categoria categoriaSelecionada = categoriaList.get(position);
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

            CategoriaProfAdapter adapter = new CategoriaProfAdapter(categoriaList, getContext());
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
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    demandadetalhe = dataSnapshot.getValue(Demanda.class);

                    dialogDetalhesDemanda();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Snackbar.make(progressBar,
                            "Erro de leitura: " + databaseError.getCode(),
                            Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }
    };
    public void dialogDetalhesDemanda() {
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


        txtDescDemanda.setText(String.valueOf(demandadetalhe.getDescricao()));
        txtCatDemanda.setText(String.valueOf(demandadetalhe.getCategoria()));
        txtTurnoDemanda.setText(String.valueOf(demandadetalhe.getTurno()));
        try {
            Date inicioformatado = formatoDataDemanda.parse(demandadetalhe.getInicio());
            String inicio = formatoData.format(inicioformatado);
            txtInicioDemanda.setText(inicio);

            Date expiracaoformatada = formatoDataDemanda.parse(demandadetalhe.getExpiracao());
            String expiracao = formatoData.format(expiracaoformatada);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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

            DemandaAluAdapter adapterDemandas = new DemandaAluAdapter(demandasList, getContext());
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
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();

            demandaSelecionada = demandasList.get(position);
            codDemanda = demandaSelecionada.getCodigo();
            descDemanda = demandaSelecionada.getDescricao();
            statusDemanda = demandaSelecionada.getStatus();

            dialogconsultaOfertas();
        }
    };

    private void dialogconsultaOfertas() {

        LayoutInflater li = getLayoutInflater();
        final View view = li.inflate(R.layout.dialog_ofertas_consultar, null);

        LinearLayout voltar = view.findViewById(R.id.ConsultaPropostas);
        recyclerOfertas = view.findViewById(R.id.ListOfertas);
        final ProgressBar progressBar = view.findViewById(R.id.loading);
        Button btnSelecionaProposta = view.findViewById(R.id.btnEscolherProposta);
        TextView txtTitulo = view.findViewById(R.id.txtTitulo);

        txtTitulo.setText("Propostas recebidas");

        if (statusDemanda.equals("Aguardando validação")){
            btnSelecionaProposta.setVisibility(View.VISIBLE);
        } else {
            btnSelecionaProposta.setVisibility(View.GONE);
        }

        btnSelecionaProposta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent oferta = new Intent(getContext(), OfertaActivity.class);
                startActivity(oferta);
            }
        });

        recyclerOfertas.setHasFixedSize(true);
        recyclerOfertas.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        progressBar.setVisibility(View.VISIBLE);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference refOfer = database.getReference("demandas/" + codDemanda + "/propostas");
        refOfer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ofertaList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Oferta oferta = ds.getValue(Oferta.class);
                    ofertaList.add(oferta);
                }
                OfertaProfAdapter adapterOfertas = new OfertaProfAdapter(ofertaList, getContext());
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

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Aprender " + descDemanda);
        builder.setView(view);
        alertapropostas = builder.create();
        alertapropostas.show();
    }

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
        Toast.makeText(getContext(),
                message,
                Toast.LENGTH_LONG)
                .show();
    }


}