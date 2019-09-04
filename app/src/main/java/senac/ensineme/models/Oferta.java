package senac.ensineme.models;

import com.google.firebase.database.DatabaseReference;

import java.util.Date;

public class Oferta {
    private String codOferta, codDemanda, professorOferta, statusOferta, dataOferta, valorOferta;


    public Oferta(Usuario professor, String valorOferta, String dataOferta) {
        this.professorOferta = professorOferta;
        this.valorOferta = valorOferta;
        this.dataOferta = dataOferta;
    }

    public Oferta() {
    }

    public String getCodDemanda() {
        return codDemanda;
    }

    public void setCodDemanda(String codDemanda) {
        this.codDemanda = codDemanda;
    }

    public String getCodOferta() {
        return codOferta;
    }

    public void setCodOferta(String codOferta) {
        this.codOferta = codOferta;
    }

    public String getProfessorOferta() {
        return professorOferta;
    }

    public void setProfessorOferta(String professorOferta) {
        this.professorOferta = professorOferta;
    }

    public String getStatusOferta() {
        return statusOferta;
    }

    public void setStatusOferta(String statusOferta) {
        this.statusOferta = statusOferta;
    }

    public String getValorOferta() {
        return valorOferta;
    }

    public void setValorOferta(String valorOferta) {
        this.valorOferta = valorOferta;
    }

    public String getDataOferta() {
        return dataOferta;
    }

    public void setDataOferta(String dataOferta) {
        this.dataOferta = dataOferta;
    }

    public void salvaOfertaDB(DatabaseReference.CompletionListener... completionListener) {
        DatabaseReference firebase = FirebaseDB.getFirebase().child("ofertas").child(getCodOferta());
        if (completionListener.length == 0) {
            firebase.setValue(this);
        } else {
            firebase.setValue(this, completionListener[0]);
        }
    }

}