package senac.ensineme.ui.aluno_pesquisa;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AlunoPesquisaViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AlunoPesquisaViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}