package com.es.api_ciervus.service;

import com.es.api_ciervus.dto.ReservaDTO;
import com.es.api_ciervus.error.exception.BadRequestException;
import com.es.api_ciervus.error.exception.ForbiddenException;
import com.es.api_ciervus.error.exception.ResourceNotFoundException;
import com.es.api_ciervus.error.exception.UnauthorizedException;
import com.es.api_ciervus.model.Producto;
import com.es.api_ciervus.model.Reserva;
import com.es.api_ciervus.model.Usuario;
import com.es.api_ciervus.repository.ProductoRepository;
import com.es.api_ciervus.repository.ReservaRepository;
import com.es.api_ciervus.repository.UsuarioRepository;
import com.es.api_ciervus.utils.Mapper;
import com.es.api_ciervus.utils.StringToLong;
import com.es.api_ciervus.utils.Validator;
import org.hibernate.annotations.NotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private Mapper mapper;

    public ReservaDTO create(ReservaDTO reservaDTO) {
        if (reservaDTO == null) {
            throw new BadRequestException("La reserva no puede estar vacia");
        }

        Usuario usuario = usuarioRepository.findById(reservaDTO.getUsuario_id()).orElseThrow(() -> new ResourceNotFoundException("El usuario no existe"));
        Producto producto = productoRepository.findById(reservaDTO.getProducto_id()).orElseThrow(() -> new ResourceNotFoundException("El producto no existe"));



        // Obtener el usuario autenticado
        String usernameAutenticado = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuarioAutenticado = usuarioRepository.findByUsername(usernameAutenticado).orElseThrow(() -> new UnauthorizedException("El usuario no esta autenticado"));
       // Validar que el usuario autenticado sea el propietario del producto
        if(!usuarioAutenticado.getRoles().contains("ADMIN")){
            if (!usuarioAutenticado.getId().equals(reservaDTO.getUsuario_id())) {
                throw new ForbiddenException("No puedes asignar la reservar un producto a otro usuario");
            }

            // Validar que el propietario no pueda reservar su propio producto
            if (producto.getPropietario().getId().equals(usuario.getId())) {
                throw new BadRequestException("El propietario no puede reservar su propio producto");
            }
        }

        Validator.validateReserva(reservaDTO);
        Reserva reserva = mapper.mapToReserva(reservaDTO, usuario, producto);
        Date fechaInicio = new Date();
        reserva.setFecha_inicio(fechaInicio);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fechaInicio);
        calendar.add(Calendar.DAY_OF_YEAR, 7);
        Date fechaFin = calendar.getTime();

        reserva.setFecha_fin(fechaFin);
        reservaRepository.save(reserva);
        return mapper.mapToReservaDTO(reserva);
    }

    public ReservaDTO getById(String id) {
        Long idLong = StringToLong.stringToLong(id);
        if (idLong == null || idLong <= 0) {
            throw new BadRequestException("El id no es valido");
        }
        Reserva reserva = reservaRepository.findById(idLong).orElseThrow(() -> new ResourceNotFoundException("La reserva no existe"));

        String usernameAutenticado = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("Usuario autenticado: " + usernameAutenticado);
        Usuario usuarioAutenticado = usuarioRepository.findByUsername(usernameAutenticado).orElseThrow(() -> new UnauthorizedException("El usuario no esta autenticado"));

        boolean isAdmin = usuarioAutenticado.getRoles().contains("ADMIN");
        boolean isPropietarioReserva = reserva.getUsuario().getId().equals(usuarioAutenticado.getId());

            if (!isAdmin && !isPropietarioReserva){
                throw new ForbiddenException("No tienes permiso para acceder a esta reserva");
            }

        // Validar que el usuario autenticado sea el propietario del producto
        /*if(!usuarioAutenticado.getRoles().contains("ADMIN")){
            if (!usuarioAutenticado.getId().equals(reserva.getUsuario().getId())) {
                throw new ForbiddenException("No puedes asignar la reservar un producto a otro usuario");
            }
        }*/

        return mapper.mapToReservaDTO(reserva);

    }

    public List<ReservaDTO> getAll() {
        List<Reserva> reservas = reservaRepository.findAll();
        if (reservas.isEmpty()) {
            throw new BadRequestException("No hay reservas disponibles");
        }
        List<ReservaDTO> reservaDTOS = new ArrayList<>();
        reservas.forEach(reserva -> reservaDTOS.add(mapper.mapToReservaDTO(reserva)));
        return reservaDTOS;
    }

    public ReservaDTO update(String id, ReservaDTO reservaDTO) {
        Long idLong = StringToLong.stringToLong(id);
        if (idLong == null || idLong <= 0) {
            throw new BadRequestException("El id no es valido");
        }

        Usuario usuario = usuarioRepository.findById(reservaDTO.getUsuario_id()).orElseThrow(() -> new ResourceNotFoundException("El usuario no existe"));
        Producto producto = productoRepository.findById(reservaDTO.getProducto_id()).orElseThrow(() -> new ResourceNotFoundException("El producto no existe"));
        Reserva reserva = reservaRepository.findById(idLong).orElseThrow(() -> new ResourceNotFoundException("La reserva no existe"));
        // Obtener el usuario autenticado
        String usernameAutenticado = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuarioAutenticado = usuarioRepository.findByUsername(usernameAutenticado).orElseThrow(() -> new UnauthorizedException("El usuario no esta autenticado"));

        // Si no es un administrador, validamos que el usuario autenticado sea el mismo que el de la reserva o el propietario del producto
        if (!usuarioAutenticado.getRoles().contains("ADMIN")) {
            // Verifica que el propietario no intente reservar su propio producto
            if (producto.getPropietario().getId().equals(usuarioAutenticado.getId())) {
                throw new BadRequestException("El propietario no puede reservar su propio producto");
            }

            // Verifica si el usuario autenticado está intentando actualizar su propia reserva
            if (!usuarioAutenticado.getId().equals(reservaDTO.getUsuario_id())) {
                throw new ForbiddenException("No puedes actualizar una reserva que no es tuya");
            }
        }
        // Validación de la reserva
        Validator.validateReserva(reservaDTO);

        reserva.setEstado(reservaDTO.getEstado());
        reserva.setProducto(producto);
        reserva.setUsuario(usuario);

        reservaRepository.save(reserva);
        return mapper.mapToReservaDTO(reserva);
    }


    public void delete(String id) {
        Long idLong = StringToLong.stringToLong(id);
        if (idLong == null || idLong <= 0) {
            throw new BadRequestException("El id no es valido");
        }
        reservaRepository.deleteById(idLong);
    }

}
