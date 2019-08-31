package senac.ensineme;

import android.app.DatePickerDialog;

import android.os.Bundle;

import com.android.volley.Cache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;


import android.util.Log;
import android.view.View;


import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import senac.ensineme.models.Categoria;
import senac.ensineme.models.Demanda;

import senac.ensineme.models.FirebaseDB;
import senac.ensineme.ui.aluno_demanda.AlunoDemandaFragment;


public class DemandaActivity extends ComumActivity implements DatabaseReference.CompletionListener, View.OnClickListener {

    private Button btnCadastrar;
    private EditText txtDescDemanda, txtCEPDemanda, txtLogradouroDemanda, txtBairroDemanda, txtComplementoDemanda, txtLocalidadeDemanda, txtEstadoDemanda, txtInicioDemanda, txtNumero;
    private Spinner spnCatDemanda, spnTurnoDemanda, spnValidadeDemanda, spnHorasaulaDemanda;
    private String status, aluno, codDemanda, descricao,  turno, cargaHoraria, CEP, logradouro, bairro, complemento, localidade, estado, inicioDemanda, validadeDemanda, data,categoria;
    private int horasaula, validade, numero;
    private ArrayAdapter<String> adapter;
    private List<Categoria> categoriaList = new ArrayList<Categoria>();
    private ArrayList<String> arrayCategoria = new ArrayList<String>();;
    private Calendar myCalendar;
    private Demanda demanda;
    private Categoria catDemanda;
    private DatabaseReference firebase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String myFormat = "dd/MM/yyyy";
    SimpleDateFormat formatoData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demanda);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Nova demanda");

        myCalendar = Calendar.getInstance();
        firebase = FirebaseDB.getFirebase();
        firebaseAuth = FirebaseAuth.getInstance();
        codDemanda = firebase.child("demandas").push().getKey();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            aluno = firebaseUser.getUid();
        }

        inicializaViews();
        addDadosSpinnerCategoria();

        if (AlunoDemandaFragment.alterar){
            getSupportActionBar().setTitle("Alterar demanda");
            //btnCadastrar.setText("Alterar");
            codDemanda = AlunoDemandaFragment.demandaSelecionada.getCodigo();
            data = AlunoDemandaFragment.demandaSelecionada.getData();
            aluno = AlunoDemandaFragment.demandaSelecionada.getAluno();
            status = AlunoDemandaFragment.demandaSelecionada.getStatus();

            txtDescDemanda.setText(String.valueOf(AlunoDemandaFragment.demandaSelecionada.getDescricao()));
            txtNumero.setText(String.valueOf(AlunoDemandaFragment.demandaSelecionada.getNumero()));
            txtCEPDemanda.setText(String.valueOf(AlunoDemandaFragment.demandaSelecionada.getCEP()));
            txtLogradouroDemanda.setText(String.valueOf(AlunoDemandaFragment.demandaSelecionada.getLogradouro()));
            txtBairroDemanda.setText(String.valueOf(AlunoDemandaFragment.demandaSelecionada.getBairro()));
            txtComplementoDemanda.setText(String.valueOf(AlunoDemandaFragment.demandaSelecionada.getComplemento()));
            txtLocalidadeDemanda.setText(String.valueOf(AlunoDemandaFragment.demandaSelecionada.getLocalidade()));
            txtEstadoDemanda.setText(String.valueOf(AlunoDemandaFragment.demandaSelecionada.getEstado()));
            txtInicioDemanda.setText(String.valueOf(AlunoDemandaFragment.demandaSelecionada.getInicio()));
            //spnCatDemanda
            //spnTurnoDemanda.
            //spnValidadeDemanda
            //spnHorasaulaDemanda

        }



        txtCEPDemanda.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Toast.makeText(getApplicationContext(), "unfocus", Toast.LENGTH_SHORT).show();
                    consultaCEP();
                }
            }
        });
        txtInicioDemanda.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Toast.makeText(getApplicationContext(), "Escolha a data", Toast.LENGTH_SHORT).show();
                    chamaCalendario();
                }
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.loading);
        btnCadastrar = (Button) findViewById(R.id.btnCadastrarCategoria);
        btnCadastrar.setOnClickListener((View.OnClickListener) this);

    }

    @Override
    public void onClick(View view) {

        inicializaConteudo();

        if (validaCampo()) {
            btnCadastrar.setEnabled(false);
            progressBar.setFocusable(true);
            openProgressBar();
            inicializaObjeto();
            demanda.salvaDemandaDB(DemandaActivity.this);
        } else {
            closeProgressBar();
        }

    }

    @Override
    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

        if (databaseError != null) {
            showSnackbar(databaseError.getMessage());
            closeProgressBar();
            btnCadastrar.setEnabled(true);
        } else {
            showToast("Demanda criada com sucesso!");
            closeProgressBar();
            finish();
        }
    }

    @Override
    protected void inicializaViews() {
        txtDescDemanda = findViewById(R.id.txtDescricaoDemanda);
        txtCEPDemanda = findViewById(R.id.txtCep);
        txtLogradouroDemanda = findViewById(R.id.txtLogradouro);
        txtBairroDemanda = findViewById(R.id.txtBairro);
        txtComplementoDemanda = findViewById(R.id.txtComplemento);
        txtLocalidadeDemanda = findViewById(R.id.txtCidade);
        txtEstadoDemanda = findViewById(R.id.txtEstado);
        txtInicioDemanda = findViewById(R.id.txtInicioDemanda);
        txtNumero = findViewById(R.id.txtNumero);
        spnCatDemanda = findViewById(R.id.spCategoria);
        spnTurnoDemanda = findViewById(R.id.spTurno);
        spnValidadeDemanda = findViewById(R.id.spValidadeDemanda);
        spnHorasaulaDemanda = findViewById(R.id.spCargaHoraria);
    }

    @Override
    protected void inicializaConteudo() {
        descricao = txtDescDemanda.getText().toString();
        categoria = spnCatDemanda.getSelectedItem().toString();
        turno = spnTurnoDemanda.getSelectedItem().toString();
        CEP = txtCEPDemanda.getText().toString();
        numero = Integer.parseInt(txtNumero.getText().toString());
        logradouro = txtLogradouroDemanda.getText().toString();
        bairro = txtBairroDemanda.getText().toString();
        complemento = txtComplementoDemanda.getText().toString();
        localidade = txtLocalidadeDemanda.getText().toString();
        estado = txtEstadoDemanda.getText().toString();
        cargaHoraria = spnHorasaulaDemanda.getSelectedItem().toString();
        inicioDemanda = txtInicioDemanda.getText().toString();
        validadeDemanda = spnValidadeDemanda.getSelectedItem().toString();
        status = "Aguardando proposta";

        switch (cargaHoraria) {
            case "4 horas/aula":
                horasaula = 4;
                break;
            case "8 horas/aula":
                horasaula = 8;
                break;
            case "12 horas/aula":
                horasaula = 12;
                break;
            case "16 horas/aula":
                horasaula = 16;
                break;
            case "24 horas/aula":
                horasaula = 24;
                break;
            case "28 horas/aula":
                horasaula = 28;
                break;
            case "32 horas/aula":
                horasaula = 32;
                break;
        }


        switch (validadeDemanda) {
            case "1 dia":
                validade = 1;
                break;
            case "3 dias":
                validade = 3;
                break;
            case "5 dias":
                validade = 5;
                break;
            case "7 dias":
                validade = 7;
                break;
            case "15 dias":
                validade = 15;
                break;
            case "30 dias":
                validade = 30;
                break;

        }

    }

    @Override
    protected void inicializaObjeto() {

        demanda = new Demanda();
        demanda.setAluno(aluno);
        demanda.setCodigo(codDemanda);
        demanda.setDescricao(descricao);
        demanda.setHorasaula(horasaula);
        demanda.setValidade(validade);
        demanda.setTurno(turno);
        demanda.setStatus(status);
        demanda.setCategoria(categoria);
        demanda.setInicio(inicioDemanda);
        demanda.setExpiracao(expiraDemanda());
        demanda.setCEP(CEP);
        demanda.setNumero(numero);
        demanda.setLogradouro(logradouro);
        demanda.setBairro(bairro);
        demanda.setComplemento(complemento);
        demanda.setLocalidade(localidade);
        demanda.setEstado(estado);
        if (AlunoDemandaFragment.alterar){
            if (data != null){
                demanda.setData(data);
            }
            demanda.setData(formatoData.format(new Date()));
        }else{
            demanda.setData(formatoData.format(new Date()));
        }
    }

    @Override
    protected boolean validaCampo() {
        if (descricao.isEmpty()) {
            txtDescDemanda.setError(getString(R.string.msg_erro_campo_empty));
            txtDescDemanda.requestFocus();
            return false;
        }
        if (inicioDemanda.isEmpty()) {
            txtInicioDemanda.setError(getString(R.string.msg_erro_campo_empty));
            txtInicioDemanda.requestFocus();
            return false;
        }
        if (CEP.isEmpty()) {
            txtCEPDemanda.setError(getString(R.string.msg_erro_campo_empty));
            txtCEPDemanda.requestFocus();
            return false;
        }
        if (logradouro.isEmpty()) {
            txtLogradouroDemanda.setError(getString(R.string.msg_erro_campo_empty));
            txtLogradouroDemanda.requestFocus();
            return false;
        }
        if (bairro.isEmpty()) {
            txtBairroDemanda.setError(getString(R.string.msg_erro_campo_empty));
            txtBairroDemanda.requestFocus();
            return false;
        }
        if (complemento.isEmpty()) {
            txtComplementoDemanda.setError(getString(R.string.msg_erro_campo_empty));
            txtComplementoDemanda.requestFocus();
            return false;
        }
        if (localidade.isEmpty()) {
            txtLocalidadeDemanda.setError(getString(R.string.msg_erro_campo_empty));
            txtLocalidadeDemanda.requestFocus();
            return false;
        }
        if (estado.isEmpty()) {
            txtEstadoDemanda.setError(getString(R.string.msg_erro_campo_empty));
            txtEstadoDemanda.requestFocus();
            return false;
        }
        return true;
    }
