package com.cleanup.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Templates")
@Getter
@Setter
public class MailTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false)
    private Integer id;

    @Column(name = "Name", unique = true, nullable = false)
    private String name;

    @Column(name = "Body", nullable = false, length = 2048)
    private String body;
}
