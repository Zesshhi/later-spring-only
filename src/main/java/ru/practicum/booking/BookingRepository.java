package ru.practicum.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Booking findByItem_Id(Long itemId);

    List<Booking> findAllByBooker_IdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByBooker_IdAndStatusOrderByStartDesc(Long bookerId, String status);

    List<Booking> findAllByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long bookerId,
            LocalDateTime currentTime,
            LocalDateTime currentTimeAgain
    );

    List<Booking> findAllByBooker_IdAndEndBeforeOrderByStartDesc(
            Long bookerId,
            LocalDateTime currentTime
    );

    List<Booking> findAllByBooker_IdAndStartAfterOrderByStartDesc(
            Long bookerId,
            LocalDateTime currentTime
    );


    List<Booking> findAllByItem_Owner_IdOrderByStartDesc(Long bookerId);

    List<Booking> findAllByItem_Owner_IdAndStatusOrderByStartDesc(Long bookerId, String status);


    List<Booking> findAllByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long bookerId,
            LocalDateTime currentTime,
            LocalDateTime currentTimeAgain
    );

    List<Booking> findAllByItem_Owner_IdAndEndBeforeOrderByStartDesc(
            Long bookerId,
            LocalDateTime currentTime
    );

    List<Booking> findAllByItem_Owner_IdAndStartAfterOrderByStartDesc(
            Long bookerId,
            LocalDateTime currentTime
    );

    List<Booking> findAllByItem_IdAndBooker_IdAndStatusAndEndBefore(
            Long itemId,
            Long bookerId,
            BookingStatus status,
            LocalDateTime currentTime
    );

    Optional<Booking> findFirstByItem_IdAndStatusAndStartBeforeOrderByEndDesc(
            Long itemId,
            BookingStatus status,
            LocalDateTime currentTime
    );

    Optional<Booking> findFirstByItem_IdAndStatusAndStartAfterOrderByStartAsc(
            Long itemId,
            BookingStatus status,
            LocalDateTime currentTime
    );

}
