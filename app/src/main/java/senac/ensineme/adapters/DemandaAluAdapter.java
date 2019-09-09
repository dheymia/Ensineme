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

import senac.ensineme.DemandaActivity;
import senac.ensineme.R;
import senac.ensineme.models.Demanda;
import senac.ensineme.models.Oferta;
import senac.ensineme.ui.aluno_inicio.AlunoInicioFragment;

public class DemandaAluAdapter extends RecyclerView.Adapter<DemandaAluAdapter.DemandaAluViewHolder> {

    private List<Demanda> demandaList;
    private List <Oferta> ofertaList = new ArrayList<>();
    private Context context;
    public View.OnClickListener mOnItemClickListener, clickConsulta;
    public static String codDemanda, codCategoria;
    private String myFormat = "dd/MM/yyyy";
    private String format = "yyyy/MM/dd";
    private SimpleDateFormat formatoData =  new SimpleDateFormat(myFormat, new Locale("pt", "BR"));
    private SimpleDateFormat formatoDataDemanda = new SimpleDateFormat(format, new Locale("pt", "BR"));


    public DemandaAluAdapter(List<Demanda> demandaList, Context context) {
        this.demandaList = demandaList;
        this.context = context;
    }

    @NonNull
    @Override
    public DemandaAluAdapter.DemandaAluViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_demandas_aluno,parent,false);
        return new DemandaAluViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DemandaAluAdapter.DemandaAluViewHolder holder, int position) {


        final DemandaAluAdapter.DemandaAluViewHolder viewHolder = (DemandaAluAdapter.DemandaAluViewHolder) holder;
        final Demanda demanda = demandaList.get(position);

        viewHolder.categoria.setText(demanda.getCategoria());
        viewHolder.descricao.setText("Aprender " + demanda.getDescricao());
        viewHolder.status.setText(demanda.getStatus());
        try {
            Date expiracaoformatada = formatoDataDemanda.parse(demanda.getExpiracao());
            String expiracao = formatoData.format(expiracaoformatada);

            Date dataformatada = formatoDataDemanda.parse(demanda.getData());
            String data = formatoData.format(dataformatada);

            viewHolder.resumo.setText("Solicitação cadastrada em " + data + " com expiração prevista para " + expiracao + ".");

        } catch (ParseException e) {
            e.printStackTrace();
        }


        if(demanda.getStatus().equals("Aguardando proposta")){
            viewHolder.excluir.setVisibility(View.VISIBLE);
            viewHolder.alterar.setVisibility(View.VISIBLE);
        } else{
            viewHolder.excluir.setVisibility(View.GONE);
            viewHolder.alterar.setVisibility(View.GONE);
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
                AlunoInicioFragment.alterar = true;
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


    class DemandaAluViewHolder extends RecyclerView.ViewHolder{

        final TextView categoria;
        final TextView status;
        final TextView descricao;
        final Button alterar;
        final Button excluir;
        final Button consulta;
        final TextView resumo;

        DemandaAluViewHolder(@NonNull View itemView) {

            super(itemView);

            categoria = itemView.findViewById(R.id.txtCatDemanda);
            status = itemView.findViewById(R.id.txtStaDemanda);
            descricao = itemView.findViewById(R.id.txtDescDemanda);
            alterar = itemView.findViewById(R.id.btnAlterarDemanda);
            excluir = itemView.findViewById(R.id.btnExcluirDemanda);
            consulta = itemView.findViewById(R.id.btnVerPropostas);
            resumo = itemView.findViewById(R.id.txtResDemanda);

            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);
            consulta.setTag(this);
            consulta.setOnClickListener(clickConsulta);

        }

    }
}

