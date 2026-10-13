package com.mediahub.config;

import com.mediahub.service.FileStorageService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final FileStorageService storage;

    public WebConfig(AuthInterceptor authInterceptor, FileStorageService storage) {
        this.authInterceptor = authInterceptor;
        this.storage = storage;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/assets/**", "/uploads/**", "/error", "/favicon.ico");
    }

    /** Sert les fichiers téléversés (dossier UPLOAD_DIR, hors du jar) sous /uploads/**, avec support des Range (lecture vidéo/audio). */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = storage.root().toUri().toString();
        if (!location.endsWith("/")) {
            location += "/";
        }
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location)
                .setCacheControl(org.springframework.http.CacheControl.maxAge(1, TimeUnit.HOURS));
    }
}
