package missao.demo.controller;

import missao.demo.dto.municipio.MunicipioResponseDTO;
import missao.demo.repository.MunicipioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/municipio")
public class MunicipioController {

    private final MunicipioRepository municipioRepository;

    public MunicipioController(MunicipioRepository municipioRepository) {
        this.municipioRepository = municipioRepository;
    }

    @GetMapping
    public ResponseEntity<List<MunicipioResponseDTO>> municipio() {
        List<MunicipioResponseDTO> lista = municipioRepository.findAll()
                .stream()
                .map(MunicipioResponseDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(lista);
    }
}
