package org.designer.tongrong_property_company_2nd;

import lombok.RequiredArgsConstructor;
import org.designer.tongrong_property_company_2nd.service.DatabaseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GetNameOfDatabaseWhichWeForgot {

    private final DatabaseService databaseService;

    @RequestMapping("/tables")
    public List<Map<String, Object>> tables() {
        return databaseService.showTables();
    }

    @GetMapping("/travel_log_in")
    public List<Map<String, Object>> travelTable() {
        return databaseService.travelLogIn();
    }

    @GetMapping("/travel_exception")
    public List<Map<String, Object>> travelExceptionTable() {
        return databaseService.travelExceptionTable();
    }
}
