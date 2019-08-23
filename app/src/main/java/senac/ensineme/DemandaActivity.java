package senac.ensineme;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import senac.ensineme.models.Categoria;
import senac.ensineme.models.Demanda;
import senac.ensineme.models.EnderecoDemanda;
import senac.ensineme.models.FirebaseDB;


public class DemandaActivity extends ComumActivity implements DatabaseReference.CompletionListener, View.OnClickListener {

    Calendar myCalendar = Calendar.getInstance();

    DatabaseReference firebase;
    private String alunoDemanda, codDemanda;
    private Date dataDemanda, expiraDemanda;



    private Button btnCadastrar;
    private EditText txtDescDemanda, txtCEPDemanda, txtLogradouroDemanda, txtBairroDemanda, txtComplementoDemanda, txtLocalidadeDemanda, txtEstadoDemanda, txtInicioDemanda;
    private Spinner spnCatDemanda, spnTurnoDemanda, spnValidadeDemanda, spnHorasaulaDemanda;
    private Demanda demanda;

    // Armazena entradas
    private String descricao, categoria, turno, horasaula, CEP, logradouro, bairro, complemento, localidade, estado;
    private Date inicioDemanda;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demanda);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Nova demanda");


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
        horasaula = spnHorasaulaDemanda.getSelectedItem().toString();
        CEP = txtCEPDemanda.getText().toString();
        logradouro = txtLogradouroDemanda.getText().toString();
        bairro = txtBairroDemanda.getText().toString();
        complemento = txtComplementoDemanda.getText().toString();
        localidade = txtLocalidadeDemanda.getText().toString();
        estado = txtEstadoDemanda.getText().toString();

    }

    @Override
    protected void inicializaObjeto() {
        inicializaConteudo();
        demanda = new Demanda();
        //alunoDemanda;
        demanda.setCodDemanda("111111");
        demanda.setDescDemanda(descricao);
        demanda.setHorasaulaDemanda (horasaula);
        //validadeDemanda;
        demanda.setTurnoDemanda(turno);
        //statusDemanda;
        demanda.setCatDemanda(categoria);
        //localDemanda;
        //dataDemanda;
        //inicioDemanda;
        //expiraDemanda;

    }

    @Override
    protected boolean validaCampo() {
        if (descricao.isEmpty()) {
            txtDescDemanda.setError(getString(R.string.msg_erro_campo_empty));
            txtDescDemanda.requestFocus();
            return false;
        }
        if (horasaula.isEmpty()) {
            spnHorasaulaDemanda.requestFocus();
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

         public void ClickDate (View view){
        new DatePickerDialog(this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateLabel() {

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("pt", "BR"));

        txtInicioDemanda.setText(sdf.format(myCalendar.getTime()));
    }
}
