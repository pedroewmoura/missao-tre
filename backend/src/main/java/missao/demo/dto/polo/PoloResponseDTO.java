package missao.demo.dto.polo;
import missao.demo.model.Polo;

public record PoloResponseDTO(
        Integer numero,
        String municipioSede
) {
    public static PoloResponseDTO fromEntity(Polo entity){
        if (entity ==null) return null;
        return new
                PoloResponseDTO(
                        entity.getNumero(),
                        entity.getMunicipioSede()
        );
    }
}
