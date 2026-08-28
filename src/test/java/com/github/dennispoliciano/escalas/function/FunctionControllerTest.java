package com.github.dennispoliciano.escalas.function;

import tools.jackson.databind.ObjectMapper;
import com.github.dennispoliciano.escalas.group.Group;
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

@WebMvcTest(FunctionController.class)
public class FunctionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FunctionRepository functionRepository;

    private final Group group = new Group("Louvor", "pln-lv-01", "Louvor");

    @Test
    void whenPostValidFunction_thenReturns200AndPersistedBody() throws Exception {
        Function function = new Function("Vocal", "🎤", group);
        Mockito.when(functionRepository.save(any(Function.class))).thenReturn(function);

        mockMvc.perform(post("/functions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(function)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Vocal"));

        Mockito.verify(functionRepository).save(any(Function.class));
    }

    @Test
    void whenPostFunctionWithBlankName_thenReturns400() throws Exception {
        Function function = new Function("", "🎤", group);

        mockMvc.perform(post("/functions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(function)))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(functionRepository);
    }
}
