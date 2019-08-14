package senac.ensineme.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AvaliaOferta {

    private Double maiorDeTodos = Double.NEGATIVE_INFINITY;

    private Double menorDeTodos = Double.POSITIVE_INFINITY;

    private List<Oferta> menoresOfertas;

    public void avalia(Demanda demanda) throws DemandaException {
        if (demanda.getOfertas().size() == 0) {
            throw new DemandaException("Nao existe oferta");
        }
        for (Oferta oferta: demanda.getOfertas ()) {
            if (oferta.getValorOferta() > maiorDeTodos) {
                maiorDeTodos = oferta.getValorOferta();
            }
            if (oferta.getValorOferta() < menorDeTodos) {
                menorDeTodos = oferta.getValorOferta();
            }
        }
        pegaMenores(demanda);
    }

    private void pegaMenores(Demanda demanda) {
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
        return menorDeTodos;
    }

    public Double getMaiorOferta() {
        return maiorDeTodos;
    }

    public List<Oferta> getMenoresOfertas() {
        return menoresOfertas;
    }
}
