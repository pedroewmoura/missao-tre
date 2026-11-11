package missao.demo.dto.zona;

import missao.demo.model.Municipio;
import missao.demo.model.Zona;

import java.util.Collections;
import java.util.List;

public record ZonaResponseDTO(
        Integer numero,
        String municipioSede,
        List<Integer> municipiosCodTse
) {
    public static ZonaResponseDTO fromEntity(Zona entity) {
        if (entity == null) return null;
        return new ZonaResponseDTO(
                entity.getNumero(),
                entity.getMunicipioSede(),
                entity.getMunicipios() != null
                        ? entity.getMunicipios().stream().map(Municipio::getCodTse).toList()
                        : Collections.emptyList()
        );
    }
}
