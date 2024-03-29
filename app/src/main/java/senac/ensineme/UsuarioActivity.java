package senac.ensineme;

import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import androidx.annotation.NonNull;

import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import senac.ensineme.models.Usuario;

public class UsuarioActivity extends ComumActivity implements DatabaseReference.CompletionListener, View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private Usuario usuario;
    private AutoCompleteTextView txtNome, txtSobrenome, txtEmail, txtCelular;
    private EditText txtSenha;
    private RadioButton rbAluno, rbProfessor;
    private Button btnCadastrar;
    private String nome, sobrenome, nomecompleto, email, celular, senha, inscricao;
    private Date dataatual = new Date();
    private String myFormat = "dd/MM/yyyy";
    private String format = "yyyy/MM/dd";
    private SimpleDateFormat formatoData =  new SimpleDateFormat(myFormat, new Locale("pt", "BR"));
    private SimpleDateFormat formatoDataDemanda = new SimpleDateFormat(format, new Locale("pt", "BR"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);

        mAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                if (firebaseUser == null || usuario.getId() != null) {
                    return;
                }

                usuario.setId(firebaseUser.getUid());
                usuario.salvaUsuarioDB(UsuarioActivity.this);
            }
        };

        inicializaViews();

        progressBar = findViewById(R.id.loading);
        btnCadastrar = findViewById(R.id.btnCadastrarUsuario);
        btnCadastrar.setOnClickListener(this);
    }

    @Override
    protected void inicializaViews() {
        txtNome = findViewById(R.id.txtNome);
        txtSobrenome = findViewById(R.id.txtSobrenome);
        txtEmail = findViewById(R.id.txtEmail);
        txtCelular = findViewById(R.id.txtCelular);
        txtSenha = findViewById(R.id.txtSenha);
        rbAluno = findViewById(R.id.rbAprender);
        rbProfessor = findViewById(R.id.rbEnsinar);
    }

    @Override
    protected void inicializaConteudo() {
        nome = txtNome.getText().toString();
        sobrenome = txtSobrenome.getText().toString();
        email = txtEmail.getText().toString();
        celular = txtCelular.getText().toString();
        senha = txtSenha.getText().toString();
        nomecompleto = nome + " "+ sobrenome;
        inscricao = (formatoDataDemanda.format(dataatual));
    }

    @Override
    protected void inicializaObjeto() {
        inicializaConteudo();
        usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setSobrenome(sobrenome);
        usuario.setNomecompleto(nomecompleto);
        usuario.setEmail(email);
        usuario.setCelular(celular);
        usuario.setPassword(senha);
        usuario.setInscricao(inscricao);

        if (rbAluno.isChecked()) {
            usuario.setTipo("aluno");
        } else if (rbProfessor.isChecked()) {
            usuario.setTipo("professor");
        }
    }

    @Override
    protected boolean validaCampo() {
        if (nome.isEmpty()) {
            txtNome.setError(getString(R.string.msg_erro_nome));
            txtNome.requestFocus();
            return false;
        }
        if (sobrenome.isEmpty()) {
            txtSobrenome.setError(getString(R.string.msg_erro_campo_empty));
            txtSobrenome.requestFocus();
            return false;
        }

        if (email.isEmpty()) {
            txtEmail.setError(getString(R.string.msg_erro_email_empty));
            txtEmail.requestFocus();
            return false;
        }

        if (celular.isEmpty()) {
            txtCelular.setError(getString(R.string.msg_erro_celular));
            txtCelular.requestFocus();
            return false;
        }

        if (senha.isEmpty()) {
            txtSenha.setError(getString(R.string.msg_erro_senha_empty));
            txtSenha.requestFocus();
            return false;
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        inicializaObjeto();
        if (validaCampo()) {
            btnCadastrar.setEnabled(false);
            progressBar.setFocusable(true);
            openProgressBar();
            salvaUsuario();
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

    private void salvaUsuario() {

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
                btnCadastrar.setEnabled(true);
            }
        });
    }

    @Override
    public void onComplete(DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
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
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}