package com.es.api_ciervus.controller;

import com.es.api_ciervus.dto.ProductoDTO;
import com.es.api_ciervus.error.exception.BadRequestException;
import com.es.api_ciervus.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping("/")
    public ResponseEntity<?> getAll() {
        List<ProductoDTO> productos = productoService.getAll();
        return new ResponseEntity<>(productos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {

        ProductoDTO producto = productoService.getById(id);
        return new ResponseEntity<>(producto, HttpStatus.OK);
    }
    @PostMapping("/")
    public ResponseEntity<?> create(@RequestBody ProductoDTO producto) {
        productoService.create(producto);
        return new ResponseEntity<>(producto, HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody ProductoDTO producto) {
        ProductoDTO updatedProducto = productoService.update(id, producto);
        return new ResponseEntity<>(updatedProducto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        productoService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
