package senac.ensineme;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;

import senac.ensineme.models.FirebaseDB;
import senac.ensineme.models.Usuario;

public class FullscreenActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser firebaseUser;
    private Usuario usuario, usuariologado;
    private String idUsuario, tipoUsuario;
    private static int SPLASH_TIME_OUT = 3000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = getFirebaseAuthResultHandler();
        databaseReference = FirebaseDB.getFirebase();
        firebaseUser = firebaseAuth.getCurrentUser();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (firebaseAuth.getCurrentUser() != null) {
                    chamarMainActivity();
                } else {
                    firebaseAuth.addAuthStateListener(authStateListener);
                }
            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }


    private FirebaseAuth.AuthStateListener getFirebaseAuthResultHandler() {
        FirebaseAuth.AuthStateListener callback = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser userFirebase = firebaseAuth.getCurrentUser();

                if (userFirebase == null) {
                   chamaPrincipal();
                }
                chamarMainActivity();
            }
        };
        return (callback);

    }

    private void chamarMainActivity() {
        usuario = new Usuario();
        if (firebaseUser != null) {
            usuario.setId(firebaseUser.getUid());
            idUsuario = usuario.getId();
        }else{
            return;
        }

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("usuarios/" + idUsuario);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usuariologado = dataSnapshot.getValue(Usuario.class);
                if(usuariologado.getTipo() != null) {
                    tipoUsuario = usuariologado.getTipo();
                    Toast.makeText(FullscreenActivity.this,
                            idUsuario + " " + usuariologado.getTipo(),
                            Toast.LENGTH_LONG)
                            .show();
                    if (tipoUsuario.equals("aluno")) {
                        Intent aluno = new Intent(FullscreenActivity.this, AlunoMainActivity.class);
                        startActivity(aluno);
                        finish();
                    } else if (tipoUsuario.equals("professor")) {
                        Intent professor = new Intent(FullscreenActivity.this, ProfessorMainActivity.class);
                        startActivity(professor);
                        finish();
                    }else if (tipoUsuario.equals("administrador")) {
                        Intent administrador = new Intent(FullscreenActivity.this, AdministradorMainActivity.class);
                        startActivity(administrador);
                        finish();
                }
                   return;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                ProgressBar progressBar = (ProgressBar) findViewById(R.id.loading);
                Snackbar.make(progressBar,
                        "Erro de leitura: " + databaseError.getCode(),
                        Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


    }

    private void chamaPrincipal(){
        Intent principal = new Intent (FullscreenActivity.this,PrincipalActivity.class);
        startActivity(principal);
        finish();
    }

}
