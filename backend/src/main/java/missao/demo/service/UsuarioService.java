package missao.demo.service;

import missao.demo.dto.usuario.UsuarioRequestDTO;
import missao.demo.dto.usuario.UsuarioResponseDTO;
import missao.demo.model.Usuario;
import missao.demo.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    public List<UsuarioResponseDTO> listarTodos() {
        return repository.findAll()
                .stream()
                .map(UsuarioResponseDTO::fromEntity)
                .toList();
    }

    public UsuarioResponseDTO buscarPorCpf(String cpf) {
        Usuario entity = repository.findById(cpf)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        return UsuarioResponseDTO.fromEntity(entity);
    }

    @Transactional
    public UsuarioResponseDTO criar(UsuarioRequestDTO dto) {
        Usuario entity = new Usuario();
        entity.setCpf(dto.cpf());
        entity.setNome(dto.nome());
        entity.setEmail(dto.email());

        entity = repository.save(entity);
        return UsuarioResponseDTO.fromEntity(entity);
    }
}