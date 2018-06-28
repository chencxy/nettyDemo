package com.cxy.demo.netty_demo;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
@ComponentScan
public class App {

    public static void main(String[] args) {
        try {
            new SpringApplicationBuilder().bannerMode(Mode.OFF).sources(App.class).run(args);
        } catch (BeanCreationException e) {
            System.err.println("请确认已正确配置该平台，请检查是否正确配�?");
            e.printStackTrace();
        }
    }
}
