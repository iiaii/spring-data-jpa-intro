package me.iiaii.springdatajpaintro.repository;

import me.iiaii.springdatajpaintro.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
