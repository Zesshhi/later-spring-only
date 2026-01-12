package ru.practicum.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwner_Id(Long ownerId);

    @Query("SELECT it " +
            "FROM Item as it " +
            "WHERE it.available is True and " +
            "LOWER(it.name) LIKE LOWER(CONCAT('%', :text, '%')) or " +
            "LOWER(it.description) like LOWER(CONCAT('%', :text, '%'))")
    List<Item> searchByTextContainingIgnoreCase(String text);

    List<Item> findAllByRequest_IdIn(List<Long> requestIds);

    List<Item> findAllByRequest_Id(Long requestId);
}
