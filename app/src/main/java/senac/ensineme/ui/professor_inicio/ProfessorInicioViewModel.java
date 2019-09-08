package senac.ensineme.ui.professor_inicio;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProfessorInicioViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ProfessorInicioViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}