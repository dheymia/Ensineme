package senac.ensineme.models;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Demanda {

    DatabaseReference firebase, firebaseUser, firebaseCat;
    private String aluno, codigo, descricao , turno, status, categoria, categoriaCod, inicio, data, expiracao,CEP, logradouro, bairro, complemento, localidade, estado;
    private int horasaula, validade, numero;
    private Boolean encerrada;

    public Demanda() {
    }

    public String getCategoriaCod() {
        return categoriaCod;
    }

    public void setCategoriaCod(String categoriaCod) {
        this.categoriaCod = categoriaCod;
    }

    public String getAluno() {
        return aluno;
    }

    public void setAluno(String aluno) {
        this.aluno = aluno;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getValidade() {
        return validade;
    }

    public void setValidade(int validade) {
        this.validade = validade;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getInicio() {
        return inicio;
    }

    public void setInicio(String inicio) {
        this.inicio = inicio;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getExpiracao() {
        return expiracao;
    }

    public void setExpiracao(String expiracao) {
        this.expiracao = expiracao;
    }

    public int getHorasaula() {
        return horasaula;
    }

    public void setHorasaula(int horasaula) {
        this.horasaula = horasaula;
    }


    public String getCEP() {
        return CEP;
    }

    public void setCEP(String CEP) {
        this.CEP = CEP;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getLocalidade() {
        return localidade;
    }

    public void setLocalidade(String localidade) {
        this.localidade = localidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public Boolean getEncerrada() {
        return encerrada;
    }

    public void setEncerrada(Boolean encerrada) {
        this.encerrada = encerrada;
    }

    public void encerrada() {
        encerrada = true;
    }

    public Boolean isEncerrado() {
        return encerrada;
    }

    public void salvaDemandaDB(DatabaseReference.CompletionListener... completionListener) {
        firebase = FirebaseDB.getFirebase().child("demandas").child(getCodigo());
        firebaseUser = FirebaseDB.getFirebase().child("usuarios").child(aluno).child("demandas").child(getCodigo());
        firebaseCat = FirebaseDB.getFirebase().child("categorias").child(categoriaCod).child("demandas").child(getCodigo());
        if (completionListener.length == 0) {
            firebase.setValue(this);
        } else {
            firebase.setValue(this, completionListener[0]);

            Map<String, Object> demandaUserUpdates = new HashMap<>();
            demandaUserUpdates.put("descricao", getDescricao());
            demandaUserUpdates.put("status", getStatus());
            demandaUserUpdates.put("categoria", getCategoria());
            demandaUserUpdates.put("categoriaCod", getCategoriaCod());
            demandaUserUpdates.put("data", getData());
            demandaUserUpdates.put("expiracao", getExpiracao());
            firebaseUser.setValue(demandaUserUpdates, completionListener[0]);

            Map<String, Object> demandaCatUpdates = new HashMap<>();
            demandaCatUpdates.put("aluno", getAluno());
            demandaCatUpdates.put("descricao", getDescricao());
            demandaCatUpdates.put("status", getStatus());
            demandaCatUpdates.put("data", getData());
            demandaCatUpdates.put("expiracao", getExpiracao());
            firebaseCat.setValue(demandaCatUpdates, completionListener[0]);

        }

    }

    public void atualizademandaDB(DatabaseReference.CompletionListener... completionListener) {
        firebase = FirebaseDB.getFirebase().child("demandas").child(getCodigo());
        firebaseUser = FirebaseDB.getFirebase().child("usuarios").child(aluno).child("demandas").child(getCodigo());
        firebaseCat = FirebaseDB.getFirebase().child("categorias").child(categoriaCod).child("demandas").child(getCodigo());
        Map<String, Object> demandaUpdates = new HashMap<>();
        demandaUpdates.put("aluno", getAluno());
        demandaUpdates.put("codigo", getCodigo());
        demandaUpdates.put("descricao", getDescricao());
        demandaUpdates.put("turno", getTurno());
        demandaUpdates.put("status", getStatus());
        demandaUpdates.put("categoria", getCategoria());
        demandaUpdates.put("categoriaCod", getCategoriaCod());
        demandaUpdates.put("inicio", getInicio());
        demandaUpdates.put("data", getData());
        demandaUpdates.put("expiracao", getExpiracao());
        demandaUpdates.put("CEP", getCEP());
        demandaUpdates.put("logradouro", getLogradouro());
        demandaUpdates.put("bairro", getBairro());
        demandaUpdates.put("complemento", getComplemento());
        demandaUpdates.put("localidade", getLocalidade());
        demandaUpdates.put("estado", getEstado());
        demandaUpdates.put("horasaula", getHorasaula());
        demandaUpdates.put("validade", getValidade());
        demandaUpdates.put("numero", getNumero());
        firebase.updateChildren(demandaUpdates, completionListener[0]);

        Map<String, Object> demandaUserUpdates = new HashMap<>();
        demandaUserUpdates.put("descricao", getDescricao());
        demandaUserUpdates.put("status", getStatus());
        demandaUserUpdates.put("categoria", getCategoria());
        demandaUserUpdates.put("categoriaCod", getCategoriaCod());
        demandaUserUpdates.put("data", getData());
        demandaUserUpdates.put("expiracao", getExpiracao());
        firebaseUser.updateChildren(demandaUserUpdates, completionListener[0]);

        Map<String, Object> demandaCatUpdates = new HashMap<>();
        demandaCatUpdates.put("aluno", getAluno());
        demandaCatUpdates.put("descricao", getDescricao());
        demandaCatUpdates.put("status", getStatus());
        demandaCatUpdates.put("data", getData());
        demandaCatUpdates.put("expiracao", getExpiracao());
        firebaseCat.updateChildren(demandaCatUpdates, completionListener[0]);
    }



}
