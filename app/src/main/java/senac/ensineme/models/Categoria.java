package senac.ensineme.models;

import java.util.ArrayList;
import java.util.List;

import senac.ensineme.R;

public class Categoria {
    private String codCategoria;
    private String nomeCategoria;
    private List <Categoria> categorias;
    private int imgCategoria;

    public Categoria(String codCategoria, String nomeCategoria) {
        this.codCategoria = codCategoria;
        this.nomeCategoria = nomeCategoria;
    }

    public String getCodCategoria() {
        return codCategoria;
    }

    public void setCodCategoria(String codCategoria) {
        this.codCategoria = codCategoria;
    }

    public String getNomeCategoria() {
        return nomeCategoria;
    }

    public void setNomeCategoria(String nomeCategoria) {
        this.nomeCategoria = nomeCategoria;
    }

    public int getFoto() {
        switch (nomeCategoria){
            case "Dança":
                imgCategoria = R.drawable.danca;
                break;
            case "Esporte":
                imgCategoria = R.drawable.esporte;
                break;
            case "Culinária":
                imgCategoria = R.drawable.culinaria;
                break;
            case "Instrumentos Musicais":
                imgCategoria = R.drawable.instrumentos;
                break;
            case "Disciplinas Escolares":
                imgCategoria = R.drawable.disciplinas;
                break;

        }
        return imgCategoria;
    }

}


