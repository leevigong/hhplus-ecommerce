package kr.hhplus.be.server.support.lock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Component
public class LockKeyGenerator {
    private static final String PREFIX = "lock:";

    public String generateKey(
            String[] paramNames,
            Object[] args,
            String spelKey,
            LockResource resource
    ) {
        log.debug("[Lock] 키 생성 시작 - spel='{}', resource={}", spelKey, resource);
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();

        for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }
        String resolved = parser.parseExpression(spelKey).getValue(context, String.class); //
        return PREFIX + resource.createKey(resolved);
    }
}
