package com.zaid.portfolio.contact;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContactMessageService {

    private final ContactMessageRepository repository;

    public ContactMessageService(ContactMessageRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ContactMessage save(ContactMessageRequest request) {
        ContactMessage message = new ContactMessage(
                request.name().trim(),
                request.email().trim().toLowerCase(),
                request.subject().trim(),
                request.message().trim());

        return repository.save(message);
    }
}
