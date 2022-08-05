package com.sign.domain.member;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class LoginArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(LoginMember.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
//        Object principal = null;
//        MyAuthentication authentication =
//                (MyAuthentication) SecurityContextHolder.getContext().getAuthentication();
//        if(authentication != null ) {
//            principal = authentication.principal;
//        }
//        if(principal == null || principal.getClass() == String.class) {
//            return null;
//        }
//
//        return principal;
        return null;
    }
}
