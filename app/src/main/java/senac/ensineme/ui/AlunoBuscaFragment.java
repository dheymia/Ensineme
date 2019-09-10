package senac.ensineme.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import senac.ensineme.R;
import senac.ensineme.adapters.DemandaAluAdapter;
import senac.ensineme.adapters.OfertaProfAdapter;
import senac.ensineme.models.Demanda;
import senac.ensineme.models.Oferta;

import static android.view.View.GONE;

public class AlunoBuscaFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private AlertDialog alerta;
    private AlertDialog alertapropostas;
    private TextView textView, txtExpiracao, txtDescDemanda, txtCatDemanda, txtTurnoDemanda, txtInicioDemanda, txtnHorasaulaDemanda, txtLocalidadeDemanda, txtEstadoDemanda, txtLogradouroDemanda, txtComplementoDemanda, txtNumeroDemanda, txtCEPDemanda, txtBairroDemanda;
    private RecyclerView recyclerView;
    private LinearLayout voltar;
    private Spinner spConsulta;
    private ProgressBar progressBar;
    private RecyclerView recyclerOfertas;
    private List <Oferta> ofertaList = new ArrayList<>();
    private ArrayAdapter<CharSequence> statusAdapter;
    public static DemandaAluAdapter adapter;
    private List<Demanda> demandasList = new ArrayList<>();
    private Demanda demandaSelecionada;
    private Demanda demandadetalhe;
    private FirebaseDatabase firebase;
    private DatabaseReference ref;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String consulta, aluno, codDemanda, inicio, expiracao;
    private Date inicioformatado, expiracaoformatada;
    private String descDemanda;

    private String myFormat = "dd/MM/yyyy";
    private String format = "yyyy/MM/dd";
    private SimpleDateFormat formatoData = new SimpleDateFormat(myFormat, new Locale("pt", "BR"));
    private SimpleDateFormat formatoDataDemanda = new SimpleDateFormat(format, new Locale("pt", "BR"));


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_aluno_busca, container, false);
        //textView = root.findViewById(R.id.text_dashboard);
        recyclerView = root.findViewById(R.id.listAlunoDemandas);
        progressBar = root.findViewById(R.id.loading);
        spConsulta = root.findViewById(R.id.spConsulta);
        firebase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            aluno = firebaseUser.getUid();
        }
        ref = firebase.getReference("usuarios/" + aluno + "/demandas");

        statusAdapter = ArrayAdapter.createFromResource(getContext(), R.array.statusDemanda, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spConsulta.setAdapter(statusAdapter);
        spConsulta.setSelection(statusAdapter.getPosition("Aguardando proposta"));
        spConsulta.setOnItemSelectedListener(this);

        consulta = spConsulta.getSelectedItem().toString();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        return root;
    }

    private final View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();

            demandaSelecionada = demandasList.get(position);
            codDemanda = demandaSelecionada.getCodigo();

            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference refDem = database.getReference("demandas/" + codDemanda);

            openProgressBar();
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

    private ValueEventListener ListenerGeral = new ValueEventListener() {
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

            adapter = new DemandaAluAdapter(demandasList, getContext());
            adapter.setOnItemClickListener(onItemClickListener);
            recyclerView.setAdapter(adapter);
            adapter.setClickConsultaPrposta(clickConsultar);
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            progressBar.setVisibility(View.GONE);
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
        btnSelecionaProposta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSnackbar("Validar");
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
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        Object item = parent.getItemAtPosition(pos);
        consulta = item.toString();
        showToast("OnItemSelectedListener : " + item.toString());

        if (consulta.equals("Todas")) {
            openProgressBar();
            ref.limitToFirst(100).addValueEventListener(ListenerGeral);
        } else {
            openProgressBar();
            ref.limitToFirst(100).orderByChild("status").equalTo(consulta).addValueEventListener(ListenerGeral);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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

        closeProgressBar();


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

    protected void openProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    protected void closeProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    protected void showSnackbar(String message) {
        Snackbar.make(progressBar,
                message,
                Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    protected void showToast(String message) {
        Toast.makeText(getContext(),
                message,
                Toast.LENGTH_LONG)
                .show();
    }
}