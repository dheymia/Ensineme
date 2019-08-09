package senac.ensineme;

import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;

import senac.ensineme.models.Usuario;

public class CadastroActivity extends ComumActivity implements DatabaseReference.CompletionListener, View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private Usuario usuario;
    private AutoCompleteTextView txtNome, txtEmail, txtCelular;
    private EditText txtSenha;
    private RadioButton rbAluno, rbProfessor;
    private FloatingActionButton FabCadastrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Cadastro");

        mAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                if (firebaseUser == null || usuario.getId() != null) {
                    return;
                }

                usuario.setId(firebaseUser.getUid());
                usuario.saveDB(CadastroActivity.this);
            }
        };

        inicializarViews();

        FabCadastrar = (FloatingActionButton) findViewById(R.id.fab);
        FabCadastrar.setOnClickListener(this);
    }

    @Override
    protected void inicializarViews() {
        txtNome = (AutoCompleteTextView) findViewById(R.id.txtNome);
        txtEmail = (AutoCompleteTextView) findViewById(R.id.txtEmail);
        txtCelular = (AutoCompleteTextView) findViewById(R.id.txtCelular);
        txtSenha = (EditText) findViewById(R.id.txtSenha);
        rbAluno = (RadioButton) findViewById(R.id.rbAprender);
        rbProfessor = (RadioButton) findViewById(R.id.rbEnsinar);
        progressBar = (ProgressBar) findViewById(R.id.sign_up_progress);
    }

    @Override
    protected void inicializarUsuario() {
        usuario = new Usuario();
        usuario.setNome(txtNome.getText().toString());
        usuario.setEmail(txtEmail.getText().toString());
        usuario.setCelular(txtCelular.getText().toString());
        usuario.setPassword(txtSenha.getText().toString());

        if (rbAluno.isChecked()) {
            usuario.setTipo("aluno");
        } else if (rbProfessor.isChecked()) {
            usuario.setTipo("professor");
        }
    }

    @Override
    public void onClick(View v) {
        inicializarUsuario();

        String nome = txtNome.getText().toString();
        String email = txtEmail.getText().toString();
        String celular = txtCelular.getText().toString();
        String senha = txtSenha.getText().toString();

        boolean ok = true;

        if (nome.isEmpty()) {
            txtNome.setError(getString(R.string.msg_erro_nome));
            ok = false;
        }

        if (email.isEmpty()) {
            txtEmail.setError(getString(R.string.msg_erro_email_empty));
            ok = false;
        }

        if (celular.isEmpty()) {
            txtCelular.setError(getString(R.string.msg_erro_celular));
            ok = false;
        }

        if (senha.isEmpty()) {
           txtSenha.setError(getString(R.string.msg_erro_senha_empty));
            ok = false;
        }

        if (ok) {
            FabCadastrar.setEnabled(false);
            progressBar.setFocusable(true);

            openProgressBar();
            salvarUsuario();
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

    private void salvarUsuario() {

        mAuth.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getPassword()
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
                FabCadastrar.setEnabled(true);
            }
        });
    }

    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        mAuth.signOut();

        showToast("Conta criada com sucesso!");
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