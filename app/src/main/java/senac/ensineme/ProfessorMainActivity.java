package senac.ensineme;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        mTextMessage = findViewById(R.id.message);
        txtNome = findViewById(R.id.txtNome);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String name = sharedPreferences.getString("signature", "visitante");


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

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
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
