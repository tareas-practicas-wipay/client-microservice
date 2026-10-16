package com.wipay.client.model;

/**
 * Expresiones usadas en las validaciones de los modelos de cliente de entrada
 */
public class ValidationPatterns {
    /** usuario@dominio.ext (extensión de al menos 2 letras). */
    public static final String EMAIL_REGEX =
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    /** NIF (12345678A), NIE (X1234567A) o CIF (B12345678) */
    public static final String CIF_NIF_NIE_REGEX =
            "^([0-9]{8}[A-Z]|[XYZ][0-9]{7}[A-Z]|[ABCDEFGHJNPQRSUVW][0-9]{7}[0-9A-J])$";

    /** Entre 9 y 15 dígitos, con prefijo internacional (+) opcional. */
    public static final String PHONE_REGEX =
            "^\\+?[0-9]{9,15}$";

    private ValidationPatterns() {}
}
