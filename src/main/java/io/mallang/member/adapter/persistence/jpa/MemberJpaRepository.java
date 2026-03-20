package io.mallang.member.adapter.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

interface MemberJpaRepository extends JpaRepository<MemberJpaEntity, String> {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);
}
