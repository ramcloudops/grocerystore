package com.turmericstore.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    private String id;

    private String fullName;

    private String addressLine1;

    private String addressLine2;

    private String city;

    private String state;

    private String postalCode;

    private String country;

    private String phoneNumber;

    private Boolean isDefault;

    private AddressType addressType;

    public enum AddressType {
        SHIPPING,
        BILLING,
        BOTH
    }
}
