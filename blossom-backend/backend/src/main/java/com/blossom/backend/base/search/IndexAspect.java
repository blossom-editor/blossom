package com.blossom.backend.base.search;

import com.blossom.backend.base.auth.AuthContext;
import com.blossom.backend.base.search.message.ArticleIndexMsg;
import com.blossom.backend.base.search.message.IndexMsgTypeEnum;
import com.blossom.backend.base.search.queue.IndexMsgQueue;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

@Aspect
@Component
@Slf4j
public class IndexAspect {

    /**
     * 用于spl解析
     */
    private static final LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();

    @Pointcut("@annotation(com.blossom.backend.base.search.EnableIndex)")
    private void cutMethod() {

    }

    /**
     * 成功返回后调用该方法, 维护索引. 使用after防止事务未提交导致的数据滞后
     */
    @AfterReturning("cutMethod()")
    public void afterReturning(JoinPoint joinPoint) {
        // 此处进行索引处理, 降低索引维护代码侵入
        // 获取注解对象
        EnableIndex annotation = getAnnotation(joinPoint);
        if (annotation == null) {
            // 记录问题， 并结束逻辑
            log.error("索引切面获取注解失败!");
            return;
        }

        IndexMsgTypeEnum indexMsgTypeEnum = annotation.type();
        String idSpEL = annotation.id();
        if (!StringUtils.hasText(idSpEL)) {
            log.error("获取id表达式失败");
            return;
        }
        Long customerId = parse(idSpEL, joinPoint, Long.class);
        if (customerId == null) {
            return;
        }
        ArticleIndexMsg indexMsg = new ArticleIndexMsg(indexMsgTypeEnum, customerId, AuthContext.getUserId());
        try {
            IndexMsgQueue.add(indexMsg);
        } catch (InterruptedException e) {
            // 不抛出, 暂时先记录
            log.error("索引操作失败" + e.getMessage());
        }
    }

    /**
     * 获取method
     *
     * @return method
     */
    private Method getTargetMethod(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method agentMethod = methodSignature.getMethod();
        try {
            return joinPoint.getTarget().getClass().getMethod(agentMethod.getName(), agentMethod.getParameterTypes());
        } catch (Exception e) {
            // 只记录异常
            log.error("获取目标方法失败");
        }
        return null;
    }

    /**
     * 获取注解声明对象
     */
    private EnableIndex getAnnotation(JoinPoint joinPoint) {
        // 获取方法上的注解
        MethodSignature sign = (MethodSignature) joinPoint.getSignature();
        Method method = sign.getMethod();
        return method.getAnnotation(EnableIndex.class);
    }

    /**
     * 解析SpEL表达式， 提供后续拓展的灵活性
     */
    private <T> T parse(String spel, JoinPoint joinPoint, Class<T> clazz) {
        ExpressionParser parser = new SpelExpressionParser();
        Method method = getTargetMethod(joinPoint);
        if (method == null) {
            return null;
        }

        String[] params = discoverer.getParameterNames(method);
        if (params == null || params.length == 0) {
            log.error("获取参数列表失败");
            return null;
        }

        Object[] args = joinPoint.getArgs();
        EvaluationContext context = new StandardEvaluationContext();
        for (int len = 0; len < params.length; len++) {
            context.setVariable(params[len], args[len]);
        }

        Expression expression = parser.parseExpression(spel);
        return expression.getValue(context, clazz);
    }


}
