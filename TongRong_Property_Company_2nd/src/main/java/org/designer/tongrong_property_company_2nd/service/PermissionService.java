package org.designer.tongrong_property_company_2nd.service;

import lombok.RequiredArgsConstructor;
import org.designer.tongrong_property_company_2nd.mapper.PermissionMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionMapper permissionMapper;

    public List<Map<String, Object>> verifyPerson(String name, String personalNumber) {
        return permissionMapper.findPerson(name, personalNumber);
    }
}
