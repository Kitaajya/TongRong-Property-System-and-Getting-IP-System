package org.designer.tongrong_property_company_2nd.purchase;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**数据库名->PurchaseBase.products**/
/**
 * 备用参数
 * @RequestParam String productsName,
 * @RequestParam String productsCategory,
 * @RequestParam double productsPrice,
 * @RequestParam int productsStock,
 * @RequestParam String productsSupplier,
 * @RequestParam String productsDescription,
 * **/

@Slf4j
@RestController
@RequestMapping("/api/purchase")

public class PurchaseBase {
    /**判断输入内容是否为空**/
    public boolean isNullOrEmpty(String name) {
        return name == null || name.isEmpty();
    }

    /**判断商品是否存在**/
    public boolean isContains(String name) {
        if (isNullOrEmpty(name)) return false;
        String SELECT_PRODUCTS = "SELECT COUNT(*) FROM PurchaseBase.products WHERE name = ?";
        Integer count = jdbcTemplate.queryForObject(SELECT_PRODUCTS, Integer.class, name);
        return count != null && count > 0;
    }

    /**从会话读取当前角色：merchant / manager / user**/
    private String currentRole(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) return null;
        Map<?, ?> loginUser = (Map<?, ?>) session.getAttribute("loginUser");
        Object roleObj = loginUser.get("role");
        String role = (roleObj == null || String.valueOf(roleObj).isBlank())
                ? (Boolean.TRUE.equals(loginUser.get("isOrdinaryUser")) ? "merchant" : "user")
                : String.valueOf(roleObj);
        return role;
    }

    /**从会话读取当前登录用户名，未登录返回 null**/
    private String currentUsername(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) return null;
        Map<?, ?> loginUser = (Map<?, ?>) session.getAttribute("loginUser");
        Object u = loginUser.get("username");
        return u == null ? null : String.valueOf(u);
    }

    /**商家专属校验，非商家返回错误 Map，商家返回 null**/
    private Map<String, Object> requireMerchant(HttpServletRequest request) {
        if (!"merchant".equals(currentRole(request)))
            return Map.of("success", false, "message", "仅商家可进行商品增删改操作");
        return null;
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/travel")
    public List<Map<String, Object>> travelProducts() {
        log.info("查看商品库");
        return jdbcTemplate.queryForList("SELECT * FROM PurchaseBase.products");
    }

    @GetMapping("/select/single/product")
    public List<Map<String, Object>> selectSingleProduct(@RequestParam String selectName) {
        log.info("查找单个商品");
        if (!isContains(selectName)) return new ArrayList<>();
        String SELECT_SQL = "select name,price,description,supplier,category from PurchaseBase.products where name=?";
        return jdbcTemplate.queryForList(SELECT_SQL, selectName);
    }

    /**增强查询，模糊查询**/
    @GetMapping("/select/single/product/undefined")
    public List<Map<String, Object>> selectSingleUndefinedProduct(@RequestParam String undefinedName) {
        log.info("模糊查找单个商品");
        if (isNullOrEmpty(undefinedName)) return new ArrayList<>();
        String SELECT_UNDEFINED_SQL = "SELECT name,price,description,supplier,category FROM PurchaseBase.products " +
                "WHERE name LIKE ? OR description LIKE ? OR supplier LIKE ?";
        return jdbcTemplate.queryForList(SELECT_UNDEFINED_SQL,
                "%" + undefinedName + "%",
                "%" + undefinedName + "%",
                "%" + undefinedName + "%");
    }
    /**添加单个商品**/
    @PostMapping("/add/single/product")
    public Map<String, Object> addSingleProduct(HttpServletRequest request,
                                                @RequestParam String productsName,
                                                @RequestParam String productsCategory,
                                                @RequestParam double productsPrice,
                                                @RequestParam int productsStock,
                                                @RequestParam String productsSupplier,
                                                @RequestParam String productsDescription) {
        log.info("添加单个商品");
        Map<String, Object> denied = requireMerchant(request);
        if (denied != null) return denied;
        if (productsName.isEmpty() || productsCategory.isEmpty() || productsSupplier.isEmpty() || productsDescription.isEmpty())
            return Map.of("success", false, "message", "商品信息无效");
        String INSERT_Single_Product =
                "INSERT INTO PurchaseBase.products (name, price, description, supplier, category, stock, create_time, update_time) " +
                        "VALUES (?, ?, ?,?, ?,?, NOW(), NOW())";
        int row = jdbcTemplate.update(INSERT_Single_Product, productsName, productsPrice,
                productsDescription, productsSupplier, productsCategory, productsStock);
        if (row > 0) return Map.of("success", true, "message", "商品添加成功", "productName", productsName);
        else return Map.of("success", false, "message", "商品添加失败");
    }

    /**添加更多商品**/
    @PostMapping("/add/more/product")
    public Map<String, Object> addMoreProduct(HttpServletRequest request,
                                              @RequestParam String productsName,
                                              @RequestParam String productsCategory,
                                              @RequestParam double productsPrice,
                                              @RequestParam int productsStock,
                                              @RequestParam String productsSupplier,
                                              @RequestParam String productsDescription,
                                              @RequestParam int quantity) {
        log.info("添加更多商品");
        Map<String, Object> denied = requireMerchant(request);
        if (denied != null) return denied;
        if (quantity <= 0) return Map.of("success", false, "message", "商品个数必须是大于零的数！");
        String insertMoreProduct =
                "INSERT INTO PurchaseBase.products (name, price, description, supplier, category, stock, create_time, update_time) " +
                        "VALUES (?,?,?,?,?,?,NOW(),NOW())";
        if (productsName.isEmpty() || productsCategory.isEmpty() || productsSupplier.isEmpty() || productsDescription.isEmpty())
            return Map.of("success", false, "message", "商品信息无效");
        for (int i = 0; i < quantity; i++) {
            int rows = jdbcTemplate.update(insertMoreProduct, productsName, productsPrice,
                    productsDescription, productsSupplier, productsCategory, productsStock);
            if (rows <= 0) return Map.of("success", false, "message", "商品添加失败");
            log.info("第{}个商品", i + 1);
        }
        return Map.of("success", true, "message", "添加成功");
    }

    /**查询单个商品的价格**/
    @GetMapping("/select/single/product/price")
    public List<Map<String, Object>> selectOfSinglePrice(@RequestParam String productName) {
        log.info("查询单个商品的价格");
        if (isNullOrEmpty(productName))
            return List.of(Map.of("success", false, "message", "商品信息无效"));
        String selectProduct = "select price from PurchaseBase.products where name = ?";
        return jdbcTemplate.queryForList(selectProduct, productName);
    }

    /**查询更多商品的价格**/
    @GetMapping("/select/more/product/price")
    public List<Map<String, Object>> selectMoreProductsPrices(@RequestParam String productsName) {
        log.info("查询更多商品的价格");
        if (isNullOrEmpty(productsName)) return new ArrayList<>();
        String selectPrices = "select price from PurchaseBase.products where name = ?";
        List<Map<String, Object>> PRICE = new ArrayList<>();
        for (String name : productsName.split(",")) {
            String trimmed = name.trim();
            if (trimmed.isEmpty()) continue;
            Map<String, Object> PRICE_MAP = new HashMap<>();
            PRICE_MAP.put(trimmed, jdbcTemplate.queryForList(selectPrices, trimmed));
            PRICE.add(PRICE_MAP);
        }
        return PRICE;
    }

    /**计算总价**/
    @GetMapping("/calculate/total/price")
    public Map<String, Object> calculateTotalPrice(@RequestParam String products,
                                                   @RequestParam int quantity) {
        log.info("计算总价");
        if (products == null || products.isEmpty() || quantity <= 0)
            return Map.of("success", false, "message", "参数无效");
        if (!isContains(products))
            return Map.of("success", false, "message", "商品不存在");
        List<Map<String, Object>> priceList =
                jdbcTemplate.queryForList("select price from PurchaseBase.products where name = ? limit 1", products);
        if (priceList.isEmpty())
            return Map.of("success", false, "message", "商品不存在");
        double total = ((Number) priceList.get(0).get("price")).doubleValue() * quantity;
        return Map.of("success", true, "totalPrice", total);
    }

    /**带商品名的计算总价**/
    @GetMapping("/calculate/total/price/with/name")
    public Map<String, Object> selectProductsWithTotalPrice(@RequestParam String productsName) {
        log.info("带商品名的计算总价");
        if (isNullOrEmpty(productsName))
            return Map.of("success", false, "message", "商品信息无效");

        double total = 0;
        for (String name : productsName.split(",")) {
            String trimmed = name.trim();
            if (trimmed.isEmpty()) continue;
            List<Map<String, Object>> priceList =
                    jdbcTemplate.queryForList("select price from PurchaseBase.products where name = ? limit 1", trimmed);
            if (!priceList.isEmpty())
                total += ((Number) priceList.get(0).get("price")).doubleValue();
        }
        return Map.of("success", true, "totalPrice", total);
    }

    /**
     * 改了 4 处：
     * 1. jdbcTemplate.update(sql, id, productName) 补上参数（顺序：id → name）
     * 2. @GetMapping → @DeleteMapping，删除操作不该用 GET（否则浏览器或爬虫也能删数据）
     * 3. 判断改成 rows > 0，删除成功是返回 1
     * 4. @RequestParam int id 记得加 @RequestParam，否则必填参数名必须是方法参数名 id
     **/

    /**删除一个商品**/
    @DeleteMapping("/delete/single/product")
    public Map<String, Object> deleteSingleProduct(HttpServletRequest request,
                                                   @RequestParam String productName,
                                                   @RequestParam int id) {
        log.info("删除一个商品");
        Map<String, Object> denied = requireMerchant(request);
        if (denied != null) return denied;
        if (isNullOrEmpty(productName))
            return Map.of("success", false, "message", "商品不能为空");
        if (!isContains(productName))
            return Map.of("success", false, "message", "商品不存在");
        String DELETE_INFORMATION = "DELETE FROM  PurchaseBase.products WHERE (id=? AND name = ?)";
        int rows = jdbcTemplate.update(DELETE_INFORMATION, id, productName);
        if (rows <= 0) return Map.of("success", false, "message", "参数无效/商品不存在/id或商品名输入错误");
        return Map.of("success", true, "message", "删除成功");
    }

    /**修改商品**/
    @PutMapping("/edit/single/product")
    public Map<String, Object> editSingleProduct(HttpServletRequest request,
                                                 @RequestParam String productsName,
                                                 @RequestParam int id,
                                                 @RequestParam String productsCategory,
                                                 @RequestParam double productsPrice,
                                                 @RequestParam int productsStock,
                                                 @RequestParam String productsSupplier,
                                                 @RequestParam String productsDescription) {
        log.info("修改商品");
        Map<String, Object> denied = requireMerchant(request);
        if (denied != null) return denied;
        if (isNullOrEmpty(productsName))
            return Map.of("success", false, "message", "商品不能为空");
        if (!isContains(productsName))
            return Map.of("success", false, "message", "商品不存在");
        String EDIT_INFORMATION = """
            
                UPDATE PurchaseBase.products
            SET category = ?, price = ?, stock = ?, supplier = ?, description = ?, update_time = NOW()
            WHERE id = ? AND name = ?
            """;
        int rows = jdbcTemplate.update(EDIT_INFORMATION,
                productsCategory, productsPrice, productsStock,
                productsSupplier, productsDescription, id, productsName);
        if (rows > 0) return Map.of("success", true, "message", "商品更新成功");
        return Map.of("success", false, "message", "商品不存在或更新失败");
    }
    /**插入一条商品评论**/
    @PostMapping("/comment/insert") public Map<String,Object> commentInsert(@RequestParam String productName,
                                            @RequestParam String content,
                                            HttpServletRequest request ){
        log.info("插入一条商品评论");
        if(isNullOrEmpty (productName)||isNullOrEmpty(content))
            return Map.of("success", false, "message", "商品名或评论内容不能为空");
        //从会话中取当前登录用户,取不到则记为匿名
        String username = currentUsername(request);
        if (username == null) username = "匿名";
        final String author = username;
        final String pName = productName.trim();
        final String pContent = content.trim();
        //插入评论并取回自增ID
        String INSERT_COMMENT =
                "INSERT INTO PurchaseBase.comments (product_name, username, content) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rows = jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(INSERT_COMMENT, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, pName);
            ps.setString(2, author);
            ps.setString(3, pContent);
            return ps;
        }, keyHolder);
        if (rows <= 0) return Map.of("success", false, "message", "评论插入失败");

        //若为回复某条评论([回复#父ID]开头)，给被回复人生成一条"收到评论消息"
        notifyReplyMessage(pName, author, pContent, keyHolder.getKey() == null ? 0 : ((Number) keyHolder.getKey()).intValue());
        return Map.of("success", true, "message", "评论插入成功");
    }

    /**回复他人评论时，给被回复人生成一条消息通知**/
    private static final Pattern REPLY_PATTERN = Pattern.compile("^\\[回复#(\\d+)\\]");
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
        List<Map<String, Object>> parents = jdbcTemplate.queryForList(
                "SELECT username FROM PurchaseBase.comments WHERE id = ?", parentId);
        if (parents.isEmpty()) return;
        String receiver = String.valueOf(parents.get(0).get("username"));
        if (receiver == null || receiver.isBlank() || receiver.equals(sender)) return;
        jdbcTemplate.update(
                "INSERT INTO PurchaseBase.comment_messages (receiver, sender, product_name, comment_id, content) VALUES (?, ?, ?, ?, ?)",
                receiver, sender, productName, newCommentId, content);
        log.info("已通知{}收到来自{}的评论回复", receiver, sender);
    }

    /**我收到的评论消息列表**/
    @GetMapping("/message/list")
    public List<Map<String,Object>> messageList(HttpServletRequest request){
        String username = currentUsername(request);
        if (username == null) return new ArrayList<>();
        return jdbcTemplate.queryForList(
                "SELECT id, sender, product_name, comment_id, content, is_read, create_time " +
                        "FROM PurchaseBase.comment_messages WHERE receiver = ? ORDER BY id DESC", username);
    }

    /**我未读的评论消息数量**/
    @GetMapping("/message/unread-count")
    public Map<String,Object> messageUnreadCount(HttpServletRequest request){
        String username = currentUsername(request);
        if (username == null) return Map.of("count", 0);
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM PurchaseBase.comment_messages WHERE receiver = ? AND is_read = 0",
                Integer.class, username);
        return Map.of("count", count == null ? 0 : count);
    }

    /**标记评论消息已读（id 为空则全部已读）**/
    @PutMapping("/message/read")
    public Map<String,Object> messageRead(@RequestParam(required = false) Integer id, HttpServletRequest request){
        String username = currentUsername(request);
        if (username == null)
            return Map.of("success", false, "message", "未登录");
        int updated;
        if (id != null)
            updated = jdbcTemplate.update(
                    "UPDATE PurchaseBase.comment_messages SET is_read = 1 WHERE id = ? AND receiver = ?", id, username);
        else
            updated = jdbcTemplate.update(
                    "UPDATE PurchaseBase.comment_messages SET is_read = 1 WHERE receiver = ? AND is_read = 0", username);
        return Map.of("success", true, "message", "操作成功", "updated", updated);
    }
    /**查询评论,可按商品名过滤**/
    @GetMapping("/comment/list")
    public List<Map<String,Object>> commentList(@RequestParam(required = false) String productName){
        String SELECT_COMMENTS_IF_NULL_OR_EMPTY ="SELECT id, product_name, username, content, create_time FROM PurchaseBase.comments ORDER BY id DESC";
        String SELECT_COMMENTS_AND_NOT_NULL_OR_EMPTY="SELECT id, product_name, username, content, create_time FROM PurchaseBase.comments WHERE product_name = ? ORDER BY id DESC";
        if (isNullOrEmpty(productName))
            return jdbcTemplate.queryForList(SELECT_COMMENTS_IF_NULL_OR_EMPTY);
        return jdbcTemplate.queryForList(SELECT_COMMENTS_AND_NOT_NULL_OR_EMPTY, productName.trim());
    }
    /**
     * 1.采购订单->表（商品+数量+供应商+总价）,状态流转:待审批->已审批->已收货->已取消。
     * 2.下单自动扣库存 + 库存校验(stock 不足就拒绝下单)。
     * 3.库存预警->  stock <= 阈值的列表，如果库存小于n，触发预警。
 **/
    //审批商品(通过/驳回)
    @PutMapping("/review")
    public Map<String,Object> reviewAndApprove(@RequestParam int id,
                                               @RequestParam String status,
                                               HttpServletRequest request) {
        Map<String, Object> denied = requireMerchant(request);
        if (denied != null) return denied;
        if (isNullOrEmpty(status))
            return Map.of("success", false, "message", "审批状态不能为空");
        if (!status.equals("已通过") && !status.equals("已驳回"))
            return Map.of("success", false, "message", "状态只能为:已通过 或 已驳回");
        String REVIEW_SQL = "UPDATE PurchaseBase.products SET status = ?, update_time = NOW() WHERE id = ?";
        int rows = jdbcTemplate.update(REVIEW_SQL, status, id);
        if (rows > 0) return Map.of("success", true, "message", "审批成功", "id", id, "status", status);
        return Map.of("success", false, "message", "商品不存在,审批失败");
    }
    /**查看待审核商品**/
    @GetMapping("/pending")
    public List<Map<String ,Object>> pendingProducts(){
        String PENDING_SQL="SELECT id,name,price,description,supplier,category,stock FROM PurchaseBase.products WHERE status = '待审核'";
        return jdbcTemplate.queryForList(PENDING_SQL);
    }
    /**库存预警列表：列出所有库存低于阈值(默认12)的商品**/
    @GetMapping("/stock/warning")
    public List<Map<String, Object>> stockWarning(@RequestParam(defaultValue = "12") int threshold) {
        if (threshold <= 0) return new ArrayList<>();
        String WARNING_SQL = "SELECT id, name, category, supplier, stock, update_time " +
                "FROM PurchaseBase.products WHERE stock < ? ORDER BY stock ASC";
        return jdbcTemplate.queryForList(WARNING_SQL, threshold);
    }

    /**检查仓库库存是否到达n阈值，否则达到库存预警**/
    @GetMapping("/check/stock")
    public Map<String, Object> checkStock(@RequestParam String productName) {
        if (isNullOrEmpty(productName))
            return Map.of("success", false, "message", "商品不能为空！", "canBeSend", false);
        if (!isContains(productName))
            return Map.of("success", false, "message", "商品不存在或为空", "canBeSend", false);

        int DEFAULT_VALUE_OF_STOCK = 12;
        String FIND_STOCK = "SELECT STOCK FROM PurchaseBase.products WHERE name=?";
        Integer k = jdbcTemplate.queryForObject(FIND_STOCK, Integer.class, productName);
        if (k == null)
            return Map.of("success", false, "message", "库存数据异常，库存量为空", "canBeSend", false);
        if (k < DEFAULT_VALUE_OF_STOCK)
            return Map.of("success", false, "message", "库存不够，数量：" + k + "个，无法发货", "canBeSend", false);

        return Map.of("success", true, "message", "库存充足，数量：" + k + "个", "canBeSend", true);
    }
    /**发送单个商品**/
    @PutMapping("/send/single/product")
    @Transactional
    public Map<String, Object> sendSingleProduct(@RequestParam String productName,
                                                 HttpServletRequest request) {
        Map<String, Object> denied = requireMerchant(request);
        if (denied != null) return denied;
        if (!isContains(productName))
            return Map.of("success", false, "message", "商品不存在");
        // 原子扣减，只有库存>=阈值时才扣，避免竞态
        String UPDATE_STOCK = "UPDATE PurchaseBase.products SET STOCK = STOCK - 1 WHERE name=? AND STOCK > ?";
        int rows = jdbcTemplate.update(UPDATE_STOCK, productName, 12);
        if (rows == 0)
            return Map.of("success", false, "message", "库存不足或商品不存在");
        return Map.of("success", true, "message", "准备发货，库存-1");
    }

    /**读取登录用户角色权限：
     * 商家(merchant)：可增删改商品，看不到员工模块。
     * 物管人员(manager)：可查看员工模块与数据，无商品增删改权限。
     * 普通用户(user)：仅可浏览商品，无增删改、无员工模块。
     **/
    @PutMapping("/identity/check")
    public Map<String,Object> revokeAndGrant(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null)
            return Map.of("success", false, "message", "未登录", "canModify", false, "canViewEmployees", false);
        Map<?, ?> loginUser = (Map<?, ?>) session.getAttribute("loginUser");
        Object roleObj = loginUser.get("role");
        String role = (roleObj == null || String.valueOf(roleObj).isBlank())
                ? (Boolean.TRUE.equals(loginUser.get("isOrdinaryUser")) ? "merchant" : "user")
                : String.valueOf(roleObj);
        boolean canModify = "merchant".equals(role);
        boolean canViewEmployees = "manager".equals(role);
        String message;
        if (canModify) message = "商家身份，可修改商品";
        else if (canViewEmployees) message = "物管人员，可查看员工模块，无商品修改权限";
        else message = "普通用户，无商品修改权限";
        return Map.of("success", true, "message", message, "canModify", canModify, "canViewEmployees", canViewEmployees);
    }

    /**授予商家权限，仅限物管人员(manager)操作，将目标用户升级为商家(merchant)**/
    @PutMapping("/identity/set")
    public Map<String,Object> setRevokeAndGrant(HttpServletRequest request, @RequestParam String nameWill){
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null)
            return Map.of("success", false, "message", "未登录", "canModify", false);
        Map<?, ?> loginUser = (Map<?, ?>) session.getAttribute("loginUser");
        String role = String.valueOf(loginUser.get("role"));
        if (!"manager".equals(role))
            return Map.of("success", false, "message", "仅物管人员可设置商家权限");
        if (isNullOrEmpty(nameWill)) return Map.of("success", false, "message", "姓名不能为空");
        String IS_CONTAINS_NAME = "SELECT username FROM LogIn.users WHERE username=?";
        if (jdbcTemplate.queryForList(IS_CONTAINS_NAME, nameWill).isEmpty())
            return Map.of("success", false, "message", "找不到此人");
        //升级为商家，同时维护 role 与旧字段 is_ordinary_user
        String SET_RIGHT = "UPDATE LogIn.users SET is_ordinary_user = 1, role = 'merchant' WHERE username=?";
        jdbcTemplate.update(SET_RIGHT, nameWill);
        return Map.of("success", true, "message", "已授予商家权限");
    }
}
