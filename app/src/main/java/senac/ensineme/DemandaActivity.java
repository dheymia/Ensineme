package senac.ensineme;

import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import java.util.Date;

import senac.ensineme.models.Demanda;


public class DemandaActivity extends ComumActivity implements DatabaseReference.CompletionListener, View.OnClickListener {

    private Button btnCadastrar;
    private Context context;
    private EditText txtLocalDemanda, txtDescDemanda;
    private CalendarView inicioDemanda;
    private Spinner spnCatDemanda, spnTurnoDemanda, spnValidadeDemanda, spnHorasaulaDemanda;
    private String aluno, codDemanda, catDemanda, descDemanda, horasaulaDemanda, turnoDemanda, localDemanda;
    private Date diaSelecionado, dataDemanda;
    Demanda demanda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demanda);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Nova demanda");

        inicializaViews();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnCadastrar = (Button) findViewById(R.id.btnCadastrarDemanda);
        btnCadastrar.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        inicializaObjeto();
        if (validaCampo()) {
            btnCadastrar.setEnabled(false);
            progressBar.setFocusable(true);
            openProgressBar();
            demanda.salvaDemandaDB();
        } else {
            closeProgressBar();
        }

    }

    @Override
    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
        showToast("Demanda criada com sucesso!");
        closeProgressBar();
        finish();
    }

    @Override
    protected void inicializaViews() {
        txtDescDemanda = findViewById(R.id.txtDemanda);

           }
    @Override
    protected void inicializaConteudo() {

    }

    @Override
    protected void inicializaObjeto() {


    }

    @Override
    protected boolean validaCampo() {
        return false;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
