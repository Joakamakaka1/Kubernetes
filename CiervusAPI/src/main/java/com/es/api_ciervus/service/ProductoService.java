package com.es.api_ciervus.service;

import com.es.api_ciervus.dto.ProductoDTO;
import com.es.api_ciervus.error.exception.*;
import com.es.api_ciervus.model.Producto;
import com.es.api_ciervus.model.Usuario;
import com.es.api_ciervus.repository.ProductoRepository;
import com.es.api_ciervus.repository.UsuarioRepository;
import com.es.api_ciervus.utils.Mapper;
import com.es.api_ciervus.utils.StringToLong;
import com.es.api_ciervus.utils.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductoService {
    @Autowired
    private Mapper mapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoRepository productoRepository;


    public ProductoDTO getById(String id) {
            Long idLong = StringToLong.stringToLong(id);
            if (idLong == null || idLong <= 0) {
                throw  new BadRequestException("El id no es valido");
            }
            Producto producto = productoRepository.findById(idLong).orElseThrow(() -> new ResourceNotFoundException("El producto no existe"));
            return mapper.mapToProductoDTO(producto);
    }

    public List<ProductoDTO> getAll() {
            List<Producto> productos = productoRepository.findAll();
            if (productos.isEmpty()) {
                throw new BadRequestException("No hay productos disponibles");
            }
            List<ProductoDTO> productoDTOS = new ArrayList<>();
            productos.forEach(producto -> productoDTOS.add(mapper.mapToProductoDTO(producto)));
            return productoDTOS;

    }

    public ProductoDTO create(ProductoDTO productoDTO) {
            if (productoDTO == null) {
                throw new BadRequestException("El producto no puede estar vacio");
            }
            String usernameAutenticado = SecurityContextHolder.getContext().getAuthentication().getName();
            Usuario usuarioAtenticado = usuarioRepository.findByUsername(usernameAutenticado).orElseThrow(() -> new UnauthorizedException("El usuario no esta autenticado"));
        Usuario propietario = usuarioRepository.findById(productoDTO.getPropietario_id()).orElseThrow(() -> new ResourceNotFoundException("El propietario no existe"));
            if (!usuarioAtenticado.getRoles().contains("ADMIN")) {
                // Comprobar que el propietario del producto sea el mismo que el usuario autenticado
                if(!usuarioAtenticado.getId().equals(productoDTO.getPropietario_id())) {
                    throw new ForbiddenException("El propietario del producto no coincide con su usuario");
                }
            }

            Validator.validateProducto(productoDTO);
            Producto producto = mapper.mapToProducto(productoDTO, propietario);
            productoRepository.save(producto);
            return mapper.mapToProductoDTO(producto);
        }

    public ProductoDTO update(String id, ProductoDTO productoDTO) {
            Long idLong = StringToLong.stringToLong(id);
          Usuario propietario = usuarioRepository.findById(productoDTO.getPropietario_id()).orElseThrow(() -> new ResourceNotFoundException("El propietario no existe"));
          if(productoDTO == null || idLong == null || idLong <= 0) {
              throw new BadRequestException("El id no es valido");
          }
          productoRepository.findById(idLong).orElseThrow(() -> new ResourceNotFoundException("El producto no existe"));

          String usernameAutenticado = SecurityContextHolder.getContext().getAuthentication().getName();
          Usuario usuarioAtenticado = usuarioRepository.findByUsername(usernameAutenticado).orElseThrow(() -> new UnauthorizedException("El usuario no esta autenticado"));
          if(!usuarioAtenticado.getRoles().contains("ADMIN")) {
              if(!usuarioAtenticado.getId().equals(productoDTO.getPropietario_id())){
                  throw new ForbiddenException("El propietario del producto no coincide con su usuario");
              }
          }
          Validator.validateProducto(productoDTO);
          Producto producto = mapper.mapToProducto(productoDTO, propietario);
          producto.setId(idLong);
          productoRepository.save(producto);
          return mapper.mapToProductoDTO(producto);
    }

    public void delete(String id) {
            Long idLong = StringToLong.stringToLong(id);
            if (idLong == null || idLong <= 0) {
                throw new BadRequestException("El id no es valido");
            }
            productoRepository.deleteById(idLong);

    }
}
