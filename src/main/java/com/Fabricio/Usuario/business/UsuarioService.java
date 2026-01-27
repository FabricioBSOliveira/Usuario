package com.Fabricio.Usuario.business;

import com.Fabricio.Usuario.business.converter.UsuarioConverter;
import com.Fabricio.Usuario.business.dto.UsuarioDTO;
import com.Fabricio.Usuario.infrastructure.entity.Usuario;
import com.Fabricio.Usuario.infrastructure.exception.ConflictException;
import com.Fabricio.Usuario.infrastructure.exception.ResourceNotFoundException;
import com.Fabricio.Usuario.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;
    private final PasswordEncoder passwordEncoder;

    public UsuarioDTO salvaUsuario(UsuarioDTO usuarioDTO){
        emailExiste(usuarioDTO.getEmail());
        usuarioDTO.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO); //Recebe usuario DTO do front
        usuario = usuarioRepository.save(usuario);//Transformar em Entity para poder salvar e salva no DB
        return usuarioConverter.paraUsuarioDTO(usuario); //Converte para Usuario DTO

    }

    public void emailExiste(String email){
        try {
            boolean existe = verificaEmailExistente(email);
            if (existe){
                throw new ConflictException("Email ja Cadastrado " + email);
            }
        }catch (ConflictException e){
            throw new ConflictException("Email ja cadastrado", e.getCause());
        }
    }

    public boolean verificaEmailExistente(String email){
        return usuarioRepository.existsByEmail(email);
    }

    public Usuario buscarUsuarioPorEmail(String email){
        return usuarioRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("Email nao encontrado" + email));

    }

    public void deletaUsuarioPorEmail(String email){
        usuarioRepository.deleteByEmail(email);

    }


}
