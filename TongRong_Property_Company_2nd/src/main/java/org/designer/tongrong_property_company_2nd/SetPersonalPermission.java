package org.designer.tongrong_property_company_2nd;

import lombok.RequiredArgsConstructor;
import org.designer.tongrong_property_company_2nd.service.PermissionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/permission")
@RequiredArgsConstructor
public class SetPersonalPermission {

    private final PermissionService permissionService;

    @GetMapping("/get")
    public List<Map<String, Object>> permission(@RequestParam String name,
                                                @RequestParam String personalNumber) {
        return permissionService.verifyPerson(name, personalNumber);
    }
}
