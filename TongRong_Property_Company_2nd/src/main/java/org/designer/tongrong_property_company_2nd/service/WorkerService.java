package org.designer.tongrong_property_company_2nd.service;

import lombok.RequiredArgsConstructor;
import org.designer.tongrong_property_company_2nd.mapper.WorkerMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkerService {

    private final WorkerMapper workerMapper;

    public int addWorker(String name, String department, String gender, String work) {
        String id = UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        return workerMapper.insert(id, name, department, gender, work);
    }

    public List<Map<String, Object>> getAllWorkers() {
        return workerMapper.selectAll();
    }

    public int editWorker(String id, String name, String department, String gender, String work) {
        return workerMapper.update(id, name, department, gender, work);
    }

    public int fireWorker(String id) {
        return workerMapper.deleteById(id);
    }
}
