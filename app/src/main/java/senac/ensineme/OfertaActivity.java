package senac.ensineme;

import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import senac.ensineme.models.FirebaseDB;
import senac.ensineme.models.Oferta;

public class OfertaActivity extends ComumActivity implements DatabaseReference.CompletionListener, View.OnClickListener {

    private Button btnCadastrar;
    private TextView txtDescDemanda, txtCatDemanda, txtTurnoDemanda, txtInicioDemanda, txtnHorasaulaDemanda, txtLocalidadeDemanda, txtEstadoDemanda, txtLogradouroDemanda, txtComplementoDemanda, txtNumeroDemanda, txtCEPDemanda, txtBairroDemanda;
    private EditText txtValorOferta;
    private String dadosdemanda, professor, codOferta, status, data, valor;
    private Oferta oferta;
    private DatabaseReference firebase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String myFormat = "dd/MM/yyyy";
    SimpleDateFormat formatoData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oferta);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Nova oferta");

        firebase = FirebaseDB.getFirebase();
        firebaseAuth = FirebaseAuth.getInstance();
        codOferta = firebase.child("ofertas").push().getKey();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            professor = firebaseUser.getUid();
        }

        inicializaViews();

        progressBar = (ProgressBar) findViewById(R.id.loading);
        btnCadastrar = (Button) findViewById(R.id.btnCadastrarOferta);
        btnCadastrar.setOnClickListener((View.OnClickListener) this);

    }

    @Override
    public void onClick(View view) {

        inicializaObjeto();

        if (validaCampo()) {
            btnCadastrar.setEnabled(false);
            progressBar.setFocusable(true);
            openProgressBar();
            oferta.salvaOfertaDB(OfertaActivity.this);
        } else {
            closeProgressBar();
        }

    }

    @Override
    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

        if (databaseError != null) {
            showSnackbar(databaseError.getMessage());
            closeProgressBar();
            btnCadastrar.setEnabled(true);
        } else {
            showToast("Oferta enviada com sucesso!");
            closeProgressBar();
            finish();
        }
    }

    @Override
    protected void inicializaViews() {
        txtDescDemanda = findViewById(R.id.txtDescricao);
        txtCatDemanda = findViewById(R.id.txtCatDemanda);
        txtTurnoDemanda = findViewById(R.id.txtTurno);
        txtInicioDemanda = findViewById(R.id.txtInicio);
        txtnHorasaulaDemanda = findViewById(R.id.txtCH);
        txtLocalidadeDemanda = findViewById(R.id.txtCidade);
        txtEstadoDemanda = findViewById(R.id.txtEstado);
        txtLogradouroDemanda = findViewById(R.id.txtLogradouro);
        txtComplementoDemanda = findViewById(R.id.txtComplemento);
        txtNumeroDemanda = findViewById(R.id.txtNumero);
        txtCEPDemanda = findViewById(R.id.txtCep);
        txtBairroDemanda = findViewById(R.id.txtBairro);;
        txtValorOferta = findViewById(R.id.txtValor);
    }

    @Override
    protected void inicializaConteudo() {
         valor = txtValorOferta.getText().toString();
    }

    @Override
    protected void inicializaObjeto() {

        inicializaConteudo();

        oferta = new Oferta();
        oferta.setProfessorOferta(professor);
        oferta.setCodOferta(codOferta);
        oferta.setValorOferta(valor);
        oferta.setStatusOferta("Aguardando avaliação");
        oferta.setDataOferta((formatoData.format(new Date())));

    }

    @Override
    protected boolean validaCampo() {
        if (valor.isEmpty()) {
            txtValorOferta.setError(getString(R.string.msg_erro_campo_empty));
            txtValorOferta.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

}
