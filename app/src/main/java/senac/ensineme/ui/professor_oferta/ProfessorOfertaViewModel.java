package senac.ensineme.ui.professor_oferta;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfessorOfertaViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ProfessorOfertaViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}