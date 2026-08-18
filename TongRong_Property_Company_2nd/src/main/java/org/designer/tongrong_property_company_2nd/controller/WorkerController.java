package org.designer.tongrong_property_company_2nd.controller;

import lombok.RequiredArgsConstructor;
import org.designer.tongrong_property_company_2nd.service.WorkerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WorkerController {

    private final WorkerService workerService;

    @PostMapping("/add")
    public int insertWorker(@RequestParam String name,
                            @RequestParam String department,
                            @RequestParam String gender,
                            @RequestParam String work) {
        return workerService.addWorker(name, department, gender, work);
    }

    @RequestMapping("/select")
    public List<Map<String, Object>> selectAll() {
        return workerService.getAllWorkers();
    }

    @PostMapping("/edit")
    public int edit(@RequestParam String id,
                    @RequestParam String name,
                    @RequestParam String department,
                    @RequestParam String gender,
                    @RequestParam String work) {
        return workerService.editWorker(id, name, department, gender, work);
    }

    @GetMapping("/delete")
    public int delete(@RequestParam String id) {
        return workerService.fireWorker(id);
    }
}
