package com.es.api_ciervus.utils;

import com.es.api_ciervus.dto.ProductoDTO;
import com.es.api_ciervus.dto.ReservaDTO;
import com.es.api_ciervus.dto.UsuarioDTO;
import com.es.api_ciervus.error.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class Validator {

    public static  void validateUser(UsuarioDTO usuarioDTO) {
        if (usuarioDTO.getEmail().isEmpty() || usuarioDTO.getEmail() == null) {
            throw new ValidationException("El email del usuario no puede estar vacio");
        }
        if (usuarioDTO.getUsername().isEmpty() || usuarioDTO.getUsername() == null) {
            throw new ValidationException("El username del usuario no puede estar vacio");
        }
        if (usuarioDTO.getPassword().isEmpty() || usuarioDTO.getPassword() == null) {
            throw new ValidationException("La contraseña del usuario no puede estar vacia");
        }
    }

    public static  void validateProducto(ProductoDTO productoDTO) {
         if (productoDTO.getNombre().isEmpty() || productoDTO.getNombre() == null) {
             throw new ValidationException("El nombre del producto no puede estar vacio");
         }
         if (productoDTO.getDescripcion().isEmpty() || productoDTO.getDescripcion() == null) {
             throw new ValidationException("La descripcion del producto no puede estar vacia");
         }
         if (productoDTO.getPrecio() <= 0) {
             throw new ValidationException("El precio del producto debe ser mayor a 0");
         }
         if (productoDTO.getFecha_publicacion() == null) {
             throw new ValidationException("La fecha de publicacion del producto no puede estar vacia");
         }
        if (productoDTO.getFecha_publicacion().after(new Date())) {
            throw new ValidationException("La fecha de publicación no puede ser una fecha futura.");
        }
         if (productoDTO.getPropietario_id() == null) {
             throw new ValidationException("El propietario del producto no puede estar vacio");
         }

    }

    public static  void validateReserva(ReservaDTO reservaDTO) {
        if (reservaDTO.getProducto_id() == null) {
            throw new ValidationException("El producto de la reserva no puede estar vacio");
        }
        if (reservaDTO.getUsuario_id() == null) {
            throw new ValidationException("El usuario de la reserva no puede estar vacio");
        }


    }
}
