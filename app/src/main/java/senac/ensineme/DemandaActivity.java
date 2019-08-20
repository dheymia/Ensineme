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

import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import java.util.Date;

import senac.ensineme.models.Demanda;


public class DemandaActivity extends ComumActivity implements DatabaseReference.CompletionListener, View.OnClickListener {

    //Não serão entradas do usuário no cadastro a demanda
    private String alunoDemanda; //obtida a partir do login e vinculada a demanda cadastrada
    private String codDemanda; //obtida a partir do firebase
    private Date dataDemanda; //obtida do sistema ou firebase na data do cadastro da demanda
    private Date expiraDemanda; //obtida da soma da data da demanda aos dias de validade da demanda

    //Entradas do usuário no cadastro a demanda
    private Button btnCadastrar; //enviará os dados para o firebase
    private AutoCompleteTextView txtDescDemanda; // descrição suscinta da demanda
    private EditText txtCEPDemanda; //selecionado através da API viaCEP
    private EditText txtLogradouroDemanda; //selecionado através da API viaCEP
    private EditText txtBairroDemanda; //selecionado através da API viaCEP
    private EditText txtComplementoDemanda; //selecionado através da API viaCEP
    private EditText txtLocalidadeDemanda; //selecionado através da API viaCEP
    private EditText txtEstadoDemanda; //selecionado através da API viaCEP
    private CalendarView inicioDemanda; //selecionado a partir de uma calendário
    private Spinner spnCatDemanda; // Dança, Esporte, Instrumentos, Culinária e Disciplinas
    private Spinner spnTurnoDemanda; // matutino, verpertino e nortuno
    private Spinner spnValidadeDemanda; // Em dias: de 1 a 30 ou multiplos de 2 até 30 ou multiplos de 5 até 30
    private Spinner spnHorasaulaDemanda; //Em horas-aula (50 minutos): de 1 a 30 ou multiplos de 2 até 30 ou multiplos de 5 até 30
    private Demanda demanda;

    // Armazena entradas
    private String descricao, categoria, turno, horasaula, CEP, logradouro, bairro, complemento, localidade, estado;
    private Date datadeinicio;

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
