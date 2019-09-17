package senac.ensineme.models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.util.Map;

public class Usuario {
    private String id;
    private String nome;
    private String sobrenome;
    private String nomecompleto;
    private String email;
    private String celular;
    private String password;
    private String tipo;
    private String inscricao;

    public String getInscricao() {
        return inscricao;
    }

    public void setInscricao(String inscricao) {
        this.inscricao = inscricao;
    }

    public String getNomecompleto() {
        return nomecompleto;
    }

    public void setNomecompleto(String nomecompleto) {
        this.nomecompleto = nomecompleto;
    }

    public Usuario() {
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    private void setNameInMap(Map<String, Object> map) {
        if (getNome() != null) {
            map.put("nome", getNome());
        }
    }

    public void setNomeIfNull(String nome) {
        if (this.nome == null) {
            this.nome = nome;
        }
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    private void setCelularInMap(Map<String, Object> map) {
        if (getCelular() != null) {
            map.put("celular", getCelular());
        }
    }

    public void setCelularIfNull(String celular) {
        if (this.celular == null) {
            this.celular = celular;
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private void setEmailInMap(Map<String, Object> map) {
        if (getEmail() != null) {
            map.put("email", getEmail());
        }
    }

    public void setEmailIfNull(String email) {
        if (this.email == null) {
            this.email = email;
        }
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    private void setTipoInMap(Map<String, Object> map) {
        if (getTipo() != null) {
            map.put("tipo", getTipo());
        }
    }

    public void setTipoIfNull(String tipo) {
        if (this.tipo == null) {
            this.tipo = tipo;
        }
    }

    @Exclude
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void salvaUsuarioDB(DatabaseReference.CompletionListener... completionListener) {
        DatabaseReference firebase = FirebaseDB.getFirebase().child("usuarios").child(getId());
        if (completionListener.length == 0) {
            firebase.setValue(this);
        } else {
            firebase.setValue(this, completionListener[0]);
        }
    }


}