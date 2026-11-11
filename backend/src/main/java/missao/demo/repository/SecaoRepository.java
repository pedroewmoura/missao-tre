
package missao.demo.repository;

import missao.demo.model.Secao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecaoRepository
        extends JpaRepository<Secao, Long> {

    boolean existsByNumeroAndZona_NumeroAndMunicipio_CodTse(
            Integer numero,
            Integer zonaNumero,
            Integer municipioCodTse
    );
}
