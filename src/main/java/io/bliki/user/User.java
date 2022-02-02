package io.bliki.user;

public record User(String id,
                   String username,
                   String email,
                   String password,
                   boolean admin,
                   String name,
                   String phone) {
}


