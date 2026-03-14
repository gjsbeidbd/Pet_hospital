package edu.mycc.xhd.pethospitalproject;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@MapperScan("edu.mycc.xhd.pethospitalproject.mapper")
public class PetHospitalProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetHospitalProjectApplication.class, args);
    }

    @Configuration
    static class WebMvcConfig implements WebMvcConfigurer {
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            // 映射/uploads/**路径到实际的文件上传目录
            registry.addResourceHandler("/uploads/**")
                    .addResourceLocations("file:./uploads/");
        }
    }
}