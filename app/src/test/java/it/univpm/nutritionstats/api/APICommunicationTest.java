package it.univpm.nutritionstats.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.threeten.bp.LocalDate;

import java.util.Date;

import it.univpm.nutritionstats.activity.MainActivity;

class APICommunicationTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void requestSignUp() {
        System.out.println(new APICommunication().requestSignUp("userName", "userEmail",
                new Date(), MainActivity.Diet.CLASSIC, 80, 170, MainActivity.Gender.MALE));
    }
}