package me.vcouturier.bouchon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@ComponentScan(basePackages = {
		"me.vcouturier.bouchon"
})
@SpringBootApplication
public class BouchonApplication {

	public static void main(String[] args) {
		SpringApplication.run(BouchonApplication.class, args);
	}

}
