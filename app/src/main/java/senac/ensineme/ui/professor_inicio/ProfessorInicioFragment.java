package senac.ensineme.ui.professor_inicio;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import senac.ensineme.CategoriasActivity;
import senac.ensineme.FullscreenActivity;
import senac.ensineme.R;
import senac.ensineme.SettingsActivity;
import senac.ensineme.adapters.CategoriaProfAdapter;
import senac.ensineme.adapters.DemandaProfAdapter;
import senac.ensineme.adapters.OfertaAdapter;
import senac.ensineme.models.Categoria;
import senac.ensineme.models.Demanda;
import senac.ensineme.models.FirebaseDB;
import senac.ensineme.models.Oferta;
import senac.ensineme.ui.aluno_inicio.AlunoInicioFragment;

public class ProfessorInicioFragment extends Fragment implements DatabaseReference.CompletionListener {

    private AlertDialog alerta, alertaoferta, alertapropostas;
    private ProfessorInicioViewModel homeViewModel;
    private TextView textView,txtProfessor, txtExpiracao, txtDescDemanda, txtCatDemanda, txtTurnoDemanda, txtInicioDemanda, txtnHorasaulaDemanda, txtLocalidadeDemanda, txtEstadoDemanda, txtLogradouroDemanda, txtComplementoDemanda, txtNumeroDemanda, txtCEPDemanda, txtBairroDemanda;
    private EditText txtValorOferta, txtComentarioOferta;
    private Button btnExcluir, btnAlterar, btnCadastrarOferta;
    private LinearLayout voltar;
    private ProgressBar progressBar;
    private ImageView imagemtoolbar;
    private Toolbar toolbar;
    private RecyclerView recyclerDemandas, recyclerCategorias, recyclerOfertas;
    private CategoriaProfAdapter adapter;
    public static DemandaProfAdapter adapterDemandas;
    private OfertaAdapter adapterOfertas;
    private List<Categoria> categoriaList = new ArrayList<>();
    private List<Demanda> demandasList = new ArrayList<>();
    public static Demanda demandaSelecionada;
    private Demanda demanda, demandadetalhe;
    private String codDemanda, inicio, expiracao, valorOferta, comentarioOferta, professor, data, codOferta, aluno, codCategoria;
    private Date inicioformatado, expiracaoformatada;
    private Categoria categoria, categoriaSelecionada;
    private Oferta oferta;
    private FirebaseDatabase firebase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference ref, refDem;
    public static boolean alterar = false;
    private Date dataatual = new Date();
    private String myFormat = "dd/MM/yyyy";
    private String format = "yyyy/MM/dd";
    private SimpleDateFormat formatoData = new SimpleDateFormat(myFormat, new Locale("pt", "BR"));
    private SimpleDateFormat formatoDataDemanda = new SimpleDateFormat(format, new Locale("pt", "BR"));
    private List<Oferta >ofertaList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(ProfessorInicioViewModel.class);
        View root = inflater.inflate(R.layout.fragment_professor_inicio, container, false);
        // textView = root.findViewById(R.id.text_home);
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
            professor = firebaseUser.getUid();
        }
        ref = firebase.getReference("categorias");
        refDem = firebase.getReference("demandas");


        recyclerCategorias.setHasFixedSize(true);
        recyclerCategorias.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        openProgressBar();
        ref.limitToFirst(100).orderByChild("nome").addValueEventListener(ListenerGeral);

        recyclerDemandas.setHasFixedSize(true);
        recyclerDemandas.setLayoutManager(new LinearLayoutManager(getContext()));
        openProgressBar();
        refDem.addValueEventListener(ListenerGeralDemandas);

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

            showToast("Consultando: " + demandaSelecionada.getDescricao());
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

    private View.OnClickListener clickInserir = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();

            demandaSelecionada = demandasList.get(position);

            showToast("Consultando: " + demandaSelecionada.getDescricao());
            codDemanda = demandaSelecionada.getCodigo();
            codCategoria = demandaSelecionada.getCategoriaCod();
            aluno = demandaSelecionada.getAluno();

            dialogOfertas();
        }
    };

    private View.OnClickListener clickConsultar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();

            demandaSelecionada = demandasList.get(position);
            codDemanda = demandaSelecionada.getCodigo();

            dialogconsultaOfertas();
        }
    };

    private void dialogconsultaOfertas() {

        LayoutInflater li = getLayoutInflater();
        final View view = li.inflate(R.layout.dialog_ofertas, null);

        recyclerOfertas = view.findViewById(R.id.ListOfertas);
        //progressBar = view.findViewById(R.id.loading);

        recyclerOfertas.setHasFixedSize(true);
        recyclerOfertas.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        //openProgressBar();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference refOfer = database.getReference("demandas/" + codDemanda + "/propostas");

        refOfer.limitToFirst(100).addValueEventListener(ListenerOfertas);


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Propostas recebidas");
        builder.setView(view);
        alertapropostas = builder.create();
        alertapropostas.show();
    }

    private ValueEventListener ListenerOfertas = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            ofertaList.clear();

            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                Oferta oferta = ds.getValue(Oferta.class);
                ofertaList.add(oferta);

                Collections.sort(ofertaList, new Comparator<Oferta>() {
                    @Override
                    public int compare(Oferta oferta, Oferta t1) {
                        if (oferta.getDataOferta() == null || t1.getDataOferta() == null)
                            return 0;
                        return oferta.getDataOferta().compareTo(t1.getDataOferta());
                    }
                });
            }

            adapterOfertas = new OfertaAdapter(ofertaList, getContext());
            recyclerOfertas.setAdapter(adapterOfertas);
            //closeProgressBar();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            //closeProgressBar();
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

            adapterDemandas = new DemandaProfAdapter(demandasList, getContext());
            adapterDemandas.setOnItemClickListener(onItemClickListenerDemandas);
            adapterDemandas.setClickInserir(clickInserir);
            adapterDemandas.setClickConsultaPrposta(clickConsultar);
            recyclerDemandas.setAdapter(adapterDemandas);
            closeProgressBar();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            closeProgressBar();
        }
    };

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
        builder.setTitle("Detalhes");

        builder.setView(view);
        alerta = builder.create();
        alerta.show();
    }

    private void dialogOfertas() {

        LayoutInflater li = getLayoutInflater();
        View view = li.inflate(R.layout.dialog_oferta, null);

        btnCadastrarOferta = view.findViewById(R.id.btnCadastrarOferta);
        //btnCancelar = view.findViewById(R.id.btnCancelar);
        //progressBar = view.findViewById(R.id.loading);
        txtValorOferta = view.findViewById(R.id.txtValorOferta);
        txtComentarioOferta = view.findViewById(R.id.txtComentarioOferta);


        view.findViewById(R.id.btnCadastrarOferta).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                valorOferta = txtValorOferta.getText().toString();
                comentarioOferta = txtComentarioOferta.getText().toString();
                data = formatoDataDemanda.format(dataatual);

                if (valorOferta.isEmpty()) {
                    txtValorOferta.setError(getString(R.string.msg_erro_campo_empty));
                    txtValorOferta.requestFocus();
                } else if (comentarioOferta.isEmpty()) {
                    txtComentarioOferta.setError(getString(R.string.msg_erro_campo_empty));
                    txtComentarioOferta.requestFocus();
                } else {
                    btnCadastrarOferta.setEnabled(false);
                    //btnCancelar.setEnabled(false);
                    //progressBar.setFocusable(true);
                    //openProgressBar();
                    oferta = new Oferta();
                    DatabaseReference database = FirebaseDB.getFirebase();
                    codOferta = database.child("propostas").push().getKey();
                    oferta.setCodOferta(codOferta);
                    oferta.setProfessor(professor);
                    oferta.setAluno(aluno);
                    oferta.setCodDemanda(codDemanda);
                    oferta.setCodCategoria(codCategoria);
                    oferta.setValorOferta(valorOferta);
                    oferta.setStatusOferta("Aguardando avaliação");
                    oferta.setDataOferta(data);
                    oferta.setComentarioOferta(comentarioOferta);
                    oferta.salvaOfertaDB(ProfessorInicioFragment.this);

                    demanda = new Demanda();
                    demanda.setCodigo(codDemanda);
                    demanda.setAluno(aluno);
                    demanda.setCategoriaCod(codCategoria);
                    demanda.setStatus("Aguardando validação");
                    demanda.atualizaStatusDemandaDB(ProfessorInicioFragment.this);
                }

            }
        });

//        view.findViewById(R.id.btnCancelar).setOnClickListener(new View.OnClickListener() {
        //           public void onClick(View arg0) {
        //               alertaoferta.dismiss();
        //           }
        //       });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Cadastrar proposta");
        builder.setView(view);
        alertaoferta = builder.create();
        alertaoferta.show();
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

    @Override
    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
        if (databaseError != null) {
            showSnackbar(databaseError.getMessage());
            // closeProgressBar();
            btnCadastrarOferta.setEnabled(true);
        } else {
            showToast("Proposta criada com sucesso!");
            // closeProgressBar();
            alertaoferta.dismiss();
        }
    }
}