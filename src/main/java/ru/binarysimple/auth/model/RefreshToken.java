package ru.binarysimple.auth.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@Entity
@Table(name = "refresh_token")
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    @Column(name = "token", unique = true, nullable = false)
    private String token;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(nullable = false)
    private Instant expiryDate;

    @Builder.Default
    private boolean revoked = false;

    public boolean isExpired() {
        return Instant.now().isAfter(this.expiryDate);
    }
}