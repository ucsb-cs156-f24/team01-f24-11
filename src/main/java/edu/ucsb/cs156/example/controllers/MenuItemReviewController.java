package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.MenuItemReviews;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.MenuItemReviewsRepository;

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

@Slf4j
@RestController
@Tag(name = "MenuItemReview")
@RequestMapping("/api/menuitemreview")
public class MenuItemReviewController extends ApiController {

    @Autowired
    MenuItemReviewsRepository menuItemReviewsRepository;


    @Operation(summary= "List all")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<MenuItemReviews> getAllMenuItemReviews() {
        Iterable<MenuItemReviews> menuItemReviews = menuItemReviewsRepository.findAll();
        return menuItemReviews;
    }




    @Operation(summary= "Create new")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")

    public MenuItemReviews createMenuItemReview(
            @Parameter(name="id") @RequestParam long id,
            @Parameter(name="itemId") @RequestParam long itemId,
            @Parameter(name="reviewEmail") @RequestParam String reviewEmail,
            @Parameter(name="stars") @RequestParam int stars,
            @Parameter(name="dateReviewed", description="date (in iso format, e.g. YYYY-mm-ddTHH:MM:SS; see https://en.wikipedia.org/wiki/ISO_8601)") @RequestParam("dateReviewed") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateReviewed,
            @Parameter(name="comments") @RequestParam String comments )
            throws JsonProcessingException {




        MenuItemReviews menuItemReviewRequest = new MenuItemReviews();
        menuItemReviewRequest.setId(id);
        menuItemReviewRequest.setItemId(itemId);
        menuItemReviewRequest.setReviewEmail(reviewEmail);
        menuItemReviewRequest.setStars(stars);
        menuItemReviewRequest.setDateReviewed(dateReviewed);
        menuItemReviewRequest.setComments(comments);




        MenuItemReviews createdReviews = menuItemReviewsRepository.save(menuItemReviewRequest);
        return createdReviews;
    }





    @Operation(summary = "Get a single menu item review by id")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public MenuItemReviews getMenuItemReviewById(
        @Parameter(name = "id") @RequestParam long id) {

        MenuItemReviews menuItemReview = menuItemReviewsRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(MenuItemReviews.class, id));

        return menuItemReview;
    }








    @Operation(summary = "Update a menu item review by id")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public MenuItemReviews updateMenuItemReview(
            @Parameter(name = "id") @RequestParam long id,
            @RequestBody @Valid MenuItemReviews updatedReview) {

        MenuItemReviews existingReview = menuItemReviewsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MenuItemReviews.class, id));


        existingReview.setItemId(updatedReview.getItemId());
        existingReview.setReviewEmail(updatedReview.getReviewEmail());
        existingReview.setStars(updatedReview.getStars());
        existingReview.setDateReviewed(updatedReview.getDateReviewed());
        existingReview.setComments(updatedReview.getComments());


        menuItemReviewsRepository.save(existingReview);

        return existingReview;
    }





@DeleteMapping("")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@Operation(summary = "Delete a menu item review by id")
public String deleteMenuItemReview(@RequestParam long id) throws EntityNotFoundException {
    MenuItemReviews menuItemReview = menuItemReviewsRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(MenuItemReviews.class, id));

    menuItemReviewsRepository.delete(menuItemReview);

    return "MenuItemReview with id " + id + " deleted";
}







}
