package yuri.petukhov.reminder.business.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import yuri.petukhov.reminder.business.model.Metric;

public interface MetricRepository extends JpaRepository<Metric, Long> {
}
