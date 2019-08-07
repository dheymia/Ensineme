package senac.ensineme.models;

import java.util.Date;

public class Oferta {
    private String codOferta;
    private String usuarioOferta;
    private String demandaOferta;
    private Double valorOferta;
    private Date dataOferta;

    public Oferta(String usuarioOferta, String demandaOferta, Double valorOferta) {
        this.usuarioOferta = usuarioOferta;
        this.demandaOferta = demandaOferta;
        this.valorOferta = valorOferta;
        this.dataOferta = new Date();
    }

    public String getCodOferta() {
        return codOferta;
    }

    public void setCodOferta(String codOferta) {
        this.codOferta = codOferta;
    }

    public String getUsuarioOferta() {
        return usuarioOferta;
    }

    public void setUsuarioOferta(String usuarioOferta) {
        this.usuarioOferta = usuarioOferta;
    }

    public String getDemandaOferta() {
        return demandaOferta;
    }

    public void setDemandaOferta(String demandaOferta) {
        this.demandaOferta = demandaOferta;
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
        if (usuarioOferta != null) {
            builder.append("Professor=");
            builder.append(usuarioOferta);
            builder.append(", ");
        }
        if (valorOferta != null) {
            builder.append("Valor da Oferta=");
            builder.append(valorOferta);
            builder.append(", ");
        }
        if (demandaOferta != null) {
            builder.append("Demanda=");
            builder.append(demandaOferta);
        }
        builder.append("]");
        return builder.toString();
    }
}
