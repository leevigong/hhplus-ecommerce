package kr.hhplus.be.server.support.lock;

public enum LockResource {
    USER_COUPON;

    public String createKey(String value) {
        return String.format("%s:%s", this.name().toLowerCase(), value);
    }
}
