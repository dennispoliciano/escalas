package com.github.dennispoliciano.escalas.organization;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(OrganizationController.class)
public class OrganizationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrganizationRepository organizationRepository;

    @Test
    void whenOrganizationExists_thenReturns200AndBody() throws Exception {
        // given: o mock decide o que o repository "encontra"
        Organization organization = new Organization("001", "Igreja Batista Central", "Igreja", "Av. Paulista");
        Mockito.when(organizationRepository.findAll())
                .thenReturn(List.of(organization));

        // when + then: simula a requisição e valida a resposta
        mockMvc.perform(get("/organizations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Igreja Batista Central"));
    }

    @Test
    void whenPostValidOrganization_thenReturns200AndPersistedBody() throws Exception {
        Organization organization = new Organization("001", "Igreja Batista Central", "Igreja", "Av. Paulista");
        Mockito.when(organizationRepository.save(any(Organization.class))).thenReturn(organization);

        mockMvc.perform(post("/organizations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(organization)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Igreja Batista Central"));

        Mockito.verify(organizationRepository).save(any(Organization.class));
    }

    @Test
    void whenPostOrganizationWithBlankName_thenReturns400() throws Exception {
        Organization organization = new Organization("001", "", "Igreja", "Av. Paulista");

        mockMvc.perform(post("/organizations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(organization)))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(organizationRepository);
    }
}
