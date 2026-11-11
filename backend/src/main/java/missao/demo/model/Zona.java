package missao.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Zona {

    @Id
    private Integer numero;

    private String municipioSede;

    @ManyToMany
    @JoinTable(
            name = "zona_municipio",
            joinColumns = @JoinColumn(name = "zona_numero"),
            inverseJoinColumns = @JoinColumn(name = "municipio_cod_tse")
    )
    @JsonIgnoreProperties({"zonas", "hibernateLazyInitializer", "handler"})
    private List<Municipio> municipios = new ArrayList<>();

    @OneToMany(mappedBy = "zona")
    @JsonIgnore
    private List<Secao> secoes = new ArrayList<>();
}
