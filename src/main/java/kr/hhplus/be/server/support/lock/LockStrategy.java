package kr.hhplus.be.server.support.lock;

public enum LockStrategy {
    SPIN_LOCK,       // 스핀 락
    PUB_SUB_LOCK     // Pub/Sub 락
}
