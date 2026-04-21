package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // ΑΥΤΟ ΕΙΝΑΙ ΤΟ ΚΛΕΙΔΙ
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Ενεργοποιεί έναν "broker" για να στέλνει μηνύματα στα κανάλια που ξεκινούν με /topic
        config.enableSimpleBroker("/topic");
        // Μηνύματα που έρχονται από το Flutter προς το Spring θα ξεκινούν με /app
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Αυτό είναι το URL που θα συνδεθεί το Flutter (π.χ. ws://localhost:8080/ws-restaurant)
        registry.addEndpoint("/ws-restaurant")
                .setAllowedOriginPatterns("*") // Επιτρέπει σύνδεση από παντού (σημαντικό!)
                .withSockJS();
    }
}