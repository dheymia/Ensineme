package senac.ensineme;

import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.material.snackbar.Snackbar;

public abstract class ComumActivity extends AppCompatActivity {

    protected ProgressBar progressBar;


    protected void showSnackbar( String message ){
        Snackbar.make(progressBar,
                message,
                Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    protected void showToast( String message ){
        Toast.makeText(this,
                message,
                Toast.LENGTH_LONG)
                .show();
    }

    protected void openProgressBar(){
        progressBar.setVisibility( View.VISIBLE );
    }

    protected void closeProgressBar(){
        progressBar.setVisibility( View.GONE );
    }

    abstract protected void inicializaViews();

    abstract protected void inicializaConteudo();

    abstract protected void inicializaObjeto();

    abstract protected boolean validaCampo();

    public abstract void onConnectionFailed(ConnectionResult connectionResult);
}