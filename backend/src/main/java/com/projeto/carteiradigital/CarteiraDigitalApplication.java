package com.projeto.carteiradigital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CarteiraDigitalApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarteiraDigitalApplication.class, args);
        System.out.println("SecOps API Carteira Digital iniciada com sucesso!");
    }

}