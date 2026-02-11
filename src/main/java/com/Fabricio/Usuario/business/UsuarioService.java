package com.Fabricio.Usuario.business;

import com.Fabricio.Usuario.business.converter.UsuarioConverter;
import com.Fabricio.Usuario.business.dto.EnderecoDTO;
import com.Fabricio.Usuario.business.dto.TelefoneDTO;
import com.Fabricio.Usuario.business.dto.UsuarioDTO;
import com.Fabricio.Usuario.infrastructure.entity.Endereco;
import com.Fabricio.Usuario.infrastructure.entity.Telefone;
import com.Fabricio.Usuario.infrastructure.entity.Usuario;
import com.Fabricio.Usuario.infrastructure.exception.ConflictException;
import com.Fabricio.Usuario.infrastructure.exception.ResourceNotFoundException;
import com.Fabricio.Usuario.infrastructure.exception.UnauthorizedException;
import com.Fabricio.Usuario.infrastructure.repository.EnderecoRepository;
import com.Fabricio.Usuario.infrastructure.repository.TelefoneRepository;
import com.Fabricio.Usuario.infrastructure.repository.UsuarioRepository;
import com.Fabricio.Usuario.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EnderecoRepository enderecoRepository;
    private final TelefoneRepository telefoneRepository;
    private final AuthenticationManager authenticationManager;

    public UsuarioDTO salvaUsuario(UsuarioDTO usuarioDTO){
        emailExiste(usuarioDTO.getEmail());
        usuarioDTO.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        Usuario usuario = usuarioConverter.paraUsuario(usuarioDTO); //Recebe usuario DTO do front
        usuario = usuarioRepository.save(usuario);//Transformar em Entity para poder salvar e salva no DB
        return usuarioConverter.paraUsuarioDTO(usuario); //Converte para Usuario DTO

    }

    public String autenticarUsuario(UsuarioDTO usuarioDTO){
        try {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(usuarioDTO.getEmail(),
                            usuarioDTO.getSenha())
            );
            return "Bearer " + jwtUtil.generateToken(authentication.getName());
        }catch (BadCredentialsException | UsernameNotFoundException | AuthorizationDeniedException e){
            throw new UnauthorizedException("Usuario ou senha invalidos: ", e.getCause());
        }
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

    public UsuarioDTO buscarUsuarioPorEmail(String email) {
        try {
            return usuarioConverter.paraUsuarioDTO(
                    usuarioRepository.findByEmail(email)
                            .orElseThrow(
                    () -> new ResourceNotFoundException("Email nao encontrado" + email)
                            )
            );

        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Email nao encontrado" + email);
        }
    }


    public void deletaUsuarioPorEmail(String email){
        usuarioRepository.deleteByEmail(email);

    }

    public UsuarioDTO atualizaDadosUsuario(String token ,UsuarioDTO dto){
        //buscado o email do token para nao ser necessario passar o email
        String email = jwtUtil.extrairEmailToken(token.substring(7));

        //senha criptografada caso seja nova
        dto.setSenha(dto.getSenha() != null ? passwordEncoder.encode(dto.getSenha()) : null);

        // Localizado o Usuario com todas sua informacoes para pegar informacoes nao passadas pelo ususario
        Usuario usuarioEntity = usuarioRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException("Email nao localizado"));

        //Mescladoo os dados recebidos entre Dto com os dados do banco de dados
        Usuario usuario = usuarioConverter.updateUsuario(dto,usuarioEntity);


        //salvou os dados dos usuarios no banco de dados e retonou um usuarioDTO
        return usuarioConverter.paraUsuarioDTO(usuarioRepository.save(usuario));

    }

    public EnderecoDTO atualizaEndereco(Long idEndereco, EnderecoDTO enderecoDTO){

        Endereco entity = enderecoRepository.findById(idEndereco).orElseThrow(() ->
                new ResourceNotFoundException("ID nao encontrado" + idEndereco));

        Endereco endereco = usuarioConverter.updateEndereco(enderecoDTO, entity);

        return usuarioConverter.paraEnderecoDTO(enderecoRepository.save(endereco));
    }

    public TelefoneDTO atualizaTelefone(Long idTelefone, TelefoneDTO dto){
        Telefone entity = telefoneRepository.findById(idTelefone).orElseThrow(() ->
                new ResourceNotFoundException("ID nao encontrado" + dto));

        Telefone telefone = usuarioConverter.updateTelefone(dto,entity);

        return usuarioConverter.paraTelefoneDTO(telefoneRepository.save(telefone));

    }

    public EnderecoDTO cadastroEndereco(String token, EnderecoDTO dto){
        String email = jwtUtil.extrairEmailToken(token.substring(7));
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException("Email nao localizado" + email));

        Endereco endereco = usuarioConverter.paraEndercoEntity(dto, usuario.getId());
        Endereco enderecoEntity = enderecoRepository.save(endereco);
        return usuarioConverter.paraEnderecoDTO(enderecoEntity);
    }

    public TelefoneDTO cadastroTelefone(String token, TelefoneDTO dto){
        String email = jwtUtil.extrairEmailToken(token.substring(7));
        Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(() ->
                new ResourceNotFoundException("Email nao localizado" + email));

        Telefone telefone= usuarioConverter.paraTelefoneEntity(dto, usuario.getId());
        Telefone telefoneEntity = telefoneRepository.save(telefone);
        return usuarioConverter.paraTelefoneDTO(telefoneEntity);
    }


}
