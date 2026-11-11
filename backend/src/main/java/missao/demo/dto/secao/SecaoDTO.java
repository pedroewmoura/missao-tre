// backend/src/main/java/missao/demo/dto/secao/SecaoDto.java
package missao.demo.dto.secao;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import missao.demo.model.Municipio;
import missao.demo.model.Secao;
import missao.demo.model.Zona;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecaoDTO {

    private Long id;
    private Integer numero;
    private Zona zona;
    private Municipio municipio;


    public SecaoDTO(Secao s) {
        if (s != null) {
            this.id        = s.getId();
            this.numero    = s.getNumero();
            this.zona      = s.getZona();
            this.municipio = s.getMunicipio();
        }
    }
}