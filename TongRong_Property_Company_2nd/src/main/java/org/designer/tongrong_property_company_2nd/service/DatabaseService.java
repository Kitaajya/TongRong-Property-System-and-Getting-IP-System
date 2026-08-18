package org.designer.tongrong_property_company_2nd.service;

import lombok.RequiredArgsConstructor;
import org.designer.tongrong_property_company_2nd.mapper.DatabaseMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DatabaseService {

    private final DatabaseMapper databaseMapper;

    public List<Map<String, Object>> showTables() {
        return databaseMapper.showTables();
    }

    public List<Map<String, Object>> travelLogIn() {
        return databaseMapper.selectAllFromLogIn();
    }

    public List<Map<String, Object>> travelExceptionTable() {
        return databaseMapper.selectAllFromExceptionTable();
    }
}
