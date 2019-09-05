package senac.ensineme.ui.aluno_demanda;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

import senac.ensineme.DemandaActivity;
import senac.ensineme.R;
import senac.ensineme.adapters.DemandaAluAdapter;
import senac.ensineme.models.Demanda;

public class AlunoDemandaFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private AlertDialog alerta;
    private AlunoDemandaViewModel dashboardViewModel;
    private FloatingActionButton fab;
    private TextView textView;
    private RecyclerView recyclerView;
    private Spinner spConsulta;
    private String consulta, aluno;
    private ProgressBar progressBar;
    private ArrayAdapter<CharSequence> statusAdapter;
    public static DemandaAluAdapter adapter;
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
        recyclerView = root.findViewById(R.id.listAlunoDemandas);
        progressBar = root.findViewById(R.id.loading);
        spConsulta = root.findViewById(R.id.spConsulta);
        dashboardViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
//                textView.setText(s);
            }
        });

        firebase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            aluno = firebaseUser.getUid();
        }
        ref = firebase.getReference("usuarios/" + aluno + "/demandas");

        statusAdapter = ArrayAdapter.createFromResource(getContext(),R.array.statusDemanda, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spConsulta.setAdapter(statusAdapter);
        spConsulta.setSelection(statusAdapter.getPosition("Aguardando proposta"));
        spConsulta.setOnItemSelectedListener(this);

        consulta = spConsulta.getSelectedItem().toString();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        progressBar.setVisibility( View.VISIBLE );


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

            dialogDetalhesDemanda();

            //Intent demanda = new Intent(getContext(), DemandaActivity.class);
            //startActivity(demanda);
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

            adapter = new DemandaAluAdapter(demandasList, getContext());
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        Object item = parent.getItemAtPosition(pos);
        consulta = item.toString();
        showToast( "OnItemSelectedListener : " + item.toString());

        if (consulta.equals("Todas")) {
            ref.limitToFirst(100).orderByChild("data").addValueEventListener(ListenerGeral);
        } else {
            ref.limitToFirst(100).orderByChild("status").equalTo(consulta).addValueEventListener(ListenerGeral);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void dialogDetalhesDemanda() {
        LayoutInflater li = getLayoutInflater();
        View view = li.inflate(R.layout.dialog_detalhes_demanda, null);


        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Detalhes da demanda");

        builder.setView(view);
        alerta = builder.create();
        alerta.show();
    }
}