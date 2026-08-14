package org.designer.tongrong_property_company_2nd.weather;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
@RestController
@RequestMapping("/api/weather")
@Component
public class OpenMeteoDemo {
    private static final Logger log = LoggerFactory.getLogger(OpenMeteoDemo.class);

    // 高并发缓存：ConcurrentHashMap 保证多线程读写安全
    private static final ConcurrentHashMap<String, String> WEATHER_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Long> WEATHER_CACHE_TIME = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Double> TEMP_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Integer> CODE_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Double> WIND_CACHE = new ConcurrentHashMap<>();
    // 地名拼音
    private static final String CACHE_KEY = "Wuhan";
    // 缓存有效期：10分钟，避免高并发下每个请求都阻塞调用外部API
    private static final long CACHE_EXPIRE_MS = 10 * 60 * 1000L;
    // IP地区缓存，避免每个请求都重复查询IP库
    private static final ConcurrentHashMap<String, String> IP_LOCATION_CACHE = new ConcurrentHashMap<>();

    public OpenMeteoDemo() throws ClassNotFoundException {
    }

    /**
     * 异步获取天气，返回 CompletableFuture，运行在 weatherExecutor 线程池
     */
    //获取人能够看懂的格式
    @GetMapping("/getWeather")
    @Async("weatherExecutor")
    public CompletableFuture<String> getWeatherAsync(HttpServletRequest request) {
        return CompletableFuture.completedFuture(getWeather(getClientIp(request)));
    }

