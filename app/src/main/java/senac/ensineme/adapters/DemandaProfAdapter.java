package senac.ensineme.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import senac.ensineme.R;
import senac.ensineme.models.Demanda;
import senac.ensineme.models.Oferta;

public class DemandaProfAdapter extends RecyclerView.Adapter<DemandaProfAdapter.DemandaProfViewHolder> {

    List<Demanda> demandaList;
    List <Oferta> ofertaList = new ArrayList<>();
    private Context context;
    public View.OnClickListener mOnItemClickListener, clickInserir, clickConsulta;
    private String expiracao;
    private Date  expiracaoformatada;
    private String myFormat = "dd/MM/yyyy";
    private String format = "yyyy/MM/dd";
    private SimpleDateFormat formatoData =  new SimpleDateFormat(myFormat, new Locale("pt", "BR"));
    private SimpleDateFormat formatoDataDemanda = new SimpleDateFormat(format, new Locale("pt", "BR"));


    public DemandaProfAdapter(List<Demanda> demandaList, Context context) {
        this.demandaList = demandaList;
        this.context = context;
    }

    @NonNull
    @Override
    public DemandaProfAdapter.DemandaProfViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_demandas_professor,parent,false);
        DemandaProfAdapter.DemandaProfViewHolder holder = new DemandaProfAdapter.DemandaProfViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull DemandaProfAdapter.DemandaProfViewHolder holder, int position) {


        final DemandaProfAdapter.DemandaProfViewHolder viewHolder = (DemandaProfAdapter.DemandaProfViewHolder) holder;
        final Demanda demanda = demandaList.get(position);

        viewHolder.categoria.setText(demanda.getCategoria());
        viewHolder.descricao.setText(demanda.getDescricao());
        try {
            expiracaoformatada = formatoDataDemanda.parse(demanda.getExpiracao());
            expiracao = formatoData.format(expiracaoformatada);
            viewHolder.expiracao.setText(expiracao);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference refOfer = database.getReference("demandas/" + demanda.getCodigo() + "/propostas");
        refOfer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ofertaList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Oferta oferta = ds.getValue(Oferta.class);
                    ofertaList.add(oferta);
                }
                if(ofertaList.size() == 0){
                    viewHolder.consulta.setVisibility(View.GONE);
                } else{
                    viewHolder.consulta.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


            }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }

    public void setClickInserir(View.OnClickListener itemClickListener) {
        clickInserir = itemClickListener;
    }

    public void setClickConsultaPrposta(View.OnClickListener itemClickListener) {
        clickConsulta = itemClickListener;
    }


    @Override
    public int getItemCount() {
        return demandaList.size();
    }


    public class DemandaProfViewHolder extends RecyclerView.ViewHolder{

        final TextView categoria;
        final TextView expiracao;
        final TextView descricao;
        final Button inserir;
        final Button consulta;

        public DemandaProfViewHolder(@NonNull View itemView) {

            super(itemView);

            categoria = itemView.findViewById(R.id.txtCategoriaDemanda);
            expiracao = itemView.findViewById(R.id.txtExpiraDemanda);
            descricao = itemView.findViewById(R.id.txtDescricaoDemanda);
            inserir = itemView.findViewById(R.id.btnInserirProposta);
            consulta = itemView.findViewById(R.id.btnConsultaProposta);


            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);
            inserir.setTag(this);
            inserir.setOnClickListener(clickInserir);
            consulta.setTag(this);
            consulta.setOnClickListener(clickConsulta);

        }

    }
}