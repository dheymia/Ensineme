package senac.ensineme.models;

import com.google.firebase.database.DatabaseReference;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Oferta {
    private DatabaseReference firebase, firebaseProf,firebaseAlu, firebaseCat, firebaseDem;
    private String professor, aluno, codDemanda, codOferta, codCategoria, valorOferta, statusOferta, dataOferta, comentarioOferta;

    public Oferta() {
    }

    public String getAluno() {
        return aluno;
    }

    public void setAluno(String aluno) {
        this.aluno = aluno;
    }

    public String getProfessor() {
        return professor;
    }

    public void setProfessor(String professor) {
        this.professor = professor;
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

    public String getCodCategoria() {
        return codCategoria;
    }

    public void setCodCategoria(String codCategoria) {
        this.codCategoria = codCategoria;
    }

    public String getValorOferta() {
        return valorOferta;
    }

    public void setValorOferta(String valorOferta) {
        this.valorOferta = valorOferta;
    }

    public String getStatusOferta() {
        return statusOferta;
    }

    public void setStatusOferta(String statusOferta) {
        this.statusOferta = statusOferta;
    }

    public String getDataOferta() {
        return dataOferta;
    }

    public void setDataOferta(String dataOferta) {
        this.dataOferta = dataOferta;
    }

    public String getComentarioOferta() {
        return comentarioOferta;
    }

    public void setComentarioOferta(String comentarioOferta) {
        this.comentarioOferta = comentarioOferta;
    }

    public void salvaOfertaDB(DatabaseReference.CompletionListener... completionListener) {
        firebase = FirebaseDB.getFirebase().child("propostas").child(getCodOferta());
        firebaseDem = FirebaseDB.getFirebase().child("demandas").child(getCodDemanda()).child("propostas").child(getCodOferta());
        firebaseProf = FirebaseDB.getFirebase().child("usuarios").child(professor).child("propostas").child(getCodOferta());
        firebaseAlu = FirebaseDB.getFirebase().child("usuarios").child(aluno).child("demandas").child(getCodDemanda()).child("propostas").child(getCodOferta());
        firebaseCat = FirebaseDB.getFirebase().child("categorias").child(codCategoria).child("demandas").child(getCodDemanda()).child("propostas").child(getCodOferta());

        if (completionListener.length == 0) {
            firebase.setValue(this);
        } else {
            firebase.setValue(this, completionListener[0]);

            Map<String, Object> ofertaProfUpdates = new HashMap<>();
            ofertaProfUpdates.put("aluno", getAluno());
            ofertaProfUpdates.put("codDemanda", getCodDemanda());
            ofertaProfUpdates.put("codOferta",getCodOferta());
            ofertaProfUpdates.put("codCategoria", getCodCategoria());
            ofertaProfUpdates.put("valorOferta", getValorOferta());
            ofertaProfUpdates.put("statusOferta", getStatusOferta());
            ofertaProfUpdates.put("dataOferta", getDataOferta());
            ofertaProfUpdates.put("comentarioOferta", getComentarioOferta());
            firebaseProf.setValue(ofertaProfUpdates, completionListener[0]);

            Map<String, Object> ofertaAluUpdates = new HashMap<>();
            ofertaAluUpdates.put("professor", getProfessor());
            ofertaAluUpdates.put("codOferta",getCodOferta());
            firebaseAlu.setValue(ofertaAluUpdates, completionListener[0]);

            Map<String, Object> ofertaDemUpdates = new HashMap<>();
            ofertaDemUpdates.put("professor", getProfessor());
            ofertaDemUpdates.put("codOferta",getCodOferta());
            ofertaDemUpdates.put("valorOferta", getValorOferta());
            ofertaDemUpdates.put("statusOferta", getStatusOferta());
            ofertaDemUpdates.put("dataOferta", getDataOferta());
            ofertaDemUpdates.put("comentarioOferta", getComentarioOferta());
            firebaseDem.setValue(ofertaDemUpdates, completionListener[0]);

            Map<String, Object> ofertaCatUpdates = new HashMap<>();
            ofertaCatUpdates.put("professor", getProfessor());
            ofertaCatUpdates.put("codOferta",getCodOferta());
            firebaseCat.setValue(ofertaCatUpdates, completionListener[0]);


        }
    }

    public void atualizaofertaDB(DatabaseReference.CompletionListener... completionListener) {
        firebase = FirebaseDB.getFirebase().child("propostas").child(getCodOferta());
        firebaseDem = FirebaseDB.getFirebase().child("demandas").child(getCodDemanda()).child("propostas").child(getCodOferta());
        firebaseProf =FirebaseDB.getFirebase().child("usuarios").child(professor).child("propostas").child(getCodOferta());
        firebaseAlu =FirebaseDB.getFirebase().child("usuarios").child(aluno).child("demandas").child(getCodDemanda()).child("propostas").child(getCodOferta());
        firebaseCat = FirebaseDB.getFirebase().child("categorias").child(codCategoria).child("demandas").child(getCodDemanda()).child("propostas").child(getCodOferta());

        Map<String, Object> ofertaUpdates = new HashMap<>();
        ofertaUpdates.put("aluno", getAluno());
        ofertaUpdates.put("professor", getProfessor());
        ofertaUpdates.put("codDemanda", getCodDemanda());
        ofertaUpdates.put("codOferta",getCodOferta());
        ofertaUpdates.put("codCategoria", getCodCategoria());
        ofertaUpdates.put("valorOferta", getValorOferta());
        ofertaUpdates.put("statusOferta", getStatusOferta());
        ofertaUpdates.put("dataOferta", getDataOferta());
        ofertaUpdates.put("comentarioOferta", getComentarioOferta());
        firebase.updateChildren(ofertaUpdates, completionListener[0]);

        Map<String, Object> ofertaProfUpdates = new HashMap<>();
        ofertaProfUpdates.put("aluno", getAluno());
        ofertaProfUpdates.put("codDemanda", getCodDemanda());
        ofertaProfUpdates.put("codOferta",getCodOferta());
        ofertaProfUpdates.put("codCategoria", getCodCategoria());
        ofertaProfUpdates.put("valorOferta", getValorOferta());
        ofertaProfUpdates.put("statusOferta", getStatusOferta());
        ofertaProfUpdates.put("dataOferta", getDataOferta());
        ofertaProfUpdates.put("comentarioOferta", getComentarioOferta());
        firebaseProf.updateChildren(ofertaProfUpdates, completionListener[0]);

        Map<String, Object> ofertaAluUpdates = new HashMap<>();
        ofertaAluUpdates.put("professor", getProfessor());
        ofertaAluUpdates.put("codOferta",getCodOferta());
        firebaseAlu.updateChildren(ofertaAluUpdates, completionListener[0]);

        Map<String, Object> ofertaDemUpdates = new HashMap<>();
        ofertaDemUpdates.put("professor", getProfessor());
        ofertaDemUpdates.put("codOferta",getCodOferta());
        ofertaDemUpdates.put("valorOferta", getValorOferta());
        ofertaDemUpdates.put("statusOferta", getStatusOferta());
        ofertaDemUpdates.put("dataOferta", getDataOferta());
        ofertaDemUpdates.put("comentarioOferta", getComentarioOferta());
        firebaseDem.updateChildren(ofertaDemUpdates, completionListener[0]);

        Map<String, Object> ofertaCatUpdates = new HashMap<>();
        ofertaCatUpdates.put("professor", getProfessor());
        ofertaCatUpdates.put("codOferta",getCodOferta());
        firebaseCat.updateChildren(ofertaCatUpdates, completionListener[0]);


    }

    public void excluiofertaDB(DatabaseReference.CompletionListener... completionListener) {
        firebase = FirebaseDB.getFirebase().child("propostas").child(getCodOferta());
        firebaseDem = FirebaseDB.getFirebase().child("demandas").child(getCodDemanda()).child("propostas").child(getCodOferta());
        firebaseProf =FirebaseDB.getFirebase().child("usuarios").child(professor).child("propostas").child(getCodOferta());
        firebaseAlu =FirebaseDB.getFirebase().child("usuarios").child(aluno).child("demandas").child(getCodDemanda()).child("propostas").child(getCodOferta());
        firebaseCat = FirebaseDB.getFirebase().child("categorias").child(codCategoria).child("demandas").child(getCodDemanda()).child("propostas").child(getCodOferta());

        if (completionListener.length == 0) {
            firebase.removeValue();
            firebaseDem.removeValue();
            firebaseCat.removeValue();
            firebaseAlu.removeValue();
            firebaseProf.removeValue();
        } else {
            firebase.removeValue((DatabaseReference.CompletionListener) this);
            firebaseDem.removeValue((DatabaseReference.CompletionListener) this);
            firebaseCat.removeValue((DatabaseReference.CompletionListener) this);
            firebaseAlu.removeValue((DatabaseReference.CompletionListener) this);
            firebaseProf.removeValue((DatabaseReference.CompletionListener) this);
        }

    }

    public void atualizaStatusOfertaDB(DatabaseReference.CompletionListener... completionListener) {
        firebase = FirebaseDB.getFirebase().child("propostas").child(getCodOferta());
        firebaseDem = FirebaseDB.getFirebase().child("demandas").child(getCodDemanda()).child("propostas").child(getCodOferta());
        firebaseProf = FirebaseDB.getFirebase().child("usuarios").child(professor).child("propostas").child(getCodOferta());

        Map<String, Object> ofertaUpdates = new HashMap<>();
        ofertaUpdates.put("statusOferta", getStatusOferta());
        firebase.updateChildren(ofertaUpdates, completionListener[0]);

        Map<String, Object> ofertaProfUpdates = new HashMap<>();
        ofertaProfUpdates.put("statusOferta", getStatusOferta());
        firebaseProf.updateChildren(ofertaProfUpdates, completionListener[0]);


        Map<String, Object> ofertaDemUpdates = new HashMap<>();
        ofertaDemUpdates.put("statusOferta", getStatusOferta());
        firebaseDem.updateChildren(ofertaDemUpdates, completionListener[0]);

    }

}