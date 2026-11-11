package missao.demo.dto.municipio;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import missao.demo.model.Municipio;

public record MunicipioRequestDTO(
        @NotNull(message = "Código do município não pode estar vazio") Integer codTse,
        @NotBlank(message = "Nome do município não pode estar vazio") String nome

){
 public Municipio toEntity(){
     Municipio municipio = new Municipio();
     municipio.setCodTse(this.codTse);
     municipio.setNome(this.nome);

        return municipio;
    }
}
