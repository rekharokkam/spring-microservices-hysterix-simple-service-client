package com.learning.spring.microservices;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

@SpringBootApplication
@RestController
//@EnableCircuitBreaker
@EnableHystrix // Adding this command to make it more generic so that we get hystrix.stream
@EnableHystrixDashboard
public class SpringMicroservicesHysterixSimpleServiceClientApplication {

	@Autowired
	private RestTemplate restTemplate;
	
	@Bean
	public RestTemplate restTemplate () {
		return new RestTemplate();
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping ("/startClient")
	//This property is telling Hystrix to wait for the number of milliseconds provided in "value" 
	//for this method to respond before failing over to fallbackMethod
	@HystrixCommand (fallbackMethod="failover", commandProperties = {
			@HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value="500")
	})
	public List<String> startClient (@RequestParam long time) throws InterruptedException {
		//simulating a delay
		Thread.sleep(time);
		return restTemplate.getForObject("http://localhost:8888/service", List.class);
	}
	
	public List<String> failover (long time) {
		return Arrays.asList("Default1", "Default2");
	}
	
	public static void main(String[] args) {
		SpringApplication.run(SpringMicroservicesHysterixSimpleServiceClientApplication.class, args);
	}
}
