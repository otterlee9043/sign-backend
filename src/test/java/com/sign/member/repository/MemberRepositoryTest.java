package com.sign.member.repository;

import com.sign.SignApplication;
import com.sign.member.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@ExtendWith(SpringExtension.class)
class MemberRepositoryTest {

    @Autowired
    MemberRepository repository;

    @Test
    public void save(){
//        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(SignApplication.class);
//		String[] beanDefinitionNames = ac.getBeanDefinitionNames();
//		for (String beanDefinitionName : beanDefinitionNames) {
//			BeanDefinition beanDefinition = ac.getBeanDefinition(beanDefinitionName);
//			if (beanDefinition.getRole() == BeanDefinition.ROLE_APPLICATION){
//				Object bean = ac.getBean(beanDefinitionName);
//				System.out.println("name = " + beanDefinitionName + " object = " + bean);
//			}
//		}

//        Member member = new Member();
//        member.setName("spring");
//        repository.save(member);
//        member.setName("sua");
//        repository.save(member);
//        Member result = repository.findById(member.getId()).get();
////        Assertions.assertEquals(member, result);
//        assertThat(member).isEqualTo(result);
        List<Member> members = repository.findAll();
        for (Member member1 : members) {
            System.out.println("member1 = " + member1);

        }
    }
}