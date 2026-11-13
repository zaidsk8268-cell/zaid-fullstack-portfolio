package com.zaid.portfolio.web;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PortfolioController {

    @GetMapping("/health")
    public HealthResponse health() {
        return new HealthResponse("online", "Zaid portfolio API");
    }

    @GetMapping("/portfolio")
    public PortfolioResponse portfolio() {
        return new PortfolioResponse(
                "Zaid Khan",
                "IT Engineering Student & Full-Stack Learner",
                "Thane, Maharashtra",
                List.of(
                        new Stat("2nd", "Year of engineering"),
                        new Stat("10+", "Technologies explored"),
                        new Stat("02", "Full-stack builds")),
                List.of(
                        new SkillGroup("Frontend", List.of("HTML5", "CSS3", "JavaScript", "React basics")),
                        new SkillGroup("Backend", List.of("Java", "Spring Boot", "REST APIs", "Bean Validation")),
                        new SkillGroup("Data & tools", List.of("MySQL", "H2 Database", "Git", "Maven"))),
                List.of(
                        new Project(
                                "Hospital Management System",
                                "A role-based application for patient registration, doctor management, appointments and record tracking.",
                                "Featured project",
                                List.of("React", "Spring Boot", "MySQL")),
                        new Project(
                                "Full-Stack Portfolio",
                                "This responsive portfolio uses a Java REST API, server-side validation and a persistent contact-message database.",
                                "Current build",
                                List.of("Java", "Spring Boot", "H2", "JavaScript"))));
    }

    public record HealthResponse(String status, String service) {
    }

    public record PortfolioResponse(
            String name,
            String title,
            String location,
            List<Stat> stats,
            List<SkillGroup> skills,
            List<Project> projects) {
    }

    public record Stat(String value, String label) {
    }

    public record SkillGroup(String name, List<String> items) {
    }

    public record Project(String name, String description, String status, List<String> technologies) {
    }
}
