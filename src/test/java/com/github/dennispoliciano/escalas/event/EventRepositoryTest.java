package com.github.dennispoliciano.escalas.event;

import com.github.dennispoliciano.escalas.AbstractIntegrationTest;
import com.github.dennispoliciano.escalas.organization.Organization;
import com.github.dennispoliciano.escalas.organization.OrganizationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class EventRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    private LocalDateTime startDateTime = LocalDateTime.of(2026, 8, 27, 10, 0, 0);
    private LocalDateTime endDateTime = LocalDateTime.of(2026, 8, 27, 11, 0, 0);

    @Test
    public void whenSaveAnEvent_thenReturnAnEvent(){

        Organization organization = new Organization("org-1", "Organization 01", "Igreja", "Av. Paulista, 999. Sao Paulo - SP");
        Event event = new Event( "Evento 1", "Description", "Tipo 1", startDateTime, endDateTime, "semanal", organization);

        Organization persistedOrganization = organizationRepository.save(organization);
        Event persistedEvent = eventRepository.save(event);

        List<Event> events = eventRepository.findAll();

        assertNotNull(persistedEvent.getId());
        assertEquals(1, events.size());
    }

    @Test
    public void whenActiveAndInactiveEventsExist_thenFindAllByActiveTrueShouldReturnOnlyActiveOnes(){

        Organization organization = new Organization("org-1", "Organization 01", "Igreja", "Av. Paulista, 999. Sao Paulo - SP");
        Event activeEvent = new Event( "Evento 1", "Description", "Tipo 1", startDateTime, endDateTime, "semanal", organization);

        Event inactiveEvent = new Event( "Evento 2", "Description", "Tipo 1", startDateTime, endDateTime, "semanal", organization);
        inactiveEvent.setActive(false);

        Organization persistedOrganization = organizationRepository.save(organization);
        Event persistedActiveEvent = eventRepository.save(activeEvent);
        Event persistedInactiveEvent = eventRepository.save(inactiveEvent);

        List<Event> events = eventRepository.findAllByActiveTrue();

        assertNotNull(persistedActiveEvent.getId());
        assertEquals(1, events.size());
        assertEquals(activeEvent.getId(), events.getFirst().getId());
    }
}
