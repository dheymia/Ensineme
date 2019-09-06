package senac.ensineme.ui.professor_inicio;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
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
import senac.ensineme.OfertaActivity;
import senac.ensineme.R;
import senac.ensineme.adapters.CategoriaProfAdapter;
import senac.ensineme.adapters.DemandaAluAdapter;
import senac.ensineme.models.Categoria;
import senac.ensineme.models.Demanda;

public class ProfessorInicioFragment extends Fragment {

    private AlertDialog alerta;
    private HomeViewModel homeViewModel;
    private TextView textView, txtExpiracao, txtDescDemanda, txtCatDemanda, txtTurnoDemanda, txtInicioDemanda, txtnHorasaulaDemanda, txtLocalidadeDemanda, txtEstadoDemanda, txtLogradouroDemanda, txtComplementoDemanda, txtNumeroDemanda, txtCEPDemanda, txtBairroDemanda;
    private Button btnExcluir, btnAlterar,btnInserirProposta;
    private LinearLayout voltar;
    private ProgressBar progressBar;
    private RecyclerView recyclerDemandas,recyclerCategorias;
    private CategoriaProfAdapter adapter;
    public static DemandaAluAdapter adapterDemandas;
    private List<Categoria> categoriaList = new ArrayList<>();
    private List<Demanda> demandasList = new ArrayList<>();
    public static Demanda demandaSelecionada;
    private Demanda demandadetalhe;
    private String codDemanda, inicio, expiracao;
    private Date inicioformatado, expiracaoformatada;
    private Categoria categoria, categoriaSelecionada;
    private FirebaseDatabase firebase;
    private DatabaseReference ref, refDem;
    private String myFormat = "dd/MM/yyyy";
    private String format = "yyyy/MM/dd";
    private SimpleDateFormat formatoData =  new SimpleDateFormat(myFormat, new Locale("pt", "BR"));
    private SimpleDateFormat formatoDataDemanda = new SimpleDateFormat(format, new Locale("pt", "BR"));

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_professor_inicio, container, false);
        textView = root.findViewById(R.id.text_home);
        recyclerDemandas = root.findViewById(R.id.listDemandas);
        recyclerCategorias = root.findViewById(R.id.listCategorias);
        progressBar = root.findViewById(R.id.loading);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
//                textView.setText(s);
            }
        });

        firebase = FirebaseDatabase.getInstance();
        ref = firebase.getReference("categorias");
        refDem = firebase.getReference( "demandas");


        recyclerCategorias.setHasFixedSize(true);
        recyclerCategorias.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        openProgressBar();
        ref.limitToFirst(100).orderByChild("nome").addValueEventListener(ListenerGeral);

        recyclerDemandas.setHasFixedSize(true);
        recyclerDemandas.setLayoutManager(new LinearLayoutManager(getContext()));
        openProgressBar();
        refDem.limitToFirst(10).orderByChild("status").equalTo("Aguardando proposta").addValueEventListener(ListenerGeralDemandas);

        return root;
    }

    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();

            categoriaSelecionada = categoriaList.get(position);
            Toast.makeText(getContext(), "Categoria selecionada: " + categoriaSelecionada.getNome(), Toast.LENGTH_SHORT).show();

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
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();

            demandaSelecionada = demandasList.get(position);
            showToast("Demanda selecionada: " + demandaSelecionada.getCodigo());
            codDemanda = demandaSelecionada.getCodigo();
            dialogDetalhesDemanda();
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
                        if (demanda.getExpiracao() == null || t1.getExpiracao() == null)
                            return 0;
                        return demanda.getExpiracao().compareTo(t1.getExpiracao());
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
        final View view = li.inflate(R.layout.dialog_detalhes_demanda, null);

        btnAlterar = view.findViewById(R.id.btnAlterar);
        btnExcluir  = view.findViewById(R.id.btnExcluir);
        voltar = view.findViewById(R.id.dialogVoltar);
        btnInserirProposta  = view.findViewById(R.id.btnInserirProposta);

        btnAlterar.setVisibility(View.GONE);
        btnExcluir.setVisibility(View.GONE);

        txtDescDemanda = view.findViewById(R.id.txtDescricao);
        txtCatDemanda = view.findViewById(R.id.txtCatDemanda);
        txtTurnoDemanda = view.findViewById(R.id.txtTurno);
        txtInicioDemanda = view.findViewById(R.id.txtInicio);
        txtnHorasaulaDemanda = view.findViewById(R.id.txtCH);
        txtLocalidadeDemanda = view.findViewById(R.id.txtCidade);
        txtEstadoDemanda = view.findViewById(R.id.txtEstado);
        txtLogradouroDemanda = view.findViewById(R.id.txtLogradouro);
        txtComplementoDemanda = view.findViewById(R.id.txtComplemento);
        txtNumeroDemanda = view.findViewById(R.id.txtnumero);
        txtCEPDemanda = view.findViewById(R.id.txtCEP);
        txtBairroDemanda = view.findViewById(R.id.txtBairro);
        txtExpiracao = view.findViewById(R.id.textExpiracao);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference refDem = database.getReference("demandas/" + codDemanda);

        refDem.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                demandadetalhe = dataSnapshot.getValue(Demanda.class);

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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.loading);
                Snackbar.make(progressBar,
                        "Erro de leitura: " + databaseError.getCode(),
                        Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alerta.dismiss();
            }
        });

        view.findViewById(R.id.btnInserirProposta).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                alerta.dismiss();
                Intent oferta = new Intent(getContext(), OfertaActivity.class);
                startActivity(oferta);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Detalhes da demanda");

        builder.setView(view);
        alerta = builder.create();
        alerta.show();
    }
}