package senac.ensineme.ui.aluno_inicio;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AlunoInicioViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AlunoInicioViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}