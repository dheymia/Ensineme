package senac.ensineme.ui.professor_busca;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfessorBuscaViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ProfessorBuscaViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}