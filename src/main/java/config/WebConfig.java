package config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {ResourceNames.REST_API, ResourceNames.CONTROLLERS, ResourceNames.DAOS, ResourceNames.SERVICES,
        ResourceNames.REST_API_VALIDATORS})
public class WebConfig extends WebMvcConfigurerAdapter {

    // OJO -> Se escanea el paquete donde está lo relativo a web (la carpeta donde están los presentadores).
    // Si no, no se puede llegar a ver y no funciona correctamente el proyecto.

    // CORS
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*").maxAge(3600);
    }

}
