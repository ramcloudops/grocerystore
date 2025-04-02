package com.turmericstore.model;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.IgnoreExtraProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IgnoreExtraProperties
public class User {

    @DocumentId
    private String id;

    private String email;

    private String password; // Hashed

    private String firstName;

    private String lastName;

    private String phoneNumber;

    @Builder.Default
    private List<Address> addresses = new ArrayList<>();

    @Builder.Default
    private Set<String> roles = new HashSet<>();

    private Boolean active;

    private Long lastLogin;

    private Long createdAt;

    private Long updatedAt;

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isAdmin() {
        return roles.contains("ROLE_ADMIN");
    }
}
