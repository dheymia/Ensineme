package senac.ensineme.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import senac.ensineme.R;
import senac.ensineme.models.Categoria;
import senac.ensineme.models.Oferta;

public class OfertaAdapter extends RecyclerView.Adapter<OfertaAdapter.OfertaViewHolder> {

    List<Oferta> ofertaList;
    private Context context;
    public View.OnClickListener mOnItemClickListener;

    public OfertaAdapter(List<Oferta> ofertaList, Context context) {
        this.ofertaList = ofertaList;
        this.context = context;
    }


    @NonNull
    @Override
    public OfertaAdapter.OfertaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ofertas,parent,false);
        OfertaAdapter.OfertaViewHolder holder = new OfertaAdapter.OfertaViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull OfertaAdapter.OfertaViewHolder holder, int position) {

        OfertaAdapter.OfertaViewHolder viewHolder = (OfertaAdapter.OfertaViewHolder) holder;
        final Oferta oferta = ofertaList.get(position);

        viewHolder.professor.setText(oferta.getProfessor());
        viewHolder.valorOferta.setText(oferta.getValorOferta());

    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }


    @Override
    public int getItemCount() {
        return ofertaList.size();
    }



    public class OfertaViewHolder extends RecyclerView.ViewHolder {

        final TextView professor, valorOferta;


        public OfertaViewHolder(@NonNull View itemView) {

            super(itemView);

            professor = itemView.findViewById(R.id.txtProfessor);
            valorOferta = itemView.findViewById(R.id.txtValorOferta);


            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);
        }
    }
}
