package com.damon;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@MapperScan("com.damon.dao")
@SpringBootApplication
public class YingxueChenApplication {
    public static void main(String[] args) {
        SpringApplication.run(YingxueChenApplication.class, args);
    }

}
