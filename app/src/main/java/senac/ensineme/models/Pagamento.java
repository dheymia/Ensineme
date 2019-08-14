package senac.ensineme.models;

import java.util.Calendar;

public class Pagamento {

        private Double valor;
        private Calendar data;

        public Pagamento(Double valor, Calendar data) {
            this.valor = valor;
            this.data = data;
        }

        public Double getValor() {
            return valor;
        }

        public void setValor(Double valor) {
            this.valor = valor;
        }

        public Calendar getData() {
            return data;
        }

        public void setData(Calendar data) {
            this.data = data;
        }

}
