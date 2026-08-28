package com.github.dennispoliciano.escalas.event;

import tools.jackson.databind.ObjectMapper;
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

@WebMvcTest(EventController.class)
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EventRepository eventRepository;

    private final Organization organization = new Organization("001", "Igreja Batista Central", "Igreja", "Av. Paulista");

    @Test
    void whenPostValidEvent_thenReturns200AndPersistedBody() throws Exception {
        Event event = new Event("Culto", "Description", "Tipo 1",
                LocalDateTime.of(2026, 8, 27, 10, 0, 0),
                LocalDateTime.of(2026, 8, 27, 11, 0, 0),
                "semanal", organization);
        Mockito.when(eventRepository.save(any(Event.class))).thenReturn(event);

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Culto"));

        Mockito.verify(eventRepository).save(any(Event.class));
    }

    @Test
    void whenPostEventWithBlankName_thenReturns400() throws Exception {
        Event event = new Event("", "Description", "Tipo 1",
                LocalDateTime.of(2026, 8, 27, 10, 0, 0),
                LocalDateTime.of(2026, 8, 27, 11, 0, 0),
                "semanal", organization);

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(eventRepository);
    }

    @Test
    void whenPostEventWithNullStartDateTime_thenReturns400() throws Exception {
        Event event = new Event("Culto", "Description", "Tipo 1",
                null,
                LocalDateTime.of(2026, 8, 27, 11, 0, 0),
                "semanal", organization);

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(eventRepository);
    }
}
