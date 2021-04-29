package me.iiaii.springdatajpaintro.repository;

import me.iiaii.springdatajpaintro.entity.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Test
    @DisplayName("save")
    public void save() throws Exception {
        // given
        Item item = new Item("A");
        itemRepository.save(item);

        // when


        // then

    }
}