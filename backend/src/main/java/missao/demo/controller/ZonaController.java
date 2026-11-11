package missao.demo.controller;

import missao.demo.dto.zona.ZonaResponseDTO;
import missao.demo.repository.ZonaRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/zona")
public class ZonaController {

    private final ZonaRepository zonaRepository;

    public ZonaController(ZonaRepository zonaRepository) {
        this.zonaRepository = zonaRepository;
    }

    @GetMapping
    public List<ZonaResponseDTO> listarTodas() {
        return zonaRepository.findAllWithMunicipios()
                .stream()
                .map(ZonaResponseDTO::fromEntity)
                .toList();
    }
}
