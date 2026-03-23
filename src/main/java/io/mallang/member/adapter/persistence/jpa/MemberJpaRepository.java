package io.mallang.member.adapter.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface MemberJpaRepository extends JpaRepository<MemberJpaEntity, String> {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<MemberJpaEntity> findByEmail(String address);
}
