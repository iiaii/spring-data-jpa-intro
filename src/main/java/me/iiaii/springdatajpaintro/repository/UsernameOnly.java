package me.iiaii.springdatajpaintro.repository;

import org.springframework.beans.factory.annotation.Value;

public interface UsernameOnly {

// open projection 은 디비에서 엔티티를 모두 조회한 다음 형식에 맞춰서 끼워넣음 (최적화 x)
//    @Value("#{target.username + ' ' + target.age}")
    String getUsername(); // (기본 projection 은 최적화 가능)
}
