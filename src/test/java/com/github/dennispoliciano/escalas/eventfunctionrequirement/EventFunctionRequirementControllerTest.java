package com.github.dennispoliciano.escalas.eventfunctionrequirement;

import tools.jackson.databind.ObjectMapper;
import com.github.dennispoliciano.escalas.event.Event;
import com.github.dennispoliciano.escalas.function.Function;
import com.github.dennispoliciano.escalas.group.Group;
import com.github.dennispoliciano.escalas.organization.Organization;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventFunctionRequirementController.class)
public class EventFunctionRequirementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EventFunctionRequirementRepository eventFunctionRequirementRepository;

    @Test
    void whenPostValidEventFunctionRequirement_thenReturns200AndPersistedBody() throws Exception {
        Organization organization = new Organization("001", "Igreja Batista Central", "Igreja", "Av. Paulista");
        Event event = new Event("Culto", "Description", "Tipo 1",
                LocalDateTime.of(2026, 8, 27, 10, 0, 0),
                LocalDateTime.of(2026, 8, 27, 11, 0, 0),
                "semanal", organization);
        Group group = new Group("Louvor", "pln-lv-01", "Louvor");
        Function function = new Function("Vocal", "🎤", group);
        EventFunctionRequirement requirement = new EventFunctionRequirement(event, function, 2);

        Mockito.when(eventFunctionRequirementRepository.save(any(EventFunctionRequirement.class))).thenReturn(requirement);

        mockMvc.perform(post("/event-function-requirements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requirement)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requiredQuantity").value(2));

        Mockito.verify(eventFunctionRequirementRepository).save(any(EventFunctionRequirement.class));
    }
}
