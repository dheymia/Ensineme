package senac.ensineme.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import senac.ensineme.R;
import senac.ensineme.models.Categoria;

public class CategoriaProfAdapter extends RecyclerView.Adapter<CategoriaProfAdapter.CategoriaProfViewHolder> {

    private List<Categoria> categoriaList;
    private Context context;
    public View.OnClickListener mOnItemClickListener;

    public CategoriaProfAdapter(List<Categoria> categoriaList, Context context) {
        this.categoriaList = categoriaList;
        this.context = context;
    }


    @NonNull
    @Override
    public CategoriaProfAdapter.CategoriaProfViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_categorias_professor,parent,false);
        return new CategoriaProfViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriaProfAdapter.CategoriaProfViewHolder holder, int position) {

        final Categoria categoria = categoriaList.get(position);
        ((CategoriaProfViewHolder) holder).categoria.setText(categoria.getNome());

        if ("Danças".equals(categoria.getNome())) {
            ((CategoriaProfViewHolder) holder).imagem.setImageResource(R.drawable.dancas);
        } else if ("Esportes".equals(categoria.getNome())) {
            ((CategoriaProfViewHolder) holder).imagem.setImageResource(R.drawable.esporte);
        } else if ("Culinária".equals(categoria.getNome())) {
            ((CategoriaProfViewHolder) holder).imagem.setImageResource(R.drawable.culinaria);
        } else if ("Instrumentos musicais".equals(categoria.getNome())) {
            ((CategoriaProfViewHolder) holder).imagem.setImageResource(R.drawable.instrumentos);
        } else if ("Disciplinas escolares".equals(categoria.getNome())) {
            ((CategoriaProfViewHolder) holder).imagem.setImageResource(R.drawable.escolares);
        } else if ("Artesanato".equals(categoria.getNome())) {
            ((CategoriaProfViewHolder) holder).imagem.setImageResource(R.drawable.artesanato);
        } else if ("Artes".equals(categoria.getNome())) {
            ((CategoriaProfViewHolder) holder).imagem.setImageResource(R.drawable.arte);
        }
        }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }


    public int getItemCount() {
        return categoriaList.size();
    }

    class CategoriaProfViewHolder extends RecyclerView.ViewHolder {

        final TextView categoria;
        final ImageView imagem;

        CategoriaProfViewHolder(@NonNull View itemView) {

            super(itemView);

            categoria = itemView.findViewById(R.id.txtNomeCategoria);
            imagem = itemView.findViewById(R.id.imgCategoria);

            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);
        }
    }


}
