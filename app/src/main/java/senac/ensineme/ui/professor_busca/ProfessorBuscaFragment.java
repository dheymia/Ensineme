package senac.ensineme.ui.professor_busca;

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

import senac.ensineme.R;

public class ProfessorBuscaFragment extends Fragment {

    private ProfessorBuscaViewModel notificationsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(ProfessorBuscaViewModel.class);
        View root = inflater.inflate(R.layout.fragment_professor_busca, container, false);
        //final TextView textView = root.findViewById(R.id.text_notifications);
        notificationsViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
         //       textView.setText(s);
            }
        });
        return root;
    }
}