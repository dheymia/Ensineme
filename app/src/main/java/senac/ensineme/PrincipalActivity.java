package senac.ensineme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
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

public class PrincipalActivity extends AppCompatActivity {

    private Button btnCadastrar, btnEntrar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);



        btnCadastrar = (Button) findViewById(R.id.btnCadastrarDemanda);
        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), UsuarioActivity.class);
                startActivity(intent);
            }
        });
        btnEntrar = (Button) findViewById(R.id.btnEntrar);
        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }

}
