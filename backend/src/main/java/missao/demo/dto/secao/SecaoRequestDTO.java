package missao.demo.dto.secao;
import missao.demo.model.Secao;
import missao.demo.model.Zona;
import missao.demo.model.Municipio;
import missao.demo.model.Polo;
import jakarta.validation.constraints.NotNull;


public record SecaoRequestDTO(
        @NotNull(message = "Número da seção não pode estar vazio") Integer numero,
        @NotNull(message = "Número da zona não pode estar vazia") Integer zonaNumero,
        @NotNull(message = "Código do município não pode estar vazio") Integer municipioCodTse) {

    public Secao toEntity(Zona zona, Municipio municipio, Polo polo ){
        Secao secao = new Secao();
        secao.setNumero(this.numero);
        secao.setZona(zona);
        secao.setMunicipio(municipio);
        secao.setPolo(polo);
        return secao;

    }
}


