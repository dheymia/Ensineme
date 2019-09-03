package senac.ensineme.models;

import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

import senac.ensineme.R;

public class Categoria {
    DatabaseReference firebase;
    private String codigo;
    private String nome;
    private int imagem;

    public Categoria() {
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String categoria) {
        this.nome = categoria;
    }

    public int getImagem() {
        return imagem;
    }

    public void setImagem(int imagem) {
        this.imagem = imagem;
    }

    public int Foto() {
        switch (nome){
            case "Dança":
                imagem = R.drawable.danca;
                break;
            case "Esporte":
                imagem = R.drawable.esporte;
                break;
            case "Culinária":
                imagem = R.drawable.culinaria;
                break;
            case "Instrumentos musicais":
                imagem = R.drawable.instrumentos;
                break;
            case "Disciplinas escolares":
                imagem = R.drawable.disciplinas;
                break;

        }
        return imagem;
    }

    public void salvaCategoriaDB(DatabaseReference.CompletionListener... completionListener) {
        firebase = FirebaseDB.getFirebase().child("categorias").child(getCodigo());
        if (completionListener.length == 0) {
            firebase.setValue(this);
        } else {
            firebase.setValue(this, completionListener[0]);
        }

    }

    public void atualizacategoriaDB(DatabaseReference.CompletionListener... completionListener){
        firebase = FirebaseDB.getFirebase().child("categorias").child(getCodigo());
        Map<String, Object> categoriaUpdates = new HashMap<>();
        categoriaUpdates.put("nome", getNome());
        categoriaUpdates.put("codigo", getCodigo());
        firebase.updateChildren(categoriaUpdates, completionListener[0]);
    }

    @Override
    public String toString()
    {
        return nome;
    }

}


