package senac.ensineme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import senac.ensineme.adapters.CategoriaProfAdapter;
import senac.ensineme.adapters.DemandaAluAdapter;
import senac.ensineme.adapters.OfertaAluAdapter;
import senac.ensineme.models.FirebaseDB;
import senac.ensineme.models.Oferta;
import senac.ensineme.ui.aluno_inicio.AlunoInicioFragment;

public class OfertaActivity extends AppCompatActivity {


    private List<Oferta> ofertaList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oferta);

        final RecyclerView recyclerOfertas = findViewById(R.id.ListOfertasValidar);
        TextView demanda = findViewById(R.id.txtSolicitacao);
        Button encerrar = findViewById(R.id.btnEncerrarSemValidar);
        Button validar = findViewById(R.id.btnValidarProposta);

        String codDemanda = AlunoInicioFragment.demandaSelecionada.getCodigo();
        demanda.setText("Aprender "+ AlunoInicioFragment.demandaSelecionada.getDescricao());

        FirebaseDatabase firebase = FirebaseDatabase.getInstance();
        DatabaseReference refOfer = firebase.getReference("demandas/" + codDemanda + "/propostas");

        recyclerOfertas.setHasFixedSize(true);
        recyclerOfertas.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //openProgressBar();
        refOfer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ofertaList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Oferta oferta = ds.getValue(Oferta.class);
                    ofertaList.add(oferta);
                }
                OfertaAluAdapter adapter = new OfertaAluAdapter(ofertaList, OfertaActivity.this);
                //adapter.setOnItemClickListener(onItemClickListener);
                recyclerOfertas.setAdapter(adapter);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        encerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //codigo encerrar

            }
        });

        validar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //codigo validar

            }
        });
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
