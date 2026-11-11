package missao.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import  com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Secao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer numero;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "zona_numero", nullable = false)
    @JsonIgnoreProperties({"municipios", "secoes"})
    private Zona zona;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "municipio_cod_tse", nullable = false)
    @JsonIgnoreProperties({"zonas", "polo"})
    private Municipio municipio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "polo_numero", nullable = false)
    @JsonIgnoreProperties({"municipios"})
    private Polo polo;
}