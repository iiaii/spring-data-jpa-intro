package me.iiaii.springdatajpaintro.repository;

public class UsernameOnlyDto {

    private final String username;

    // 파라미터 명으로 projection 함
    public UsernameOnlyDto(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
