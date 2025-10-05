package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
public class AutoFillAspect {

    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill))")
    public void autoFillPointcut(){};

    @Before("autoFillPointcut()")
    public void autoFill(JoinPoint joinPoint){
        // 1.获取注解所标注方法的类型（insert或update）
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        AutoFill annotation = methodSignature.getMethod().getAnnotation(AutoFill.class);
        OperationType value = annotation.value();
        // 2.获取方法的参数（默认将实体类放在索引0）
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0){
            // 3.无参数，则直接返回
            return;
        }
        Object entity = args[0];
        // 4.为参数赋值
        try {
            LocalDateTime now = LocalDateTime.now();
            Long id = BaseContext.getCurrentId();
            Method setUpdateTime = entity.getClass().getDeclaredMethod("setUpdateTime", LocalDateTime.class);
            Method setUpdateUser = entity.getClass().getDeclaredMethod("setUpdateUser", Long.class);
            setUpdateTime.invoke(entity, now);
            setUpdateUser.invoke(entity, id);
            if (value == OperationType.INSERT){
                Method setCreateTime = entity.getClass().getDeclaredMethod("setCreateTime", LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod("setCreateUser", Long.class);
                setCreateTime.invoke(entity, now);
                setCreateUser.invoke(entity, id);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
