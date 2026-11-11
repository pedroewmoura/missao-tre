package missao.demo.dto.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;



public record UsuarioRequestDTO(
        @NotBlank(message = "O CPF é obrigatório")
        String cpf, // Alinhado com o frontend (string)

        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "Formato de e-mail inválido")
        String email
) {}