package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private Long id;
    private String name;
    private String email;

    public User copyOf() {
        return User.builder()
                .id(this.id)
                .name(this.name)
                .email(this.email)
                .build();
    }

    public void updateWith(User other) {
        if (other.getName() != null) {
            this.name = other.getName();
        }
        if (other.getEmail() != null) {
            this.email = other.getEmail();
        }
    }
}
