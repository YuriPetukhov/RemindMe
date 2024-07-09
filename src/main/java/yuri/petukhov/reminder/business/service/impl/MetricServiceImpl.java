package yuri.petukhov.reminder.business.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yuri.petukhov.reminder.business.model.Metric;
import yuri.petukhov.reminder.business.repository.MetricRepository;
import yuri.petukhov.reminder.business.service.MetricService;
@Service
@RequiredArgsConstructor
public class MetricServiceImpl implements MetricService {

    private final MetricRepository metricRepository;
    @Override
    public void saveMetric(String message, Long chatId) {
        Metric metric = new Metric();
        metric.setMessage(message);
        metric.setChatId(chatId);
        metricRepository.save(metric);
    }
}
