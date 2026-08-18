package org.designer.tongrong_property_company_2nd.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class CommentMapper {

    private final JdbcTemplate jdbcTemplate;

    public Integer insertComment(String productName, String username, String content) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO PurchaseBase.comments (product_name, username, content) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, productName);
            ps.setString(2, username);
            ps.setString(3, content);
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        return key == null ? null : key.intValue();
    }

    public String selectCommentAuthor(int id) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT id, username FROM PurchaseBase.comments WHERE id = ?", id);
        if (rows.isEmpty()) return null;
        return String.valueOf(rows.get(0).get("username"));
    }

    public boolean selectCommentExists(int id) {
        return !jdbcTemplate.queryForList(
                "SELECT id FROM PurchaseBase.comments WHERE id = ?", id).isEmpty();
    }

    public int deleteCommentLikesByCommentId(int commentId) {
        return jdbcTemplate.update("DELETE FROM PurchaseBase.comment_likes WHERE comment_id = ?", commentId);
    }

    public int deleteCommentMessagesByCommentId(int commentId) {
        return jdbcTemplate.update("DELETE FROM PurchaseBase.comment_messages WHERE comment_id = ?", commentId);
    }

    public int deleteCommentById(int id) {
        return jdbcTemplate.update("DELETE FROM PurchaseBase.comments WHERE id = ?", id);
    }

    public String selectParentUsername(int parentId) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT username FROM PurchaseBase.comments WHERE id = ?", parentId);
        if (rows.isEmpty()) return null;
        return String.valueOf(rows.get(0).get("username"));
    }

    public int insertMessage(String receiver, String sender, String productName, int commentId, String content) {
        return jdbcTemplate.update(
                "INSERT INTO PurchaseBase.comment_messages (receiver, sender, product_name, comment_id, content) VALUES (?, ?, ?, ?, ?)",
                receiver, sender, productName, commentId, content);
    }

    public List<Map<String, Object>> selectMessagesByUsername(String username) {
        return jdbcTemplate.queryForList(
                "SELECT id, sender, product_name, comment_id, content, is_read, create_time " +
                        "FROM PurchaseBase.comment_messages WHERE receiver = ? ORDER BY id DESC", username);
    }

    public int countUnreadMessages(String username) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM PurchaseBase.comment_messages WHERE receiver = ? AND is_read = 0",
                Integer.class, username);
        return count == null ? 0 : count;
    }

    public int markMessageReadById(int id, String username) {
        return jdbcTemplate.update(
                "UPDATE PurchaseBase.comment_messages SET is_read = 1 WHERE id = ? AND receiver = ?", id, username);
    }

    public int markAllMessagesRead(String username) {
        return jdbcTemplate.update(
                "UPDATE PurchaseBase.comment_messages SET is_read = 1 WHERE receiver = ? AND is_read = 0", username);
    }

    public List<Map<String, Object>> selectAllComments() {
        return jdbcTemplate.queryForList(
                "SELECT id, product_name, username, content, like_count, create_time " +
                        "FROM PurchaseBase.comments ORDER BY id DESC");
    }

    public List<Map<String, Object>> selectCommentsByProductName(String productName) {
        return jdbcTemplate.queryForList(
                "SELECT id, product_name, username, content, like_count, create_time " +
                        "FROM PurchaseBase.comments WHERE product_name = ? ORDER BY id DESC", productName);
    }

    public List<Integer> selectLikedCommentIds(String username) {
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                "SELECT comment_id FROM PurchaseBase.comment_likes WHERE username = ?", username);
        return rows.stream()
                .map(row -> ((Number) row.get("comment_id")).intValue())
                .toList();
    }

    public boolean selectLikeExists(int commentId, String username) {
        return !jdbcTemplate.queryForList(
                "SELECT id FROM PurchaseBase.comment_likes WHERE comment_id = ? AND username = ?",
                commentId, username).isEmpty();
    }

    public int insertLike(int commentId, String username) {
        return jdbcTemplate.update(
                "INSERT INTO PurchaseBase.comment_likes (comment_id, username) VALUES (?, ?)", commentId, username);
    }

    public int deleteLike(int commentId, String username) {
        return jdbcTemplate.update(
                "DELETE FROM PurchaseBase.comment_likes WHERE comment_id = ? AND username = ?", commentId, username);
    }

    public int decrementLikeCount(int commentId) {
        return jdbcTemplate.update("UPDATE PurchaseBase.comments SET like_count = like_count - 1 WHERE id = ? AND like_count > 0", commentId);
    }

    public int incrementLikeCount(int commentId) {
        return jdbcTemplate.update("UPDATE PurchaseBase.comments SET like_count = like_count + 1 WHERE id = ?", commentId);
    }
}
