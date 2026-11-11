
package missao.demo.repository;

import missao.demo.model.Polo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PoloRepository
        extends JpaRepository<Polo, Integer> {
}
