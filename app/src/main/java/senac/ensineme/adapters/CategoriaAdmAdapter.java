package senac.ensineme.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import senac.ensineme.R;
import senac.ensineme.models.Categoria;

public class CategoriaAdmAdapter extends RecyclerView.Adapter<CategoriaAdmAdapter.CategoriaAdmViewHolder> {

    List<Categoria> categoriaList;
    private Context context;
    public View.OnClickListener mOnItemClickListener;

    public CategoriaAdmAdapter(List<Categoria> categoriaList, Context context) {
        this.categoriaList = categoriaList;
        this.context = context;
    }


    @NonNull
    @Override
    public CategoriaAdmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_adm_categorias,parent,false);
        CategoriaAdmViewHolder holder = new CategoriaAdmViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriaAdmViewHolder holder, int position) {

        CategoriaAdmViewHolder viewHolder = (CategoriaAdmViewHolder) holder;
        final Categoria categoria = categoriaList.get(position);

        viewHolder.categoria.setText(categoria.getNome());
        viewHolder.excluir.setOnClickListener(new View.OnClickListener() {

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



    public class CategoriaAdmViewHolder extends RecyclerView.ViewHolder {

        final TextView categoria;
        final Button excluir;

        public CategoriaAdmViewHolder(@NonNull View itemView) {

            super(itemView);

            categoria = itemView.findViewById(R.id.txtNomeCategoria);
            excluir = itemView.findViewById(R.id.btnExcluirCategoria);

            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);
        }
    }
}
