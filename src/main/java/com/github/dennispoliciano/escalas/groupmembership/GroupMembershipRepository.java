package com.github.dennispoliciano.escalas.groupmembership;

import com.github.dennispoliciano.escalas.group.Group;
import com.github.dennispoliciano.escalas.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupMembershipRepository extends JpaRepository<GroupMembership, Long> {

    boolean existsByMemberAndActiveTrue(Member member);

    boolean existsByMemberAndGroupAndActiveTrue(Member member, Group group);

    List<GroupMembership> findByActiveTrue();

    List<GroupMembership> findByGroupAndActiveTrue(Group group);
}
