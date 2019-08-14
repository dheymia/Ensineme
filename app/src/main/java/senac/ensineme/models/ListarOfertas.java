package senac.ensineme.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ListarOfertas {

    private Double maior = Double.NEGATIVE_INFINITY;

    private Double menor = Double.POSITIVE_INFINITY;

    private List<Oferta> menoresOfertas;

    public void listar(Demanda demanda) throws DemandaException {
        if (demanda.getOfertas().size() == 0) {
            throw new DemandaException("Nao existe oferta");
        }
        for (Oferta oferta: demanda.getOfertas ()) {
            if (oferta.getValorOferta() > maior) {
                maior = oferta.getValorOferta();
            }
            if (oferta.getValorOferta() < menor) {
                menor = oferta.getValorOferta();
            }
        }
        listarMenores(demanda);
    }

    private void listarMenores(Demanda demanda) {
        menoresOfertas = new ArrayList<Oferta>(demanda.getOfertas());
        Collections.sort(menoresOfertas, new Comparator<Oferta>() {
            @Override
            public int compare(Oferta o1, Oferta o2) {
                return o2.getValorOferta().compareTo(o1.getValorOferta());
            }
        });
        menoresOfertas = menoresOfertas.subList(0,menoresOfertas.size() >= 5? 5: menoresOfertas.size());
    }

    public Double getMenorOferta() {
        return menor;
    }

    public Double getMaiorOferta() {
        return maior;
    }

    public List<Oferta> getMenoresOfertas() {
        return menoresOfertas;
    }
}
