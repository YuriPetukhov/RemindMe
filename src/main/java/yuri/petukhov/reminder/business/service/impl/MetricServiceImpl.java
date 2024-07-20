package yuri.petukhov.reminder.business.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import yuri.petukhov.reminder.business.model.Metric;
import yuri.petukhov.reminder.business.repository.MetricRepository;
import yuri.petukhov.reminder.business.service.MetricService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MetricServiceImpl implements MetricService {

    private final MetricRepository metricRepository;
    @Override
    public void saveMetric(String userName, Long userChatId, Long adminChatId) {
        Metric metric = new Metric();
        metric.setMessage("New user: " + userName + ", chatId: " + userChatId);
        metric.setAdminChatId(adminChatId);
        metricRepository.save(metric);
    }

    @Override
    public List<Metric> getNewUserMetrics() {
        return metricRepository.findAll();
    }

    @Override
    public void deleteNewUserMetrics(Long messageId) {
        metricRepository.deleteById(messageId);
    }
}
