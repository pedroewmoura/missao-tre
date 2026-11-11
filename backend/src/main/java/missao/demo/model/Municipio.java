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
public class Municipio {

    @Id
    private Integer codTse;

    private String nome;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "polo_numero")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "municipios"})
    private Polo polo;

    @ManyToMany(mappedBy = "municipios")
    @JsonIgnore
    private List<Zona> zonas = new ArrayList<>();
}
