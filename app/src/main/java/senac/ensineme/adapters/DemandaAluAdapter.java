package senac.ensineme.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import senac.ensineme.DemandaActivity;
import senac.ensineme.R;
import senac.ensineme.models.Demanda;
import senac.ensineme.ui.aluno_inicio.AlunoInicioFragment;

public class DemandaAluAdapter extends RecyclerView.Adapter<DemandaAluAdapter.DemandaAluViewHolder> {

    List<Demanda> demandaList;
    private Context context;
    public View.OnClickListener mOnItemClickListener, clickConsulta;
    public static String codDemanda, codCategoria;


    public DemandaAluAdapter(List<Demanda> demandaList, Context context) {
        this.demandaList = demandaList;
        this.context = context;
    }

    @NonNull
    @Override
    public DemandaAluAdapter.DemandaAluViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_demandas_aluno,parent,false);
        DemandaAluAdapter.DemandaAluViewHolder holder = new DemandaAluAdapter.DemandaAluViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull DemandaAluAdapter.DemandaAluViewHolder holder, int position) {


        DemandaAluAdapter.DemandaAluViewHolder viewHolder = (DemandaAluAdapter.DemandaAluViewHolder) holder;
        final Demanda demanda = demandaList.get(position);

        viewHolder.categoria.setText(demanda.getCategoria());
        viewHolder.descricao.setText(demanda.getDescricao());
        viewHolder.status.setText(demanda.getStatus());
        viewHolder.alterar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlunoInicioFragment.alterar = true;
                codDemanda = demanda.getCodigo();
                codCategoria = demanda.getCategoriaCod();
                Intent demanda = new Intent(context, DemandaActivity.class);
                context.startActivity(demanda);
            }
        });

        if(demanda.getStatus().equals("Aguardando proposta")){
            viewHolder.excluir.setVisibility(View.VISIBLE);
            viewHolder.alterar.setVisibility(View.VISIBLE);
        } else{
            viewHolder.excluir.setVisibility(View.GONE);
            viewHolder.alterar.setVisibility(View.GONE);
        }

        viewHolder.excluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                builder
                        .setMessage("Deseja excluir esta solicitação?")
                        .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                demanda.excluiDemandaDB();
                                dialog.cancel();

                            }
                        })

                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .show();
            }
        });


        viewHolder.consulta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Consultando " + demanda.getDescricao(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }

    public void setClickConsultaPrposta(View.OnClickListener itemClickListener) {
        clickConsulta = itemClickListener;
    }

    @Override
    public int getItemCount() {
        return demandaList.size();
    }


    public class DemandaAluViewHolder extends RecyclerView.ViewHolder{

        final TextView categoria;
        final TextView status;
        final TextView descricao;
        final Button alterar;
        final Button excluir;
        final Button consulta;

        public DemandaAluViewHolder(@NonNull View itemView) {

            super(itemView);

            categoria = itemView.findViewById(R.id.txtCatDemanda);
            status = itemView.findViewById(R.id.txtStaDemanda);
            descricao = itemView.findViewById(R.id.txtDescDemanda);
            alterar = itemView.findViewById(R.id.btnAlterarDemanda);
            excluir = itemView.findViewById(R.id.btnExcluirDemanda);
            consulta = itemView.findViewById(R.id.btnVerPropostas);

            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);
            consulta.setTag(this);
            consulta.setOnClickListener(clickConsulta);

        }

    }
}

