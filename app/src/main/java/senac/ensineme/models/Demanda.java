package senac.ensineme.models;

import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

public class Demanda {

    private DatabaseReference firebase, firebaseUser, firebaseCat;
    private String aluno, codigo, descricao , turno, status, categoria, categoriaCod, inicio, data, expiracao,CEP, logradouro, bairro, complemento, localidade, estado, atualizacao, situacao;
    private int horasaula, validade, numero;

    public Demanda() {
    }

    public String getAtualizacao() {
        return atualizacao;
    }

    public void setAtualizacao(String atualizacao) {
        this.atualizacao = atualizacao;
    }

    public String getSituacao(String status) {
        switch (status) {
            case "Aguardando proposta":
                situacao = "ATIVA";
                break;
            case "Aguardando validação":
                situacao = "ATIVA";
                break;
            case "Encerrada com aceite":
                situacao = "INATIVA";
                break;
            case "Encerrada com rejeite":
                situacao = "INATIVA";
                break;
            case "Encerrada sem proposta":
                situacao = "INATIVA";
                break;
            case "Encerrada sem validação de proposta":
                situacao = "INATIVA";
                break;
        }
        return situacao;
    }

    public void setSituacao(String status) {

        switch (status) {
            case "Aguardando proposta":
                this.situacao = "ATIVA";
                break;
            case "Aguardando validação":
                this.situacao = "ATIVA";
                break;
            case "Encerrada com aceite":
                this.situacao = "INATIVA";
                break;
            case "Encerrada com rejeite":
                this.situacao = "INATIVA";
                break;
            case "Encerrada sem proposta":
                this.situacao = "INATIVA";
                break;
            case "Encerrada sem validação de proposta":
                this.situacao = "INATIVA";
                break;
        }
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

    public void salvaDemandaDB(DatabaseReference.CompletionListener... completionListener) {
        firebase = FirebaseDB.getFirebase().child("demandas").child(getCodigo());
        firebaseUser = FirebaseDB.getFirebase().child("usuarios").child(aluno).child("demandas").child(getCodigo());
        firebaseCat = FirebaseDB.getFirebase().child("categorias").child(categoriaCod).child("demandas").child(getCodigo());
        if (completionListener.length == 0) {
            firebase.setValue(this);
        } else {
            firebase.setValue(this, completionListener[0]);
            firebaseUser.setValue(this, completionListener[0]);
            firebaseCat.setValue(this, completionListener[0]);

            Map<String, Object> demandaUpdates = new HashMap<>();
            demandaUpdates.put("situacao", getSituacao(getStatus()));

            firebase.updateChildren(demandaUpdates, completionListener[0]);
            firebaseUser.updateChildren(demandaUpdates, completionListener[0]);
            firebaseCat.updateChildren(demandaUpdates, completionListener[0]);

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
        demandaUpdates.put("situacao", getSituacao(getStatus()));
        demandaUpdates.put("atualizacao", getAtualizacao());

        firebase.updateChildren(demandaUpdates, completionListener[0]);
        firebaseUser.updateChildren(demandaUpdates, completionListener[0]);
        firebaseCat.updateChildren(demandaUpdates, completionListener[0]);

    }

    public void excluiDemandaDB(DatabaseReference.CompletionListener... completionListener) {
        firebase = FirebaseDB.getFirebase().child("demandas").child(getCodigo());
        firebaseUser = FirebaseDB.getFirebase().child("usuarios").child(aluno).child("demandas").child(getCodigo());
        firebaseCat = FirebaseDB.getFirebase().child("categorias").child(categoriaCod).child("demandas").child(getCodigo());
        if (completionListener.length == 0) {
            firebase.removeValue();
            firebaseUser.removeValue();
            firebaseCat.removeValue();
        } else {
            firebase.removeValue((DatabaseReference.CompletionListener) this);
            firebaseUser.removeValue((DatabaseReference.CompletionListener) this);
            firebaseCat.removeValue((DatabaseReference.CompletionListener) this);
        }

    }


    public void atualizaStatusDemandaDB(DatabaseReference.CompletionListener... completionListener) {
        firebase = FirebaseDB.getFirebase().child("demandas").child(getCodigo());
        firebaseUser = FirebaseDB.getFirebase().child("usuarios").child(aluno).child("demandas").child(getCodigo());
        firebaseCat = FirebaseDB.getFirebase().child("categorias").child(categoriaCod).child("demandas").child(getCodigo());

        Map<String, Object> demandaUpdates = new HashMap<>();
        demandaUpdates.put("status", getStatus());
        demandaUpdates.put("situacao", getSituacao(getStatus()));
        demandaUpdates.put("atualizacao", getAtualizacao());

        firebase.updateChildren(demandaUpdates, completionListener[0]);
        firebaseUser.updateChildren(demandaUpdates, completionListener[0]);
        firebaseCat.updateChildren(demandaUpdates, completionListener[0]);

    }

}
