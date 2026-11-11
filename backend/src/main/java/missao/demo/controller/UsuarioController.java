package missao.demo.controller;

import missao.demo.dto.usuario.UsuarioRequestDTO;
import missao.demo.dto.usuario.UsuarioResponseDTO;
import missao.demo.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listar() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    @GetMapping("/{cpf}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorCpf(@PathVariable String cpf) {
        return ResponseEntity.ok(usuarioService.buscarPorCpf(cpf));
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criar(@Valid @RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO response = usuarioService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}