package ca.mcgill.ecse321.coolearn;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.Set;

import org.springframework.boot.SpringApplication;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@SpringBootApplication
public class CooLearnApplication {

	public static Set<Integer> currentUserRoleLoggedIn;

	public static void main(String[] args) {
		SpringApplication.run(CooLearnApplication.class, args);
	}

	@RequestMapping("/")
	public String greeting(){
		return "Hello world!";
	}

}