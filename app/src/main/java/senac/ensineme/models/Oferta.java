package senac.ensineme.models;

import java.util.Date;

public class Oferta {
    private Usuario usuario;
    private String codOferta;
    private Double valorOferta;
    private Date dataOferta;

    public Oferta(Usuario usuario, Double valorOferta) {
        this.usuario = usuario;
        this.valorOferta = valorOferta;
        this.dataOferta = new Date();
    }

    public String getCodOferta() {
        return codOferta;
    }

    public void setCodOferta(String codOferta) {
        this.codOferta = codOferta;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Oferta [");
        if (codOferta != null) {
            builder.append("Código da Oferta=");
            builder.append(codOferta);
            builder.append(", ");
        }
        if (usuario != null) {
            builder.append("Professor=");
            builder.append(usuario);
            builder.append(", ");
        }
        if (valorOferta != null) {
            builder.append("Valor da Oferta=");
            builder.append(valorOferta);
            builder.append(", ");
        }
        builder.append("]");
        return builder.toString();
    }
}
