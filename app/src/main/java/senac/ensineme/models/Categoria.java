package senac.ensineme.models;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import senac.ensineme.CategoriasActivity;
import senac.ensineme.R;

public class Categoria {
    DatabaseReference firebase;
    private String codigo;
    private String categoria;
    private int imagem;

    public Categoria() {
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getFoto() {
        switch (categoria){
            case "Dança":
                imagem = R.drawable.danca;
                break;
            case "Esporte":
                imagem = R.drawable.esporte;
                break;
            case "Culinária":
                imagem = R.drawable.culinaria;
                break;
            case "Instrumentos Musicais":
                imagem = R.drawable.instrumentos;
                break;
            case "Disciplinas Escolares":
                imagem = R.drawable.disciplinas;
                break;

        }
        return imagem;
    }

    public void salvaCategoriaDB(DatabaseReference.CompletionListener... completionListener) {
        firebase = FirebaseDB.getFirebase().child("demandas").child(getCodigo());
        if (completionListener.length == 0) {
            firebase.setValue(this);
        } else {
            firebase.setValue(this, completionListener[0]);
        }

    }

}


