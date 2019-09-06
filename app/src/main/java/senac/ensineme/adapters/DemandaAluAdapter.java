package senac.ensineme.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import senac.ensineme.R;
import senac.ensineme.models.Demanda;

public class DemandaAluAdapter extends RecyclerView.Adapter<DemandaAluAdapter.DemandaViewHolder> {

    List<Demanda> demandaList;
    private AlertDialog alerta;
    private Context context;
    public View.OnClickListener mOnItemClickListener;
    private String expiracao;
    private Date  expiracaoformatada;
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
    public DemandaAluAdapter.DemandaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_aluno_demanda,parent,false);
        DemandaAluAdapter.DemandaViewHolder holder = new DemandaAluAdapter.DemandaViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull DemandaAluAdapter.DemandaViewHolder holder, int position) {


        DemandaAluAdapter.DemandaViewHolder viewHolder = (DemandaAluAdapter.DemandaViewHolder) holder;
        final Demanda demanda = demandaList.get(position);

        viewHolder.categoria.setText(demanda.getCategoria());
        try {
            expiracaoformatada = formatoDataDemanda.parse(demanda.getExpiracao());
            expiracao = formatoData.format(expiracaoformatada);
            viewHolder.data.setText(expiracao);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        viewHolder.descricao.setText(demanda.getDescricao());
        viewHolder.detalhes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Demanda selecionada: " + demanda.getCodigo(), Toast.LENGTH_SHORT).show();

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

        public DemandaViewHolder(@NonNull View itemView) {

            super(itemView);

            categoria = itemView.findViewById(R.id.txtCatDemanda);
            data = itemView.findViewById(R.id.txtDataDemanda);
            descricao = itemView.findViewById(R.id.txtDescDemanda);
            detalhes = itemView.findViewById(R.id.txtDetalhesDemanda);

            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);
        }

    }
}

