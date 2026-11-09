package com.wipay.client.model;

/**
 * Valores del atributo status. Un cliente borrado no se elimina de la tabla: pasa a INACTIVE.
 */
public final class EntityStatus {

    public static final String ACTIVE = "ACTIVE";
    public static final String INACTIVE = "INACTIVE";

    private EntityStatus() {}
}
