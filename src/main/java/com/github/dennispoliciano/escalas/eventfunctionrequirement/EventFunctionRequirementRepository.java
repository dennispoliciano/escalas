package com.github.dennispoliciano.escalas.eventfunctionrequirement;

import com.github.dennispoliciano.escalas.event.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventFunctionRequirementRepository extends JpaRepository<EventFunctionRequirement, Long> {

    List<EventFunctionRequirement> findByEvent(Event event);

}
