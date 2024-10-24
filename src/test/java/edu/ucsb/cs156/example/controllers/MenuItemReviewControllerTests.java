



package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.MenuItemReviews;
import edu.ucsb.cs156.example.repositories.MenuItemReviewsRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;




@WebMvcTest(controllers = MenuItemReviewController.class)
@Import(TestConfig.class)
public class MenuItemReviewControllerTests extends ControllerTestCase{

    @MockBean
    MenuItemReviewsRepository menuItemReviewsRepository;

    @MockBean
    UserRepository userRepository;

    @Test
    public void logged_out_users_cannot_get_all() throws Exception {
        mockMvc.perform(get("/api/menuitemreview/all"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = {"USER"})
    @Test
    public void logged_in_users_can_get_all() throws Exception {
        mockMvc.perform(get("/api/menuitemreview/all"))
                .andExpect(status().isOk());
    }



    @WithMockUser(roles = {"ADMIN"})
    @Test
    public void admin_can_post_a_new_review() throws Exception {
        MenuItemReviews review = MenuItemReviews.builder()
                .id(10)
                .itemId(3)
                .reviewEmail("johnlins@ucsb.edu")
                .stars(4)
                .dateReviewed(LocalDateTime.parse("2022-01-03T00:00:00"))
                .comments("very good")
                .build();

        when(menuItemReviewsRepository.save(eq(review))).thenReturn(review);

        MvcResult response = mockMvc.perform(post("/api/menuitemreview/post?id=10&itemId=3&reviewEmail=johnlins@ucsb.edu&stars=4&dateReviewed=2022-01-03T00:00:00&comments=very good")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        verify(menuItemReviewsRepository, times(1)).save(review);
        String expectedJson = mapper.writeValueAsString(review);
        assertEquals(expectedJson, response.getResponse().getContentAsString());
    }





    @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_can_get_all_menu_item_reviews() throws Exception {



                MenuItemReviews review1 = MenuItemReviews.builder()
                .id(10)
                .itemId(3)
                .reviewEmail("johnlins@ucsb.edu")
                .stars(4)
                .dateReviewed(LocalDateTime.parse("2022-01-03T00:00:00"))
                .comments("very good")
                .build();


                MenuItemReviews review2 = MenuItemReviews.builder()
                .id(11)
                .itemId(4)
                .reviewEmail("johnlins@ucsb.edu")
                .stars(5)
                .dateReviewed(LocalDateTime.parse("2022-01-03T00:00:00"))
                .comments("very goodish")
                .build();

                ArrayList<MenuItemReviews> expected = new ArrayList<>();
                expected.addAll(Arrays.asList(review1, review2));

                when(menuItemReviewsRepository.findAll()).thenReturn(expected);


                MvcResult response = mockMvc.perform(get("/api/menuitemreview/all"))
                                .andExpect(status().isOk()).andReturn();



                verify(menuItemReviewsRepository, times(1)).findAll();
                String expectedJson = mapper.writeValueAsString(expected);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }






}