    /**
     * 从请求中解析真实客户端IP，兼容反向代理（nginx）透传的 X-Forwarded-For
     */
    private static String getClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank())
            return xff.split(",")[0].trim();
        return request.getRemoteAddr();
    }

    /**
     * 异步获取天气（带访问者IP，用于日志定位）
     */
    @Async("weatherExecutor")
    public CompletableFuture<String> getWeatherAsync(String clientIp) {
        return CompletableFuture.completedFuture(getWeather(clientIp));
    }
    double lat;//北纬
    double lon;//东经
    public String getWeather() {
        return getWeather(null);
    }

    public String getWeather(String clientIp) {
        // 每次请求都记录访问者地理位置(IP定位到省+市)
        log.info("访问者IP:{} 地理位置:{}", clientIp, getLocation(clientIp));
        // 先查缓存，命中直接返回，避免阻塞式网络IO
        long now = System.currentTimeMillis();
        String cached = WEATHER_CACHE.get(CACHE_KEY);
        Long cachedTime = WEATHER_CACHE_TIME.get(CACHE_KEY);
        if (cached != null && cachedTime != null && now - cachedTime < CACHE_EXPIRE_MS) {
            return cached;
        }

        // 地区坐标
        lat =40.191534; //北纬
        lon =118.120463;//东经
        String urlStr = String.format(
                "https://api.open-meteo.com/v1/forecast?latitude=%s&longitude=%s&timezone=Asia/Shanghai&current=temperature_2m,weather_code,wind_speed_10m",
                lat, lon
        );

        String json = getWeatherJson(urlStr);
        if (json == null || json.isBlank()) {
            String failText = "天气接口请求失败，请检查网络！";
            log.info(failText);
            return failText;
        }

        //System.out.println("接口返回数据：");
        //System.out.println(json);

        // 只截取 current {} 内部片段，避开current_units重复key
        int currentStart = json.indexOf("\"current\":{");
        int currentEnd = json.indexOf("}", currentStart);
        String currentJson = json.substring(currentStart, currentEnd + 1);

        double temp = extractDouble(currentJson, "\"temperature_2m\":");
        int weatherCode = extractInt(currentJson, "\"weather_code\":");
        double windSpeed = extractDouble(currentJson, "\"wind_speed_10m\":");

        TEMP_CACHE.put(CACHE_KEY, temp);
        CODE_CACHE.put(CACHE_KEY, weatherCode);
        WIND_CACHE.put(CACHE_KEY, windSpeed);

        String weatherText = "\n("+"北纬"+lat+"、东经"+lon+"地区)\n"
                + "🌡️ 温度：" + temp + " ℃\n"
                + "💨 风速：" + windSpeed + " km/h\n"
                + "☁️ 天气状况：" + getWeatherText(weatherCode);
        // 写入缓存
        WEATHER_CACHE.put(CACHE_KEY, weatherText);
        WEATHER_CACHE_TIME.put(CACHE_KEY, now);
        log.info(weatherText);
        return weatherText;
    }

    /**
     * 返回结构化天气信息(前端用来展示天气图片/温度/风速)
     */
    @GetMapping("/info")
    public Map<String, Object> getWeatherInfo(HttpServletRequest request) {
        getWeather(getClientIp(request));
        double temp = TEMP_CACHE.getOrDefault(CACHE_KEY, 0.0);
        double wind = WIND_CACHE.getOrDefault(CACHE_KEY, 0.0);
        int code = CODE_CACHE.getOrDefault(CACHE_KEY, 0);
        return Map.of(
                "location", "北纬" + lat + "、东经" + lon + "地区",
                "temperature", temp,
                "windSpeed", wind,
                "weatherCode", code,
                "weatherText", getWeatherText(code)
        );
    }

    /**
     * 应用启动完成后后台预热缓存，让第一个请求直接命中缓存
     */
    @EventListener(ApplicationReadyEvent.class)
    public void warmUpCache() {
        CompletableFuture.runAsync(this::getWeather);
    }

    /**
     * 根据访问者IP查询精确地区（省+市），带缓存避免重复请求IP库
     */
    public static String getLocation(String ip) {
        if (ip == null || ip.isBlank()) return "未知";
        if (isLocalIp(ip)) return "本机/内网";
        String cached = IP_LOCATION_CACHE.get(ip);
        if (cached != null) return cached;
        // 主接口 pconline，失败自动切换备用接口 ip-api
        String location = lookupPconline(ip);
        if (location == null) location = lookupIpApi(ip);

        if (location != null) {
            IP_LOCATION_CACHE.put(ip, location);
            return location;
        }
        return ip;
    }

    /**
     * pconline接口：返回 省+市；直辖市返回 市+区
     */
    private static String lookupPconline(String ip) {
        String json = getIpLocationJson("https://whois.pconline.com.cn/ipJson.jsp?ip=" + ip);
        if (json == null || json.isBlank()) return null;
        String province = parseField(json, "pro");
        String city = parseField(json, "city");
        if (province == null || province.isBlank()) return null;
        if (province.equals(city)) {
            String region = parseField(json, "region");
            return province + (region == null || region.isBlank() ? "" : region);
        }
        return province + (city == null ? "" : city);
    }

    /**
     * ip-api备用接口：返回 省+市（省名不带"省/市"后缀）
     */
    private static String lookupIpApi(String ip) {
        String json = getWeatherJson("http://ip-api.com/json/" + ip + "?lang=zh-CN&fields=status,regionName,city");
        if (json == null || json.isBlank()) return null;
        if (!"success".equals(parseField(json, "status"))) return null;
        String region = parseField(json, "regionName");
        String city = parseField(json, "city");
        if (region == null || region.isBlank()) return null;
        if (region.equals(city)) return region;
        return region + (city == null ? "" : city);
    }

    private static boolean isLocalIp(String ip) {
        return ip.startsWith("127.")
                || ip.startsWith("10.")
                || ip.startsWith("192.168.")
                || ip.startsWith("172.")
                || ip.startsWith("0.")
                || ip.startsWith("localhost")
                || "::1".equals(ip);
    }

    private static String parseField(String json, String key) {
        String token = "\"" + key + "\":\"";
        int idx = json.indexOf(token);
        if (idx < 0) return null;
        int start = idx + token.length();
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }

    /**
     * 请求pconline IP库接口（返回GBK编码的JSONP文本），用于查询访问者精确地区
     */
    private static String getIpLocationJson(String apiUrl) {
        HttpURLConnection conn = null;
        BufferedReader br = null;
        try {
            URL url = new URL(apiUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setRequestProperty("Referer", "https://whois.pconline.com.cn/");
            br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "GBK"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            return sb.toString();
        } catch (Exception e) {
            log.error("查询IP地区失败：{}", e.getMessage());
            return null;
        } finally {
            try {
                if (br != null) br.close();
            } catch (Exception ignored) {
                log.error("异常发生于获取地址Json的方法中{}",ignored.getMessage());
            }
            if (conn != null) conn.disconnect();
        }
    }

    /**
     * 请求接口获取json字符串
     */
    public static String getWeatherJson(String apiUrl) {
        HttpURLConnection conn = null;
        BufferedReader br = null;
        try {
            URL url = new URL(apiUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);
            conn.setRequestProperty("User-Agent", "JavaWeatherClient/1.0");

            br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (br != null) br.close();
            } catch (Exception ignored) {
                log.error("获取天气信息操作出现异常：{}",ignored.getMessage());
            }
            if (conn != null) conn.disconnect();
        }

    }

    /**
     * 截取浮点数值
     */
    private static double extractDouble(String json, String key) {
        int index = json.indexOf(key);
        String subStr = json.substring(index + key.length());
        int endIndex = subStr.indexOf(",");
        if (endIndex == -1) endIndex = subStr.indexOf("}");
        return Double.parseDouble(subStr.substring(0, endIndex).trim());
    }

    /**
     * 截取整数
     * */
    private static int extractInt(String json, String key) {
        int index = json.indexOf(key);
        String subStr = json.substring(index + key.length());
        int endIndex = subStr.indexOf(",");
        if (endIndex == -1) endIndex = subStr.indexOf("}");
        return Integer.parseInt(subStr.substring(0, endIndex).trim());
    }

    /**
     * WMO天气编码转中文文字
     */
    public static String getWeatherText(int code) {
        return switch (code) {
            case 0 -> "晴";
            case 1, 2, 3 -> "多云";
            case 45, 48 -> "雾";
            case 51, 53, 55 -> "毛毛雨/小雨";
            case 61, 63, 65 -> "雨";
            case 80, 81, 82 -> "阵雨";
            case 95, 96, 99 -> "雷暴";
            default -> "未知天气";
        };
    }
}