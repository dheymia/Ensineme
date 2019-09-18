package senac.ensineme.adapters;

import android.content.Context;
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

import senac.ensineme.ProfessorMainActivity;
import senac.ensineme.R;
import senac.ensineme.models.Demanda;
import senac.ensineme.models.Oferta;
import senac.ensineme.models.Usuario;

public class UsuariosAdapter extends RecyclerView.Adapter<UsuariosAdapter.UsuariosViewHolder> implements Filterable {

    private List<Usuario> usuarioList;
    private List<Usuario> backup;
    private List<Oferta> ofertaList = new ArrayList<>();
    private List<Demanda> demandaList = new ArrayList<>();
    private Context context;
    private String myFormat = "dd/MM/yyyy";
    private String format = "yyyy/MM/dd";
    private SimpleDateFormat formatoData = new SimpleDateFormat(myFormat, new Locale("pt", "BR"));
    private SimpleDateFormat formatoDataDemanda = new SimpleDateFormat(format, new Locale("pt", "BR"));

    public UsuariosAdapter(List<Usuario> usuarioList, Context context) {
        this.usuarioList = usuarioList;
        this.backup = usuarioList;
        this.context = context;
    }

    public List<Usuario> getUsuarioList() {
        return usuarioList;
    }

    @NonNull
    @Override
    public UsuariosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_usuarios_cadastrados, parent, false);
        return new UsuariosAdapter.UsuariosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuariosViewHolder holder, int position) {

        final UsuariosAdapter.UsuariosViewHolder viewHolder = (UsuariosAdapter.UsuariosViewHolder) holder;
        final Usuario usuario = usuarioList.get(position);

        viewHolder.nomecompleto.setText(usuario.getNomecompleto());
        viewHolder.email.setText(usuario.getEmail());
        viewHolder.celular.setText(usuario.getCelular());
        viewHolder.tipo.setText(usuario.getTipo());

        try {
            Date dataformatada = formatoDataDemanda.parse(usuario.getInscricao());
            String inscricao = formatoData.format(dataformatada);
            viewHolder.data.setText(inscricao);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference refOfer = database.getReference("usuarios/" + usuario.getId() + "/propostas");
        refOfer.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ofertaList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Oferta oferta = ds.getValue(Oferta.class);
                    ofertaList.add(oferta);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference refDem = database.getReference("usuarios/" + usuario.getId() + "/Demandas");
        refDem.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                demandaList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Demanda demanda = ds.getValue(Demanda.class);
                    demandaList.add(demanda);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

  /*      if (usuario.getTipo().equals("professor")) {
            viewHolder.demandas.setVisibility(View.GONE);
            viewHolder.propostas.setText(String.valueOf(ofertaList.size()));
        } else if (usuario.getTipo().equals("aluno")) {
            viewHolder.propostas.setVisibility(View.GONE);
            viewHolder.demandas.setText(String.valueOf(demandaList.size()));
        } else {
            viewHolder.demandas.setVisibility(View.INVISIBLE);
            viewHolder.propostas.setVisibility(View.INVISIBLE);
        }
*/
    }

    @Override
    public int getItemCount() {
        return usuarioList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String query = charSequence.toString().toLowerCase();
                if (query.isEmpty()) {
                    usuarioList = backup;
                } else {
                    List<Usuario> filtro = new ArrayList<>();

                    for (Usuario u : backup) {

                        if (u.getNomecompleto().toLowerCase().startsWith(query) ||
                                u.getTipo().toLowerCase().startsWith(query)) {
                            filtro.add(u);
                        } else if (u.getCelular().toLowerCase().startsWith(query) ||
                                u.getEmail().toLowerCase().startsWith(query)) {
                            filtro.add(u);
                        }
                    }
                    usuarioList = filtro;
                }
                FilterResults resultado = new FilterResults();
                resultado.values = usuarioList;
                return resultado;

            }

            @Override

            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                usuarioList = (List<Usuario>) filterResults.values;
                notifyDataSetChanged();
            }

        };
    }

    public class UsuariosViewHolder extends RecyclerView.ViewHolder {

        final TextView nomecompleto;
        final TextView data;
        final TextView email;
        final TextView celular;
        final TextView tipo;
        final Button propostas;
        final Button demandas;

        public UsuariosViewHolder(@NonNull View itemView) {
            super(itemView);

            nomecompleto = itemView.findViewById(R.id.txtNomeCompletoUsuario);
            data = itemView.findViewById(R.id.txtInscricaoUsuario);
            email = itemView.findViewById(R.id.txtEmailUsuario);
            celular = itemView.findViewById(R.id.txtCelularUsuario);
            tipo = itemView.findViewById(R.id.txtTipoUsuario);
            propostas = itemView.findViewById(R.id.btnVerPropostas);
            demandas = itemView.findViewById(R.id.btnVerDemandas);
        }
    }
}
