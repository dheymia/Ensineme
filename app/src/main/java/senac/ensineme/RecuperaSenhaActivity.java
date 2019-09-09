package senac.ensineme;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class RecuperaSenhaActivity extends ComumActivity implements View.OnClickListener {

    private Button btnRecuperaSenha;
    private EditText txtEmail;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recupera_senha);

        inicializaViews();

        progressBar = findViewById(R.id.loading);
        btnRecuperaSenha = findViewById(R.id.btnCadastrarOferta);
        btnRecuperaSenha.setOnClickListener(this);
    }

    @Override
    protected void inicializaViews() {
        txtEmail = findViewById(R.id.txtValorOferta);

    }

    @Override
    protected void inicializaConteudo() {
        email = txtEmail.getText().toString();

    }

    @Override
    protected void inicializaObjeto() {

    }

    @Override
    protected boolean validaCampo() {
        if (email.isEmpty()) {
            txtEmail.setError(getString(R.string.msg_erro_email_empty));
            txtEmail.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onClick(View v) {
        inicializaConteudo();
               if (validaCampo()) {
                btnRecuperaSenha.setEnabled(false);
                progressBar.setFocusable(true);
                openProgressBar();
                recuperaSenha();
            } else {
                closeProgressBar();
            }
    }

    public void recuperaSenha() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAddress = email;

        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            showToast("Email enviado com sucesso!");
                            closeProgressBar();
                            chamarLoginActivity();
                            finish();
                        } else {
                            showToast("Email informado não cadastrado!");
                            closeProgressBar();
                            btnRecuperaSenha.setEnabled(true);
                                                    }
                    }
                });
    }

    private void chamarLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
