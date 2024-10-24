package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.UCSBDiningCommons;
import edu.ucsb.cs156.example.entities.UCSBDiningCommonsMenuItem;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.UCSBDiningCommonsMenuItemRepository;

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
 * This is a REST controller for UCSBDiningCommonsMenuItem
 */

@Tag(name = "UCSBDiningCommonsMenuItems")
@RequestMapping("/api/ucsbdiningcommonsmenuitems")
@RestController
@Slf4j
public class UCSBDiningCommonsMenuItemsController extends ApiController {

    @Autowired
    UCSBDiningCommonsMenuItemRepository ucsbDiningCommonsMenuItemRepository;

    /**
     * List all UCSB Dining Commons Menu Items
     * 
     * @return an iterable of UCSBDiningCommonsMenuItem
     */
    @Operation(summary= "List all ucsb dining commons menu items")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<UCSBDiningCommonsMenuItem> allUCSBDiningCommonsMenuItems() {
        Iterable<UCSBDiningCommonsMenuItem> ucsbDiningCommonsMenuItems = ucsbDiningCommonsMenuItemRepository.findAll();
        return ucsbDiningCommonsMenuItems;
    }

    /**
     * Create a new UCSBDiningCommonsMenuItem
     * 
     * @param diningCommonsCode code of the UCSBDiningCommons
     * @param name name of the UCSBDiningCommons
     * @param station station of the UCSBDiningCommons
     * @return the created UCSBDiningCommonsMenuItem
     */
    @Operation(summary= "Create a new UCSBDiningCommonsMenuItem")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public UCSBDiningCommonsMenuItem postUCSBDiningCommonsMenuItem(
            @Parameter(name="diningCommonsCode") @RequestParam String diningCommonsCode,
            @Parameter(name="name") @RequestParam String name,
            @Parameter(name="station") @RequestParam String station
    ) throws JsonProcessingException{
        UCSBDiningCommonsMenuItem newUcsbDiningCommonsMenuItem = new UCSBDiningCommonsMenuItem();
        newUcsbDiningCommonsMenuItem.setDiningCommonsCode(diningCommonsCode);
        newUcsbDiningCommonsMenuItem.setName(name);
        newUcsbDiningCommonsMenuItem.setStation(station);

        UCSBDiningCommonsMenuItem savedItems = ucsbDiningCommonsMenuItemRepository.save(newUcsbDiningCommonsMenuItem);

        return savedItems;
    }
}
