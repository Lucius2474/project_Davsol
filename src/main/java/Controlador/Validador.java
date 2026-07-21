/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.regex.Pattern;

public class Validador {
    private static final Pattern CORREO_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern NUMERICO_PATTERN = Pattern.compile("^[0-9]+$");
    private static final Pattern ALFA_PATTERN = Pattern.compile("^[a-zA-Z]+$");

    private static final Validator VALIDATOR;

    static {
        // Inicializa el motor de Hibernate Validator
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        VALIDATOR = factory.getValidator();
    }

    /**
     * Valida si una cadena contiene estrictamente solo letras o números.
     */
    public static boolean esCorreo(String texto) {
        if (texto == null) return false;
        return CORREO_PATTERN.matcher(texto).matches();
    }
    
    public static boolean esNumerico(String texto) {
        if (texto == null) return false;
        return NUMERICO_PATTERN.matcher(texto).matches();
    }
    
    public static boolean esAlfa(String texto) {
        if (texto == null) return false;
        return ALFA_PATTERN.matcher(texto).matches();
    }

}


    

