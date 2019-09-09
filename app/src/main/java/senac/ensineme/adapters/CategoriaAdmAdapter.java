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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import senac.ensineme.R;
import senac.ensineme.models.Categoria;
import senac.ensineme.models.Demanda;
import senac.ensineme.models.Oferta;

public class CategoriaAdmAdapter extends RecyclerView.Adapter<CategoriaAdmAdapter.CategoriaAdmViewHolder> {

    private List<Categoria> categoriaList;
    private Context context;
    public View.OnClickListener mOnItemClickListener;
    private List <Demanda> demandaList = new ArrayList<>();

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
    public void onBindViewHolder(@NonNull final CategoriaAdmViewHolder holder, int position) {

        final Categoria categoria = categoriaList.get(position);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference refOfer = database.getReference("categorias/" + categoria.getCodigo() + "/demandas");
        refOfer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                demandaList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Demanda demanda = ds.getValue(Demanda.class);
                    demandaList.add(demanda);
                }
                if (demandaList.size() == 0) {
                    ((CategoriaAdmViewHolder) holder).excluir.setVisibility(View.VISIBLE);
                    ((CategoriaAdmViewHolder) holder).categorias.setVisibility(View.GONE);
                } else {
                    ((CategoriaAdmViewHolder) holder).excluir.setVisibility(View.GONE);
                    ((CategoriaAdmViewHolder) holder).categorias.setVisibility(View.VISIBLE);
                    ((CategoriaAdmViewHolder) holder).categorias.setText(String.valueOf(demandaList.size()));
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

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
        }else{
            ((CategoriaAdmViewHolder) holder).imagem.setImageResource(R.drawable.novo);
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
        final Button excluir, categorias;
        final ImageView imagem;

        CategoriaAdmViewHolder(@NonNull View itemView) {

            super(itemView);

            categoria = itemView.findViewById(R.id.txtNomeCategoria);
            excluir = itemView.findViewById(R.id.btnExcluirCategoria);
            imagem = itemView.findViewById(R.id.imgCategoria);
            categorias = itemView.findViewById(R.id.btnQuantCategorias);


            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);
        }
    }
}
