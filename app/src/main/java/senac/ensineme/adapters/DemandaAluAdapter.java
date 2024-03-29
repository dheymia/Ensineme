package senac.ensineme.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import senac.ensineme.AlunoMainActivity;
import senac.ensineme.DemandaActivity;
import senac.ensineme.R;
import senac.ensineme.models.Demanda;
import senac.ensineme.models.Oferta;

public class DemandaAluAdapter extends RecyclerView.Adapter<DemandaAluAdapter.DemandaAluViewHolder> implements Filterable {

    private List<Demanda> demandaList;
    private List<Demanda> backup;
    private List <Oferta> ofertaList = new ArrayList<>();
    private Context context;
    public View.OnClickListener mOnItemClickListener, clickConsulta;
    public static String codDemanda, codCategoria;
    private String myFormat = "dd/MM/yyyy";
    private String format = "yyyy/MM/dd";
    private SimpleDateFormat formatoData =  new SimpleDateFormat(myFormat, new Locale("pt", "BR"));
    private SimpleDateFormat formatoDataDemanda = new SimpleDateFormat(format, new Locale("pt", "BR"));


    public DemandaAluAdapter(List<Demanda> demandaList, Context context) {
        super();
        this.demandaList = demandaList;
        this.backup = demandaList;
        this.context = context;
    }

    public List<Demanda> getDemandaList() {
        return demandaList;
    }

    @NonNull
    @Override
    public DemandaAluAdapter.DemandaAluViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_demandas_aluno,parent,false);
        return new DemandaAluViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DemandaAluAdapter.DemandaAluViewHolder holder, final int position) {


        final DemandaAluAdapter.DemandaAluViewHolder viewHolder = (DemandaAluAdapter.DemandaAluViewHolder) holder;
        final Demanda demanda = demandaList.get(position);

        viewHolder.categoria.setText(demanda.getCategoria());
        viewHolder.descricao.setText(demanda.getDescricao());
        viewHolder.status.setText(demanda.getStatus());
        try {
            Date atualizacaoformatada = formatoDataDemanda.parse(demanda.getAtualizacao());
            String atualizacao = formatoData.format(atualizacaoformatada);

            Date expiracaoformatada = formatoDataDemanda.parse(demanda.getExpiracao());
            String expiracao = formatoData.format(expiracaoformatada);

            Date dataformatada = formatoDataDemanda.parse(demanda.getData());
            String data = formatoData.format(dataformatada);

            viewHolder.atualizacao.setText(atualizacao);


            if(demanda.getStatus().equals("Aguardando proposta")){
                viewHolder.excluir.setVisibility(View.VISIBLE);
                viewHolder.alterar.setVisibility(View.VISIBLE);
                viewHolder.resumo.setText("Solicitação cadastrada em " + data + " com expiração prevista para " + expiracao + ".");
            } else if (demanda.getStatus().equals("Aguardando validação")) {
                viewHolder.excluir.setVisibility(View.GONE);
                viewHolder.alterar.setVisibility(View.GONE);
                viewHolder.resumo.setText("Solicitação cadastrada em " + data + " com expiração prevista para " + expiracao + ".");
            } else {
                viewHolder.excluir.setVisibility(View.GONE);
                viewHolder.alterar.setVisibility(View.GONE);
                viewHolder.resumo.setText("Solicitação cadastrada em " + data + ".");
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }


        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference refOfer = database.getReference("demandas/" + demanda.getCodigo() + "/propostas");
        refOfer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ofertaList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Oferta oferta = ds.getValue(Oferta.class);
                    ofertaList.add(oferta);
                }
                if(ofertaList.size() == 0){
                    viewHolder.consulta.setVisibility(View.GONE);
                } else{
                    viewHolder.consulta.setVisibility(View.VISIBLE);
                    viewHolder.consulta.setText(String.valueOf(ofertaList.size()));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        viewHolder.alterar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlunoMainActivity.alterar = true;
                codDemanda = demanda.getCodigo();
                codCategoria = demanda.getCategoriaCod();
                Intent demanda = new Intent(context, DemandaActivity.class);
                context.startActivity(demanda);
            }
        });

        viewHolder.excluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                builder
                        .setMessage("Deseja excluir esta solicitação?")
                        .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                removerItem(position);
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


    private void removerItem(int position) {

        demandaList.remove(position);

        notifyItemRemoved(position);

        notifyItemRangeChanged(position, demandaList.size());



    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String query = charSequence.toString().toLowerCase();
                if(query.isEmpty())
                {
                    demandaList = backup;
                }
                else
                {
                    List<Demanda> filtro = new ArrayList<>();

                    for(Demanda d : backup)

                    {

                        if(d.getDescricao().toLowerCase().startsWith(query) ||
                                d.getStatus().toLowerCase().startsWith(query))
                        {
                            filtro.add(d);
                        } else  if(d.getAtualizacao().toLowerCase().startsWith(query) ||
                                d.getExpiracao().toLowerCase().startsWith(query))
                        {
                            filtro.add(d);
                        }
                    }
                    demandaList = filtro;
                }
                FilterResults resultado = new FilterResults();
                resultado.values = demandaList;
                return resultado;

            }
            @Override

            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                demandaList = (List<Demanda>) filterResults.values;
                notifyDataSetChanged();
            }

        };
    }


    class DemandaAluViewHolder extends RecyclerView.ViewHolder{

        final TextView categoria;
        final TextView status;
        final TextView descricao;
        final Button alterar;
        final Button excluir;
        final Button consulta;
        final TextView resumo;
        final TextView bemvindo;
        final TextView atualizacao;

        DemandaAluViewHolder(@NonNull View itemView) {

            super(itemView);

            categoria = itemView.findViewById(R.id.txtCatDemanda);
            status = itemView.findViewById(R.id.txtStaDemandas);
            descricao = itemView.findViewById(R.id.txtDescDemanda);
            alterar = itemView.findViewById(R.id.btnAlterarDemanda);
            excluir = itemView.findViewById(R.id.btnExcluirDemanda);
            consulta = itemView.findViewById(R.id.btnVerPropostas);
            resumo = itemView.findViewById(R.id.txtResDemanda);
            bemvindo = itemView.findViewById(R.id.txtSejaBemvindo);
            atualizacao = itemView.findViewById(R.id.txtAtualizaDemanda);

            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);
            consulta.setTag(this);
            consulta.setOnClickListener(clickConsulta);

        }

    }
}

