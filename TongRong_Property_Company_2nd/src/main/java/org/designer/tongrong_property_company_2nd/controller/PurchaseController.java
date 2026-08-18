package org.designer.tongrong_property_company_2nd.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.designer.tongrong_property_company_2nd.common.MerchantOnly;
import org.designer.tongrong_property_company_2nd.common.SessionHelper;
import org.designer.tongrong_property_company_2nd.service.CommentService;
import org.designer.tongrong_property_company_2nd.service.ProductService;
import org.designer.tongrong_property_company_2nd.service.UserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/purchase")
@RequiredArgsConstructor
public class PurchaseController {

    private final ProductService productService;
    private final CommentService commentService;
    private final UserService userService;

    /**商品接口**/

    @GetMapping("/travel")
    public List<Map<String, Object>> travelProducts() {
        return productService.travelProducts();
    }

    @GetMapping("/select/single/product")
    public List<Map<String, Object>> selectSingleProduct(@RequestParam String selectName) {
        return productService.selectSingleProduct(selectName);
    }

    @GetMapping("/select/single/product/undefined")
    public List<Map<String, Object>> selectSingleUndefinedProduct(@RequestParam String undefinedName) {
        return productService.selectFuzzyProducts(undefinedName);
    }

    @PostMapping("/add/single/product")
    @MerchantOnly
    public Map<String, Object> addSingleProduct(@RequestParam String productsName,
                                                @RequestParam String productsCategory,
                                                @RequestParam double productsPrice,
                                                @RequestParam int productsStock,
                                                @RequestParam String productsSupplier,
                                                @RequestParam String productsDescription) {
        return productService.addSingleProduct(productsName, productsCategory,
                productsPrice, productsStock, productsSupplier, productsDescription);
    }

    @PostMapping("/add/more/product")
    @MerchantOnly
    public Map<String, Object> addMoreProduct(@RequestParam String productsName,
                                              @RequestParam String productsCategory,
                                              @RequestParam double productsPrice,
                                              @RequestParam int productsStock,
                                              @RequestParam String productsSupplier,
                                              @RequestParam String productsDescription,
                                              @RequestParam int quantity) {
        return productService.addMoreProduct(productsName, productsCategory,
                productsPrice, productsStock, productsSupplier, productsDescription, quantity);
    }

    @GetMapping("/select/single/product/price")
    public List<Map<String, Object>> selectOfSinglePrice(@RequestParam String productName) {
        return productService.selectSinglePrice(productName);
    }

    @GetMapping("/select/more/product/price")
    public List<Map<String, Object>> selectMoreProductsPrices(@RequestParam String productsName) {
        return productService.selectMorePrices(productsName);
    }

    @GetMapping("/calculate/total/price")
    public Map<String, Object> calculateTotalPrice(@RequestParam String products,
                                                   @RequestParam int quantity) {
        return productService.calculateTotalPrice(products, quantity);
    }

    @GetMapping("/calculate/total/price/with/name")
    public Map<String, Object> selectProductsWithTotalPrice(@RequestParam String productsName) {
        return productService.calculateTotalPriceWithNames(productsName);
    }

    @DeleteMapping("/delete/single/product")
    @MerchantOnly
    public Map<String, Object> deleteSingleProduct(@RequestParam String productName,
                                                   @RequestParam int id) {
        return productService.deleteSingleProduct(productName, id);
    }

    @PutMapping("/edit/single/product")
    @MerchantOnly
    public Map<String, Object> editSingleProduct(@RequestParam String productsName,
                                                 @RequestParam int id,
                                                 @RequestParam String productsCategory,
                                                 @RequestParam double productsPrice,
                                                 @RequestParam int productsStock,
                                                 @RequestParam String productsSupplier,
                                                 @RequestParam String productsDescription) {
        return productService.editSingleProduct(productsName, id, productsCategory,
                productsPrice, productsStock, productsSupplier, productsDescription);
    }

    @PutMapping("/review")
    @MerchantOnly
    public Map<String, Object> reviewAndApprove(@RequestParam int id,
                                                @RequestParam String status) {
        return productService.reviewProduct(id, status);
    }

    @GetMapping("/pending")
    public List<Map<String, Object>> pendingProducts() {
        return productService.pendingProducts();
    }

