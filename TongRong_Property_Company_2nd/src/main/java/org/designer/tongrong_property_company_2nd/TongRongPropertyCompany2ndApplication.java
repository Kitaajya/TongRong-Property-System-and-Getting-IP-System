package org.designer.tongrong_property_company_2nd;

import org.designer.tongrong_property_company_2nd.weather.OpenMeteoDemo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TongRongPropertyCompany2ndApplication {

    public static void main(String[] args) throws ClassNotFoundException {
        SpringApplication.run(TongRongPropertyCompany2ndApplication.class, args);
        OpenMeteoDemo openMeteoDemo=new OpenMeteoDemo();
        openMeteoDemo.getWeather();
    }

}
