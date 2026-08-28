package com.github.dennispoliciano.escalas.groupmembership;

import tools.jackson.databind.ObjectMapper;
import com.github.dennispoliciano.escalas.function.Function;
import com.github.dennispoliciano.escalas.group.Group;
import com.github.dennispoliciano.escalas.group.GroupRepository;
import com.github.dennispoliciano.escalas.member.Member;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GroupMembershipController.class)
public class GroupMembershipControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GroupMembershipRepository groupMembershipRepository;

    @MockitoBean
    private GroupRepository groupRepository;

    @Test
    void whenPostValidGroupMembership_thenReturns200AndPersistedBody() throws Exception {
        Group group = new Group("Louvor", "pln-lv-01", "Louvor");
        Member member = new Member("João Silva", "joao@email.com", "+5511999999999", LocalDate.of(1990, 1, 1));
        Function function = new Function("Vocal", "🎤", group);
        GroupMembership groupMembership = new GroupMembership(group, member, function);

        Mockito.when(groupMembershipRepository.save(any(GroupMembership.class))).thenReturn(groupMembership);

        mockMvc.perform(post("/group-membership")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(groupMembership)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));

        Mockito.verify(groupMembershipRepository).save(any(GroupMembership.class));
    }
}
