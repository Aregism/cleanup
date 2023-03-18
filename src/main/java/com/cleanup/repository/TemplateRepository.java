package com.cleanup.repository;

import com.cleanup.model.MailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateRepository extends JpaRepository<MailTemplate, Integer> {
    MailTemplate findByName(String name);
}
