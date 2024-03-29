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


import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import android.util.Log;
import android.view.MenuItem;
import android.view.View;


import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


import senac.ensineme.adapters.DemandaAluAdapter;
import senac.ensineme.models.Categoria;
import senac.ensineme.models.Demanda;

import senac.ensineme.models.FirebaseDB;


public class DemandaActivity extends ComumActivity implements DatabaseReference.CompletionListener, View.OnClickListener, AdapterView.OnItemSelectedListener {

    private Button btnCadastrar;
    private EditText txtDescDemanda, txtCEPDemanda, txtLogradouroDemanda, txtBairroDemanda, txtComplementoDemanda, txtLocalidadeDemanda, txtEstadoDemanda, txtInicioDemanda, txtNumero;
    private Spinner spnCatDemanda, spnTurnoDemanda, spnValidadeDemanda, spnHorasaulaDemanda;
    private String situacao, status, aluno, codDemanda, descricao,  turno, cargaHoraria, CEP, logradouro, bairro, complemento, localidade, estado, inicioDemanda, validadeDemanda, data,nomeCategoria, codCategoria, numero, inicio;
    private int horasaula, validade;
    private ArrayAdapter<CharSequence> turnoAdapter, validadeAdapter, horasAulaAdapter;
    private ArrayAdapter<Categoria> categoriaAdapter;
    private ArrayList<Categoria> categoriaList = new ArrayList<>();
    private Calendar myCalendar;
    private Categoria categoria;
    private Demanda demanda, demandaSelecionada;
    private Date dataformatada, inicioformatado;
    private DatabaseReference firebase;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private Date dataatual = new Date();
    private String myFormat = "dd/MM/yyyy";
    private String format = "yyyy/MM/dd";
    private SimpleDateFormat formatoData =  new SimpleDateFormat(myFormat, new Locale("pt", "BR"));
    private SimpleDateFormat formatoDataDemanda = new SimpleDateFormat(format, new Locale("pt", "BR"));
    private TextView txtCategoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demanda);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        myCalendar = Calendar.getInstance();
        firebase = FirebaseDB.getFirebase();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        inicializaViews();
        inicializaSpinners();
        progressBar = (ProgressBar) findViewById(R.id.loading);
        btnCadastrar = (Button) findViewById(R.id.btnCadastrarDemanda);
        btnCadastrar.setOnClickListener((View.OnClickListener) this);

        if (AlunoMainActivity.alterar){
            getSupportActionBar().setTitle("Alterar demanda");
            spnCatDemanda.setVisibility( View.GONE );
            btnCadastrar.setText(getString(R.string.acaoAlterar));
            codDemanda = DemandaAluAdapter.codDemanda;
            codCategoria = DemandaAluAdapter.codCategoria;
            inicializaCamposPreenchidos();
        } else {
            txtCategoria.setVisibility(View.GONE);
            codDemanda = firebase.child("demandas").push().getKey();
            if (firebaseUser != null) {
                aluno = firebaseUser.getUid();
            }
        }

        txtCEPDemanda.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    showToast("Consultando CEP");
                    consultaCEP();
                }
            }
        });
        txtInicioDemanda.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showToast("Escolha a data");
                    chamaCalendario();
                }
            }
        });

    }

    private void inicializaSpinners() {

        categoriaSpinner();

        turnoAdapter = ArrayAdapter.createFromResource(this,R.array.turnoDemanda, android.R.layout.simple_spinner_item);
        turnoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnTurnoDemanda.setAdapter(turnoAdapter);
        spnTurnoDemanda.setOnItemSelectedListener(this);

        validadeAdapter = ArrayAdapter.createFromResource(this,R.array.validadeDemanda, android.R.layout.simple_spinner_item);
        validadeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnValidadeDemanda.setAdapter(validadeAdapter);
        spnValidadeDemanda.setOnItemSelectedListener(this);

        horasAulaAdapter = ArrayAdapter.createFromResource(this,R.array.cargaHoraria, android.R.layout.simple_spinner_item);
        horasAulaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnHorasaulaDemanda.setAdapter(horasAulaAdapter);
        spnHorasaulaDemanda.setOnItemSelectedListener(this);
    }

    @Override
    public void onClick(View view) {

        try {
            inicializaConteudo();

            if (validaCampo()) {
                btnCadastrar.setEnabled(false);
                progressBar.setFocusable(true);
                openProgressBar();
                inicializaObjeto();
                if(AlunoMainActivity.alterar){
                    demanda.atualizademandaDB(DemandaActivity.this);
                } else{
                    demanda.salvaDemandaDB(DemandaActivity.this);
                }

            } else {
                closeProgressBar();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

        if (databaseError != null) {
            showSnackbar(databaseError.getMessage());
            closeProgressBar();
            btnCadastrar.setEnabled(true);
        } else {
            if(AlunoMainActivity.alterar){
                showToast("Demanda atualizada com sucesso!");
            } else{
                showToast("Demanda criada com sucesso!");
            }
            closeProgressBar();
            finish();
        }
    }

    protected void categoriaSpinner() {

        categoriaList.clear();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("categorias");

        ref.orderByChild("nome").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    categoria = ds.getValue(Categoria.class);
                    categoriaList.add(categoria);
                }

                categoriaAdapter = new ArrayAdapter<Categoria> (DemandaActivity.this, android.R.layout.simple_spinner_item, categoriaList);
                categoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnCatDemanda.setAdapter(categoriaAdapter);
                spnCatDemanda.setOnItemSelectedListener(DemandaActivity.this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                ProgressBar progressBar = (ProgressBar) findViewById(R.id.loading);
                Snackbar.make(progressBar,
                        "Erro de leitura: " + databaseError.getCode(),
                        Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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
        txtCategoria = findViewById(R.id.txtCategoria);
    }

    protected void inicializaCamposPreenchidos(){

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("demandas/" + codDemanda);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                demandaSelecionada = dataSnapshot.getValue(Demanda.class);

                txtDescDemanda.setText(String.valueOf(demandaSelecionada.getDescricao()));
                txtNumero.setText(String.valueOf(demandaSelecionada.getNumero()));
                txtCEPDemanda.setText(String.valueOf(demandaSelecionada.getCEP()));
                txtLogradouroDemanda.setText(String.valueOf(demandaSelecionada.getLogradouro()));
                txtBairroDemanda.setText(String.valueOf(demandaSelecionada.getBairro()));
                txtComplementoDemanda.setText(String.valueOf(demandaSelecionada.getComplemento()));
                txtLocalidadeDemanda.setText(String.valueOf(demandaSelecionada.getLocalidade()));
                txtEstadoDemanda.setText(String.valueOf(demandaSelecionada.getEstado()));
                spnTurnoDemanda.setSelection(turnoAdapter.getPosition(String.valueOf(demandaSelecionada.getTurno())));
                txtCategoria.setText(String.valueOf(demandaSelecionada.getCategoria()));
                try {
                    inicioformatado = formatoDataDemanda.parse(demandaSelecionada.getInicio());
                    inicio = formatoData.format(inicioformatado);
                    txtInicioDemanda.setText(inicio);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                aluno = demandaSelecionada.getAluno();
                horasaula = demandaSelecionada.getHorasaula();
                validade = demandaSelecionada.getValidade();
                switch (horasaula) {
                    case 4:
                        cargaHoraria = "4 horas/aula";
                        break;
                    case 8:
                        cargaHoraria = "8 horas/aula";
                        break;
                    case 12:
                        cargaHoraria = "12 horas/aula";
                        break;
                    case 16:
                        cargaHoraria = "16 horas/aula";
                        break;
                    case 24:
                        cargaHoraria = "24 horas/aula";
                        break;
                    case 28:
                        cargaHoraria = "28 horas/aula";
                        break;
                    case 32:
                        cargaHoraria = "32 horas/aula";
                        break;
                }

                switch (validade) {
                    case 1:
                        validadeDemanda = "1 dia";
                        break;
                    case 3:
                        validadeDemanda = "3 dias";
                        break;
                    case 5:
                        validadeDemanda = "5 dias";
                        break;
                    case 7:
                        validadeDemanda = "7 dias";
                        break;
                    case 15:
                        validadeDemanda = "15 dias";
                        break;
                    case 30:
                        validadeDemanda = "30 dias";
                        break;

                }
                spnValidadeDemanda.setSelection(validadeAdapter.getPosition(String.valueOf(validadeDemanda)));
                spnHorasaulaDemanda.setSelection(horasAulaAdapter.getPosition(String.valueOf(cargaHoraria)));

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

    @Override
    protected void inicializaConteudo() throws ParseException {
        descricao = txtDescDemanda.getText().toString();
        turno = spnTurnoDemanda.getSelectedItem().toString();
        CEP = txtCEPDemanda.getText().toString();
        numero = txtNumero.getText().toString();
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
    protected void inicializaObjeto() throws ParseException {

        if(AlunoMainActivity.alterar){
            status = demandaSelecionada.getStatus();
            data = demandaSelecionada.getData();
        } else{
            status = "Aguardando proposta";
            data = formatoDataDemanda.format(dataatual);
            nomeCategoria = spnCatDemanda.getSelectedItem().toString();
            codCategoria = ((Categoria)spnCatDemanda.getSelectedItem()).getCodigo();
            inicioformatado = formatoData.parse(inicioDemanda);
            inicio = formatoDataDemanda.format(inicioformatado);
        }

        demanda = new Demanda();
        demanda.setAluno(aluno);
        demanda.setCodigo(codDemanda);
        demanda.setDescricao(descricao);
        demanda.setHorasaula(horasaula);
        demanda.setValidade(validade);
        demanda.setTurno(turno);
        demanda.setStatus(status);
        demanda.setCategoria(nomeCategoria);
        demanda.setCategoriaCod(codCategoria);
        demanda.setInicio(inicio);
        demanda.setExpiracao(expiraDemanda());
        demanda.setCEP(CEP);
        demanda.setNumero(Integer.parseInt(numero));
        demanda.setLogradouro(logradouro);
        demanda.setBairro(bairro);
        demanda.setComplemento(complemento);
        demanda.setLocalidade(localidade);
        demanda.setEstado(estado);
        demanda.setData(data);
        demanda.setAtualizacao(formatoDataDemanda.format(dataatual));
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
        if (numero.isEmpty()) {
            txtNumero.setError(getString(R.string.msg_erro_campo_empty));
            txtNumero.requestFocus();
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

    public String expiraDemanda (){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new java.util.Date());
        calendar.add (Calendar.DAY_OF_MONTH, validade);
        String diaFormat = formatoDataDemanda.format(calendar.getTime());
        return diaFormat;
    }

    final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        final Object item = parent.getItemAtPosition(pos);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

}
