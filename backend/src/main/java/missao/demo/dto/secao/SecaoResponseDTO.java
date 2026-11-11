package missao.demo.dto.secao;
import  missao.demo.model.Secao;

public record SecaoResponseDTO(
        Long id,
        Integer numero,
        Integer zonaNumero,
        Integer municipioCodTse,
        String municipioNome,
        Integer poloNumero
) {
    public static SecaoResponseDTO fromEntity(Secao entity) {
        if (entity == null) return null;
        return new SecaoResponseDTO(
                entity.getId(),
                entity.getNumero(),
                entity.getZona() != null ? entity.getZona().getNumero() : null,
                entity.getMunicipio() != null ? entity.getMunicipio().getCodTse() : null,
                entity.getMunicipio() != null ? entity.getMunicipio().getNome() : null,
                entity.getPolo() != null ? entity.getPolo().getNumero() : null
        );
    }
}
