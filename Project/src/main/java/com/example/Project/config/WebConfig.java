package com.example.Project.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuratie Spring MVC pentru CORS, interceptori si bean-uri de securitate.
 *
 * <p>Inregistreaza {@link AuthInterceptor} pe toate rutele {@code /api/**} si
 * configureaza politica CORS pentru a permite accesul din frontend-ul React
 * (implicit pe {@code http://localhost:5173}).</p>
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    /**
     * Configureaza politica CORS pentru rutele API.
     * Permite cereri GET, POST, PUT, DELETE si OPTIONS de la adresa frontend-ului React.
     *
     * @param registry Registrul de configuratii CORS al Spring MVC.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }

    /**
     * Inregistreaza {@link AuthInterceptor} ca middleware pe toate rutele {@code /api/**}.
     * Orice cerere catre aceste rute va trece prin interceptor inainte de a ajunge la controller;
     * daca token-ul lipseste sau este invalid, cererea este respinsa cu HTTP 401.
     *
     * @param registry Registrul de interceptori al Spring MVC.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**");
    }

    /**
     * Creeaza bean-ul de encodare a parolelor bazat pe algoritmul BCrypt.
     * Factorul de cost 10 ofera un echilibru bun intre securitate si performanta.
     *
     * @return Instanta {@link BCryptPasswordEncoder} cu cost 10.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
