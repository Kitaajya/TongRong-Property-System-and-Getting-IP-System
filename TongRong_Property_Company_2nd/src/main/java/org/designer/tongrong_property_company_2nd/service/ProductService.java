package org.designer.tongrong_property_company_2nd.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.designer.tongrong_property_company_2nd.mapper.ProductMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper productMapper;

    private static final int DEFAULT_STOCK_THRESHOLD = 12;
    private static final Path UPLOAD_DIR = Paths.get("uploads").toAbsolutePath().normalize();

    public List<Map<String, Object>> travelProducts() {
        log.info("查看商品库");
        return productMapper.selectAll();
    }

    public List<Map<String, Object>> selectSingleProduct(String selectName) {
        log.info("查找单个商品");
        if (productMapper.countByName(selectName) == 0) return new ArrayList<>();
        return productMapper.selectByName(selectName);
    }

    public List<Map<String, Object>> selectFuzzyProducts(String keyword) {
        log.info("模糊查找单个商品");
        if (keyword == null || keyword.isEmpty()) return new ArrayList<>();
        return productMapper.selectByNameLike(keyword);
    }

    public Map<String, Object> addSingleProduct(String name, String category, double price,
                                                 int stock, String supplier, String description) {
        log.info("添加单个商品");
        if (name.isEmpty() || category.isEmpty() || supplier.isEmpty() || description.isEmpty())
            return Map.of("success", false, "message", "商品信息无效");
        int row = productMapper.insert(name, price, description, supplier, category, stock);
        if (row > 0) return Map.of("success", true, "message", "商品添加成功", "productName", name);
        return Map.of("success", false, "message", "商品添加失败");
    }

    public Map<String, Object> addMoreProduct(String name, String category, double price,
                                               int stock, String supplier, String description, int quantity) {
        log.info("添加更多商品");
        if (quantity <= 0) return Map.of("success", false, "message", "商品个数必须是大于零的数！");
        if (name.isEmpty() || category.isEmpty() || supplier.isEmpty() || description.isEmpty())
            return Map.of("success", false, "message", "商品信息无效");
        for (int i = 0; i < quantity; i++) {
            int rows = productMapper.insert(name, price, description, supplier, category, stock);
            if (rows <= 0) return Map.of("success", false, "message", "商品添加失败");
            log.info("第{}个商品", i + 1);
        }
        return Map.of("success", true, "message", "添加成功");
    }

    public List<Map<String, Object>> selectSinglePrice(String productName) {
        log.info("查询单个商品的价格");
        if (productName == null || productName.isEmpty())
            return List.of(Map.of("success", false, "message", "商品信息无效"));
        return productMapper.selectPriceByName(productName);
    }

    public List<Map<String, Object>> selectMorePrices(String productsName) {
        log.info("查询更多商品的价格");
        if (productsName == null || productsName.isEmpty()) return new ArrayList<>();
        List<Map<String, Object>> result = new ArrayList<>();
        for (String name : productsName.split(",")) {
            String trimmed = name.trim();
            if (trimmed.isEmpty()) continue;
            Map<String, Object> priceMap = new HashMap<>();
            priceMap.put(trimmed, productMapper.selectPriceByName(trimmed));
            result.add(priceMap);
        }
        return result;
    }

    public Map<String, Object> calculateTotalPrice(String products, int quantity) {
        log.info("计算总价");
        if (products == null || products.isEmpty() || quantity <= 0)
            return Map.of("success", false, "message", "参数无效");
        if (productMapper.countByName(products) == 0)
            return Map.of("success", false, "message", "商品不存在");
        List<Map<String, Object>> priceList = productMapper.selectPriceByNameLimit1(products);
        if (priceList.isEmpty())
            return Map.of("success", false, "message", "商品不存在");
        double total = ((Number) priceList.get(0).get("price")).doubleValue() * quantity;
        return Map.of("success", true, "totalPrice", total);
    }

    public Map<String, Object> calculateTotalPriceWithNames(String productsName) {
        log.info("带商品名的计算总价");
        if (productsName == null || productsName.isEmpty())
            return Map.of("success", false, "message", "商品信息无效");
        double total = 0;
        for (String name : productsName.split(",")) {
            String trimmed = name.trim();
            if (trimmed.isEmpty()) continue;
            List<Map<String, Object>> priceList = productMapper.selectPriceByNameLimit1(trimmed);
            if (!priceList.isEmpty())
                total += ((Number) priceList.get(0).get("price")).doubleValue();
        }
        return Map.of("success", true, "totalPrice", total);
    }

    public Map<String, Object> deleteSingleProduct(String productName, int id) {
        log.info("删除一个商品");
        if (productName == null || productName.isEmpty())
            return Map.of("success", false, "message", "商品不能为空");
        if (productMapper.countByName(productName) == 0)
            return Map.of("success", false, "message", "商品不存在");
        int rows = productMapper.deleteByIdAndName(id, productName);
        if (rows <= 0) return Map.of("success", false, "message", "参数无效/商品不存在/id或商品名输入错误");
        return Map.of("success", true, "message", "删除成功");
    }

    public Map<String, Object> editSingleProduct(String productName, int id, String category,
                                                  double price, int stock, String supplier, String description) {
        log.info("修改商品");
        if (productName == null || productName.isEmpty())
            return Map.of("success", false, "message", "商品不能为空");
        if (productMapper.countByName(productName) == 0)
            return Map.of("success", false, "message", "商品不存在");
        int rows = productMapper.update(id, productName, category, price, stock, supplier, description);
        if (rows > 0) return Map.of("success", true, "message", "商品更新成功");
        return Map.of("success", false, "message", "商品不存在或更新失败");
    }

    public Map<String, Object> reviewProduct(int id, String status) {
        log.info("审批商品");
        if (status == null || status.isEmpty())
            return Map.of("success", false, "message", "审批状态不能为空");
        if (!status.equals("已通过") && !status.equals("已驳回"))
            return Map.of("success", false, "message", "状态只能为:已通过 或 已驳回");
        int rows = productMapper.updateStatus(id, status);
        if (rows > 0) return Map.of("success", true, "message", "审批成功", "id", id, "status", status);
        return Map.of("success", false, "message", "商品不存在,审批失败");
    }

    public List<Map<String, Object>> pendingProducts() {
        log.info("查看待审核商品");
        return productMapper.selectPending();
    }

    public List<Map<String, Object>> stockWarning(int threshold) {
        if (threshold <= 0) return new ArrayList<>();
        return productMapper.selectByStockBelow(threshold);
    }

    public Map<String, Object> checkStock(String productName) {
        if (productName == null || productName.isEmpty())
            return Map.of("success", false, "message", "商品不能为空！", "canBeSend", false);
        if (productMapper.countByName(productName) == 0)
            return Map.of("success", false, "message", "商品不存在或为空", "canBeSend", false);
        Integer stock = productMapper.selectStockByName(productName);
        if (stock == null)
            return Map.of("success", false, "message", "库存数据异常，库存量为空", "canBeSend", false);
        if (stock < DEFAULT_STOCK_THRESHOLD)
            return Map.of("success", false, "message", "库存不够，数量：" + stock + "个，无法发货", "canBeSend", false);
        return Map.of("success", true, "message", "库存充足，数量：" + stock + "个", "canBeSend", true);
    }

    @Transactional
    public Map<String, Object> sendSingleProduct(String productName) {
        log.info("发送单个商品");
        if (productMapper.countByName(productName) == 0)
            return Map.of("success", false, "message", "商品不存在");
        int rows = productMapper.decrementStock(productName);
        if (rows == 0) return Map.of("success", false, "message", "库存不足或商品不存在");
        return Map.of("success", true, "message", "准备发货，库存-1");
    }

    public Map<String, Object> addPicture(int id, String originalFilename, InputStream inputStream)
            throws IOException {
        if (originalFilename == null)
            return Map.of("success", false, "message", "文件名异常");
        String ext = originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf('.'))
                : ".jpg";
        String filename = "product_" + id + "_" + System.currentTimeMillis() + ext;
        Files.createDirectories(UPLOAD_DIR);
        Path target = UPLOAD_DIR.resolve(filename);
        Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
        String imageUrl = "/uploads/" + filename;
        int rows = productMapper.updateImageUrl(id, imageUrl);
        if (rows <= 0) return Map.of("success", false, "message", "商品不存在");
        return Map.of("success", true, "message", "图片上传成功", "imageUrl", imageUrl);
    }
}
