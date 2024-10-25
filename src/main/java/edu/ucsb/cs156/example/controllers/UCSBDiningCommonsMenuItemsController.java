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
     * Get a single UCSBDiningCommonsMenuItem via ID
     *  
     * @param id the id of the UCSBDiningCommonsMenuItem
     * @return a single UCSBDiningCommonsMenuItem
     * @throws EntityNotFoundException if the UCSBDiningCommonsMenuItem is not found
     */
    @Operation(summary= "Get a single ucsb dining commons menu item")   
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public UCSBDiningCommonsMenuItem getById(
            @Parameter(name="id") @RequestParam Long id) 
    throws EntityNotFoundException {
        UCSBDiningCommonsMenuItem ucsbDiningCommonsMenuItem = ucsbDiningCommonsMenuItemRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UCSBDiningCommonsMenuItem.class, id));
        return ucsbDiningCommonsMenuItem;
    }

    /**
     * Update a single UCSBDiningCommonsMenuItem via diningCommonsCode
     * 
     * @param id the id of the UCSBDiningCommonsMenuItem
     * @param ucsbDiningCommonsMenuItem the new UCSBDiningCommonsMenuItem
     * @return a single UCSBDiningCommonsMenuItem
     */
    @Operation(summary= "Update a single ucsb dining commons menu item")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public UCSBDiningCommonsMenuItem updateUCSBDiningCommonsMenuItem(
            @Parameter(name="id") @RequestParam Long id,
            @Valid @RequestBody UCSBDiningCommonsMenuItem ucsbDiningCommonsMenuItem) {

        UCSBDiningCommonsMenuItem ucsbDiningCommonsMenuItemToUpdate = ucsbDiningCommonsMenuItemRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UCSBDiningCommonsMenuItem.class, id));

        ucsbDiningCommonsMenuItemToUpdate.setDiningCommonsCode(ucsbDiningCommonsMenuItem.getDiningCommonsCode());
        ucsbDiningCommonsMenuItemToUpdate.setName(ucsbDiningCommonsMenuItem.getName());
        ucsbDiningCommonsMenuItemToUpdate.setStation(ucsbDiningCommonsMenuItem.getStation());
        ucsbDiningCommonsMenuItemRepository.save(ucsbDiningCommonsMenuItemToUpdate);
        return ucsbDiningCommonsMenuItemToUpdate;
    }

    /**
     * Deletes a UCSBDiningCommonsMenuItem
     * 
     * @param id the id of the UCSBDiningCommonsMenuItem
     * @throws EntityNotFoundException if the UCSBDiningCommonsMenuItem is not found
     */
    @Operation(summary= "Delete a UCSBDiningCommonsMenuItem")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteById(
            @Parameter(name="id") @RequestParam Long id) 
    throws EntityNotFoundException {
        UCSBDiningCommonsMenuItem ucsbDiningCommonsMenuItem = ucsbDiningCommonsMenuItemRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UCSBDiningCommonsMenuItem.class, id));
        ucsbDiningCommonsMenuItemRepository.delete(ucsbDiningCommonsMenuItem);
        return genericMessage("UCSBDiningCommonsMenuItem with id %s deleted".formatted(id));
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
