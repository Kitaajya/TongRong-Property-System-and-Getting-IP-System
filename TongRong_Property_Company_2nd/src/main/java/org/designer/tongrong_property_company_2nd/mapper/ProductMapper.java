package org.designer.tongrong_property_company_2nd.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ProductMapper {

    private final JdbcTemplate jdbcTemplate;

    public int countByName(String name) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM PurchaseBase.products WHERE name = ?", Integer.class, name);
        return count == null ? 0 : count;
    }

    public List<Map<String, Object>> selectAll() {
        return jdbcTemplate.queryForList("SELECT * FROM PurchaseBase.products");
    }

    public List<Map<String, Object>> selectByName(String name) {
        return jdbcTemplate.queryForList(
                "SELECT name,price,description,supplier,category FROM PurchaseBase.products WHERE name=?", name);
    }

    public List<Map<String, Object>> selectByNameLike(String keyword) {
        String sql = "SELECT name,price,description,supplier,category FROM PurchaseBase.products " +
                "WHERE name LIKE ? OR description LIKE ? OR supplier LIKE ?";
        String like = "%" + keyword + "%";
        return jdbcTemplate.queryForList(sql, like, like, like);
    }

    public int insert(String name, double price, String description,
                      String supplier, String category, int stock) {
        String sql = "INSERT INTO PurchaseBase.products (name, price, description, supplier, category, stock, create_time, update_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, NOW(), NOW())";
        return jdbcTemplate.update(sql, name, price, description, supplier, category, stock);
    }

    public int deleteByIdAndName(int id, String name) {
        return jdbcTemplate.update(
                "DELETE FROM PurchaseBase.products WHERE (id=? AND name = ?)", id, name);
    }

    public int update(int id, String name, String category, double price, int stock,
                      String supplier, String description) {
        String sql = "UPDATE PurchaseBase.products SET category = ?, price = ?, stock = ?, " +
                "supplier = ?, description = ?, update_time = NOW() WHERE id = ? AND name = ?";
        return jdbcTemplate.update(sql, category, price, stock, supplier, description, id, name);
    }

    public List<Map<String, Object>> selectPriceByName(String name) {
        return jdbcTemplate.queryForList(
                "SELECT price FROM PurchaseBase.products WHERE name = ?", name);
    }

    public List<Map<String, Object>> selectPriceByNameLimit1(String name) {
        return jdbcTemplate.queryForList(
                "SELECT price FROM PurchaseBase.products WHERE name = ? LIMIT 1", name);
    }

    public int updateStatus(int id, String status) {
        return jdbcTemplate.update(
                "UPDATE PurchaseBase.products SET status = ?, update_time = NOW() WHERE id = ?", status, id);
    }

    public List<Map<String, Object>> selectPending() {
        return jdbcTemplate.queryForList(
                "SELECT id,name,price,description,supplier,category,stock FROM PurchaseBase.products WHERE status = '待审核'");
    }

    public List<Map<String, Object>> selectByStockBelow(int threshold) {
        return jdbcTemplate.queryForList(
                "SELECT id, name, category, supplier, stock, update_time " +
                        "FROM PurchaseBase.products WHERE stock < ? ORDER BY stock ASC", threshold);
    }

    public Integer selectStockByName(String name) {
        return jdbcTemplate.queryForObject(
                "SELECT stock FROM PurchaseBase.products WHERE name=?", Integer.class, name);
    }

    public int decrementStock(String name) {
        return jdbcTemplate.update(
                "UPDATE PurchaseBase.products SET stock = stock - 1 WHERE name=? AND stock > 0", name);
    }

    public int updateImageUrl(int id, String imageUrl) {
        return jdbcTemplate.update("UPDATE PurchaseBase.products SET image_url = ? WHERE id = ?", imageUrl, id);
    }
}
