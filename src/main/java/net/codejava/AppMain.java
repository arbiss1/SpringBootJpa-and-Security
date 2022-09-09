package net.codejava;

import net.codejava.entities.User;
import net.codejava.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.CacheControl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class AppMain implements CommandLineRunner {

	@Autowired
	UserRepository userRepository;
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(AppMain.class, args);
	}

	@Override
	public void run(String... args) throws Exception {


		User user = new User();
		user.setUsername("arbiss2");
		user.setEnabled(true);
		user.setFirst_name("A");
		user.setLastName("B");
		user.setUserAddress("C");
		user.setUserNumber("D");
		user.setPassword(bCryptPasswordEncoder.encode("Bisoo355!"));
		userRepository.save(user);
	}
}
