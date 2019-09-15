package senac.ensineme;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import senac.ensineme.adapters.CategoriaAdmAdapter;
import senac.ensineme.models.Categoria;
import senac.ensineme.models.FirebaseDB;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class CategoriasActivity extends AppCompatActivity implements DatabaseReference.CompletionListener {

    private AlertDialog alerta;
    private ProgressBar progressBar;
    private EditText txtCategoria;
    private RecyclerView recyclerView;
    private CategoriaAdmAdapter adapter;
    private List<Categoria> categoriaList = new ArrayList<>();
    private Button btnCadastrarCategoria, btnCancelar;
    private String nomeCategoria, codCategoria;
    private Categoria categoria, categoriaSelecionada;
    private FirebaseDatabase firebase;
    private DatabaseReference ref;
    static boolean alterar = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria);
        progressBar = findViewById(R.id.loading);


        firebase = FirebaseDatabase.getInstance();
        ref = firebase.getReference("categorias");

        recyclerView = findViewById(R.id.listCategorias);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        openProgressBar();
        ref.limitToFirst(100).orderByChild("nome").addValueEventListener(ListenerGeral);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alterar = false;
                dialogCategorias();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();

            categoriaSelecionada = categoriaList.get(position);
            Toast.makeText(CategoriasActivity.this, "Categoria selecionada: " + categoriaSelecionada.getNome(), Toast.LENGTH_SHORT).show();

            alterar = true;

            dialogCategorias();
        }
    };

    private ValueEventListener ListenerGeral = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            categoriaList.clear();

            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                Categoria categoria = ds.getValue(Categoria.class);
                categoriaList.add(categoria);
            }

            adapter = new CategoriaAdmAdapter(categoriaList, CategoriasActivity.this);
            adapter.setOnItemClickListener(onItemClickListener);
            recyclerView.setAdapter(adapter);

            closeProgressBar();
        }

          @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            closeProgressBar();
        }
    };


    private void dialogCategorias() {

        LayoutInflater li = getLayoutInflater();
        View view = li.inflate(R.layout.dialog_categorias, null);

        btnCadastrarCategoria = view.findViewById(R.id.btnCadastrarCategoria);
        btnCancelar = view.findViewById(R.id.btnCancelar);
        progressBar = view.findViewById(R.id.loading);
        txtCategoria = view.findViewById(R.id.txtCatDemanda);

        if(alterar){
            txtCategoria.setText(String.valueOf(categoriaSelecionada.getNome()));
            btnCadastrarCategoria.setText("alterar");
        }


        view.findViewById(R.id.btnCadastrarCategoria).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                nomeCategoria = txtCategoria.getText().toString();

                if (nomeCategoria.isEmpty()) {
                    txtCategoria.setError(getString(R.string.msg_erro_campo_empty));
                    txtCategoria.requestFocus();
                } else {
                    btnCadastrarCategoria.setEnabled(false);
                    btnCancelar.setEnabled(false);
                    progressBar.setFocusable(true);
                    openProgressBar();
                    categoria = new Categoria();
                    if (alterar){
                        categoria.setNome(nomeCategoria);
                        categoria.setCodigo(categoriaSelecionada.getCodigo());
                        categoria.atualizacategoriaDB(CategoriasActivity.this);
                    } else {
                        DatabaseReference database = FirebaseDB.getFirebase();
                        codCategoria = database.child("categorias").push().getKey();
                        categoria.setNome(nomeCategoria);
                        categoria.setCodigo(codCategoria);
                        categoria.salvaCategoriaDB(CategoriasActivity.this);
                    }


                }

            }
        });

        view.findViewById(R.id.btnCancelar).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                alerta.dismiss();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (alterar){
            builder.setTitle("Alterar categoria");
        } else{
            builder.setTitle("Nova categoria");
        }

        builder.setView(view);
        alerta = builder.create();
        alerta.show();
    }

    @Override
    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

        if (databaseError != null) {
            showSnackbar(databaseError.getMessage());
            closeProgressBar();
            btnCadastrarCategoria.setEnabled(true);
            btnCancelar.setEnabled(true);
        } else {
            closeProgressBar();
            alerta.dismiss();
            if(alterar){
                showToast("Categoria atualizada com sucesso!");
            }else{
                showToast("Categoria criada com sucesso!");
            }

        }
    }

    protected void openProgressBar(){
        progressBar.setVisibility(VISIBLE);
    }

    protected void closeProgressBar(){
        progressBar.setVisibility( GONE );
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
