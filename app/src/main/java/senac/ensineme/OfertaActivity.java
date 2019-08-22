package senac.ensineme;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.Date;

import senac.ensineme.models.Oferta;

public class OfertaActivity extends ComumActivity implements DatabaseReference.CompletionListener, View.OnClickListener {

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

    }

    @Override
    protected void inicializaViews() {

    }

    @Override
    protected void inicializaConteudo() {

    }

    @Override
    protected void inicializaObjeto() {

    }

    @Override
    protected boolean validaCampo() {
        return false;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}