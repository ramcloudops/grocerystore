package com.turmericstore.config;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class FirestoreConfig {

    @Value("${spring.cloud.gcp.firestore.project-id:#{null}}")
    private String projectId;

    @Value("${spring.cloud.gcp.firestore.emulator.enabled:false}")
    private boolean emulatorEnabled;

    @Value("${spring.cloud.gcp.firestore.emulator.host:localhost:8081}")
    private String emulatorHost;

    @Bean
    @Profile("dev")
    public Firestore firestoreForDevelopment() {
        FirestoreOptions.Builder builder = FirestoreOptions.getDefaultInstance().toBuilder();

        if (emulatorEnabled) {
            builder.setHost(emulatorHost)
                    .setProjectId(projectId != null ? projectId : "local-project")
                    .setCredentials(null);
        }

        return builder.build().getService();
    }

    @Bean
    @Profile("prod")
    public Firestore firestoreForProduction() {
        FirestoreOptions.Builder builder = FirestoreOptions.getDefaultInstance().toBuilder();

        if (projectId != null) {
            builder.setProjectId(projectId);
        }

        return builder.build().getService();
    }
}