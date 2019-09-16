package senac.ensineme.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import senac.ensineme.R;
import senac.ensineme.models.Oferta;

public class StatusDemandaAdapter extends RecyclerView.Adapter<StatusDemandaAdapter.StatusDemandaViewHolder> {

    private List<CharSequence> statusList;
    private Context context;
    public View.OnClickListener mOnItemClickListener;

    public StatusDemandaAdapter(List<CharSequence> statusList, Context context) {
        this.statusList = statusList;
        this.context = context;
    }

    @NonNull
    @Override
    public StatusDemandaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_status_demanda,parent,false);
        return new StatusDemandaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusDemandaViewHolder holder, int position) {

        final CharSequence status = statusList.get(position);

        ((StatusDemandaViewHolder) holder).status.setText(status);

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener) {
        mOnItemClickListener = itemClickListener;
    }

    public class StatusDemandaViewHolder extends RecyclerView.ViewHolder {

        TextView status;

        public StatusDemandaViewHolder(@NonNull View itemView) {
            super(itemView);

            status = itemView.findViewById(R.id.txtMenuStatusDemanda);
        }
    }
}
