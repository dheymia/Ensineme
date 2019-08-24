package senac.ensineme;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;

import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import senac.ensineme.models.Categoria;
import senac.ensineme.models.Demanda;
import senac.ensineme.models.EnderecoDemanda;
import senac.ensineme.models.FirebaseDB;


public class DemandaActivity extends ComumActivity implements DatabaseReference.CompletionListener, View.OnClickListener {

    private Button btnCadastrar;
    private EditText txtDescDemanda, txtCEPDemanda, txtLogradouroDemanda, txtBairroDemanda, txtComplementoDemanda, txtLocalidadeDemanda, txtEstadoDemanda, txtInicioDemanda;
    private Spinner spnCatDemanda, spnTurnoDemanda, spnValidadeDemanda, spnHorasaulaDemanda;
    private String aluno, codDemanda, descricao, categoria, turno, cargaHoraria, CEP, logradouro, bairro, complemento, localidade, estado, inicioDemanda, validadeDemanda;
    private int horasaula, validade;
    private Date dataDemanda, expiraDemanda;
    private Calendar myCalendar;
    private Demanda demanda;
    private DatabaseReference firebase;
    private FirebaseAuth firebaseAuth;
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

        txtCEPDemanda = findViewById(R.id.txtCep);
        txtLogradouroDemanda = findViewById(R.id.txtLogradouro);
        txtBairroDemanda = findViewById(R.id.txtBairro);
        txtComplementoDemanda = findViewById(R.id.txtComplemento);
        txtLocalidadeDemanda = findViewById(R.id.txtCidade);
        txtEstadoDemanda = findViewById(R.id.txtEstado);

        txtCEPDemanda.setOnFocusChangeListener (new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange (View v, boolean hasFocus){
                if (!hasFocus)
                    Toast.makeText(getApplicationContext(), "unfocus", 2000).show();

                RequestQueue requestQueue;

                // Instantiate the cache
                Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

                // Set up the network to use HttpURLConnection as the HTTP client.
                Network network = new BasicNetwork(new HurlStack());

                // Instantiate the RequestQueue with the cache and network.
                requestQueue = new RequestQueue(cache, network);

                // Start the queue
                requestQueue.start();

                String cep = txtCEPDemanda.getText().toString();

                String url = "https://viacep.com.br/ws/" + cep +"/json/";

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
        });

        myCalendar = Calendar.getInstance();
        firebase = FirebaseDB.getFirebase();
        firebaseAuth = FirebaseAuth.getInstance();
        codDemanda = firebase.child("demandas").push().getKey();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            aluno = firebaseUser.getUid();
        }
        inicializaViews();

        progressBar = (ProgressBar) findViewById(R.id.loading);
        btnCadastrar = (Button) findViewById(R.id.btnCadastrarDemanda);
        btnCadastrar.setOnClickListener((View.OnClickListener) this);

    }

    @Override
    public void onClick(View view) {

        inicializaObjeto();

        if (validaCampo()) {
            btnCadastrar.setEnabled(false);
            progressBar.setFocusable(true);
            openProgressBar();
            demanda.salvaDemandaDB();
        } else {
            closeProgressBar();
        }

    }

    @Override
    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
        showToast("Demanda criada com sucesso!");
        closeProgressBar();
        finish();
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
        logradouro = txtLogradouroDemanda.getText().toString();
        bairro = txtBairroDemanda.getText().toString();
        complemento = txtComplementoDemanda.getText().toString();
        localidade = txtLocalidadeDemanda.getText().toString();
        estado = txtEstadoDemanda.getText().toString();
        cargaHoraria = spnHorasaulaDemanda.getSelectedItem().toString();
        inicioDemanda = txtInicioDemanda.getText().toString();
        validadeDemanda = spnValidadeDemanda.getSelectedItem().toString();

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

        inicializaConteudo();

        demanda = new Demanda();
        demanda.setAluno(aluno);
        demanda.setCodigo(codDemanda);
        demanda.setDescricao(descricao);
        demanda.setHorasaula(horasaula);
        demanda.setValidade(validade);
        demanda.setTurno(turno);
        demanda.setStatus("Aguardando proposta");
        demanda.setCategoria(categoria);
        //data;
        demanda.setInicio(inicioDemanda);
        //expiracao;
        demanda.setCEP(CEP);
        demanda.setLogradouro(logradouro);
        demanda.setBairro(bairro);
        demanda.setComplemento(complemento);
        demanda.setLocalidade(localidade);
        demanda.setEstado(estado);
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

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

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


    public void ClickDate(View view) {
        new DatePickerDialog(this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateLabel() {

        formatoData = new SimpleDateFormat(myFormat, new Locale("pt", "BR"));
        txtInicioDemanda.setText(formatoData.format(myCalendar.getTime()));
    }


}
