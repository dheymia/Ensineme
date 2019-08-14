package senac.ensineme.models;

import java.util.Date;

public class Oferta {
    private Usuario professor;
    private String codOferta;
    private Double valorOferta;
    private Date dataOferta;

    public Oferta(Usuario professor, Double valorOferta) {
        this.professor = professor;
        this.valorOferta = valorOferta;
        this.dataOferta = new Date();
    }

    public String getCodOferta() {
        return codOferta;
    }

    public void setCodOferta(String codOferta) {
        this.codOferta = codOferta;
    }

    public Usuario getProfessor() {
        return professor;
    }

    public void setProfessor(Usuario professor) {
        this.professor = professor;
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
        if (professor != null) {
            builder.append("Usuário Professor=");
            builder.append(professor);
            builder.append(", ");
        }
        if (valorOferta != null) {
            builder.append("Valor da Oferta=");
            builder.append(valorOferta);
            builder.append(", ");
        }
        if (dataOferta != null) {
            builder.append("Data da Oferta=");
            builder.append(dataOferta);
            builder.append(", ");
        }
        builder.append("]");
        return builder.toString();
    }
}
