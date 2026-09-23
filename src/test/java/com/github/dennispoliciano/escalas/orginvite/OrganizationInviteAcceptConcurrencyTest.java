package com.github.dennispoliciano.escalas.orginvite;

import com.github.dennispoliciano.escalas.AbstractIntegrationTest;
import com.github.dennispoliciano.escalas.organization.Organization;
import com.github.dennispoliciano.escalas.organization.OrganizationRepository;
import com.github.dennispoliciano.escalas.orgmembership.OrgMembershipRepository;
import com.github.dennispoliciano.escalas.user.User;
import com.github.dennispoliciano.escalas.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Sem @Transactional: cada chamada concorrente precisa da sua própria transação/conexão
// para exercitar o lock pessimista de verdade, não só o comportamento dentro de uma única transação de teste.
@SpringBootTest
public class OrganizationInviteAcceptConcurrencyTest extends AbstractIntegrationTest {

    @Autowired
    private OrganizationInviteService organizationInviteService;

    @Autowired
    private OrganizationInviteRepository organizationInviteRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrgMembershipRepository orgMembershipRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Organization org;
    private OrganizationInvite invite;
    private User userA;
    private User userB;

    @AfterEach
    void cleanUp() {
        // Este teste comita de verdade (sem @Transactional, de propósito, para exercitar o lock
        // pessimista entre conexões reais) então precisa limpar manualmente, na ordem das FKs.
        if (userA != null) {
            orgMembershipRepository.findByUserAndOrganization(userA, org).ifPresent(orgMembershipRepository::delete);
        }
        if (userB != null) {
            orgMembershipRepository.findByUserAndOrganization(userB, org).ifPresent(orgMembershipRepository::delete);
        }
        if (invite != null) {
            organizationInviteRepository.deleteById(invite.getId());
        }
        if (userA != null) {
            userRepository.deleteById(userA.getId());
        }
        if (userB != null) {
            userRepository.deleteById(userB.getId());
        }
        if (org != null) {
            organizationRepository.deleteById(org.getId());
        }
    }

    @Test
    void whenTwoUsersAcceptTheSameInviteConcurrently_thenOnlyOneSucceeds() throws Exception {
        org = organizationRepository.save(new Organization("igreja-concorrencia", "Igreja Concorrencia", "church", "Rua Concorrencia, 1"));
        invite = organizationInviteRepository.save(
                new OrganizationInvite("token-concorrencia", org, Instant.now().plus(1, ChronoUnit.DAYS)));

        userA = userRepository.save(new User("concorrencia.a@email.com", passwordEncoder.encode("senha123")));
        userB = userRepository.save(new User("concorrencia.b@email.com", passwordEncoder.encode("senha123")));

        CountDownLatch readyToStart = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(2);

        boolean succeededA;
        boolean succeededB;
        try {
            Callable<Boolean> acceptAttempt = attemptFor(userA, invite.getToken(), readyToStart, start);
            Callable<Boolean> otherAcceptAttempt = attemptFor(userB, invite.getToken(), readyToStart, start);

            Future<Boolean> resultA = executor.submit(acceptAttempt);
            Future<Boolean> resultB = executor.submit(otherAcceptAttempt);

            readyToStart.await();
            start.countDown();

            succeededA = resultA.get(10, TimeUnit.SECONDS);
            succeededB = resultB.get(10, TimeUnit.SECONDS);
        } finally {
            executor.shutdown();
        }

        assertTrue(succeededA ^ succeededB, "Exatamente uma das duas tentativas deveria ter sucesso");

        OrganizationInvite persisted = organizationInviteRepository.findByToken("token-concorrencia").orElseThrow();
        assertTrue(persisted.getUsed());

        long membershipsCreated = List.of(userA, userB).stream()
                .filter(user -> orgMembershipRepository.findByUserAndOrganization(user, org).isPresent())
                .count();
        assertEquals(1, membershipsCreated);
    }

    private Callable<Boolean> attemptFor(User user, String token, CountDownLatch readyToStart, CountDownLatch start) {
        return () -> {
            readyToStart.countDown();
            start.await();
            try {
                organizationInviteService.accept(token, user);
                return true;
            } catch (ResponseStatusException e) {
                return false;
            }
        };
    }
}
