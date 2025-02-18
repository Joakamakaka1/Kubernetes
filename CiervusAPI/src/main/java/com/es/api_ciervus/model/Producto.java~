package com.es.api_ciervus.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "productos")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nombre;
    private String descripcion;
    @Column(nullable = false)
    private double precio;
    @Column(nullable = false)
    private Date fecha_publicacion;
    @ManyToOne
    @JoinColumn(name = "propietario_id", nullable = false)
    private Usuario propietario_id;
    @OneToMany(mappedBy = "producto_id", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reserva> reservas;

    public Producto(String nombre, String descripcion, double precio, Date fecha_publicacion, Usuario propietario_id) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.fecha_publicacion = fecha_publicacion;
        this.propietario_id = propietario_id;
    }
}
