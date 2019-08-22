package senac.ensineme;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.Date;

import senac.ensineme.models.Oferta;

public class OfertaActivity extends ComumActivity implements DatabaseReference.CompletionListener, View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private Oferta oferta;
    private AutoCompleteTextView txtValor;
    private Button btnEnviarOferta;
    private String professor;
    private Double valor;
    private Date data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oferta);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cadastro");

        mAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                if (firebaseUser == null || oferta.getCodOferta() != null) {
                    return;
                }

                oferta.setCodOferta(firebaseUser.getUid());
                oferta.salvaOfertaDB(OfertaActivity.this);
            }
        };

        inicializaViews();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnEnviarOferta = (Button) findViewById(R.id.btnCadastrarUsuario);
        btnEnviarOferta.setOnClickListener(this);
    }

    @Override
    protected void inicializaViews() {
        txtProfessor = (AutoCompleteTextView) findViewById(R.id.txtProfessor);
        txtValor = (AutoCompleteTextView) findViewById(R.id.txtValor);
    }

    @Override
    protected void inicializaConteudo() {
        professor = txtProfessor.getText().toString();
        valor = Double.valueOf(txtValor.getText().toString());
    }

    @Override
    protected void inicializaObjeto() {
        inicializaConteudo();
        oferta = new Oferta();
        oferta.setProfessorOferta(professor);
        oferta.setValorOferta(valor);
        oferta.setDataOferta(data);

    }

    @Override
    protected boolean validaCampo() {
        if (professor.isEmpty()) {
            txtProfessor.setError(getString(R.string.msg_erro_nome));
            txtProfessor.requestFocus();
            return false;
        }

        if (valor.isEmpty()) {
            txtValor.setError(getString(R.string.msg_erro_email_empty));
            txtValor.requestFocus();
            return false;
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        inicializaObjeto();
        if (validaCampo()) {
            btnEnviarOferta.setEnabled(false);
            progressBar.setFocusable(true);
            openProgressBar();
            salvaOferta();
        } else {
            closeProgressBar();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    private void salvaOferta() {

        mAuth.createUserWithEmailAndPassword(
                oferta.getEmail(),
                oferta.getPassword()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (!task.isSuccessful()) {
                    closeProgressBar();
                }
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                showSnackbar(e.getMessage());
                btnEnviarOferta.setEnabled(true);
            }
        });
    }

    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        mAuth.signOut();

        showToast("Oferta enviada com sucesso!");
        closeProgressBar();
        finish();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

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