package senac.ensineme.ui.aluno_demanda;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
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

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import senac.ensineme.R;
import senac.ensineme.adapters.DemandaAluAdapter;
import senac.ensineme.models.Demanda;

public class AlunoDemandaFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private AlertDialog alerta;
    private AlunoDemandaViewModel dashboardViewModel;
    private FloatingActionButton fab;
    private TextView textView, txtExpiracao, txtDescDemanda, txtCatDemanda, txtTurnoDemanda, txtInicioDemanda, txtnHorasaulaDemanda, txtLocalidadeDemanda, txtEstadoDemanda, txtLogradouroDemanda, txtComplementoDemanda, txtNumeroDemanda, txtCEPDemanda, txtBairroDemanda;
    private Button btnExcluir, btnAlterar, btnInserirProposta;
    private RecyclerView recyclerView;
    private LinearLayout voltar;
    private Spinner spConsulta;
    private ProgressBar progressBar;
    private ArrayAdapter<CharSequence> statusAdapter;
    public static DemandaAluAdapter adapter;
    private List<Demanda> demandasList = new ArrayList<>();
    public static Demanda demandaSelecionada;
    private Demanda demandadetalhe;
    private FirebaseDatabase firebase;
    private DatabaseReference ref;
    public static boolean alterar = false;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String consulta, aluno, codDemanda, inicio, expiracao;
    private Date inicioformatado, expiracaoformatada;
    private String myFormat = "dd/MM/yyyy";
    private String format = "yyyy/MM/dd";
    private SimpleDateFormat formatoData =  new SimpleDateFormat(myFormat, new Locale("pt", "BR"));
    private SimpleDateFormat formatoDataDemanda = new SimpleDateFormat(format, new Locale("pt", "BR"));


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
        ViewModelProviders.of(this).get(AlunoDemandaViewModel.class);
        View root = inflater.inflate(R.layout.fragment_aluno_demanda, container, false);
        textView = root.findViewById(R.id.text_dashboard);
        fab = root.findViewById(R.id.fab);
        recyclerView = root.findViewById(R.id.listAlunoDemandas);
        progressBar = root.findViewById(R.id.loading);
        spConsulta = root.findViewById(R.id.spConsulta);
        dashboardViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
//                textView.setText(s);
            }
        });

        firebase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            aluno = firebaseUser.getUid();
        }
        ref = firebase.getReference("usuarios/" + aluno + "/demandas");

        statusAdapter = ArrayAdapter.createFromResource(getContext(),R.array.statusDemanda, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spConsulta.setAdapter(statusAdapter);
        spConsulta.setSelection(statusAdapter.getPosition("Aguardando proposta"));
        spConsulta.setOnItemSelectedListener(this);

        consulta = spConsulta.getSelectedItem().toString();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


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

    private final View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();

            demandaSelecionada = demandasList.get(position);
            codDemanda = demandaSelecionada.getCodigo();
            showToast("Demanda selecionada: " + demandaSelecionada.getCodigo());

            dialogDetalhesDemanda();
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
            progressBar.setVisibility( View.GONE );
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            progressBar.setVisibility( View.GONE );
        }
    };


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        Object item = parent.getItemAtPosition(pos);
        consulta = item.toString();
        showToast( "OnItemSelectedListener : " + item.toString());

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
        final View view = li.inflate(R.layout.dialog_detalhes_demanda, null);

        btnAlterar = view.findViewById(R.id.btnAlterar);
        btnExcluir  = view.findViewById(R.id.btnExcluir);
        voltar = view.findViewById(R.id.dialogVoltar);
        btnInserirProposta  = view.findViewById(R.id.btnInserirProposta);

        btnInserirProposta.setVisibility(View.GONE);

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

        openProgressBar();
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

                closeProgressBar();


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

                view.findViewById(R.id.btnExcluir).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
                        builder
                                .setMessage("Deseja excluir esta solicitação?")
                                .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        demandadetalhe.excluiDemandaDB();
                                        alerta.dismiss();

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
                });


        view.findViewById(R.id.btnAlterar).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                alerta.dismiss();
                alterar = true;
                Intent demanda = new Intent(getContext(), DemandaActivity.class);
                startActivity(demanda);
            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Detalhes da demanda");

        builder.setView(view);
        alerta = builder.create();
        alerta.show();
    }

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
}