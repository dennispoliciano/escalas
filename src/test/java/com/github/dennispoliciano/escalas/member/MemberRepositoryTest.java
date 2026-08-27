package com.github.dennispoliciano.escalas.member;

import com.github.dennispoliciano.escalas.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MemberRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void mustSaveAndReturnAValidMember() {
        Member member = new Member("João Silva", "joao@email.com", "+5511999999999", LocalDate.of(1990, 1, 1));
        Member persistedMember = memberRepository.save(member);
        assertNotNull(persistedMember.getId());
        assertNotNull(persistedMember.getPhone());
        assertTrue(persistedMember.getActive());
    }

}
