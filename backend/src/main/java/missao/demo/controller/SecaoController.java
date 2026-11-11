package missao.demo.controller;

import missao.demo.dto.secao.SecaoResponseDTO;
import missao.demo.repository.SecaoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/secao")

public class SecaoController {


    private final SecaoRepository secaoRepository;

    public SecaoController(SecaoRepository secaoRepository) {
        this.secaoRepository = secaoRepository;
    }



    @GetMapping
    public ResponseEntity<List<SecaoResponseDTO>> listarTodas() {
        List<SecaoResponseDTO> secoes = secaoRepository.findAll()
                .stream()
                .map(SecaoResponseDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(secoes);

    }

}
