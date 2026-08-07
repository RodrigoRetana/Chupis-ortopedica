package com.project.demo.logic.entity.category;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Order(1)
@Component
public class CategorySeeder implements ApplicationListener<ContextRefreshedEvent> {
    private final CategoryRepository categoryRepository;

    public CategorySeeder(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) { loadCategories(); }

    private void loadCategories() {
        Map<String, String> categories = new LinkedHashMap<>();
        categories.put("Soportes Ortopédicos", "Muñequeras, tobilleras, rodilleras y fajas para cuello, hombro, espalda y clavícula.");
        categories.put("Sillas de Ruedas", "Sillas de ruedas manuales, eléctricas y especializadas para personas con discapacidad.");
        categories.put("Movilidad y Desplazamiento", "Andaderas, bastones, muletas y bipedestadores para apoyo en la movilidad.");
        categories.put("Cuidado en Casa y Vida Diaria", "Sillas de baño, camas clínicas y artículos de apoyo para el día a día.");
        categories.put("Rehabilitación y Terapia Física", "Equipos y accesorios para procesos de rehabilitación física y respiratoria.");
        categories.put("Calzado Ortopédico", "Zapatos y plantillas ortopédicas diseñadas a la medida del paciente.");
        categories.put("Órtesis y Prótesis", "Órtesis y prótesis a la medida para pacientes amputados o con necesidades especiales.");

        categories.forEach((name, description) -> {
            categoryRepository.findByNameIgnoreCase(name).ifPresentOrElse(
                    existing -> {
                        // ya existe, no se sobreescribe
                    },
                    () -> {
                        Category category = new Category();
                        category.setName(name);
                        category.setDescription(String.valueOf(description));
                        categoryRepository.save(category);
                    }
            );
        });
    }
}
