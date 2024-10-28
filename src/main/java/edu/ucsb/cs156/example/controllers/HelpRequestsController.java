package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.HelpRequests;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.HelpRequestsRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import java.time.LocalDateTime;

/**
 * This is a REST controller for HelpRequestss
 */

@Tag(name = "HelpRequests")
@RequestMapping("/api/helprequests")
@RestController
@Slf4j
public class HelpRequestsController extends ApiController {

    @Autowired
    HelpRequestsRepository helpRequestsRepository;

    /**
     * List all help requests
     * 
     * @return an iterable of HelpRequests
     */
    @Operation(summary= "List all help requests")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<HelpRequests> allHelpRequestss() {
        Iterable<HelpRequests> dates = helpRequestsRepository.findAll();
        return dates;
    }

    /**
     * Get a single date by id
     * 
     * @param id the id of HelpRequests
     * @return a HelpRequests
     */
    @Operation(summary= "Get a single date")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public HelpRequests getById(
            @Parameter(name="id") @RequestParam Long id) {
        HelpRequests HelpRequest = helpRequestsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(HelpRequests.class, id));

        return HelpRequest;
    }

    /**
     * Create a new date
     * @param requesterEmail
     * @param teamId
     * @param tableOrBreakoutRoom
     * @param requestTime
     * @param explanation
     * @param solved
     * @return the saved HelpRequests
     */
    @Operation(summary= "Create a new date")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public HelpRequests postHelpRequests(
            @Parameter(name="requesterEmail") @RequestParam String requesterEmail,
            @Parameter(name="teamId") @RequestParam String teamId,
            @Parameter(name="tableOrBreakoutRoom") @RequestParam String tableOrBreakoutRoom,
            @Parameter(name="requestTime", description="date (in iso format, e.g. YYYY-mm-ddTHH:MM:SS; see https://en.wikipedia.org/wiki/ISO_8601)") @RequestParam("requestTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime requestTime, 
            @Parameter(name = "explanation") @RequestParam String explanation,
            @Parameter(name = "solved") @RequestParam boolean solved)

            throws JsonProcessingException {

        // For an explanation of @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        // See: https://www.baeldung.com/spring-date-parameters

        log.info("requestTime={}", requestTime);

        HelpRequests HelpRequest = new HelpRequests();
        HelpRequest.setRequesterEmail(requesterEmail);
        HelpRequest.setTeamId(teamId);
        HelpRequest.setTableOrBreakoutRoom(tableOrBreakoutRoom);
        HelpRequest.setRequestTime(requestTime);
        HelpRequest.setExplanation(explanation);
        HelpRequest.setSolved(solved);

        HelpRequests savedHelpRequests = helpRequestsRepository.save(HelpRequest);

        return savedHelpRequests;
    }

    /**
     * Delete a HelpRequests
     * 
     * @param id the id of the HelpRequests to delete
     * @return a message indicating the HelpRequests was deleted
     */
    @Operation(summary= "Delete a HelpRequests")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteHelpRequests(
            @Parameter(name="id") @RequestParam Long id) {
        HelpRequests HelpRequest = helpRequestsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(HelpRequests.class, id));

        helpRequestsRepository.delete(HelpRequest);
        return genericMessage("HelpRequests with id %s deleted".formatted(id));
    }

    /**
     * Update a single date
     * 
     * @param id       id of the date to update
     * @param incoming the new date
     * @return the updated date object
     */
    @Operation(summary= "Update a single date")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public HelpRequests updateHelpRequests(
            @Parameter(name="id") @RequestParam Long id,
            @RequestBody @Valid HelpRequests incoming) {

        HelpRequests HelpRequest = helpRequestsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(HelpRequests.class, id));
        HelpRequest.setRequesterEmail(incoming.getRequesterEmail());
        HelpRequest.setTeamId(incoming.getTeamId());
        HelpRequest.setTableOrBreakoutRoom(incoming.getTableOrBreakoutRoom());
        HelpRequest.setRequestTime(incoming.getRequestTime());
        HelpRequest.setExplanation(incoming.getExplanation());
        HelpRequest.setSolved(incoming.getSolved());
    

        helpRequestsRepository.save(HelpRequest);

        return HelpRequest;
    }
}
