package com.zaid.portfolio.contact;

import java.time.Instant;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/messages")
public class ContactMessageController {

    private final ContactMessageService service;

    public ContactMessageController(ContactMessageService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SubmissionResponse> submit(@Valid @RequestBody ContactMessageRequest request) {
        ContactMessage saved = service.save(request);
        SubmissionResponse response = new SubmissionResponse(
                saved.getId(),
                "Thanks! Your message has been saved.",
                saved.getCreatedAt());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    public record SubmissionResponse(Long id, String message, Instant submittedAt) {
    }
}
