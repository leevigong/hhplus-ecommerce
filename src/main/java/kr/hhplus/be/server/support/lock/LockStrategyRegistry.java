package kr.hhplus.be.server.support.lock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class LockStrategyRegistry {

    private final Map<LockStrategy, LockTemplate> lockMap;

    public LockStrategyRegistry(List<LockTemplate> templates) {
        this.lockMap = new EnumMap<>(LockStrategy.class);
        templates.forEach(t -> lockMap.put(t.getLockStrategy(), t));
    }

    public LockTemplate getLockTemplate(LockStrategy strategy) {
        log.debug("[Lock] 전략 선택 - strategy={}", strategy);
        return lockMap.get(strategy);
    }
}
