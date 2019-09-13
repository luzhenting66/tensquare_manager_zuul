package cn.pipilu.tensquare.manager.zuul;

import cn.pipilu.plus.common.util.JwtUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableZuulProxy
@EnableEurekaClient
@ComponentScan(basePackages = "cn.pipilu")
public class ManagerZuulApplication {
    public static void main(String[] args) {
        SpringApplication.run(ManagerZuulApplication.class,args);
    }

}
