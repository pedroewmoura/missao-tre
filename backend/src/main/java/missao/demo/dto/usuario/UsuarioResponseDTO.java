package missao.demo.dto.usuario;

public record UsuarioResponseDTO(
        String cpf,
        String nome,
        String email
) {
    public static UsuarioResponseDTO fromEntity(missao.demo.model.Usuario entity) {
        if (entity == null) return null;
        return new UsuarioResponseDTO(
                entity.getCpf(),
                entity.getNome(),
                entity.getEmail()
        );
    }
}
