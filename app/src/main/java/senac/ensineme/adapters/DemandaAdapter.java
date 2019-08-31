package senac.ensineme.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import senac.ensineme.R;
import senac.ensineme.models.Categoria;
import senac.ensineme.models.Demanda;

public class DemandaAdapter extends RecyclerView.Adapter<DemandaAdapter.DemandaViewHolder> {

    List<Demanda> demandaList;
    private Context context;
    public View.OnClickListener mOnItemClickListener;

    public DemandaAdapter(List<Demanda> demandaList, Context context) {
        this.demandaList = demandaList;
        this.context = context;
    }

    @NonNull
    @Override
    public DemandaAdapter.DemandaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_aluno_demanda,parent,false);
        DemandaAdapter.DemandaViewHolder holder = new DemandaAdapter.DemandaViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull DemandaAdapter.DemandaViewHolder holder, int position) {


        DemandaAdapter.DemandaViewHolder viewHolder = (DemandaAdapter.DemandaViewHolder) holder;
        final Demanda demanda = demandaList.get(position);

        viewHolder.categoria.setText(demanda.getCategoria());
        viewHolder.data.setText(demanda.getData());
        viewHolder.descricao.setText(demanda.getDescricao());
        viewHolder.detalhes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        viewHolder.propostas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }

    @Override
    public int getItemCount() {
        return demandaList.size();
    }

    public class DemandaViewHolder extends RecyclerView.ViewHolder{

        final TextView categoria;
        final TextView data;
        final TextView descricao;
        final TextView detalhes;
        final TextView propostas;

        public DemandaViewHolder(@NonNull View itemView) {

            super(itemView);

            categoria = itemView.findViewById(R.id.txtCatDemanda);
            data = itemView.findViewById(R.id.txtDataDemanda);
            descricao = itemView.findViewById(R.id.txtDescDemanda);
            detalhes = itemView.findViewById(R.id.txtDetalhesDemanda);
            propostas = itemView.findViewById(R.id.txtVerPropostas);

            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);
        }

    }
}

