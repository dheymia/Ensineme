package senac.ensineme.models;

public class Categoria {
    private String codCategoria;
    private String nomeCategoria;
    private String imgCategoria;

    public Categoria(String codCategoria, String nomeCategoria, String imgCategoria) {
        this.codCategoria = codCategoria;
        this.nomeCategoria = nomeCategoria;
        this.imgCategoria = imgCategoria;
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

    public String getImgCategoria() {
        return imgCategoria;
    }

    public void setImgCategoria(String imgCategoria) {
        this.imgCategoria = imgCategoria;
    }
}


