package org.example.expert.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
class PasswordEncoderTest {
    // @InjectMocks를 제거한 이유 : 단순히 new 키워드로 인스턴스를 생성하면 되는 클래스
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void matches_메서드가_정상적으로_동작한다() {
        // given
        String rawPassword = "testPassword";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // when
        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword); // 인자 순서 문제

        // then
        assertTrue(matches);
    }
}
