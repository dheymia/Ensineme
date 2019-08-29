package senac.ensineme.ui.aluno_demanda;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import senac.ensineme.DemandaActivity;
import senac.ensineme.R;

public class AlunoDemandaFragment extends Fragment {

    private AlunoDemandaViewModel dashboardViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(AlunoDemandaViewModel.class);
        View root = inflater.inflate(R.layout.fragment_aluno_demanda, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);
        final FloatingActionButton fab = root.findViewById(R.id.fab);
        dashboardViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);

                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), DemandaActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });
        return root;


    }
}