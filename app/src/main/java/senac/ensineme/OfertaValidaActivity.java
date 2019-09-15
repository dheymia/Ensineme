package senac.ensineme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import senac.ensineme.adapters.OfertaAluAdapter;
import senac.ensineme.models.Demanda;
import senac.ensineme.models.Oferta;
import senac.ensineme.ui.AlunoBuscaFragment;
import senac.ensineme.ui.AlunoInicioFragment;

import static android.view.View.GONE;

public class OfertaValidaActivity extends AppCompatActivity implements DatabaseReference.CompletionListener{


    private List<Oferta> ofertaList = new ArrayList<>();
    private String codDemanda, aluno, codCategoria, descDemanda;
    private ProgressBar progressBar;
    private Oferta ofertaSelecionada;
    private Date dataatual = new Date();
    private String myFormat = "dd/MM/yyyy";
    private String format = "yyyy/MM/dd";
    private SimpleDateFormat formatoData =  new SimpleDateFormat(myFormat, new Locale("pt", "BR"));
    private SimpleDateFormat formatoDataDemanda = new SimpleDateFormat(format, new Locale("pt", "BR"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oferta);

        final RecyclerView recyclerOfertas = findViewById(R.id.ListOfertasValidar);
        TextView demanda = findViewById(R.id.txtSolicitacao);
        Button encerrar = findViewById(R.id.btnEncerrarSemValidar);
        Button validar = findViewById(R.id.btnValidarProposta);
        progressBar = findViewById(R.id.loading);

        if (AlunoInicioFragment.validar){
            codDemanda = AlunoInicioFragment.demandaSelecionada.getCodigo();
            aluno = AlunoInicioFragment.demandaSelecionada.getAluno();
            codCategoria = AlunoInicioFragment.demandaSelecionada.getCategoriaCod();
            descDemanda = AlunoInicioFragment.demandaSelecionada.getDescricao();
        }else if(AlunoBuscaActivity.validar){
            codDemanda = AlunoBuscaActivity.demandaSelecionada.getCodigo();
            aluno = AlunoBuscaActivity.demandaSelecionada.getAluno();
            codCategoria = AlunoBuscaActivity.demandaSelecionada.getCategoriaCod();
            descDemanda = AlunoBuscaActivity.demandaSelecionada.getDescricao();
        }

        demanda.setText("Aprender "+ descDemanda);

        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        DatabaseReference refOfer = firebase.getReference("demandas/" + codDemanda + "/propostas");

        recyclerOfertas.setHasFixedSize(true);
        recyclerOfertas.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        progressBar.setFocusable(true);
        openProgressBar();

        refOfer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ofertaList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Oferta oferta = ds.getValue(Oferta.class);
                    ofertaList.add(oferta);
                }
                OfertaAluAdapter adapter = new OfertaAluAdapter(ofertaList, OfertaValidaActivity.this);
                recyclerOfertas.setAdapter(adapter);
                closeProgressBar();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        encerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(OfertaValidaActivity.this);
                builder
                        .setMessage("Tem certeza que deseja encerrar a solicitação sem escolher uma proposta?")
                        .setPositiveButton("Confirmar",  new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                openProgressBar();
                                EncerrarTodasPropostas();
                            }
                        })

                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        })
                        .show();
            }

        });

        validar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(OfertaValidaActivity.this);
                builder
                        .setMessage(OfertaAluAdapter.mensagem)
                        .setPositiveButton("Confirmar",  new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                openProgressBar();
                                Selecionaproposta();
                            }
                        })

                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        })
                        .show();

            }
        });
    }


    private void Selecionaproposta() {

        for (int i = 0; i < ofertaList.size(); ++i) {
            Oferta obj = (Oferta) ofertaList.get(i);
            Oferta proposta = new Oferta();
            proposta.setCodOferta(obj.getCodOferta());
            proposta.setProfessor(obj.getProfessor());
            proposta.setCodDemanda(codDemanda);
            proposta.setStatusOferta("Rejeitada");
            proposta.atualizaStatusOfertaDB(OfertaValidaActivity.this);

        }
        Oferta propostaselecionada = new Oferta();
        propostaselecionada.setCodOferta(OfertaAluAdapter.codOferta);
        propostaselecionada.setProfessor(OfertaAluAdapter.codProfessor);
        propostaselecionada.setCodDemanda(codDemanda);
        propostaselecionada.setStatusOferta("Aceita");
        propostaselecionada.atualizaStatusOfertaDB(OfertaValidaActivity.this);

        Demanda demanda = new Demanda();
        demanda.setCodigo(codDemanda);
        demanda.setAluno(aluno);
        demanda.setCategoriaCod(codCategoria);
        demanda.setStatus("Encerrada com aceite");
        demanda.setAtualizacao(formatoDataDemanda.format(dataatual));
        demanda.atualizaStatusDemandaDB(OfertaValidaActivity.this);

    }

    private void EncerrarTodasPropostas() {

        for (int i = 0; i < ofertaList.size(); ++i) {
            Oferta obj = (Oferta) ofertaList.get(i);
            Oferta proposta = new Oferta();
            proposta.setCodOferta(obj.getCodOferta());
            proposta.setProfessor(obj.getProfessor());
            proposta.setCodDemanda(codDemanda);
            proposta.setStatusOferta("Rejeitada");
            proposta.atualizaStatusOfertaDB(OfertaValidaActivity.this);

        }

        Demanda demanda = new Demanda();
        demanda.setCodigo(codDemanda);
        demanda.setAluno(aluno);
        demanda.setCategoriaCod(codCategoria);
        demanda.setStatus("Encerrada com rejeite");
        demanda.setAtualizacao(formatoDataDemanda.format(dataatual));
        demanda.atualizaStatusDemandaDB(OfertaValidaActivity.this);

    }

    @Override
    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
        if (databaseError != null) {
            showSnackbar(databaseError.getMessage());
            closeProgressBar();
            //btnCadastrar.setEnabled(true);
        } else {
            showToast("As alterações foram salvas.");
            closeProgressBar();
            finish();
        }
    }

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
        Toast.makeText(this,
                message,
                Toast.LENGTH_LONG)
                .show();
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
