package senac.ensineme.ui.aluno_busca;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AlunoBuscaViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AlunoBuscaViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}