private void addDadosSpinnerCategoria() {
    FirebaseDatabase data = FirebaseDatabase.getInstance();
    DatabaseReference categoriaRef = data.getReference("categorias");

    ValueEventListener ListenerGeral = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            categoriaList.clear();
            arrayCategoria.clear();
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                catDemanda = ds.getValue(Categoria.class);
                categoriaList.add(catDemanda);
                arrayCategoria.add(catDemanda.getCategoria());

            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    categoriaRef.orderByChild("categoria").addValueEventListener(ListenerGeral);
    adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, arrayCategoria);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    //spnCatDemanda.setAdapter(adapter);

    spnCatDemanda.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView parent, View arg1,
                                   int arg2, long arg3) {
        }
        @Override
        public void onNothingSelected(AdapterView arg0) {
        }
    });
}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public String expiraDemanda (){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new java.util.Date());
        calendar.add (Calendar.DAY_OF_MONTH, validade);
        String diaFormat = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
        return diaFormat;
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };



    public void chamaCalendario() {
        new DatePickerDialog(this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }


    private void updateLabel() {

        formatoData = new SimpleDateFormat(myFormat, new Locale("pt", "BR"));
        txtInicioDemanda.setText(formatoData.format(myCalendar.getTime()));
    }

    private void consultaCEP() {
        RequestQueue requestQueue;

        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

        BasicNetwork network = new BasicNetwork(new HurlStack());

        requestQueue = new RequestQueue(cache, (com.android.volley.Network) network);

        requestQueue.start();

        String cep = txtCEPDemanda.getText().toString();

        String url = "https://viacep.com.br/ws/" + cep + "/json/";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    bairro = (response.getString("bairro"));
                    CEP = (response.getString("cep"));
                    complemento = (response.getString("complemento"));
                    localidade = (response.getString("localidade"));
                    logradouro = (response.getString("logradouro"));
                    estado = (response.getString("uf"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                txtLogradouroDemanda.setText(logradouro);
                txtBairroDemanda.setText(bairro);
                txtLocalidadeDemanda.setText(localidade);
                txtEstadoDemanda.setText(estado);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("jsonObjectError", error.toString());
                txtCEPDemanda.setError("Digite um CEP Válido!");
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

}
