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

public class CategoriaProfAdapter extends RecyclerView.Adapter<CategoriaProfAdapter.CategoriaProfViewHolder> {

    List<Categoria> categoriaList;
    private Context context;
    public View.OnClickListener mOnItemClickListener;

    public CategoriaProfAdapter(List<Categoria> categoriaList, Context context) {
        this.categoriaList = categoriaList;
        this.context = context;
    }


    @NonNull
    @Override
    public CategoriaProfAdapter.CategoriaProfViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_categorias_inicio,parent,false);
        CategoriaProfAdapter.CategoriaProfViewHolder holder = new CategoriaProfAdapter.CategoriaProfViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriaProfAdapter.CategoriaProfViewHolder holder, int position) {

        CategoriaProfAdapter.CategoriaProfViewHolder viewHolder = (CategoriaProfAdapter.CategoriaProfViewHolder) holder;
        final Categoria categoria = categoriaList.get(position);

        viewHolder.categoria.setText(categoria.getNome());

    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }


    @Override
    public int getItemCount() {
        return categoriaList.size();
    }

    public class CategoriaProfViewHolder extends RecyclerView.ViewHolder {

        final TextView categoria;

        public CategoriaProfViewHolder(@NonNull View itemView) {

            super(itemView);

            categoria = itemView.findViewById(R.id.txtNomeCategoria);

            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);
        }
    }
}
