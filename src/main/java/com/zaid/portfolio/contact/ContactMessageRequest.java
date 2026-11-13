package com.zaid.portfolio.contact;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContactMessageRequest(
        @NotBlank(message = "Please enter your name")
        @Size(max = 80, message = "Name must be 80 characters or fewer")
        String name,

        @NotBlank(message = "Please enter your email address")
        @Email(message = "Please enter a valid email address")
        @Size(max = 160, message = "Email must be 160 characters or fewer")
        String email,

        @NotBlank(message = "Please enter a subject")
        @Size(max = 120, message = "Subject must be 120 characters or fewer")
        String subject,

        @NotBlank(message = "Please enter a message")
        @Size(min = 10, max = 2000, message = "Message must be between 10 and 2000 characters")
        String message) {
}
