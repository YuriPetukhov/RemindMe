package yuri.petukhov.reminder.business.service;

import yuri.petukhov.reminder.business.model.Metric;

import java.util.List;

public interface MetricService {
    void saveMetric(String userName, Long userId, Long adminId);

    List<Metric> getNewUserMetrics();

    void deleteNewUserMetrics(Long messageId);
}
