package senac.ensineme;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import senac.ensineme.models.Usuario;
import senac.ensineme.ui.professor_busca.ProfessorBuscaFragment;

public class ProfessorMainActivity extends AppCompatActivity {

    private Usuario usuario;
    private Usuario usuariologado;
    private String tipoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_professor);
        BottomNavigationView navView = findViewById(R.id.nav_view);

       //getSupportActionBar().hide();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String name = sharedPreferences.getString("signature", "visitante");


        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        usuario = new Usuario();
        String idUsuario;
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
                usuariologado = dataSnapshot.getValue(Usuario.class);
                if (usuariologado.getTipo() != null) {
                    tipoUsuario = usuariologado.getTipo();
//                    txtNome.setText("Olá " + nomeUsuario);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_inicio, R.id.navigation_busca)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
       // NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

    }

  /*
    @Override
    public void onBackPressed()    {

        if (tipoUsuario.equals("administrador")) {
            Intent administrador = new Intent(ProfessorMainActivity.this, AdministradorMainActivity.class);
            startActivity(administrador);
            finish();

        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder
                    .setMessage("Deseja sair?")
                    .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            finishAffinity();

                        }
                    })

                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .show();
        }
    }

   */

}
