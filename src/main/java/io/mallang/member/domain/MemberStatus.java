package io.mallang.member.domain;

public enum MemberStatus {
    ACTIVE,
    WITHDRAWN;

    boolean isActive() {
        return this == ACTIVE;
    }
}
