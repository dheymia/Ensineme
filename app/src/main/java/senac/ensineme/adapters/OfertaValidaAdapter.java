package senac.ensineme.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import senac.ensineme.R;
import senac.ensineme.models.Oferta;
import senac.ensineme.models.Usuario;

public class OfertaValidaAdapter extends RecyclerView.Adapter<OfertaValidaAdapter.OfertaViewHolder> {

    private List<Oferta> ofertaList;
    private Context context;
    private Usuario professorselecionado;
    private int lastSelectedPosition = -1;
    public static String codProfessor, codOferta, mensagem;

    public OfertaValidaAdapter(List<Oferta> ofertaList, Context context) {
        this.ofertaList = ofertaList;
        this.context = context;
    }


    @NonNull
    @Override
    public OfertaValidaAdapter.OfertaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ofertas_aluno,parent,false);
        return new OfertaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final OfertaValidaAdapter.OfertaViewHolder holder, int position) {

        Oferta oferta = ofertaList.get(position);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference refUsu = database.getReference("usuarios/" + oferta.getProfessor());
        refUsu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                professorselecionado = dataSnapshot.getValue(Usuario.class);
                ((OfertaViewHolder) holder).professor.setText(professorselecionado.getNomecompleto());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        ((OfertaViewHolder) holder).valorOferta.setText(oferta.getValorOferta());
        ((OfertaViewHolder) holder).comentario.setText(oferta.getComentarioOferta());
        ((OfertaViewHolder) holder).codOfer.setText(oferta.getCodOferta());
        ((OfertaViewHolder) holder).codProf.setText(oferta.getProfessor());
        ((OfertaViewHolder) holder).professor.setChecked(lastSelectedPosition == position);

    }


    @Override
    public int getItemCount() {
        return ofertaList.size();
    }



    class OfertaViewHolder extends RecyclerView.ViewHolder {

        final TextView valorOferta, comentario, codProf, codOfer;
        final RadioButton professor;


        OfertaViewHolder(@NonNull View itemView) {

            super(itemView);

            professor = itemView.findViewById(R.id.rbProfessor);
            valorOferta = itemView.findViewById(R.id.txtValor);
            comentario = itemView.findViewById(R.id.txtComentario);
            codProf = itemView.findViewById(R.id.txtCodProfessor);
            codOfer = itemView.findViewById(R.id.txtCodOferta);

            professor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lastSelectedPosition = getAdapterPosition();
                    notifyDataSetChanged();
                    Toast.makeText(OfertaValidaAdapter.this.context,
                            "Seleção: proposta R$ " + valorOferta.getText() + " de " + professor.getText(),
                            Toast.LENGTH_LONG).show();

                    codProfessor = (String) codProf.getText();
                    codOferta = (String) codOfer.getText();
                    mensagem = "Você selecionou a proposta no valor de R$ " + valorOferta.getText() + " de " + professor.getText() + ".";

                }
            });
        }
    }
}
