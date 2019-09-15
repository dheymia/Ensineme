package senac.ensineme.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

public class OfertaProfAdapter extends RecyclerView.Adapter<OfertaProfAdapter.OfertaViewHolder> {

    private List<Oferta> ofertaList;
    private Context context;
    public View.OnClickListener mOnItemClickListener;

    public OfertaProfAdapter(List<Oferta> ofertaList, Context context) {
        this.ofertaList = ofertaList;
        this.context = context;
    }


    @NonNull
    @Override
    public OfertaProfAdapter.OfertaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ofertas_professor,parent,false);
        return new OfertaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final OfertaProfAdapter.OfertaViewHolder holder, int position) {

        final Oferta oferta = ofertaList.get(position);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference refUsu = database.getReference("usuarios/" + oferta.getProfessor());
        refUsu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Usuario professor = dataSnapshot.getValue(Usuario.class);
                ((OfertaViewHolder) holder).professor.setText(professor.getNome());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        ((OfertaViewHolder) holder).valorOferta.setText(oferta.getValorOferta());
        ((OfertaViewHolder) holder).status.setText(oferta.getStatusOferta());

    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }


    @Override
    public int getItemCount() {
        return ofertaList.size();
    }



    class OfertaViewHolder extends RecyclerView.ViewHolder {

        final TextView professor, valorOferta, status;


        OfertaViewHolder(@NonNull View itemView) {

            super(itemView);

            professor = itemView.findViewById(R.id.txtProfessor);
            valorOferta = itemView.findViewById(R.id.txtValorOferta);
            status = itemView.findViewById(R.id.txtStatusProposta);


            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);
        }
    }
}
