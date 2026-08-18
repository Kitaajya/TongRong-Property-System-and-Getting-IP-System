package org.designer.tongrong_property_company_2nd.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.designer.tongrong_property_company_2nd.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;

    public Map<String, Object> grantMerchantRole(String nameWill) {
        if (nameWill == null || nameWill.isEmpty())
            return Map.of("success", false, "message", "姓名不能为空");
        if (!userMapper.selectUserExists(nameWill))
            return Map.of("success", false, "message", "找不到此人");
        userMapper.grantMerchantRole(nameWill);
        return Map.of("success", true, "message", "已授予商家权限");
    }

    public Map<String, Object> editName(String oldName, String newName) {
        log.info("改名操作");
        if (newName == null || newName.isEmpty())
            return Map.of("success", false, "message", "新用户名不能为空");

        int rowLogin = userMapper.updateLoginUsername(oldName, newName);
        if (rowLogin <= 0) return Map.of("success", false, "message", "登录表改名失败！");

        int rowPurchase = userMapper.updateCommentUsername(oldName, newName);
        if (rowPurchase <= 0) return Map.of("success", false, "message", "商品表改名失败！");

        userMapper.updateMessageSender(oldName, newName);
        userMapper.updateMessageReceiver(oldName, newName);
        userMapper.updateLikeUsername(oldName, newName);

        return Map.of("success", true, "message", "改名成功");
    }
}
