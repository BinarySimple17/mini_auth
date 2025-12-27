package ru.binarysimple.auth.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
@Entity
@Table(name = "refresh_token")
public class RefreshToken {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;


    @Column(name = "token", unique = true, nullable = false)
    private String token;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(nullable = false)
    private Instant expiryDate;

    private boolean revoked = false;

    public boolean isExpired() {
        return Instant.now().isAfter(this.expiryDate);
    }
}