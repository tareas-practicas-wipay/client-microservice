package com.wipay.client.model.entity;

import com.wipay.dynamodb.entity.MainTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

import java.util.Locale;

@DynamoDbBean
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class ClientEntity extends MainTable {

    public static final String PREFIX = "CLIENT#";
    public static final String SK_DATA = "CLIENTDATA";
    public static final String EMAIL_PREFIX = "CLIENTEMAIL#";
    public static final String ENTITY_TYPE = "ENTITY#CLIENT";

    private String name;

    private String surname;

    private String cifNifNie;

    private String phone;

    private String email;

    private String keywordsSearch;

    public static String pkOf(String id) {
        return PREFIX + id;
    }

    public static String gIndex2PkOf(String email) {
        return EMAIL_PREFIX + email.toLowerCase(Locale.ROOT);
    }

    public static String keywordsOf(String name) {
        return name == null ? null : name.trim().toLowerCase(Locale.ROOT);
    }
}

