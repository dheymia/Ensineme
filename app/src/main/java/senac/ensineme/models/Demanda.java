package senac.ensineme.models;

import java.util.Date;

public class Demanda {

    private String codDemanda;
    private String catDemanda;
    private String descDemanda;
    private String horasaulaDemanda;
    private String turnoDemanda;
    private Date inicioDemanda;
    private String localDemanda;

    public Demanda(String codDemanda, String catDemanda, String subcatDemanda, String descDemanda, String horasaulaDemanda, String turnoDemanda, String localDemanda) {
        this.codDemanda = codDemanda;
        this.catDemanda = catDemanda;
        this.descDemanda = descDemanda;
        this.horasaulaDemanda = horasaulaDemanda;
        this.turnoDemanda = turnoDemanda;
        this.localDemanda = localDemanda;
        this.inicioDemanda = new Date();
    }

    public String getCodDemanda() {
        return codDemanda;
    }

    public void setCodDemanda(String codDemanda) {
        this.codDemanda = codDemanda;
    }

    public String getCatDemanda() {
        return catDemanda;
    }

    public void setCatDemanda(String catDemanda) {
        this.catDemanda = catDemanda;
    }

    public String getDescDemanda() {
        return descDemanda;
    }

    public void setDescDemanda(String descDemanda) {
        this.descDemanda = descDemanda;
    }

    public String getHorasaulaDemanda() {
        return horasaulaDemanda;
    }

    public void setHorasaulaDemanda(String horasaulaDemanda) {
        this.horasaulaDemanda = horasaulaDemanda;
    }

    public String getTurnoDemanda() {
        return turnoDemanda;
    }

    public void setTurnoDemanda(String turnoDemanda) {
        this.turnoDemanda = turnoDemanda;
    }

    public Date getInicioDemanda() {
        return inicioDemanda;
    }

    public void setInicioDemanda(Date inicioDemanda) {
        this.inicioDemanda = inicioDemanda;
    }

    public String getLocalDemanda() {
        return localDemanda;
    }

    public void setLocalDemanda(String localDemanda) {
        this.localDemanda = localDemanda;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Demanda Ensine-me [");
        if (codDemanda != null) {
            builder.append("Código da Demanda: ");
            builder.append(codDemanda);
            builder.append(", ");
        }
        if (catDemanda != null) {
            builder.append("Categoria da Demanda: ");
            builder.append(catDemanda);
            builder.append(", ");
        }
        if (descDemanda != null) {
            builder.append("Descrição da Demanda:");
            builder.append(codDemanda);
            builder.append(", ");
        }
        if (horasaulaDemanda != null) {
            builder.append("Carga Horária da Demanda = ");
            builder.append(horasaulaDemanda);
            builder.append(", ");
        }
        if (turnoDemanda != null) {
            builder.append("Turno da Demanda = ");
            builder.append(turnoDemanda);
            builder.append(", ");
        }
        if (inicioDemanda != null) {
            builder.append("Data de Início=");
            builder.append(inicioDemanda);
        }
        if (localDemanda != null) {
            builder.append("Local da Demanda=");
            builder.append(localDemanda);
        }

        builder.append("]");
        return builder.toString();
    }
}
