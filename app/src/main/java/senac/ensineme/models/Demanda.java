package senac.ensineme.models;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Demanda {

    private String aluno, codDemanda, descDemanda, horasaulaDemanda,validadeDemanda, turnoDemanda, localDemanda;
    private Categoria catDemanda;
    private Calendar dataDemanda;
    private Date inicioDemanda;
    private Boolean encerrada;
    private List<Oferta> ofertas;

    public Demanda() {
    }

    public Demanda(String aluno, String codDemanda, Categoria catDemanda, String subcatDemanda, String descDemanda, Calendar dataDemanda, String horasaulaDemanda, String turnoDemanda, String localDemanda, String validadeDemanda) {
        this.aluno = aluno;
        this.codDemanda = codDemanda;
        this.catDemanda = catDemanda;
        this.descDemanda = descDemanda;
        this.dataDemanda = dataDemanda;
        this.horasaulaDemanda = horasaulaDemanda;
        this.turnoDemanda = turnoDemanda;
        this.localDemanda = localDemanda;
        this.inicioDemanda = new Date();
        this.validadeDemanda = validadeDemanda;
        this.ofertas = new ArrayList<Oferta>();
        this.encerrada = false;
    }

    public String getAluno() {
        return aluno;
    }

    public void setAluno(String aluno) {
        this.aluno = aluno;
    }

    public String getCodDemanda() {
        return codDemanda;
    }

    public void setCodDemanda(String codDemanda) {
        this.codDemanda = codDemanda;
    }

    public Categoria getCatDemanda() {
        return catDemanda;
    }

    public void setCatDemanda(Categoria catDemanda) {
        this.catDemanda = catDemanda;
    }

    public String getDescDemanda() {
        return descDemanda;
    }

    public void setDescDemanda(String descDemanda) {
        this.descDemanda = descDemanda;
    }

    public Calendar getDataDemanda() {
        return dataDemanda;
    }

    public void setDataDemanda(Calendar dataDemanda) {
        this.dataDemanda = dataDemanda;
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

    public String getValidadeDemanda() {
        return validadeDemanda;
    }

    public void setValidadeDemanda(String validadeDemanda) {
        this.validadeDemanda = validadeDemanda;
    }

    public List<Oferta> getOfertas() {
        return ofertas;
    }

    public void setOfertas(List<Oferta> ofertas) {
        this.ofertas = ofertas;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Demanda Ensine-me [");
        if (aluno != null) {
            builder.append("Usuário Aluno: ");
            builder.append(aluno);
            builder.append(", ");
        }
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
        if (dataDemanda != null) {
            builder.append("Data da Demanda:");
            builder.append(dataDemanda);
            builder.append(", ");
        }
        if (horasaulaDemanda != null) {
            builder.append("CH da Demanda = ");
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
        if (validadeDemanda != null) {
            builder.append("Dias de validade da demanda=");
            builder.append(validadeDemanda);
        }
        if (localDemanda != null) {
            builder.append("Local da Demanda=");
            builder.append(localDemanda);
        }
        builder.append("]");
        return builder.toString();

    }

    public void propoe(Oferta oferta) {
        if (podeOfertar(oferta.getProfessor())) {
            this.ofertas.add(oferta);
        }
    }

    private boolean podeOfertar(Usuario professor) {
        return !professor.equals(ultimaOferta().getProfessor())
                && qtdOfertas(professor) < 1;
    }
    private int qtdOfertas(Usuario professor) {
        int total = 0;
        for (Oferta ofertaRecebida: ofertas) {
            if (ofertaRecebida.getProfessor().equals(professor)) {
                total++;
            }
        }
        return total;
    }
    private Oferta ultimaOferta() {
        return ofertas.get(ofertas.size() - 1);
    }

    public void encerra() {
        encerrada = true;
    }

    public Boolean isEncerrado() {
        return encerrada;
    }

    public void salvaDemandaDB(DatabaseReference.CompletionListener... completionListener) {
        DatabaseReference firebase = Bibioteca.getFirebase().child("demandas").child(getCodDemanda());
        if (completionListener.length == 0) {
            firebase.setValue(this);
        } else {
            firebase.setValue(this, completionListener[0]);
        }
    }

}
