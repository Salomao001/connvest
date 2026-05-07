package com.conninvest.backend.dto;

public record StartupMemberDTO(
        Long userId,
        String name,
        String email,
        String photo,
        String role
) {
}