    @GetMapping("/stock/warning")
    public List<Map<String, Object>> stockWarning(@RequestParam(defaultValue = "12") int threshold) {
        return productService.stockWarning(threshold);
    }

    @GetMapping("/check/stock")
    public Map<String, Object> checkStock(@RequestParam String productName) {
        return productService.checkStock(productName);
    }

    @PutMapping("/send/single/product")
    @MerchantOnly
    public Map<String, Object> sendSingleProduct(@RequestParam String productName) {
        return productService.sendSingleProduct(productName);
    }

    @PutMapping("/add/picture")
    @MerchantOnly
    public Map<String, Object> addPicture(@RequestParam int id,
                                          @RequestParam MultipartFile file) throws IOException {
        if (file.isEmpty()) return Map.of("success", false, "message", "请选择图片");
        return productService.addPicture(id, file.getOriginalFilename(), file.getInputStream());
    }

    /**评论接口**/

    @PostMapping("/comment/insert")
    public Map<String, Object> commentInsert(@RequestParam String productName,
                                             @RequestParam String content,
                                             HttpServletRequest request) {
        return commentService.insertComment(productName, content, SessionHelper.currentUsername(request));
    }

    @DeleteMapping("/comment/delete")
    public Map<String, Object> deleteMyComment(@RequestParam int deleteId,
                                               HttpServletRequest request) {
        return commentService.deleteComment(deleteId, SessionHelper.currentUsername(request));
    }

    @GetMapping("/comment/list")
    public List<Map<String, Object>> commentList(@RequestParam(required = false) String productName,
                                                 HttpServletRequest request) {
        return commentService.commentList(productName, SessionHelper.currentUsername(request));
    }

    @PutMapping("/comment/like")
    public Map<String, Object> toggleLike(@RequestParam int commentId, HttpServletRequest request) {
        return commentService.toggleLike(commentId, SessionHelper.currentUsername(request));
    }

    /**消息接口**/

    @GetMapping("/message/list")
    public List<Map<String, Object>> messageList(HttpServletRequest request) {
        return commentService.messageList(SessionHelper.currentUsername(request));
    }

    @GetMapping("/message/unread-count")
    public Map<String, Object> messageUnreadCount(HttpServletRequest request) {
        return commentService.messageUnreadCount(SessionHelper.currentUsername(request));
    }

    @PutMapping("/message/read")
    public Map<String, Object> messageRead(@RequestParam(required = false) Integer id,
                                           HttpServletRequest request) {
        return commentService.messageRead(id, SessionHelper.currentUsername(request));
    }

    /**用户接口**/

    @PutMapping("/identity/check")
    public Map<String, Object> identityCheck(HttpServletRequest request) {
        if (!SessionHelper.isLoggedIn(request))
            return Map.of("success", false, "message", "未登录", "canModify", false, "canViewEmployees", false);
        String role = SessionHelper.currentRole(request);
        boolean canModify = "merchant".equals(role);
        boolean canViewEmployees = "manager".equals(role);
        String message;
        if (canModify) message = "商家身份，可修改商品";
        else if (canViewEmployees) message = "物管人员，可查看员工模块，无商品修改权限";
        else message = "普通用户，无商品修改权限";
        return Map.of("success", true, "message", message, "canModify", canModify, "canViewEmployees", canViewEmployees);
    }

    @PutMapping("/identity/set")
    public Map<String, Object> setRevokeAndGrant(HttpServletRequest request,
                                                  @RequestParam String nameWill) {
        String role = SessionHelper.currentRole(request);
        if (role == null)
            return Map.of("success", false, "message", "未登录", "canModify", false);
        if (!"manager".equals(role))
            return Map.of("success", false, "message", "仅物管人员可设置商家权限");
        return userService.grantMerchantRole(nameWill);
    }

    @PutMapping("/edit/username")
    public Map<String, Object> editName(@RequestParam String oldName,
                                        @RequestParam String newName,
                                        HttpServletRequest request) {
        String currentUser = SessionHelper.currentUsername(request);
        if (currentUser == null) return Map.of("success", false, "message", "未登录！");
        if (!java.util.Objects.equals(currentUser, oldName))
            return Map.of("success", false, "message", "只能改自己的名！");
        Map<String, Object> result = userService.editName(oldName, newName);
        if (Boolean.TRUE.equals(result.get("success"))) {
            SessionHelper.updateUsername(request, newName);
        }
        return result;
    }
}
