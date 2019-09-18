package senac.ensineme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import senac.ensineme.adapters.CategoriaProfAdapter;
import senac.ensineme.adapters.DemandaProfAdapter;
import senac.ensineme.adapters.UsuariosAdapter;
import senac.ensineme.models.Categoria;
import senac.ensineme.models.Usuario;
import senac.ensineme.providers.DemandaSuggestionProvider;

public class UsuariosCadastradosActivity extends AppCompatActivity {

    SearchView searchView;
    UsuariosAdapter adapter;
    RecyclerView recycler;
    ProgressBar progressBar;
    private List<Usuario> usuarioList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuarios_cadastrados);

        recycler = findViewById(R.id.ListaUsuarios);
        progressBar = findViewById(R.id.loading);

        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        progressBar.setFocusable(true);
        progressBar.setVisibility(View.VISIBLE);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference refUsu = database.getReference("usuarios/");

        refUsu.orderByChild("nomecompleto").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                usuarioList.clear();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Usuario usuario = ds.getValue(Usuario.class);
                    usuarioList.add(usuario);
                }

                adapter = new UsuariosAdapter(usuarioList,UsuariosCadastradosActivity.this);
                recycler.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        if (searchManager != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        searchView.setIconifiedByDefault(false);
        searchView.setQueryRefinementEnabled(true);
        searchView.requestFocus(1);
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return true;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                CursorAdapter selectedView = searchView.getSuggestionsAdapter();
                Cursor cursor = (Cursor) selectedView.getItem(position);
                int index = cursor.getColumnIndexOrThrow(SearchManager.SUGGEST_COLUMN_TEXT_1);
                searchView.setQuery(cursor.getString(index), true);
                return true;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String d) {
                adapter.getFilter().filter(d);
                searchView.clearFocus();
                if(adapter.getUsuarioList().size() > 0){
                    SearchRecentSuggestions suggestions = new SearchRecentSuggestions(UsuariosCadastradosActivity.this,
                            DemandaSuggestionProvider.AUTHORITY, DemandaSuggestionProvider.MODE);
                    suggestions.saveRecentQuery(d, null);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });

        return true;
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            adapter.getFilter().filter(query);
        }
    }

    @Override
    public boolean onSearchRequested() {
        return super.onSearchRequested();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.app_bar_search) {
            return true;
        }

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

}
