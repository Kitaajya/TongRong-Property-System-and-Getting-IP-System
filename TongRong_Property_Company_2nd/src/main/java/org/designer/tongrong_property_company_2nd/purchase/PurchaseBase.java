package org.designer.tongrong_property_company_2nd.purchase;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data

class ProductProperty{
    private int id;
    private String name;
    private String category;
    private double price;
    private int stock;
    private String supplier;
    private String description;
    private String create_time;
    private String update_time;
}

//数据库名——>PurchaseBase.products
@Slf4j
@RestController
@RequestMapping("/api/purchase")

public class PurchaseBase {

    public boolean isNullOrEmpty(String name){
        return name == null || name.isEmpty();
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @GetMapping("/travel")
    public List<Map<String,Object>> travelProducts(){
        log.info("查看商品库");
        return jdbcTemplate.queryForList("SELECT * FROM PurchaseBase.products");
    }
    @GetMapping("/select/single/product")
    public List<Map<String,Object>> selectSingleProduct(@RequestParam String selectName){
        log.info("查找单个商品");
        String selectSQL ="select name,price,description,supplier,category from PurchaseBase.products where name=?";
        return jdbcTemplate.queryForList(selectSQL,selectName);
    }
    @PostMapping("/add/single/product")
    public Map<String,Object> addSingleProduct(@RequestParam String productsName,
                                             @RequestParam String productsCategory,
                                             @RequestParam double productsPrice,
                                             @RequestParam int productsStock,
                                             @RequestParam String productsSupplier,
                                             @RequestParam String productsDescription){
        if(productsName.isEmpty()||productsCategory.isEmpty()||productsSupplier.isEmpty()|| productsDescription.isEmpty())
            return Map.of("success", false, "message", "商品信息无效");
        String insertSingleProduct=
                "INSERT INTO PurchaseBase.products (name, price, description, supplier, category, stock, create_time, update_time) " +
                        "VALUES (?, ?, ?,?, ?,?, NOW(), NOW())";
        int row=jdbcTemplate.update(insertSingleProduct,productsName, productsPrice,
                productsDescription,productsSupplier,productsCategory, productsStock);
        if (row > 0) return Map.of("success", true, "message", "商品添加成功", "productName", productsName);
        else return Map.of("success", false, "message", "商品添加失败");
    }
    //
    @PostMapping("/add/more/product")
    public Map<String, Object> addMoreProduct(@RequestParam String productsName, @RequestParam String productsCategory,
                                              @RequestParam double productsPrice, @RequestParam int productsStock,
                                              @RequestParam String productsSupplier, @RequestParam String productsDescription,
                                              @RequestParam int quantity) {
        String insertMoreProduct =
                "INSERT INTO PurchaseBase.products (name, price, description, supplier, category, stock, create_time, update_time) " +
                        "VALUES (?,?,?,?,?,?,NOW(),NOW())";
        if(productsName.isEmpty()||productsCategory.isEmpty()||productsSupplier.isEmpty()|| productsDescription.isEmpty())
            return Map.of("success", false, "message", "商品信息无效");
        for(int i=0;i<quantity;i++) {
            int rows=jdbcTemplate.update(insertMoreProduct,productsName,productsPrice,
                    productsDescription,productsSupplier,productsCategory,productsStock);
            if(rows<0) return Map.of("success", false, "message", "商品添加失败");
            log.info("第{}个商品",i + 1);
        }
        return Map.of("success", true, "message", "添加成功");
    }
    //查询单个商品的价格
    @GetMapping("/select/single/product/price")
    public List<Map<String,Object>> selectOfSinglePrice(@RequestParam String productName){
        if(isNullOrEmpty(productName)) return List.of(Map.of("success", false, "message", "商品信息无效"));
        String selectProduct="select price from PurchaseBase.products where name = ?";
        return jdbcTemplate.queryForList(selectProduct,productName);
    }
    //查询更多商品的价格
    @GetMapping("/select/more/product/price")
    public List<Map<String, Object>> selectMoreProductsPrices(@RequestParam String productsName) {
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
    public Map<String, Object> calculateTotalPrice(@RequestParam String products, @RequestParam int quantity) {
        if (products == null || products.isEmpty() || quantity <= 0)
            return Map.of("success", false, "message", "参数无效");
        List<Map<String, Object>> priceList =
                jdbcTemplate.queryForList("select price from PurchaseBase.products where name = ? limit 1", products);
        if (priceList.isEmpty()) return Map.of("success", false, "message", "商品不存在");
        double total = ((Number) priceList.get(0).get("price")).doubleValue() * quantity;
        return Map.of("success", true, "totalPrice", total);
    }
    //带商品名的计算总价
    @GetMapping("/calculate/total/price/with/name")
    public Map<String, Object> selectProductsWithTotalPrice(@RequestParam String productsName) {
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
}

