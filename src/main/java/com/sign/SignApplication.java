package com.sign;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

//@EnableJpaRepositories(basePackages = {"com.sign.member.repository"})
//@EntityScan(basePackages = {"com.sign.member"})
@SpringBootApplication(scanBasePackages = "com.sign.member")
public class SignApplication {

	public static void main(String[] args) {
		SpringApplication.run(SignApplication.class, args);
		AnnotationConfigApplicationContext ac = new ApplicationContext(SpringConfig.class);
		String[] beanDefinitionNames = ac.getBeanDefinitionNames();
		for (String beanDefinitionName : beanDefinitionNames) {
			BeanDefinition beanDefinition = ac.getBeanDefinition(beanDefinitionName);
			if (beanDefinition.getRole() == BeanDefinition.ROLE_APPLICATION){
				Object bean = ac.getBean(beanDefinitionName);
				System.out.println("name = " + beanDefinitionName + " object = " + bean);
			}
		}

	}
}
