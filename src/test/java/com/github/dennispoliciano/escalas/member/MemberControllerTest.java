package com.github.dennispoliciano.escalas.member;

import tools.jackson.databind.ObjectMapper;
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

@WebMvcTest(MemberController.class)
public class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MemberRepository memberRepository;

    @Test
    void whenPostValidMember_thenReturns200AndPersistedBody() throws Exception {
        Member member = new Member("João Silva", "joao@email.com", "+5511999999999", LocalDate.of(1990, 1, 1));
        Mockito.when(memberRepository.save(any(Member.class))).thenReturn(member);

        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(member)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("João Silva"));

        Mockito.verify(memberRepository).save(any(Member.class));
    }

    @Test
    void whenPostMemberWithInvalidEmail_thenReturns400() throws Exception {
        Member member = new Member("João Silva", "joao-email.com", "+5511999999999", LocalDate.of(1990, 1, 1));

        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(member)))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(memberRepository);
    }

    @Test
    void whenPostMemberWithInvalidPhone_thenReturns400() throws Exception {
        Member member = new Member("João Silva", "joao@email.com", "abc", LocalDate.of(1990, 1, 1));

        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(member)))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(memberRepository);
    }
}
