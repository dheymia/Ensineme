package senac.ensineme.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import java.util.Objects;

import senac.ensineme.AlunoBuscaActivity;
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

    private FirebaseDatabase firebase;
    private String nomeProfessor;
    private String idProfessor;
    private String tipoUsuario;
    private ProgressBar progressBar;
    public static String codCategoria;
    private AlertDialog alerta;
    private AlertDialog alertaoferta;
    private AlertDialog alertapropostas;
    private RecyclerView recyclerDemandas;
    private RecyclerView recyclerCategorias;
    private RecyclerView recyclerOfertas;
    private List <Oferta> ofertaList = new ArrayList<>();
    private List<Categoria> categoriaList = new ArrayList<>();
    private List<Demanda> demandasList = new ArrayList<>();
    private Date dataatual = new Date();
    private String myFormat = "dd/MM/yyyy";
    private String format = "yyyy/MM/dd";
    private SimpleDateFormat formatoData = new SimpleDateFormat(myFormat, new Locale("pt", "BR"));
    private SimpleDateFormat formatoDataDemanda = new SimpleDateFormat(format, new Locale("pt", "BR"));
    private String nomeAluno;
    private String emailAluno;
    private String aluno;
    private String codigo;
    private String descricao;
    private String turno;
    private String status;
    private String categoria;
    private String categoriaCod;
    private String inicio;
    private String data;
    private String expiracao;
    private String CEP;
    private String logradouro;
    private String bairro;
    private String complemento;
    private String localidade;
    private String estado;
    private String atualizacao;
    private String situacao;
    private String valorOferta;
    private String comentarioOferta;
    private int horasaula;
    private int numero;



    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_professor_inicio, container, false);



         FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        firebase = FirebaseDatabase.getInstance();

        Usuario usuario = new Usuario();
        if (firebaseUser != null) {
            usuario.setId(firebaseUser.getUid());
            idProfessor = usuario.getId();
        }

        DatabaseReference refProf = firebase.getReference("usuarios/" + idProfessor);
        refProf.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario alunoSelecionado = dataSnapshot.getValue(Usuario.class);
                nomeProfessor = alunoSelecionado.getNome();
                tipoUsuario = alunoSelecionado.getTipo();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                showSnackbar("Erro de leitura: " + databaseError.getCode());
            }
        });

        DatabaseReference refCategorias = firebase.getReference("categorias");
        DatabaseReference refDemandas = firebase.getReference("demandas");

        recyclerCategorias.setHasFixedSize(true);
        recyclerCategorias.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerDemandas.setHasFixedSize(true);
        recyclerDemandas.setLayoutManager(new LinearLayoutManager(getContext()));


        progressBar.setFocusable(true);
        openProgressBar();
        refCategorias.orderByChild("nome").addValueEventListener(ListaCategorias);
        progressBar.setFocusable(true);
        openProgressBar();
        //refDemandas.orderByChild("situacao").equalTo("ATIVA").addValueEventListener(ListaDemandas);
        refDemandas.addValueEventListener(ListaDemandas);

        return root;
    }

    private ValueEventListener ListaCategorias = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            categoriaList.clear();

            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                Categoria categoria = ds.getValue(Categoria.class);
                categoriaList.add(categoria);
            }

            CategoriaProfAdapter adapter = new CategoriaProfAdapter(categoriaList, getContext());
            adapter.setOnItemClickListener(ClickItemCategoria);
            recyclerCategorias.setAdapter(adapter);

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            showSnackbar("Erro de leitura: " + databaseError.getCode());
            closeProgressBar();
        }
    };

    private ValueEventListener ListaDemandas = new ValueEventListener() {
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
            adapterDemandas.setOnItemClickListener(ClickItemDemandas);
            adapterDemandas.setClickInserir(clickInserirProposta);
            adapterDemandas.setClickConsultaPrposta(clickConsultarPropostas);
            recyclerDemandas.setAdapter(adapterDemandas);
            closeProgressBar();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            showSnackbar("Erro de leitura: " + databaseError.getCode());
            closeProgressBar();
        }
    };

    private View.OnClickListener ClickItemCategoria = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();

            Categoria categoriaSelecionada = categoriaList.get(position);
            codCategoria = categoriaSelecionada.getCodigo();

            Intent busca = new Intent(getContext(), AlunoBuscaActivity.class);
            startActivity(busca);
        }
    };


    private final View.OnClickListener ClickItemDemandas = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) view.getTag();
            int position = holder.getAdapterPosition();

            Demanda demandaClicada = demandasList.get(position);
            codigo = demandaClicada.getCodigo();
            aluno = demandaClicada.getAluno();

            DatabaseReference refUsu = firebase.getReference("usuarios/" + aluno);
            refUsu.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Usuario alunoSelecionado = dataSnapshot.getValue(Usuario.class);
                    nomeAluno = alunoSelecionado.getNome();
                    nomeProfessor = alunoSelecionado.getNome();
                    emailAluno = alunoSelecionado.getEmail();
                    tipoUsuario = alunoSelecionado.getTipo();

                    DatabaseReference refDem = firebase.getReference("demandas/" + codigo);
                    refDem.addValueEventListener (new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Demanda demandaSelecionada = dataSnapshot.getValue(Demanda.class);
                            aluno = demandaSelecionada.getAluno();
                            codigo = demandaSelecionada.getCodigo();
                            descricao = demandaSelecionada.getDescricao();
                            turno = demandaSelecionada.getTurno();
                            status = demandaSelecionada.getStatus();
                            categoria = demandaSelecionada.getCategoria();
                            categoriaCod = demandaSelecionada.getCategoriaCod();
                            inicio = demandaSelecionada.getInicio();
                            expiracao = demandaSelecionada.getExpiracao();
                            CEP = demandaSelecionada.getCEP();
                            logradouro = demandaSelecionada.getLogradouro();
                            bairro = demandaSelecionada.getBairro();
                            complemento = demandaSelecionada.getComplemento();
                            localidade = demandaSelecionada.getLocalidade();
                            estado = demandaSelecionada.getEstado();
                            horasaula = demandaSelecionada.getHorasaula();
                            numero = demandaSelecionada.getNumero();

                            dialogDetalhesDemanda();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            showSnackbar("Erro de leitura: " + databaseError.getCode());
                        }
                    });
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    showSnackbar("Erro de leitura: " + databaseError.getCode());
                    closeProgressBar();
                }
            });
        }
    };

    private void dialogDetalhesDemanda() {
        LayoutInflater li = getLayoutInflater();
        final View view = li.inflate(R.layout.dialog_demanda_detalhes, null);

        LinearLayout voltar = view.findViewById(R.id.VerPropostas);

        final TextView txtDescDemanda = view.findViewById(R.id.txtAluno);
        final TextView txtCatDemanda = view.findViewById(R.id.txtCatDemanda);
        final TextView txtTurnoDemanda = view.findViewById(R.id.txtTurno);
        final TextView txtInicioDemanda = view.findViewById(R.id.txtInicio);
        final TextView txtnHorasaulaDemanda = view.findViewById(R.id.txtCH);
        final TextView txtLocalidadeDemanda = view.findViewById(R.id.txtCidade);
        final TextView txtEstadoDemanda = view.findViewById(R.id.txtEstado);
        final TextView txtLogradouroDemanda = view.findViewById(R.id.txtLogradouro);
        final TextView txtComplementoDemanda = view.findViewById(R.id.txtComplemento);
        final TextView txtNumeroDemanda = view.findViewById(R.id.txtNumero);
        final TextView txtCEPDemanda = view.findViewById(R.id.txtCEP);
        final TextView txtBairroDemanda = view.findViewById(R.id.txtBairro);
        final TextView txtExpiracao = view.findViewById(R.id.textExpiracao);

        txtDescDemanda.setText(String.valueOf(descricao));
        txtCatDemanda.setText(String.valueOf(categoria));
        txtTurnoDemanda.setText(String.valueOf(turno));
        try {
            Date inicioformatado = formatoDataDemanda.parse(inicio);
            String inicio = formatoData.format(Objects.requireNonNull(inicioformatado));
            txtInicioDemanda.setText(inicio);

            Date expiracaoformatada = formatoDataDemanda.parse(expiracao);
            String expiracao = formatoData.format(expiracaoformatada);
            txtExpiracao.setText(expiracao);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        txtnHorasaulaDemanda.setText(String.valueOf(horasaula) + " horas/aula");
        txtLocalidadeDemanda.setText(String.valueOf(localidade));
        txtEstadoDemanda.setText(String.valueOf(estado));
        txtLogradouroDemanda.setText(String.valueOf(logradouro));
        txtComplementoDemanda.setText(String.valueOf(complemento));
        txtNumeroDemanda.setText(String.valueOf(numero));
        txtCEPDemanda.setText(String.valueOf(CEP));
        txtBairroDemanda.setText(String.valueOf(bairro));

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(String.valueOf(nomeAluno) + " quer aprender");
        builder.setView(view);
        alerta = builder.create();
        alerta.show();

        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alerta.dismiss();
            }
        });
    }

    private View.OnClickListener clickInserirProposta = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();

            Demanda demandaClicada = demandasList.get(position);
            codigo = demandaClicada.getCodigo();
            aluno = demandaClicada.getAluno();

            DatabaseReference refUsu = firebase.getReference("usuarios/" + aluno);
            refUsu.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Usuario alunoSelecionado = dataSnapshot.getValue(Usuario.class);
                    nomeAluno = alunoSelecionado.getNome();
                    nomeProfessor = alunoSelecionado.getNome();
                    emailAluno = alunoSelecionado.getEmail();
                    tipoUsuario = alunoSelecionado.getTipo();

                    DatabaseReference refDem = firebase.getReference("demandas/" + codigo);
                    refDem.addValueEventListener (new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Demanda demandaSelecionada = dataSnapshot.getValue(Demanda.class);
                            aluno = demandaSelecionada.getAluno();
                            codigo = demandaSelecionada.getCodigo();
                            descricao = demandaSelecionada.getDescricao();
                            turno = demandaSelecionada.getTurno();
                            status = demandaSelecionada.getStatus();
                            categoria = demandaSelecionada.getCategoria();
                            categoriaCod = demandaSelecionada.getCategoriaCod();
                            inicio = demandaSelecionada.getInicio();
                            expiracao = demandaSelecionada.getExpiracao();
                            CEP = demandaSelecionada.getCEP();
                            logradouro = demandaSelecionada.getLogradouro();
                            bairro = demandaSelecionada.getBairro();
                            complemento = demandaSelecionada.getComplemento();
                            localidade = demandaSelecionada.getLocalidade();
                            estado = demandaSelecionada.getEstado();
                            horasaula = demandaSelecionada.getHorasaula();
                            numero = demandaSelecionada.getNumero();

                            dialogOfertas();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            showSnackbar("Erro de leitura: " + databaseError.getCode());
                        }
                    });
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    showSnackbar("Erro de leitura: " + databaseError.getCode());
                    closeProgressBar();
                }
            });


        }
    };

    private void dialogOfertas() {

        LayoutInflater li = getLayoutInflater();
        View view = li.inflate(R.layout.dialog_ofertas_inserir, null);
        final ProgressBar progressBar = view.findViewById(R.id.loading);
        final Button btnCadastrarOferta = view.findViewById(R.id.btnInserirOferta);
        final EditText txtValorOferta = view.findViewById(R.id.txtValorOferta);
        final EditText txtComentarioOferta = view.findViewById(R.id.txtComentarioOferta);
        LinearLayout voltar = view.findViewById(R.id.inserirPropostas);
        TextView txtAluno = view.findViewById(R.id.txtAluno);
        TextView txtInicio = view.findViewById(R.id.txtInicio);
        TextView txtEmail= view.findViewById(R.id.txtEmail);

        txtAluno.setText(String.valueOf(nomeAluno));
        txtEmail.setText(String.valueOf(emailAluno));

        try {
            Date inicioformatado = formatoDataDemanda.parse(inicio);
            String inicio = formatoData.format(Objects.requireNonNull(inicioformatado));
            txtInicio.setText(inicio);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Ensinar "+ descricao);
        builder.setView(view);
        alertaoferta = builder.create();
        alertaoferta.show();

        voltar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                alertaoferta.dismiss();
            }
        });

        btnCadastrarOferta.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {



                valorOferta = txtValorOferta.getText().toString();
                comentarioOferta = txtComentarioOferta.getText().toString();
                data = formatoDataDemanda.format(dataatual);

                if (comentarioOferta.isEmpty()) {
                    txtComentarioOferta.setError(getString(R.string.msg_erro_campo_empty));
                    txtComentarioOferta.requestFocus();
                } else if (valorOferta.isEmpty()) {
                    txtValorOferta.setError(getString(R.string.msg_erro_campo_empty));
                    txtValorOferta.requestFocus();
                } else {
                    progressBar.setFocusable(true);
                    progressBar.setVisibility(View.VISIBLE);
                    CadastrarOferta();
                }
            }

        });
    }

    public void CadastrarOferta(){


            Oferta novaoferta = new Oferta();
            DatabaseReference database = FirebaseDB.getFirebase();
            String codOferta = database.child("propostas").push().getKey();
            novaoferta.setCodOferta(codOferta);
            novaoferta.setProfessor(idProfessor);
            novaoferta.setAluno(aluno);
            novaoferta.setCodDemanda(codigo);
            novaoferta.setCodCategoria(categoriaCod);
            novaoferta.setValorOferta(valorOferta);
            novaoferta.setStatusOferta("Aguardando avaliação");
            novaoferta.setDataOferta(data);
            novaoferta.setAtualizacao(data);
            novaoferta.setComentarioOferta(comentarioOferta);
            novaoferta.salvaOfertaDB(ProfessorInicioFragment.this);

            Demanda demanda = new Demanda();
            demanda.setCodigo(codigo);
            demanda.setAluno(aluno);
            demanda.setCategoriaCod(categoriaCod);
            demanda.setStatus("Aguardando validação");
            demanda.setAtualizacao(data);
            demanda.atualizaStatusDemandaDB(ProfessorInicioFragment.this);
    }

    private View.OnClickListener clickConsultarPropostas = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();

            Demanda demandaClicada = demandasList.get(position);
            codigo = demandaClicada.getCodigo();

            DatabaseReference refDem = firebase.getReference("demandas/" + codigo);
            refDem.addValueEventListener (new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Demanda demandaSelecionada = dataSnapshot.getValue(Demanda.class);
                    aluno = demandaSelecionada.getAluno();
                    codigo = demandaSelecionada.getCodigo();
                    descricao = demandaSelecionada.getDescricao();
                    turno = demandaSelecionada.getTurno();
                    status = demandaSelecionada.getStatus();
                    categoria = demandaSelecionada.getCategoria();
                    categoriaCod = demandaSelecionada.getCategoriaCod();
                    inicio = demandaSelecionada.getInicio();
                    expiracao = demandaSelecionada.getExpiracao();
                    CEP = demandaSelecionada.getCEP();
                    logradouro = demandaSelecionada.getLogradouro();
                    bairro = demandaSelecionada.getBairro();
                    complemento = demandaSelecionada.getComplemento();
                    localidade = demandaSelecionada.getLocalidade();
                    estado = demandaSelecionada.getEstado();
                    horasaula = demandaSelecionada.getHorasaula();
                    numero = demandaSelecionada.getNumero();

                    dialogconsultaOfertas();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    showSnackbar("Erro de leitura: " + databaseError.getCode());
                }
            });

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

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Ensinar " + descricao);
        builder.setView(view);
        alertapropostas = builder.create();
        alertapropostas.show();

        progressBar.setFocusable(true);
        progressBar.setVisibility(View.VISIBLE);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference refOfer = database.getReference("demandas/" + codigo + "/propostas");
        refOfer.addValueEventListener(ListaOfertas);
        progressBar.setVisibility(GONE);

        voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertapropostas.cancel();
            }
        });

    }

    private ValueEventListener ListaOfertas = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            ofertaList.clear();
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                Oferta oferta = ds.getValue(Oferta.class);
                ofertaList.add(oferta);
            }
            OfertaProfAdapter adapterOfertas = new OfertaProfAdapter(ofertaList, getContext());
            //inserir click oferta editar
            recyclerOfertas.setAdapter(adapterOfertas);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            showSnackbar("Erro de leitura: " + databaseError.getCode());
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
    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
        if (databaseError != null) {
            showSnackbar(databaseError.getMessage());
        } else {
            alertaoferta.dismiss();
            ((AppCompatActivity)getActivity()).finish();
            showToast("Proposta criada com sucesso!");
        }
    }

}