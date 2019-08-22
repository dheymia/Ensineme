package senac.ensineme.models;

import com.google.firebase.database.DatabaseReference;

import java.util.Date;

public class Oferta {
    private String codOferta, professorOferta;
    private Double valorOferta;
    private Date dataOferta;

    public Oferta(Usuario professor, Double valorOferta) {
        this.professorOferta = professorOferta;
        this.valorOferta = valorOferta;
        this.dataOferta = new Date();
    }

    public Oferta() {
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

    public Double getValorOferta() {
        return valorOferta;
    }

    public void setValorOferta(Double valorOferta) {
        this.valorOferta = valorOferta;
    }

    public Date getDataOferta() {
        return dataOferta;
    }

    public void setDataOferta(Date dataOferta) {
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