package senac.ensineme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import senac.ensineme.models.FirebaseDB;
import senac.ensineme.models.Usuario;

public class ProfessorMainActivity extends AppCompatActivity {
    private TextView mTextMessage;
    private TextView txtNome;
    Usuario usuario,usuariologado;
    private DatabaseReference firebase;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private String idUsuario, nomeUsuario;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);
        txtNome = findViewById(R.id.txtNome);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String name = sharedPreferences.getString("signature", "visitante");

        //txtNome.setText("Olá " + name);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

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
                                          if (usuariologado.getTipo() != null) {
                                              nomeUsuario = usuariologado.getNome();
                                              txtNome.setText("Olá " + nomeUsuario);
                                          }
                                      }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), DemandaActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.acaoSair) {
            FirebaseAuth.getInstance().signOut();
            Intent principal = new Intent(getBaseContext(), FullscreenActivity.class);
            startActivity(principal);
            finish();
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.acaoConfigurar) {
            Intent novaConfig = new Intent(getBaseContext(), ConfiguracaoActivity.class);
            startActivity(novaConfig);
        }

        return super.onOptionsItemSelected(item);
    }

}
