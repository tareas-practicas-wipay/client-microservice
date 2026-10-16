package com.wipay.client.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientModel {
    private String id;
    private String name;
    private String surname;
    private String cifNifNie;
    private String phone;
    private String email;
    private String status;
    private Instant createdDate;

}
