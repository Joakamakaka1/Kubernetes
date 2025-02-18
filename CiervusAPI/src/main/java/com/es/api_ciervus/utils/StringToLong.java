package com.es.api_ciervus.utils;

import org.springframework.stereotype.Component;

@Component
public class StringToLong {
    public static Long stringToLong(String id) {
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            return null;//Cambiar cuando tenga las excepciones ;)
        }

    }
}
