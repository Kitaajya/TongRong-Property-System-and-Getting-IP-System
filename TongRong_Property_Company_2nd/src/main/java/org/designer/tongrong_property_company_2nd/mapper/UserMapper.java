package org.designer.tongrong_property_company_2nd.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserMapper {

    private final JdbcTemplate jdbcTemplate;

    public boolean selectUserExists(String username) {
        return !jdbcTemplate.queryForList(
                "SELECT username FROM LogIn.users WHERE username=?", username).isEmpty();
    }

    public int grantMerchantRole(String username) {
        return jdbcTemplate.update(
                "UPDATE LogIn.users SET is_ordinary_user = 1, role = 'merchant' WHERE username=?", username);
    }

    public int updateLoginUsername(String oldName, String newName) {
        return jdbcTemplate.update(
                "UPDATE LogIn.users SET username=?, full_name=? WHERE username=?", newName, newName, oldName);
    }

    public int updateCommentUsername(String oldName, String newName) {
        return jdbcTemplate.update(
                "UPDATE PurchaseBase.comments SET username=? WHERE username=?", newName, oldName);
    }

    public int updateMessageSender(String oldName, String newName) {
        return jdbcTemplate.update(
                "UPDATE PurchaseBase.comment_messages SET sender=? WHERE sender=?", newName, oldName);
    }

    public int updateMessageReceiver(String oldName, String newName) {
        return jdbcTemplate.update(
                "UPDATE PurchaseBase.comment_messages SET receiver=? WHERE receiver=?", newName, oldName);
    }

    public int updateLikeUsername(String oldName, String newName) {
        return jdbcTemplate.update(
                "UPDATE PurchaseBase.comment_likes SET username=? WHERE username=?", newName, oldName);
    }
}
