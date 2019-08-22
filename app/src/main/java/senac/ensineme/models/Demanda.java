package senac.ensineme.models;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Demanda {

    private String alunoDemanda, codDemanda, descDemanda, horasaulaDemanda,validadeDemanda, turnoDemanda, statusDemanda;
    private Categoria catDemanda;
    private EnderecoDemanda localDemanda;
    private Date dataDemanda,inicioDemanda,expiraDemanda;
    private List <Oferta> ofertas;
    private Boolean encerrada;

    public Demanda() {
    }

    public Demanda(String alunoDemanda, String codDemanda, String descDemanda, String horasaulaDemanda, String validadeDemanda, String turnoDemanda, String statusDemanda, Categoria catDemanda, EnderecoDemanda localDemanda, Date dataDemanda, Date inicioDemanda, Date expiraDemanda, List<Oferta> ofertas, Boolean encerrada) {
        this.alunoDemanda = alunoDemanda;
        this.codDemanda = codDemanda;
        this.descDemanda = descDemanda;
        this.horasaulaDemanda = horasaulaDemanda;
        this.validadeDemanda = validadeDemanda;
        this.turnoDemanda = turnoDemanda;
        this.statusDemanda = statusDemanda;
        this.catDemanda = catDemanda;
        this.localDemanda = localDemanda;
        this.dataDemanda = new Date();
        this.inicioDemanda = new Date();
        this.expiraDemanda = new Date();
        this.ofertas = new ArrayList<Oferta>();
        this.encerrada = encerrada;
    }

    public String getAlunoDemanda() {
        return alunoDemanda;
    }

    public void setAlunoDemanda(String alunoDemanda) {
        this.alunoDemanda = alunoDemanda;
    }

    public String getCodDemanda() {
        return codDemanda;
    }

    public void setCodDemanda(String codDemanda) {
        this.codDemanda = codDemanda;
    }

    public String getDescDemanda() {
        return descDemanda;
    }

    public void setDescDemanda(String descDemanda) {
        this.descDemanda = descDemanda;
    }

    public String getHorasaulaDemanda() {
        return horasaulaDemanda;
    }

    public void setHorasaulaDemanda(String horasaulaDemanda) {
        this.horasaulaDemanda = horasaulaDemanda;
    }

    public String getValidadeDemanda() {
        return validadeDemanda;
    }

    public void setValidadeDemanda(String validadeDemanda) {
        this.validadeDemanda = validadeDemanda;
    }

    public String getTurnoDemanda() {
        return turnoDemanda;
    }

    public void setTurnoDemanda(String turnoDemanda) {
        this.turnoDemanda = turnoDemanda;
    }

    public String getStatusDemanda() {
        return statusDemanda;
    }

    public void setStatusDemanda(String statusDemanda) {
        this.statusDemanda = statusDemanda;
    }

    public Categoria getCatDemanda() {
        return catDemanda;
    }

    public void setCatDemanda(Categoria catDemanda) {
        this.catDemanda = catDemanda;
    }

    public EnderecoDemanda getLocalDemanda() {
        return localDemanda;
    }

    public void setLocalDemanda(EnderecoDemanda localDemanda) {
        this.localDemanda = localDemanda;
    }

    public Date getDataDemanda() {
        return dataDemanda;
    }

    public void setDataDemanda(Date dataDemanda) {
        this.dataDemanda = dataDemanda;
    }

    public Date getInicioDemanda() {
        return inicioDemanda;
    }

    public void setInicioDemanda(Date inicioDemanda) {
        this.inicioDemanda = inicioDemanda;
    }

    public Date getExpiraDemanda() {
        return expiraDemanda;
    }

    public void setExpiraDemanda(Date expiraDemanda) {
        this.expiraDemanda = expiraDemanda;
    }

    public List<Oferta> getOfertas() {
        return ofertas;
    }

    public void setOfertas(List<Oferta> ofertas) {
        this.ofertas = ofertas;
    }

    public Boolean getEncerrada() {
        return encerrada;
    }

    public void setEncerrada(Boolean encerrada) {
        this.encerrada = encerrada;
    }

    public void encerra() {
        encerrada = true;
    }

    public Boolean isEncerrado() {
        return encerrada;
    }

    public void salvaDemandaDB(DatabaseReference.CompletionListener... completionListener) {
        DatabaseReference firebase = FirebaseDB.getFirebase().child("demandas").child(getCodDemanda());
        if (completionListener.length == 0) {
            firebase.setValue(this);
        } else {
            firebase.setValue(this, completionListener[0]);
        }
    }

}
