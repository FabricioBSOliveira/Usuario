package com.Fabricio.Usuario.business;

import com.Fabricio.Usuario.business.converter.UsuarioConverter;
import com.Fabricio.Usuario.business.dto.UsuarioDTO;
import com.Fabricio.Usuario.infrastructure.entity.Usuario;
import com.Fabricio.Usuario.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;

    public UsuarioDTO salvaUsuario(UsuarioDTO usuarioDTO){
        Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO); //Recebe usuario DTO do front
        usuario = usuarioRepository.save(usuario);//Transformar em Entity para poder salvar e salva no DB
        return usuarioConverter.paraUsuarioDTO(usuario); //Converte para Usuario DTO

    }


}
