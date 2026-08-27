package com.github.dennispoliciano.escalas.groupmembership;

import com.github.dennispoliciano.escalas.AbstractIntegrationTest;
import com.github.dennispoliciano.escalas.function.Function;
import com.github.dennispoliciano.escalas.function.FunctionRepository;
import com.github.dennispoliciano.escalas.group.Group;
import com.github.dennispoliciano.escalas.group.GroupRepository;
import com.github.dennispoliciano.escalas.member.Member;
import com.github.dennispoliciano.escalas.member.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class GroupMembershipRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private GroupMembershipRepository groupMembershipRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void whenMembershipHasInactiveFunction_thenFunctionActiveStatusShouldBeQueryable() {
        Group group = groupRepository.save(new Group("Louvor", "pln-lv-01", "Louvor"));
        Member member = memberRepository.save(new Member("John", "john@email.com", "+5511999999999", LocalDate.now()));

        Function function = new Function("guitar", "🎸", group);
        function.setActive(false);
        Function persistedFunction = functionRepository.save(function);

        GroupMembership membership = new GroupMembership(group, member, persistedFunction);
        GroupMembership persistedMembership = groupMembershipRepository.save(membership);

        assertNotNull(persistedMembership.getId());
        assertTrue(persistedMembership.getActive());
        assertFalse(persistedMembership.getFunction().getActive());
    }

    @Test
    void whenMemberHasActiveMembership_thenExistsByMemberAndActiveTrueShouldReturnTrue() {
        Group group = groupRepository.save(new Group("Louvor", "pln-lv-01", "Louvor"));
        Member member = memberRepository.save(new Member("John", "john@email.com", "+5511999999999", LocalDate.now()));
        Function function = functionRepository.save(new Function("guitar", "🎸", group));
        groupMembershipRepository.save(new GroupMembership(group, member, function));

        boolean exists = groupMembershipRepository.existsByMemberAndActiveTrue(member);

        assertTrue(exists);
    }

    @Test
    void whenMemberHasOnlyInactiveMembership_thenExistsByMemberAndActiveTrueShouldReturnFalse() {
        Group group = groupRepository.save(new Group("Louvor", "pln-lv-01", "Louvor"));
        Member member = memberRepository.save(new Member("John", "john@email.com", "+5511999999999", LocalDate.now()));
        Function function = functionRepository.save(new Function("guitar", "🎸", group));
        GroupMembership membership = new GroupMembership(group, member, function);
        membership.setActive(false);
        groupMembershipRepository.save(membership);

        boolean exists = groupMembershipRepository.existsByMemberAndActiveTrue(member);

        assertFalse(exists);
    }

    @Test
    void whenMemberHasActiveMembershipInAnotherGroup_thenExistsByMemberAndGroupAndActiveTrueShouldReturnFalse() {
        Group group = groupRepository.save(new Group("Louvor", "pln-lv-01", "Louvor"));
        Group otherGroup = groupRepository.save(new Group("Som", "pln-som-01", "Som"));
        Member member = memberRepository.save(new Member("John", "john@email.com", "+5511999999999", LocalDate.now()));
        Function function = functionRepository.save(new Function("guitar", "🎸", group));
        groupMembershipRepository.save(new GroupMembership(group, member, function));

        boolean exists = groupMembershipRepository.existsByMemberAndGroupAndActiveTrue(member, otherGroup);

        assertFalse(exists);
    }

    @Test
    void whenSeveralMembershipsExist_thenFindByActiveTrueShouldReturnOnlyActiveOnes() {
        Group group = groupRepository.save(new Group("Louvor", "pln-lv-01", "Louvor"));
        Member member = memberRepository.save(new Member("John", "john@email.com", "+5511999999999", LocalDate.now()));
        Function function = functionRepository.save(new Function("guitar", "🎸", group));

        GroupMembership activeMembership = groupMembershipRepository.save(new GroupMembership(group, member, function));
        GroupMembership inactiveMembership = new GroupMembership(group, member, function);
        inactiveMembership.setActive(false);
        groupMembershipRepository.save(inactiveMembership);

        List<GroupMembership> result = groupMembershipRepository.findByActiveTrue();

        assertEquals(1, result.size());
        assertEquals(activeMembership.getId(), result.get(0).getId());
    }

    @Test
    void whenGroupHasActiveAndInactiveMemberships_thenFindByGroupAndActiveTrueShouldReturnOnlyActiveOnes() {
        Group group = groupRepository.save(new Group("Louvor", "pln-lv-01", "Louvor"));
        Group otherGroup = groupRepository.save(new Group("Som", "pln-som-01", "Som"));
        Member member = memberRepository.save(new Member("John", "john@email.com", "+5511999999999", LocalDate.now()));
        Function function = functionRepository.save(new Function("guitar", "🎸", group));

        GroupMembership activeMembership = groupMembershipRepository.save(new GroupMembership(group, member, function));
        groupMembershipRepository.save(new GroupMembership(otherGroup, member, function));

        List<GroupMembership> result = groupMembershipRepository.findByGroupAndActiveTrue(group);

        assertEquals(1, result.size());
        assertEquals(activeMembership.getId(), result.get(0).getId());
    }

}
