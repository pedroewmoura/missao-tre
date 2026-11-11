package missao.demo.repository;

import missao.demo.model.Zona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ZonaRepository extends JpaRepository<Zona, Integer> {
    @Query("SELECT z FROM Zona z LEFT JOIN FETCH z.municipios")
    List<Zona> findAllWithMunicipios();
}
