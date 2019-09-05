package senac.ensineme.ui.professor_inicio;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import senac.ensineme.R;
import senac.ensineme.adapters.CategoriaProfAdapter;
import senac.ensineme.adapters.DemandaAluAdapter;
import senac.ensineme.models.Categoria;
import senac.ensineme.models.Demanda;

public class ProfessorInicioFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private ProgressBar progressBar;
    private RecyclerView recyclerDemandas,recyclerCategorias;
    private CategoriaProfAdapter adapter;
    public static DemandaAluAdapter adapterDemandas;
    private List<Categoria> categoriaList = new ArrayList<>();
    private List<Demanda> demandasList = new ArrayList<>();
    public static Demanda demandaSelecionada;
    private String nomeCategoria, codCategoria;
    private Categoria categoria, categoriaSelecionada;
    private FirebaseDatabase firebase;
    private DatabaseReference ref, refDem;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_professor_inicio, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        recyclerDemandas = root.findViewById(R.id.listDemandas);
        recyclerCategorias = root.findViewById(R.id.listCategorias);
        progressBar = root.findViewById(R.id.loading);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
//                textView.setText(s);
            }
        });

        firebase = FirebaseDatabase.getInstance();
        ref = firebase.getReference("categorias");
        refDem = firebase.getReference( "demandas");


        recyclerCategorias.setHasFixedSize(true);
        recyclerCategorias.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        openProgressBar();
        ref.limitToFirst(100).orderByChild("nome").addValueEventListener(ListenerGeral);

        recyclerDemandas.setHasFixedSize(true);
        recyclerDemandas.setLayoutManager(new LinearLayoutManager(getContext()));
        progressBar.setVisibility( View.VISIBLE );
        refDem.limitToFirst(10).orderByChild("expiracao").addValueEventListener(ListenerGeralDemandas);

        return root;
    }

    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();

            categoriaSelecionada = categoriaList.get(position);
            Toast.makeText(getContext(), "Categoria selecionada: " + categoriaSelecionada.getNome(), Toast.LENGTH_SHORT).show();

            //alterar = true;

            //dialogCategorias();
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

            adapter = new CategoriaProfAdapter(categoriaList, getContext());
            adapter.setOnItemClickListener(onItemClickListener);
            recyclerCategorias.setAdapter(adapter);

            closeProgressBar();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            closeProgressBar();
        }
    };

    private final View.OnClickListener onItemClickListenerDemandas = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();

            demandaSelecionada = demandasList.get(position);
            showToast("Demanda selecionada: " + demandaSelecionada.getCodigo());

            //alterar = true;

            //Intent demanda = new Intent(getContext(), DemandaActivity.class);
            //startActivity(demanda);
        }
    };

    private ValueEventListener ListenerGeralDemandas = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            demandasList.clear();

            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                Demanda demanda = ds.getValue(Demanda.class);
                demandasList.add(demanda);
            }

            adapterDemandas = new DemandaAluAdapter(demandasList, getContext());
            adapterDemandas.setOnItemClickListener(onItemClickListenerDemandas);
            recyclerDemandas.setAdapter(adapterDemandas);
            closeProgressBar();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
           closeProgressBar();
        }
    };

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
        Toast.makeText(getContext(),
                message,
                Toast.LENGTH_LONG)
                .show();
    }
}