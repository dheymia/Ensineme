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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import senac.ensineme.AlunoMainActivity;
import senac.ensineme.DemandaActivity;
import senac.ensineme.OfertaActivity;
import senac.ensineme.ProfessorMainActivity;
import senac.ensineme.R;
import senac.ensineme.models.Demanda;
import senac.ensineme.models.Oferta;

public class DemandaProfAdapter extends RecyclerView.Adapter<DemandaProfAdapter.DemandaProfViewHolder> implements Filterable {

    public static String codDemanda, codCategoria, aluno, inicio, descricao;
    private List<Demanda> demandaList;
    private List<Oferta> professorOfertaList = new ArrayList<>();
    private List<Demanda> backup;
    private List <Oferta> ofertaList = new ArrayList<>();
    private Context context;
    private String datainicio;
    private Oferta ofertaSelecionada;
    public View.OnClickListener mOnItemClickListener, clickInserir, clickConsulta;
    private String myFormat = "dd/MM/yyyy";
    private String format = "yyyy/MM/dd";
    private SimpleDateFormat formatoData =  new SimpleDateFormat(myFormat, new Locale("pt", "BR"));
    private SimpleDateFormat formatoDataDemanda = new SimpleDateFormat(format, new Locale("pt", "BR"));


    public DemandaProfAdapter(List<Demanda> demandaList, Context context) {
        this.demandaList = demandaList;
        this.backup = demandaList;
        this.context = context;
    }

    public List<Demanda> getDemandaList() {
        return demandaList;
    }

    @NonNull
    @Override
    public DemandaProfAdapter.DemandaProfViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_demandas_professor,parent,false);
        return new DemandaProfViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DemandaProfAdapter.DemandaProfViewHolder holder, int position) {


        final DemandaProfAdapter.DemandaProfViewHolder viewHolder = (DemandaProfAdapter.DemandaProfViewHolder) holder;
        final Demanda demanda = demandaList.get(position);

        viewHolder.categoria.setText(demanda.getCategoria());
        viewHolder.descricao.setText("Ensinar " + demanda.getDescricao());
        viewHolder.status.setText(demanda.getStatus());

        if (demanda.getStatus().equals("Aguardando proposta")){
            viewHolder.inserir.setVisibility(View.VISIBLE);
        } else if (demanda.getStatus().equals("Aguardando validação")){
            viewHolder.inserir.setVisibility(View.VISIBLE);
        } else{
            viewHolder.inserir.setVisibility(View.GONE);
        }

        try {
            Date expiracaoformatada = formatoDataDemanda.parse(demanda.getExpiracao());
            String expiracao = formatoData.format(expiracaoformatada);
            viewHolder.expiracao.setText(expiracao);

            Date inicioformatada = formatoDataDemanda.parse(demanda.getInicio());
            datainicio = formatoData.format(inicioformatada);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        viewHolder.resumo.setText(demanda.getHorasaula() + " horas/aula a partir de " + datainicio + " em " + demanda.getLocalidade() + " (" + demanda.getEstado() + ").");

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference refOfer = database.getReference("demandas/" + demanda.getCodigo() + "/propostas");
        refOfer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ofertaList.clear();
                professorOfertaList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Oferta oferta = ds.getValue(Oferta.class);
                    if (oferta.getProfessor().equals(ProfessorMainActivity.idUsuario)){
                        professorOfertaList.add(oferta);
                        ofertaSelecionada = oferta;
                    }
                    ofertaList.add(oferta);
                }
                if (ofertaList.size() == 0) {
                    viewHolder.consulta.setVisibility(View.GONE);
                } else {
                    viewHolder.consulta.setText(String.valueOf(ofertaList.size()));
                }

                if (demanda.getSituacao(demanda.getStatus()).equals("ATIVA")) {
                    if (professorOfertaList.size() > 0) {
                        viewHolder.inserir.setVisibility(View.GONE);
                    } else {
                        viewHolder.alterar.setVisibility(View.GONE);
                        viewHolder.excluir.setVisibility(View.GONE);
                    }
                } else {
                    viewHolder.inserir.setVisibility(View.GONE);
                    viewHolder.alterar.setVisibility(View.GONE);
                    viewHolder.excluir.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        viewHolder.alterar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfessorMainActivity.alterar = true;
                codDemanda = demanda.getCodigo();
                codCategoria  = demanda.getCategoriaCod();
                aluno = demanda.getAluno();
                descricao = demanda.getDescricao();
                inicio = demanda.getInicio();
                Intent oferta = new Intent(context, OfertaActivity.class);
                context.startActivity(oferta);
            }
        });
        viewHolder.inserir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfessorMainActivity.alterar = false;
                codDemanda = demanda.getCodigo();
                codCategoria  = demanda.getCategoriaCod();
                aluno = demanda.getAluno();
                descricao = demanda.getDescricao();
                inicio = demanda.getInicio();
                Intent oferta = new Intent(context, OfertaActivity.class);
                context.startActivity(oferta);
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
                                ofertaSelecionada.excluiofertaDB();
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

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String query = charSequence.toString().toLowerCase();
                if (query.isEmpty()) {
                    demandaList = backup;
                } else {
                    List<Demanda> filtro = new ArrayList<>();

                    for (Demanda d : backup) {

                        if (d.getDescricao().toLowerCase().startsWith(query) ||
                                d.getStatus().toLowerCase().startsWith(query)) {
                            filtro.add(d);
                        } else if (d.getAtualizacao().toLowerCase().startsWith(query) ||
                                d.getExpiracao().toLowerCase().startsWith(query)) {
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


    class DemandaProfViewHolder extends RecyclerView.ViewHolder{

        final TextView categoria;
        final TextView expiracao;
        final TextView descricao;
        final TextView resumo;
        final TextView status;
        final Button inserir;
        final Button consulta;
        final Button alterar;
        final Button excluir;

        DemandaProfViewHolder(@NonNull View itemView) {

            super(itemView);

            categoria = itemView.findViewById(R.id.txtCategoriaDemanda);
            resumo = itemView.findViewById(R.id.txtResumoDemanda);
            expiracao = itemView.findViewById(R.id.txtExpiraDemanda);
            descricao = itemView.findViewById(R.id.txtDescricaoDemanda);
            inserir = itemView.findViewById(R.id.btnInserirProposta);
            consulta = itemView.findViewById(R.id.btnConsultaPropostas);
            alterar = itemView.findViewById(R.id.btnAlteraProposta);
            excluir = itemView.findViewById(R.id.btnExcluirProposta);
            status = itemView.findViewById(R.id.txtStaDemandas);


            inserir.setTag(this);
            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);
            consulta.setTag(this);
            consulta.setOnClickListener(clickConsulta);

        }

    }
}