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
import androidx.fragment.app.Fragment;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import senac.ensineme.FullscreenActivity;
import senac.ensineme.R;
import senac.ensineme.SettingsActivity;
import senac.ensineme.adapters.CategoriaProfAdapter;
import senac.ensineme.adapters.DemandaProfAdapter;
import senac.ensineme.adapters.OfertaProfAdapter;
import senac.ensineme.models.Categoria;
import senac.ensineme.models.Demanda;
import senac.ensineme.models.FirebaseDB;
import senac.ensineme.models.Oferta;
import senac.ensineme.models.Usuario;

import static android.view.View.GONE;

public class ProfessorInicioFragment extends Fragment implements DatabaseReference.CompletionListener {

    private AlertDialog alerta;
    private AlertDialog alertaoferta;
    private AlertDialog alertapropostas;
    private ProgressBar progressBar;
    private RecyclerView recyclerDemandas;
    private RecyclerView recyclerCategorias;
    private RecyclerView recyclerOfertas;
    private List <Oferta> ofertaList = new ArrayList<>();
    private List<Categoria> categoriaList = new ArrayList<>();
    private List<Demanda> demandasList = new ArrayList<>();
    private static Demanda demandaSelecionada;
    private Demanda demanda, demandadetalhe;
    private String codDemanda;
    private String valorOferta;
    private String comentarioOferta;
    private String professor;
    private String data;
    private String codOferta;
    private String aluno;
    private String codCategoria;
    private Oferta novaoferta;
    private Date dataatual = new Date();
    private String myFormat = "dd/MM/yyyy";
    private String format = "yyyy/MM/dd";
    private SimpleDateFormat formatoData = new SimpleDateFormat(myFormat, new Locale("pt", "BR"));
    private SimpleDateFormat formatoDataDemanda = new SimpleDateFormat(format, new Locale("pt", "BR"));
    private String nomeAluno;
    private String emailAluno;
    private String descDemanda;
    private String inicioDemanda;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ProfessorInicioViewModel homeViewModel = ViewModelProviders.of(this).get(ProfessorInicioViewModel.class);
        View root = inflater.inflate(R.layout.fragment_professor_inicio, container, false);
        recyclerDemandas = root.findViewById(R.id.listDemandas);
        recyclerCategorias = root.findViewById(R.id.listCategorias);
        progressBar = root.findViewById(R.id.loading);
        ImageView imagemtoolbar = root.findViewById(R.id.app_bar_image);


