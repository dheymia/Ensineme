package senac.ensineme.models;

public class EnderecoDemanda {

        private String ibge, gia, cep, logradouro, bairro, complemento, localidade, uf, unidade;

        public EnderecoDemanda() {
        }

        public EnderecoDemanda(String ibge, String gia, String cep, String logradouro, String bairro, String complemento, String localidade, String uf, String unidade) {
            this.ibge = ibge;
            this.gia = gia;
            this.cep = cep;
            this.logradouro = logradouro;
            this.bairro = bairro;
            this.complemento = complemento;
            this.localidade = localidade;
            this.uf = uf;
            this.unidade = unidade;
        }

        public String getIbge() {
            return ibge;
        }

        public void setIbge(String ibge) {
            this.ibge = ibge;
        }

        public String getGia() {
            return gia;
        }

        public void setGia(String gia) {
            this.gia = gia;
        }

        public String getCep() {
            return cep;
        }

        public void setCep(String cep) {
            this.cep = cep;
        }

        public String getLogradouro() {
            return logradouro;
        }

        public void setLogradouro(String logradouro) {
            this.logradouro = logradouro;
        }

        public String getBairro() {
            return bairro;
        }

        public void setBairro(String bairro) {
            this.bairro = bairro;
        }

        public String getComplemento() {
            return complemento;
        }

        public void setComplemento(String complemento) {
            this.complemento = complemento;
        }

        public String getLocalidade() {
            return localidade;
        }

        public void setLocalidade(String localidade) {
            this.localidade = localidade;
        }

        public String getUf() {
            return uf;
        }

        public void setUf(String uf) {
            this.uf = uf;
        }

        public String getUnidade() {
            return unidade;
        }

        public void setUnidade(String unidade) {
            this.unidade = unidade;
        }
}
