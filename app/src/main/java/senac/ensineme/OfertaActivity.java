package senac.ensineme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import senac.ensineme.adapters.DemandaAluAdapter;
import senac.ensineme.adapters.DemandaProfAdapter;
import senac.ensineme.adapters.OfertaConsultaAdapter;
import senac.ensineme.models.Demanda;
import senac.ensineme.models.FirebaseDB;
import senac.ensineme.models.Oferta;
import senac.ensineme.models.Usuario;

public class OfertaActivity extends ComumActivity implements DatabaseReference.CompletionListener, View.OnClickListener{

    private List<Oferta> professorOfertaList = new ArrayList<>();
    private FirebaseDatabase firebase;
    private DatabaseReference database, refOfe, refUsu;
    private Button btnCadastrarOferta;
    private EditText txtValorOferta ;
    private EditText txtComentarioOferta;
    private TextView txtAluno;
    private TextView txtInicio ;
    private TextView txtEmail;
    private Oferta novaoferta, ofertaselecionada;
    private Demanda demanda;
    private Date dataatual = new Date();
    private String myFormat = "dd/MM/yyyy";
    private String format = "yyyy/MM/dd";
    private SimpleDateFormat formatoData =  new SimpleDateFormat(myFormat, new Locale("pt", "BR"));
    private SimpleDateFormat formatoDataDemanda = new SimpleDateFormat(format, new Locale("pt", "BR"));
    private String comentarioOferta, valorOferta, data, professor, aluno, codDemanda, codOferta, codCategoria, status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oferta);
        getSupportActionBar().setTitle("Ensinar " + DemandaProfAdapter.descricao);

        progressBar = (ProgressBar) findViewById(R.id.loading);
        btnCadastrarOferta = findViewById(R.id.btnInserirOferta);
        btnCadastrarOferta.setOnClickListener((View.OnClickListener) this);


        firebase = FirebaseDatabase.getInstance();
        database = FirebaseDB.getFirebase();
        refOfe = firebase.getReference("demandas/" + DemandaProfAdapter.codDemanda + "/propostas");
        refUsu = firebase.getReference("usuarios/" + DemandaProfAdapter.aluno);

        inicializaViews();
        camposPreenchidos();

        if (ProfessorMainActivity.alterar){
            btnCadastrarOferta.setText(getString(R.string.acaoAlterar));

            refOfe.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    professorOfertaList.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Oferta oferta = ds.getValue(Oferta.class);
                        if (oferta.getProfessor().equals(ProfessorMainActivity.idUsuario)){
                            ofertaselecionada = oferta;
                        }
                        professor = ofertaselecionada.getProfessor();
                        aluno = ofertaselecionada.getAluno();
                        codDemanda = ofertaselecionada.getCodDemanda();
                        codOferta = ofertaselecionada.getCodOferta();
                        codCategoria = ofertaselecionada.getCodCategoria();
                        inicializaCamposPreenchidos();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

        } else {
            codOferta = database.child("propostas").push().getKey();
            professor = ProfessorMainActivity.idUsuario;
            aluno = DemandaProfAdapter.aluno;
            codDemanda = DemandaProfAdapter.codDemanda;
            codCategoria = DemandaProfAdapter.codCategoria;
            }
        }

    private void inicializaCamposPreenchidos() {
        txtComentarioOferta.setText(String.valueOf(ofertaselecionada.getComentarioOferta()));
        txtValorOferta.setText(String.valueOf(ofertaselecionada.getValorOferta()));
    }

    @Override
    protected void inicializaViews() {

        txtValorOferta = findViewById(R.id.txtValorOferta);
        txtComentarioOferta = findViewById(R.id.txtComentarioOferta);
        txtAluno = findViewById(R.id.txtAluno);
        txtInicio = findViewById(R.id.txtInicio);
        txtEmail= findViewById(R.id.txtEmail);

    }

    private void camposPreenchidos() {

        progressBar.setFocusable(true);
        openProgressBar();

        refUsu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario alunoSelecionado = dataSnapshot.getValue(Usuario.class);

                txtAluno.setText(String.valueOf(alunoSelecionado.getNomecompleto()));
                txtEmail.setText(String.valueOf(alunoSelecionado.getEmail()));

                try {
                    Date inicioformatado = formatoDataDemanda.parse(DemandaProfAdapter.inicio);
                    String inicio = formatoData.format(Objects.requireNonNull(inicioformatado));
                    txtInicio.setText(inicio);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                closeProgressBar();
            }
        });

        closeProgressBar();
    }

    @Override
    protected void inicializaConteudo() throws ParseException {
        valorOferta = txtValorOferta.getText().toString();
        comentarioOferta = txtComentarioOferta.getText().toString();
        data = formatoDataDemanda.format(dataatual);
    }

    @Override
    protected void inicializaObjeto() throws ParseException {

        if(AlunoMainActivity.alterar){
            status = ofertaselecionada.getStatusOferta();
            data = ofertaselecionada.getDataOferta();
        } else {
            status = "Aguardando avaliação";
            data = formatoDataDemanda.format(dataatual);
        }

        novaoferta = new Oferta();
        novaoferta.setCodOferta(codOferta);
        novaoferta.setProfessor(professor);
        novaoferta.setAluno(aluno);
        novaoferta.setCodDemanda(codDemanda);
        novaoferta.setCodCategoria(codCategoria);
        novaoferta.setValorOferta(valorOferta);
        novaoferta.setStatusOferta(status);
        novaoferta.setDataOferta(data);
        novaoferta.setAtualizacao(data);
        novaoferta.setComentarioOferta(comentarioOferta);
    }

    private void atualizaDemanda () throws ParseException {

        demanda = new Demanda();
        demanda.setCodigo(codDemanda);
        demanda.setAluno(aluno);
        demanda.setCategoriaCod(codCategoria);
        demanda.setStatus("Aguardando validação");
        demanda.setAtualizacao(data);

    }

    @Override
    protected boolean validaCampo() {
        if (comentarioOferta.isEmpty()) {
            txtComentarioOferta.setError(getString(R.string.msg_erro_campo_empty));
            txtComentarioOferta.requestFocus();
            return false;
        }
        if (valorOferta.isEmpty()) {
            txtValorOferta.setError(getString(R.string.msg_erro_campo_empty));
            txtValorOferta.requestFocus();
            return false;
        }

        return true;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onClick(View view) {

        try {
            inicializaConteudo();

            if (validaCampo()) {
                btnCadastrarOferta.setEnabled(false);
                progressBar.setFocusable(true);
                openProgressBar();
                inicializaObjeto();
                atualizaDemanda();
                if(ProfessorMainActivity.alterar){
                    novaoferta.atualizaofertaDB(OfertaActivity.this);
                    demanda.atualizaStatusDemandaDB(OfertaActivity.this);
                } else{
                    novaoferta.salvaOfertaDB(OfertaActivity.this);
                    demanda.atualizaStatusDemandaDB(OfertaActivity.this);
                }
            } else {
                closeProgressBar();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

        if (databaseError != null) {
            showSnackbar(databaseError.getMessage());
            closeProgressBar();
            btnCadastrarOferta.setEnabled(true);
        } else {
            if(ProfessorMainActivity.alterar){
                showToast("Proposta atualizada com sucesso!");
            } else{
                showToast("Proposta inserida com sucesso!");
            }
            closeProgressBar();
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