        imagemtoolbar.setImageResource(R.drawable.ensinemeprincipal);

        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            professor = firebaseUser.getUid();
        }
        DatabaseReference ref = firebase.getReference("categorias");
        DatabaseReference refDem = firebase.getReference("demandas");


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
            aluno = demandaSelecionada.getAluno();
            descDemanda = demandaSelecionada.getDescricao();

            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference refDem = database.getReference("demandas/" + codDemanda);

            refDem.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    demandadetalhe = dataSnapshot.getValue(Demanda.class);

                    DatabaseReference refUsu = database.getReference("usuarios/" + aluno);
                    refUsu.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            Usuario alunoselecionado = dataSnapshot.getValue(Usuario.class);
                            nomeAluno = alunoselecionado.getNome();

                            dialogDetalhesDemanda();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
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

    private void dialogDetalhesDemanda() {
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
            String inicio = formatoData.format(Objects.requireNonNull(inicioformatado));
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

        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alerta.dismiss();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(String.valueOf(nomeAluno) + " quer aprender");
        builder.setView(view);
        alerta = builder.create();
        alerta.show();
    }

    private View.OnClickListener clickInserir = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();

            demandaSelecionada = demandasList.get(position);

            codDemanda = demandaSelecionada.getCodigo();
            codCategoria = demandaSelecionada.getCategoriaCod();
            aluno = demandaSelecionada.getAluno();
            descDemanda = demandaSelecionada.getDescricao();
            inicioDemanda = demandaSelecionada.getInicio();

            final FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference refUsu = database.getReference("usuarios/" + aluno);
            refUsu.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Usuario alunoselecionado = dataSnapshot.getValue(Usuario.class);
                    nomeAluno = alunoselecionado.getNome();
                    emailAluno = alunoselecionado.getEmail();

                            dialogOfertas();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        }
    };

    private void dialogOfertas() {

        LayoutInflater li = getLayoutInflater();
        View view = li.inflate(R.layout.dialog_ofertas_inserir, null);

        final Button btnCadastrarOferta = view.findViewById(R.id.btnInserirOferta);
        final EditText txtValorOferta = view.findViewById(R.id.txtValorOferta);
        final EditText txtComentarioOferta = view.findViewById(R.id.txtComentarioOferta);
        LinearLayout voltar = view.findViewById(R.id.inserirPropostas);
        final ProgressBar progressBar = view.findViewById(R.id.loading);
        TextView txtAluno = view.findViewById(R.id.txtAluno);
        TextView txtInicio = view.findViewById(R.id.txtInicio);
        TextView txtEmail= view.findViewById(R.id.txtEmail);

        txtAluno.setText(String.valueOf(nomeAluno));
        txtEmail.setText(String.valueOf(emailAluno));

        try {
            Date inicioformatado = formatoDataDemanda.parse(inicioDemanda);
            String inicio = formatoData.format(Objects.requireNonNull(inicioformatado));
            txtInicio.setText(inicio);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        btnCadastrarOferta.setOnClickListener(new View.OnClickListener() {
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
                    progressBar.setFocusable(true);
                    progressBar.setVisibility(View.VISIBLE);
                    novaoferta = new Oferta();
                    DatabaseReference database = FirebaseDB.getFirebase();
                    codOferta = database.child("propostas").push().getKey();
                    novaoferta.setCodOferta(codOferta);
                    novaoferta.setProfessor(professor);
                    novaoferta.setAluno(aluno);
                    novaoferta.setCodDemanda(codDemanda);
                    novaoferta.setCodCategoria(codCategoria);
                    novaoferta.setValorOferta(valorOferta);
                    novaoferta.setStatusOferta("Aguardando avaliação");
                    novaoferta.setDataOferta(data);
                    novaoferta.setComentarioOferta(comentarioOferta);
                    novaoferta.salvaOfertaDB(ProfessorInicioFragment.this);

                    demanda = new Demanda();
                    demanda.setCodigo(codDemanda);
                    demanda.setAluno(aluno);
                    demanda.setCategoriaCod(codCategoria);
                    demanda.setStatus("Aguardando validação");
                    demanda.atualizaStatusDemandaDB(ProfessorInicioFragment.this);
                }
                progressBar.setVisibility(GONE);
                btnCadastrarOferta.setEnabled(true);

            }
        });

        voltar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                alertaoferta.dismiss();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Ensinar "+ String.valueOf(descDemanda));
        builder.setView(view);
        alertaoferta = builder.create();
        alertaoferta.show();
    }

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

        txtTitulo.setText("Propostas enviadas");
        btnSelecionaProposta.setVisibility(GONE);

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
        builder.setTitle("Ensinar " + descDemanda);
        builder.setView(view);
        alertapropostas = builder.create();
        alertapropostas.show();
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
                        if (demanda.getExpiracao() == null || t1.getExpiracao() == null)
                            return 0;
                        return demanda.getExpiracao().compareTo(t1.getExpiracao());
                    }
                });


            }

            DemandaProfAdapter adapterDemandas = new DemandaProfAdapter(demandasList, getContext());
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

    private void openProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void closeProgressBar() {
        progressBar.setVisibility(GONE);
    }

    private void showSnackbar(String message) {
        Snackbar.make(progressBar,
                message,
                Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    private void showToast(String message) {
        Toast.makeText(getContext(),
                message,
                Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
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

        } else {
            showToast("Proposta criada com sucesso!");
            alertaoferta.dismiss();
        }
    }
}