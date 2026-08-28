package com.github.dennispoliciano.escalas.eventfunctionrequirement;

import com.github.dennispoliciano.escalas.AbstractIntegrationTest;
import com.github.dennispoliciano.escalas.event.Event;
import com.github.dennispoliciano.escalas.event.EventRepository;
import com.github.dennispoliciano.escalas.function.Function;
import com.github.dennispoliciano.escalas.function.FunctionRepository;
import com.github.dennispoliciano.escalas.group.Group;
import com.github.dennispoliciano.escalas.group.GroupRepository;
import com.github.dennispoliciano.escalas.organization.Organization;
import com.github.dennispoliciano.escalas.organization.OrganizationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class EventFunctionRequirementRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private EventFunctionRequirementRepository eventFunctionRequirementRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Test
    void whenEventHasMultipleFunctionRequirements_thenFindByEventShouldReturnEachRequirementWithItsQuantity() {
        Organization organization = organizationRepository.save(
                new Organization("org-1", "Organization 01", "Igreja", "Av. Paulista, 999. Sao Paulo - SP"));
        Event event = eventRepository.save(new Event(
                "Culto", "Description", "Tipo 1",
                LocalDateTime.of(2026, 8, 27, 10, 0, 0),
                LocalDateTime.of(2026, 8, 27, 11, 0, 0),
                "semanal", organization));

        Group group = groupRepository.save(new Group("Louvor", "pln-lv-01", "Louvor"));
        Function guitar = functionRepository.save(new Function("guitar", "🎸", group));
        Function vocal = functionRepository.save(new Function("vocal", "🎤", group));
        Function drums = functionRepository.save(new Function("drums", "🥁", group));

        eventFunctionRequirementRepository.save(new EventFunctionRequirement(event, guitar, 2));
        eventFunctionRequirementRepository.save(new EventFunctionRequirement(event, vocal, 3));
        eventFunctionRequirementRepository.save(new EventFunctionRequirement(event, drums, 1));

        List<EventFunctionRequirement> requirements = eventFunctionRequirementRepository.findByEvent(event);

        Map<Long, Integer> quantityByFunctionId = requirements.stream()
                .collect(java.util.stream.Collectors.toMap(
                        requirement -> requirement.getFunction().getId(),
                        EventFunctionRequirement::getRequiredQuantity));

        assertEquals(3, requirements.size());
        assertEquals(2, quantityByFunctionId.get(guitar.getId()));
        assertEquals(3, quantityByFunctionId.get(vocal.getId()));
        assertEquals(1, quantityByFunctionId.get(drums.getId()));
    }
}
