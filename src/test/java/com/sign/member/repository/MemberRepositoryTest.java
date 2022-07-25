package com.sign.member.repository;

import com.sign.SpringConfig;
import com.sign.member.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.swing.text.html.parser.Entity;

import static org.junit.jupiter.api.Assertions.*;

class MemberRepositoryTest {

//    @Autowired
//    MemberRepository repository;

        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(SpringConfig.class);
//    @PersistenceContext
//    EntityManager entityManager;

    @Test
    public void save(){
//        MemberRepository memberRepository = ac.getBean(MemberRepository.class);
//        System.out.println("memberRepository = " + memberRepository);
//        System.out.println("entityManager = " + entityManager);
        String[] beanDefinitionNames = ac.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            BeanDefinition beanDefinition = ac.getBeanDefinition(beanDefinitionName);
            if (beanDefinition.getRole() == BeanDefinition.ROLE_APPLICATION){
                Object bean = ac.getBean(beanDefinitionName);
                System.out.println("name = " + beanDefinitionName + " object = " + bean);
            }
        }
//        MemberRepository memberRepository = ac.getBean(MemberRepository.class);
//        Assertions.assertThat(memberRepository).isInstanceOf(JpaMemberRepository.class);
//
//        String[] beanDefinitionNames = ac.getBeanDefinitionNames();
//        for (String beanDefinitionName : beanDefinitionNames) {
//            BeanDefinition beanDefinition = ac.getBeanDefinition(beanDefinitionName);
//            if (beanDefinition.getRole() == BeanDefinition.ROLE_APPLICATION){
//                Object bean = ac.getBean(beanDefinitionName);
//                System.out.println("name = " + beanDefinitionName + " object = " + bean);
//            }
//        }
//        Member member = new Member();
//        member.setName("spring");
//
//        repository.save(member);
//
//        Member result = repository.findById(member.getId()).get();
////        Assertions.assertEquals(member, result);
//        Assertions.assertThat(member).isEqualTo(result);
    }
}