package senac.ensineme.ui.aluno_demanda;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AlunoDemandaViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AlunoDemandaViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}