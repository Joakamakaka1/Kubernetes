package com.es.api_ciervus.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessageForClient {
    private String mensaje;
    private String uri;

}
