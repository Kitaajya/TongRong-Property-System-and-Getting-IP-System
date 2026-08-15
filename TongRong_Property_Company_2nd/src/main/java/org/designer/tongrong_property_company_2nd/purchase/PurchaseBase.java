package org.designer.tongrong_property_company_2nd.purchase;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 备用参数
 * @RequestParam String productsName, @RequestParam String productsCategory,
 * @RequestParam double productsPrice, @RequestParam int productsStock,
 * @RequestParam String productsSupplier, @RequestParam String productsDescription,
 * **/
//数据库名——>PurchaseBase.products
@Slf4j
@RestController
@RequestMapping("/api/purchase")

public class PurchaseBase {
    //判断输入内容是否为空
    public boolean isNullOrEmpty(String name) {
        return name == null || name.isEmpty();
    }

    //判断商品是否存在
    public boolean isContains(String name) {
        if (isNullOrEmpty(name)) return false;
        String SELECT_PRODUCTS = "SELECT COUNT(*) FROM PurchaseBase.products WHERE name = ?";
        Integer count = jdbcTemplate.queryForObject(SELECT_PRODUCTS, Integer.class, name);
        return count != null && count > 0;
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

    //增强查询，模糊查询
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
    //添加单个商品
    @PostMapping("/add/single/product")
    public Map<String, Object> addSingleProduct(@RequestParam String productsName,
                                                @RequestParam String productsCategory,
                                                @RequestParam double productsPrice,
                                                @RequestParam int productsStock,
                                                @RequestParam String productsSupplier,
                                                @RequestParam String productsDescription) {
        log.info("添加单个商品");
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

    //添加更多商品
    @PostMapping("/add/more/product")
    public Map<String, Object> addMoreProduct(@RequestParam String productsName,
                                              @RequestParam String productsCategory,
                                              @RequestParam double productsPrice,
                                              @RequestParam int productsStock,
                                              @RequestParam String productsSupplier,
                                              @RequestParam String productsDescription,
                                              @RequestParam int quantity) {
        log.info("添加更多商品");
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

    //查询单个商品的价格
    @GetMapping("/select/single/product/price")
    public List<Map<String, Object>> selectOfSinglePrice(@RequestParam String productName) {
        log.info("查询单个商品的价格");
        if (isNullOrEmpty(productName))
            return List.of(Map.of("success", false, "message", "商品信息无效"));
        String selectProduct = "select price from PurchaseBase.products where name = ?";
        return jdbcTemplate.queryForList(selectProduct, productName);
    }

    //查询更多商品的价格
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

    //计算总价
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

    //带商品名的计算总价
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
     *
     **/
    //删除一个商品
    @DeleteMapping("/delete/single/product")
    public Map<String, Object> deleteSingleProduct(@RequestParam String productName,
                                                   @RequestParam int id) {
        log.info("删除一个商品");
        if (isNullOrEmpty(productName))
            return Map.of("success", false, "message", "商品不能为空");
        if (!isContains(productName))
            return Map.of("success", false, "message", "商品不存在");
        String DELETE_INFORMATION = "DELETE FROM  PurchaseBase.products WHERE (id=? AND name = ?)";
        int rows = jdbcTemplate.update(DELETE_INFORMATION, id, productName);
        if (rows <= 0) return Map.of("success", false, "message", "参数无效/商品不存在/id或商品名输入错误");
        return Map.of("success", true, "message", "删除成功");
    }

    //修改商品
    @PutMapping("/edit/single/product")
    public Map<String, Object> editSingleProduct(@RequestParam String productsName,
                                                 @RequestParam int id,
                                                 @RequestParam String productsCategory,
                                                 @RequestParam double productsPrice,
                                                 @RequestParam int productsStock,
                                                 @RequestParam String productsSupplier,
                                                 @RequestParam String productsDescription) {
        log.info("修改商品");
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
    //插入一条商品评论
    @PostMapping("/comment/insert") public Map<String,Object> commentInsert(@RequestParam String productName,
                                            @RequestParam String content,
                                            HttpServletRequest request ){
        log.info("插入一条商品评论");
        if(isNullOrEmpty (productName)||isNullOrEmpty(content))
            return Map.of("success", false, "message", "商品名或评论内容不能为空");
        //从会话中取当前登录用户,取不到则记为匿名
        HttpSession session = request.getSession(false);
        String username = "匿名";
        if (session != null) {
            Object loginUser = session.getAttribute("loginUser");
            if (loginUser instanceof Map<?, ?> m && m.get("username") != null)
                username = String.valueOf(m.get("username"));
        }
        String INSERT_COMMENT =
                "INSERT INTO PurchaseBase.comments (product_name, username, content) VALUES (?, ?, ?)";
        int rows = jdbcTemplate.update(INSERT_COMMENT, productName.trim(), username, content.trim());
        if (rows > 0) return Map.of("success", true, "message", "评论插入成功");
        else return Map.of("success", false, "message", "评论插入失败");
    }
    //查询评论,可按商品名过滤
    @GetMapping("/comment/list")
    public List<Map<String,Object>> commentList(@RequestParam(required = false) String productName){
        String SELECT_COMMENTS_IF_NULL_OR_EMPTY ="SELECT id, product_name, username, content, create_time FROM PurchaseBase.comments ORDER BY id DESC";
        String SELECT_COMMENTS_AND_NOT_NULL_OR_EMPTY="SELECT id, product_name, username, content, create_time FROM PurchaseBase.comments WHERE product_name = ? ORDER BY id DESC";
        if (isNullOrEmpty(productName))
            return jdbcTemplate.queryForList(SELECT_COMMENTS_IF_NULL_OR_EMPTY);
        return jdbcTemplate.queryForList(SELECT_COMMENTS_AND_NOT_NULL_OR_EMPTY, productName.trim());
    }
    /**
     * 1.采购订单->表（商品+数量+供应商+总价）,状态流转:待审批->已审批->已收货->已取消  *
     * 2.下单自动扣库存 + 库存校验(stock 不足就拒绝下单)                           *
     * 3.库存预警->  stock <= 阈值的列表，如果库存小于n，触发预警
 **/
    //审批商品(通过/驳回)
    @PutMapping("/review")
    public Map<String,Object> reviewAndApprove(@RequestParam int id,
                                               @RequestParam String status,
                                               HttpServletRequest request) {
        if (isNullOrEmpty(status))
            return Map.of("success", false, "message", "审批状态不能为空");
        if (!status.equals("已通过") && !status.equals("已驳回"))
            return Map.of("success", false, "message", "状态只能为:已通过 或 已驳回");
        String REVIEW_SQL = "UPDATE PurchaseBase.products SET status = ?, update_time = NOW() WHERE id = ?";
        int rows = jdbcTemplate.update(REVIEW_SQL, status, id);
        if (rows > 0) return Map.of("success", true, "message", "审批成功", "id", id, "status", status);
        return Map.of("success", false, "message",

    "商品不存在,审批失败");
    }
    //查看待审核商品
    @GetMapping("/pending")
    public List<Map<String ,Object>> pendingProducts(){
        String PENDING_SQL="SELECT id,name,price,description,supplier,category,stock FROM PurchaseBase.products WHERE status = '待审核'";
        return jdbcTemplate.queryForList(PENDING_SQL);
    }
    //库存预警列表：列出所有库存低于阈值(默认12)的商品
    @GetMapping("/stock/warning")
    public List<Map<String, Object>> stockWarning(@RequestParam(defaultValue = "12") int threshold) {
        if (threshold <= 0) return new ArrayList<>();
        String WARNING_SQL = "SELECT id, name, category, supplier, stock, update_time " +
                "FROM PurchaseBase.products WHERE stock < ? ORDER BY stock ASC";
        return jdbcTemplate.queryForList(WARNING_SQL, threshold);
    }

    //检查仓库库存是否到达n阈值，否则达到库存预警
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
    //发送单个商品
    @PutMapping("/send/single/product")
    @Transactional
    public Map<String, Object> sendSingleProduct(@RequestParam String productName) {
        if (!isContains(productName))
            return Map.of("success", false, "message", "商品不存在");
        // 原子扣减，只有库存>=阈值时才扣，避免竞态
        String UPDATE_STOCK = "UPDATE PurchaseBase.products SET STOCK = STOCK - 1 WHERE name=? AND STOCK > ?";
        int rows = jdbcTemplate.update(UPDATE_STOCK, productName, 12);
        if (rows == 0)
            return Map.of("success", false, "message", "库存不足或商品不存在");
        return Map.of("success", true, "message", "准备发货，库存-1");
    }

    //读取注册用户身份，如果是普通用户，对商品操作没有任何增删改的操作权限。商家有权限修改商品信息
    @PutMapping("/identity/check")
    public Map<String,Object> revokeAndGrant(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null)
            return Map.of("success", false, "message", "未登录", "canModify", false);
        Map<?, ?> loginUser = (Map<?, ?>) session.getAttribute("loginUser");
        String username = String.valueOf(loginUser.get("username"));
        Integer k = jdbcTemplate.queryForObject(
                "SELECT is_ordinary_user FROM LogIn.users WHERE username = ?",
                Integer.class, username);
        if (k == null || k == 0)
            return Map.of("success", false, "message", "你没有任何权限修改商品", "canModify", false);
        return Map.of("success", true, "message", "商家身份，可修改商品", "canModify", true);
    }

    //UPDATE LogIn.users SET is_ordinary_user = 1 WHERE username='贾奕嘉'
    @PutMapping("/identity/set")
    public Map<String,Object> setRevokeAndGrant(Map<?,?> REVOKE_GRANT,@RequestParam String nameWill){
        if(REVOKE_GRANT
                .equals(Map.of("success", false, "message", "你没有任何权限修改商品", "canModify", false))){
            return Map.of("success", false, "message", "你没有任何权限修改商品", "canModify", false);
        }
        //表中找不到人
        String SET_RIGHT="UPDATE LogIn.users SET is_ordinary_user = 1 WHERE username=?";
        String IS_CONTAINS_NAME="SELECT username FROM LogIn.users WHERE username=?";
        if(jdbcTemplate.queryForList(IS_CONTAINS_NAME,nameWill).isEmpty())
            return Map.of("success",false,"message","找不到此人");
        //输入null姓名
        if(isNullOrEmpty(nameWill)) return Map.of("success",false,"message","姓名不能为空");
        else {
            jdbcTemplate.update(SET_RIGHT,nameWill);
            return Map.of("success",true,"message","修改成功!");
        }
    }
}
