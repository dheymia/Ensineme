package senac.ensineme;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import senac.ensineme.models.FirebaseDB;
import senac.ensineme.models.Usuario;

public class FullscreenActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser firebaseUser;
    private String idUsuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = getFirebaseAuthResultHandler();
        DatabaseReference databaseReference = FirebaseDB.getFirebase();
        firebaseUser = firebaseAuth.getCurrentUser();

        int SPLASH_TIME_OUT = 3000;
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
        return (new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth1) {

                FirebaseUser userFirebase = firebaseAuth1.getCurrentUser();

                if (userFirebase == null) {
                   chamaPrincipal();
                }
                chamarMainActivity();
            }
        });

    }

    private void chamarMainActivity() {
        Usuario usuario = new Usuario();
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
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuariologado = dataSnapshot.getValue(Usuario.class);
                if (usuariologado.getTipo() != null) {
                    String tipoUsuario = usuariologado.getTipo();
                    Toast.makeText(FullscreenActivity.this,
                            idUsuario + " " + usuariologado.getTipo(),
                            Toast.LENGTH_LONG)
                            .show();
                    switch (tipoUsuario) {
                        case "aluno":
                            Intent aluno = new Intent(FullscreenActivity.this, AlunoMainActivity.class);
                            startActivity(aluno);
                            finish();
                            break;
                        case "professor":
                            Intent professor = new Intent(FullscreenActivity.this, ProfessorMainActivity.class);
                            startActivity(professor);
                            finish();
                            break;
                        case "administrador":
                            Intent administrador = new Intent(FullscreenActivity.this, AdministradorMainActivity.class);
                            startActivity(administrador);
                            finish();
                            break;
                        default:
                            throw new IllegalStateException("Tipo do usuário: " + tipoUsuario);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                ProgressBar progressBar = findViewById(R.id.loading);
                Snackbar.make(progressBar,
                        "Erro de leitura: " + databaseError.getCode(),
                        Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


    }

    private void chamaPrincipal(){
        Intent principal;
        principal = new Intent (FullscreenActivity.this,PrincipalActivity.class);
        startActivity(principal);
        finish();
    }

}
