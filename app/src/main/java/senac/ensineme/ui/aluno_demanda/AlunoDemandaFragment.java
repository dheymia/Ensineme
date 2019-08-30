package senac.ensineme.ui.aluno_demanda;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import senac.ensineme.AlunoMainActivity;
import senac.ensineme.CategoriasActivity;
import senac.ensineme.DemandaActivity;
import senac.ensineme.R;
import senac.ensineme.adapters.CategoriaAdapter;
import senac.ensineme.adapters.DemandaAdapter;
import senac.ensineme.models.Categoria;
import senac.ensineme.models.Demanda;

public class AlunoDemandaFragment extends Fragment {

    private AlunoDemandaViewModel dashboardViewModel;
    private FloatingActionButton fab;
    private TextView textView;
    private RecyclerView recyclerView;
    private Spinner spConsulta;
    private String consulta, aluno;
    private ProgressBar progressBar;
    public static DemandaAdapter adapter;
    private List<Demanda> demandasList = new ArrayList<>();
    public static Demanda demandaSelecionada;
    private FirebaseDatabase firebase;
    private DatabaseReference ref;
    public static boolean alterar = false;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(AlunoDemandaViewModel.class);
        View root = inflater.inflate(R.layout.fragment_aluno_demanda, container, false);
        textView = root.findViewById(R.id.text_dashboard);
        fab = root.findViewById(R.id.fab);
        recyclerView = root.findViewById(R.id.listAlunoDemanda);
        progressBar = root.findViewById(R.id.loading);
        spConsulta = root.findViewById(R.id.spConsulta);
        dashboardViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        firebase = FirebaseDatabase.getInstance();
        ref = firebase.getReference("demandas");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            aluno = firebaseUser.getUid();
        }

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        progressBar.setVisibility( View.VISIBLE );

        consulta = spConsulta.getSelectedItem().toString();

        AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (consulta == "Todas") {
                    ref.limitToFirst(100).orderByChild("aluno").equalTo(aluno).addValueEventListener(ListenerGeral);
                } else {
                    ref.limitToFirst(100).orderByChild("aluno").equalTo(aluno).addValueEventListener(ListenerGeral);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        };
        spConsulta.setOnItemSelectedListener(onItemSelectedListener);



        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alterar = false;
                Intent intent = new Intent(getContext(), DemandaActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }

    private final View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int position = viewHolder.getAdapterPosition();

            demandaSelecionada = demandasList.get(position);
            showToast("Demanda selecionada: " + demandaSelecionada.getCodigo());

            alterar = true;

            Intent demanda = new Intent(getContext(), DemandaActivity.class);
            startActivity(demanda);
        }
    };

    private ValueEventListener ListenerGeral = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            demandasList.clear();

            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                Demanda demanda = ds.getValue(Demanda.class);
                demandasList.add(demanda);
            }

            adapter = new DemandaAdapter(demandasList, getContext());
            adapter.setOnItemClickListener(onItemClickListener);
            recyclerView.setAdapter(adapter);
            progressBar.setVisibility( View.GONE );
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            progressBar.setVisibility( View.GONE );
        }
    };

    protected void showToast(String message){
        Toast.makeText(getContext(),
                message,
                Toast.LENGTH_LONG)
                .show();
    }

}