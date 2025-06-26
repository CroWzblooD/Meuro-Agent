package com.example.crudspring;

import com.example.crudspring.models.Employe;
import com.example.crudspring.services.EmployeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class CrudSpringApplication implements CommandLineRunner {

    @Autowired
    private EmployeService employeService;

    public static void main(String[] args) {
        SpringApplication.run(CrudSpringApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        Employe employe = new Employe(1L, "kemane", "Donfack","kemane@gmail.com");
        Employe employe1 = new Employe(1L, "ivan", "Nafack","nafack@gmail.com");

        employeService.createEmployee(employe);
        employeService.createEmployee(employe1);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:3000")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
