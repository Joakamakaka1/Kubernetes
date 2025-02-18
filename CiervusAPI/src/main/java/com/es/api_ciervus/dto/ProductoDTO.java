package com.es.api_ciervus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductoDTO {

    private String nombre;
    private String descripcion;
    private double precio;
    private Date fecha_publicacion;
    private Long propietario_id;
}
