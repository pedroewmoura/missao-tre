package missao.demo.dto.municipio;

import missao.demo.dto.polo.PoloResponseDTO;
import missao.demo.model.Municipio;

public record MunicipioResponseDTO(
        long codTse,
        String nome,
        Integer poloNumero,
        PoloResponseDTO polo
) {
    public static MunicipioResponseDTO fromEntity(Municipio entity) {
        if (entity == null) return null;
        return new MunicipioResponseDTO(
                entity.getCodTse(),
                entity.getNome(),
                entity.getPolo() != null ? entity.getPolo().getNumero() : null,
                entity.getPolo() != null ? PoloResponseDTO.fromEntity(entity.getPolo()) : null
        );
    }
}
