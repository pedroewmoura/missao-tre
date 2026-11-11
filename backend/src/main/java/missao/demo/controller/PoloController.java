package missao.demo.controller;

import missao.demo.dto.polo.PoloResponseDTO;
import missao.demo.repository.PoloRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/polo")
public class PoloController {

    private final PoloRepository poloRepository;

    public PoloController(PoloRepository poloRepository) {
        this.poloRepository = poloRepository;
    }

    @GetMapping
    public ResponseEntity<List<PoloResponseDTO>> polo() {
        List<PoloResponseDTO> lista = poloRepository.findAll()
                .stream()
                .map(PoloResponseDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(lista);
    }
}
