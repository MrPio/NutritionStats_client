package it.univpm.nutritionstats.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.univpm.nutritionstats.activity.MainActivity;

class APICommunicationTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void requestSignUp() {
        System.out.println(new APICommunication().requestSignUp("userName", "userEmail",
                1990, MainActivity.Diet.CLASSIC, 80, 170, MainActivity.Gender.MALE));
    }
}