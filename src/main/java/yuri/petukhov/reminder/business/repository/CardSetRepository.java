package yuri.petukhov.reminder.business.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yuri.petukhov.reminder.business.model.CardSet;

import java.util.List;

public interface CardSetRepository extends JpaRepository<CardSet, Long> {
    List<CardSet> findAllByUserId(Long userId);
}
