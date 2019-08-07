package senac.ensineme.models;

public class Usuario {

    private String codUsuario;
    private String nomeUsuario;
    private String emailUsuario;
    private String foneUsuario;
    private String tipoUsuario;

    public Usuario(String codUsuario, String nomeUsuario, String emailUsuario, String foneUsuario, String tipoUsuario) {
        this.codUsuario = codUsuario;
        this.nomeUsuario = nomeUsuario;
        this.emailUsuario = emailUsuario;
        this.foneUsuario = foneUsuario;
        this.tipoUsuario = tipoUsuario;
    }

    public String getCodUsuario() {
        return codUsuario;
    }

    public void setCodUsuario(String codUsuario) {
        this.codUsuario = codUsuario;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }

    public String getFoneUsuario() {
        return foneUsuario;
    }

    public void setFoneUsuario(String foneUsuario) {
        this.foneUsuario = foneUsuario;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }
}
