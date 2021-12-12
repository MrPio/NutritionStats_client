package it.univpm.nutritionstats.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class APICommunicationTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void requestSignUp() {
        System.out.println(new APICommunication().requestSignUp("email@example.com"));
    }
}