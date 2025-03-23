package ru.practicum.StatsServerApp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "hits")
@NoArgsConstructor
@AllArgsConstructor
public class EndpointHit {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "app", nullable = false)
    private String app;
    @Column(name = "uri", nullable = false)
    private String uri;
    @Column(name = "ip", nullable = false)
    private String ip;
    @Column(name = "created", nullable = false)
    private LocalDateTime timestamp;
}
