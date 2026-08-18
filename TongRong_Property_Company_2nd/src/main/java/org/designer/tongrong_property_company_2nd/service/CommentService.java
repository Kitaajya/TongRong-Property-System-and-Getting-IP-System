package org.designer.tongrong_property_company_2nd.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.designer.tongrong_property_company_2nd.mapper.CommentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentMapper commentMapper;

    private static final Pattern REPLY_PATTERN = Pattern.compile("^\\[回复#(\\d+)\\]");

    public Map<String, Object> insertComment(String productName, String content, String username) {
        log.info("插入一条商品评论");
        if (productName == null || productName.isEmpty() || content == null || content.isEmpty())
            return Map.of("success", false, "message", "商品名或评论内容不能为空");
        String author = username == null ? "匿名" : username;
        String trimmedName = productName.trim();
        String trimmedContent = content.trim();

        Integer commentId = commentMapper.insertComment(trimmedName, author, trimmedContent);
        if (commentId == null) return Map.of("success", false, "message", "评论插入失败");

        notifyReplyMessage(trimmedName, author, trimmedContent, commentId);
        return Map.of("success", true, "message", "评论插入成功");
    }

    public Map<String, Object> deleteComment(int deleteId, String username) {
        log.info("删除评论");
        if (username == null) return Map.of("success", false, "message", "未登录");
        String author = commentMapper.selectCommentAuthor(deleteId);
        if (author == null) return Map.of("success", false, "message", "评论不存在");
        if (!author.equals(username))
            return Map.of("success", false, "message", "只能删除自己的评论");
        commentMapper.deleteCommentLikesByCommentId(deleteId);
        commentMapper.deleteCommentMessagesByCommentId(deleteId);
        commentMapper.deleteCommentById(deleteId);
        return Map.of("success", true, "message", "删除成功");
    }

    public List<Map<String, Object>> commentList(String productName, String username) {
        log.info("查询评论列表");
        List<Map<String, Object>> list;
        if (productName == null || productName.isEmpty())
            list = commentMapper.selectAllComments();
        else
            list = commentMapper.selectCommentsByProductName(productName.trim());

        if (username != null && !list.isEmpty()) {
            Set<Integer> likedIds = new HashSet<>(commentMapper.selectLikedCommentIds(username));
            for (Map<String, Object> c : list) {
                int cid = ((Number) c.get("id")).intValue();
                c.put("liked", likedIds.contains(cid));
            }
        } else {
            for (Map<String, Object> c : list) c.put("liked", false);
        }
        return list;
    }

    @Transactional
    public Map<String, Object> toggleLike(int commentId, String username) {
        log.info("点赞/取消点赞");
        if (username == null) return Map.of("success", false, "message", "未登录");
        if (!commentMapper.selectCommentExists(commentId))
            return Map.of("success", false, "message", "评论不存在");
        if (commentMapper.selectLikeExists(commentId, username)) {
            commentMapper.deleteLike(commentId, username);
            commentMapper.decrementLikeCount(commentId);
            return Map.of("success", true, "liked", false, "message", "已取消点赞");
        } else {
            commentMapper.insertLike(commentId, username);
            commentMapper.incrementLikeCount(commentId);
            return Map.of("success", true, "liked", true, "message", "点赞成功");
        }
    }

    public List<Map<String, Object>> messageList(String username) {
        if (username == null) return new ArrayList<>();
        return commentMapper.selectMessagesByUsername(username);
    }

    public Map<String, Object> messageUnreadCount(String username) {
        if (username == null) return Map.of("count", 0);
        return Map.of("count", commentMapper.countUnreadMessages(username));
    }

    public Map<String, Object> messageRead(Integer id, String username) {
        log.info("标记消息已读");
        if (username == null) return Map.of("success", false, "message", "未登录");
        int updated;
        if (id != null)
            updated = commentMapper.markMessageReadById(id, username);
        else
            updated = commentMapper.markAllMessagesRead(username);
        return Map.of("success", true, "message", "操作成功", "updated", updated);
    }

    private void notifyReplyMessage(String productName, String sender, String content, int newCommentId) {
        if (content == null || newCommentId <= 0) return;
        Matcher m = REPLY_PATTERN.matcher(content);
        if (!m.find()) return;
        int parentId;
        try {
            parentId = Integer.parseInt(m.group(1));
        } catch (NumberFormatException e) {
            return;
        }
        String receiver = commentMapper.selectParentUsername(parentId);
        if (receiver == null || receiver.isBlank() || receiver.equals(sender)) return;
        commentMapper.insertMessage(receiver, sender, productName, newCommentId, content);
        log.info("已通知{}收到来自{}的评论回复", receiver, sender);
    }
}
