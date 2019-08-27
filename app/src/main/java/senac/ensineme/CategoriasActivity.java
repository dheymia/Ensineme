package senac.ensineme;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import senac.ensineme.models.Categoria;
import senac.ensineme.models.FirebaseDB;

public class CategoriasActivity extends AppCompatActivity implements DatabaseReference.CompletionListener {

    private AlertDialog alerta;
    private ProgressBar progressBar;
    private EditText txtCategoria;
    private Button btnCadastrar;
    private String nomeCategoria, codCategoria;
    private Categoria categoria;
    private DatabaseReference firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogCategorias();
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void dialogCategorias() {

        LayoutInflater li = getLayoutInflater();
        View view = li.inflate(R.layout.dialog_categorias, null);

        final EditText input = new EditText(CategoriasActivity.this);

        btnCadastrar = findViewById(R.id.btnCadastrar);
        txtCategoria = findViewById(R.id.txtCategoria);

        view.findViewById(R.id.btnCadastrar).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                firebase = FirebaseDB.getFirebase();
                codCategoria = firebase.child("categorias").push().getKey();
                nomeCategoria = txtCategoria.getText().toString();

                if (nomeCategoria.isEmpty()) {
                    txtCategoria.setError(getString(R.string.msg_erro_campo_empty));
                    txtCategoria.requestFocus();
                } else {
                   categoria = new Categoria();
                   categoria.setCategoria(nomeCategoria);
                   categoria.setCodigo(codCategoria);
                   btnCadastrar.setEnabled(false);
                   progressBar.setFocusable(true);
                   openProgressBar();
                   categoria.salvaCategoriaDB(CategoriasActivity.this);
               }

            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nova categoria");
        builder.setView(view);
        alerta = builder.create();
        alerta.show();
    }

    @Override
    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

        if (databaseError != null) {
            showSnackbar(databaseError.getMessage());
            closeProgressBar();
            btnCadastrar.setEnabled(true);
        } else {
            showToast("Demanda criada com sucesso!");
            closeProgressBar();
            alerta.dismiss();
        }
    }

    protected void openProgressBar(){
        progressBar.setVisibility( View.VISIBLE );
    }

    protected void closeProgressBar(){
        progressBar.setVisibility( View.GONE );
    }

    protected void showSnackbar(String message){
        Snackbar.make(progressBar,
                message,
                Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    protected void showToast(String message){
        Toast.makeText(CategoriasActivity.this,
                message,
                Toast.LENGTH_LONG)
                .show();
    }

}
