package senac.ensineme;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import senac.ensineme.models.Usuario;

public class LoginActivity extends ComumActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference firebase;
    private Usuario usuario, usuariologado;
    private TextView cadastrar;
    private AutoCompleteTextView txtEmail;
    private EditText txtSenha;
    private Button btnLogin;
    private String email, senha, idUsuario, tipoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = getFirebaseAuthResultHandler();


        inicializaViews();

        btnLogin = (Button) findViewById(R.id.btnCancelar);
        btnLogin.setOnClickListener(this);
    }

    protected void inicializaViews() {
        txtEmail = (AutoCompleteTextView) findViewById(R.id.txtValor);
        txtSenha = (EditText) findViewById(R.id.txtSenha);
        progressBar = (ProgressBar) findViewById(R.id.loading);
        cadastrar = (TextView) findViewById(R.id.txtCadastrar);
    }

    @Override
    protected void inicializaConteudo() {
        email = txtEmail.getText().toString();
        senha = txtSenha.getText().toString();
    }

    protected void inicializaObjeto() {
        inicializaConteudo();
        usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setPassword(senha);
    }

    @Override
    protected boolean validaCampo() {
        if (email.isEmpty()) {
            txtEmail.setError(getString(R.string.msg_erro_email_empty));
            txtEmail.requestFocus();
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
        int id = v.getId();
        if (id == R.id.btnCancelar) {
            if (validaCampo()) {
                btnLogin.setEnabled(false);
                cadastrar.setEnabled(false);
                progressBar.setFocusable(true);
                openProgressBar();
                verifyLogin();
            } else {
                closeProgressBar();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        verifyLogged();
    }

    private void verifyLogged() {

        if (firebaseAuth.getCurrentUser() != null) {
            chamarMainActivity();
        } else {
            firebaseAuth.addAuthStateListener(authStateListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    private FirebaseAuth.AuthStateListener getFirebaseAuthResultHandler() {
        FirebaseAuth.AuthStateListener callback = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser userFirebase = firebaseAuth.getCurrentUser();

                if (userFirebase == null) {
                    return;
                }

                if (usuario.getId() == null && isNameOk(usuario, userFirebase)) {

                    usuario.setId(userFirebase.getUid());
                    usuario.setNomeIfNull(userFirebase.getDisplayName());
                    usuario.setEmailIfNull(userFirebase.getEmail());
                    usuario.salvaUsuarioDB();

                }

                chamarMainActivity();
            }
        };
        return (callback);
    }

    private void verifyLogin() {
        firebaseAuth.signInWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            closeProgressBar();

                            btnLogin.setEnabled(true);
                            cadastrar.setEnabled(true);

                            return;
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        showSnackbar(connectionResult.getErrorMessage());
    }

    private boolean isNameOk(Usuario usuario, FirebaseUser firebaseUser) {
        return (usuario.getNome() != null || firebaseUser.getDisplayName() != null);
    }

    private void chamarMainActivity() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            usuario.setId(user.getUid());
            idUsuario = usuario.getId();
        } else{
            return;
        }

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("usuarios/" + idUsuario);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usuariologado = dataSnapshot.getValue(Usuario.class);
                tipoUsuario = usuariologado.getTipo();
                showToast(idUsuario + " " + usuariologado.getTipo());

                if (tipoUsuario.equals("aluno")) {
                    Intent aluno = new Intent(LoginActivity.this, AlunoMainActivity.class);
                    startActivity(aluno);
                    finish();
                } else if (tipoUsuario.equals("professor")) {
                    Intent professor = new Intent(LoginActivity.this, ProfessorMainActivity.class);
                    startActivity(professor);
                    finish();

                }else if (tipoUsuario.equals("administrador")) {
                    Intent administrador = new Intent(LoginActivity.this, AdministradorMainActivity.class);
                    startActivity(administrador);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showSnackbar("Erro de leitura: " + databaseError.getCode());
            }
        });
    }

    public void recuperaSenha(View view) {
        Intent intent = new Intent(this, RecuperaSenhaActivity.class);
        startActivity(intent);
    }


}

