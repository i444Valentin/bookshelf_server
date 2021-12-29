package ru.codeinside.lisova.bookshelf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class }) // чтобы пароли не генерировал
public class BookshelfApplication {

	public static void main(String[] args) {
		System.setProperty("java.awt.headless", "false"); // чтобы открывался файл в браузере
		SpringApplication.run(BookshelfApplication.class, args);
	}

}
