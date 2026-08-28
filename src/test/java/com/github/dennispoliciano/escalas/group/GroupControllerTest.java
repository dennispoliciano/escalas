package com.github.dennispoliciano.escalas.group;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GroupController.class)
public class GroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GroupRepository groupRepository;

    @Test
    void whenPostValidGroup_thenReturns200AndPersistedBody() throws Exception {
        Group group = new Group("Louvor", "pln-lv-01", "Louvor");
        Mockito.when(groupRepository.save(any(Group.class))).thenReturn(group);

        mockMvc.perform(post("/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(group)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Louvor"));

        Mockito.verify(groupRepository).save(any(Group.class));
    }

    @Test
    void whenPostGroupWithBlankName_thenReturns400() throws Exception {
        Group group = new Group("", "pln-lv-01", "Louvor");

        mockMvc.perform(post("/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(group)))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(groupRepository);
    }
}
