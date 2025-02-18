package com.es.api_ciervus.controller;

import com.es.api_ciervus.dto.ReservaDTO;
import com.es.api_ciervus.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @GetMapping("/")
    public ResponseEntity<?> getAll() {
        List<ReservaDTO> reservas = reservaService.getAll();
        return new ResponseEntity<>(reservas, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        ReservaDTO reserva = reservaService.getById(id);
        return new ResponseEntity<>(reserva, HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<?> create(@RequestBody ReservaDTO reserva) {
        reservaService.create(reserva);
        return new ResponseEntity<>(reserva, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody ReservaDTO reserva) {
        ReservaDTO updatedReserva = reservaService.update(id, reserva);
        return new ResponseEntity<>(updatedReserva, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        reservaService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
