package org.designer.tongrong_property_company_2nd;

import org.designer.tongrong_property_company_2nd.weather.OpenMeteoDemo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class TongRongPropertyCompany2ndApplication {
    public static void main(String[] args) throws ClassNotFoundException {
       SpringApplication.run(TongRongPropertyCompany2ndApplication.class, args);
       OpenMeteoDemo openMeteoDemo=new OpenMeteoDemo();
       openMeteoDemo.getWeather();
       TongRongPropertyCompany2ndApplication tongRongPropertyCompany2ndApplication=new TongRongPropertyCompany2ndApplication();
       Logger log=LoggerFactory.getLogger(TongRongPropertyCompany2ndApplication.class);
       log.info("测试");
    }

}
