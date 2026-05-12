package com.abilitybridge.user.repository;

import com.abilitybridge.user.entity.AccountStatus;
import com.abilitybridge.user.entity.User;
import com.abilitybridge.user.entity.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    Optional<User> findByEmailOrPhone(String email, String phone);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

    Page<User> findByRole(UserRole role, Pageable pageable);
    Page<User> findByStatus(AccountStatus status, Pageable pageable);
    Page<User> findByRoleAndStatus(UserRole role, AccountStatus status, Pageable pageable);

    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.status = 'ACTIVE'")
    long countActiveByRole(@Param("role") UserRole role);

    @Query("SELECT u FROM User u WHERE u.provider = 'LOCAL' AND u.email = :email")
    Optional<User> findLocalByEmail(@Param("email") String email);
}
