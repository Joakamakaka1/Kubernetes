package com.es.api_ciervus.service;

import com.es.api_ciervus.dto.UserRegisterDTO;
import com.es.api_ciervus.dto.UsuarioDTO;
import com.es.api_ciervus.error.exception.BadRequestException;
import com.es.api_ciervus.error.exception.ConflictException;
import com.es.api_ciervus.error.exception.ResourceNotFoundException;
import com.es.api_ciervus.model.Usuario;
import com.es.api_ciervus.repository.UsuarioRepository;
import com.es.api_ciervus.utils.Mapper;
import com.es.api_ciervus.utils.StringToLong;
import com.es.api_ciervus.utils.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private Mapper mapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario No encontrado"));

        List<GrantedAuthority> authorities = Arrays.stream(usuario.getRoles().split(","))
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.trim()))
                .collect(Collectors.toList());

        UserDetails userDetails = User // User pertenece a SpringSecurity
                .builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword())
                .authorities(authorities)
                .build();

        return userDetails;
    }

    public UserRegisterDTO register(UserRegisterDTO user){
        Validator.validateUserRegister(user);
        if (usuarioRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new ConflictException("El usuario ya existe");
        }
        if (!user.getPassword1().equals(user.getPassword2())) {
            throw new BadRequestException("Las contraseñas no coinciden");
        }
        if (!user.getRoles().equals("ADMIN") && !user.getRoles().equals("USER")) {
            throw new BadRequestException("El rol debe ser ADMIN o USER");
        }

        Usuario newUser = new Usuario();
        newUser.setEmail(user.getEmail());
        newUser.setUsername(user.getUsername());
        newUser.setPassword(passwordEncoder.encode(user.getPassword1()));
        newUser.setRoles(user.getRoles());
        newUser.setFecha_registro(new Date());
        usuarioRepository.save(newUser);
        return user;
    }
    public UsuarioDTO getById(String id){
        Long idLong = StringToLong.stringToLong(id);
        if (idLong == null || idLong <= 0) {
            throw new BadRequestException("El id no es valido");
        }
        Usuario usuario = usuarioRepository.findById(idLong).orElseThrow(() -> new ResourceNotFoundException("El usuario no existe"));
        return mapper.mapToUsuarioDTO(usuario);
    }

    public List<UsuarioDTO> getAll(){
        List<Usuario> usuarios = usuarioRepository.findAll();
        if (usuarios.isEmpty()) {
            throw new BadRequestException("No hay usuarios registrados");
        }

        List<UsuarioDTO> usuarioDTOS = new ArrayList<>();
        usuarios.forEach(usuario -> usuarioDTOS.add(mapper.mapToUsuarioDTO(usuario)));
        return usuarioDTOS;
    }

    public UsuarioDTO update(String id, UsuarioDTO user){
        Long idLong = StringToLong.stringToLong(id);
        if (idLong == null || idLong <= 0) {
            throw new BadRequestException("El id no es valido");
        }
        Validator.validateUser(user);
        usuarioRepository.findByUsername(user.getUsername()).ifPresent(existingUser -> {
            if (!existingUser.getId().equals(idLong)) {
                throw new ConflictException("El nombre de usuario ya existe");
            }
        });
        usuarioRepository.findByEmail(user.getEmail()).ifPresent(existingUser -> {
            if (!existingUser.getId().equals(idLong)) {
                throw new ConflictException("El email ya existe");
            }
        });
        /*
        if(usuarioRepository.findByEmail(user.getEmail()).isPresent()){
            throw new ConflictException("El email ya existe");
        }*/

        Usuario existingUser = usuarioRepository.findById(idLong).orElseThrow(() -> new ResourceNotFoundException("El usuario no existe"));
        existingUser.setEmail(user.getEmail());
        existingUser.setUsername(user.getUsername());
        existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        existingUser.setRoles(user.getRoles());
        usuarioRepository.save(existingUser);
        return mapper.mapToUsuarioDTO(existingUser);
    }

    public void delete(String id) {
        Long idLong = StringToLong.stringToLong(id);
        if (idLong == null || idLong <= 0) {
            throw new BadRequestException("El id no es valido");
        }
        usuarioRepository.deleteById(idLong);
    }
}
