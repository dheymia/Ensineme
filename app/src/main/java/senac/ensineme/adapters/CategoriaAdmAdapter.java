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

public class CategoriaAdmAdapter extends RecyclerView.Adapter<CategoriaAdmAdapter.CategoriaAdmViewHolder> {

    private List<Categoria> categoriaList;
    private Context context;
    public View.OnClickListener mOnItemClickListener;

    public CategoriaAdmAdapter(List<Categoria> categoriaList, Context context) {
        this.categoriaList = categoriaList;
        this.context = context;
    }


    @NonNull
    @Override
    public CategoriaAdmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_categorias_administrador,parent,false);
        return new CategoriaAdmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriaAdmViewHolder holder, int position) {

        final Categoria categoria = categoriaList.get(position);

        ((CategoriaAdmViewHolder) holder).categoria.setText(categoria.getNome());

        if ("Danças".equals(categoria.getNome())) {
            ((CategoriaAdmViewHolder) holder).imagem.setImageResource(R.drawable.dancas);
        } else if ("Esportes".equals(categoria.getNome())) {
            ((CategoriaAdmViewHolder) holder).imagem.setImageResource(R.drawable.esporte);
        } else if ("Culinária".equals(categoria.getNome())) {
            ((CategoriaAdmViewHolder) holder).imagem.setImageResource(R.drawable.culinaria);
        } else if ("Instrumentos musicais".equals(categoria.getNome())) {
            ((CategoriaAdmViewHolder) holder).imagem.setImageResource(R.drawable.instrumentos);
        } else if ("Disciplinas escolares".equals(categoria.getNome())) {
            ((CategoriaAdmViewHolder) holder).imagem.setImageResource(R.drawable.escolares);
        } else if ("Artesanato".equals(categoria.getNome())) {
            ((CategoriaAdmViewHolder) holder).imagem.setImageResource(R.drawable.artesanato);
        } else if ("Artes".equals(categoria.getNome())) {
            ((CategoriaAdmViewHolder) holder).imagem.setImageResource(R.drawable.arte);
        }

        ((CategoriaAdmViewHolder) holder).excluir.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder
                        .setMessage("Deseja excluir esta categoria?")
                        .setPositiveButton("Confirmar",  new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                categoria.excluiCategoriaDB();

                            }
                        })

                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        })
                        .show();            }
        });

    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }


    @Override
    public int getItemCount() {
        return categoriaList.size();
    }



    class CategoriaAdmViewHolder extends RecyclerView.ViewHolder {

        final TextView categoria;
        final Button excluir;
        final ImageView imagem;

        CategoriaAdmViewHolder(@NonNull View itemView) {

            super(itemView);

            categoria = itemView.findViewById(R.id.txtNomeCategoria);
            excluir = itemView.findViewById(R.id.btnExcluirCategoria);
            imagem = itemView.findViewById(R.id.imgCategoria);


            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);
        }
    }
}
