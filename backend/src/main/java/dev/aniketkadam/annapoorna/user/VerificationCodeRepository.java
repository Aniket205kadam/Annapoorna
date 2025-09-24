package dev.aniketkadam.annapoorna.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, String> {

    @Query("""
            SELECT verificationCode
            FROM VerificationCode verificationCode
            WHERE verificationCode.code = :code
            AND verificationCode.email = :email
           """)
    Optional<VerificationCode> findByEmailAndCode(String email, String code);

    Optional<VerificationCode> findTopByEmailOrderByCreatedAtDesc(String email);
}
