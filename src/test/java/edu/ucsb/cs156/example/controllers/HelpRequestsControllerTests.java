package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.HelpRequests;
import edu.ucsb.cs156.example.repositories.HelpRequestsRepository;

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

import java.time.LocalDateTime;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = HelpRequestsController.class)
@Import(TestConfig.class)
public class HelpRequestsControllerTests extends ControllerTestCase {

        @MockBean
        HelpRequestsRepository helpRequestsRepository;

        @MockBean
        UserRepository userRepository;

        // Authorization tests for /api/HelpRequests/admin/all

        @Test
        public void logged_out_users_cannot_get_all() throws Exception {
                mockMvc.perform(get("/api/helprequests/all"))
                                .andExpect(status().is(403)); // logged out users can't get all
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_users_can_get_all() throws Exception {
                mockMvc.perform(get("/api/helprequests/all"))
                                .andExpect(status().is(200)); // logged
        }

        

        @Test
        public void logged_out_users_cannot_get_by_id() throws Exception {
                mockMvc.perform(get("/api/helprequests?id=7"))
                                .andExpect(status().is(403)); // logged out users can't get by id
        }

        // Authorization tests for /api/HelpRequests/post
        // (Perhaps should also have these for put and delete)

        @Test
        public void logged_out_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/helprequests/post"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_regular_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/helprequests/post"))
                                .andExpect(status().is(403)); // only admins can post
        }

        // // Tests with mocks for database actions

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {

                // arrange
                LocalDateTime reqTime = LocalDateTime.parse("2022-01-03T00:00:00");
                
                HelpRequests helpRequests = HelpRequests.builder()
                                .requesterEmail("monkey@ucsb.edu")
                                .teamId("12345")
                                .tableOrBreakoutRoom("table")
                                .requestTime(reqTime)
                                .explanation("The cows")
                                .solved(true)
                                .build();
                when(helpRequestsRepository.findById(eq(7L))).thenReturn(Optional.of(helpRequests));

                // act
                MvcResult response = mockMvc.perform(get("/api/helprequests?id=7"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(helpRequestsRepository, times(1)).findById(eq(7L));
                String expectedJson = mapper.writeValueAsString(helpRequests);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

                // arrange

                when(helpRequestsRepository.findById(eq(7L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(get("/api/helprequests?id=7"))
                                .andExpect(status().isNotFound()).andReturn();

                // assert

                verify(helpRequestsRepository, times(1)).findById(eq(7L));
                Map<String, Object> json = responseToJson(response);
                assertEquals("EntityNotFoundException", json.get("type"));
                assertEquals("HelpRequests with id 7 not found", json.get("message"));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_can_get_all_HelpRequests() throws Exception {

                // arrange
                LocalDateTime reqTime = LocalDateTime.parse("2022-01-03T11:00:00");

                HelpRequests helpRequests1 = HelpRequests.builder()
                                .requesterEmail("monkey1@ucsb.edu")
                                .teamId("123456")
                                .tableOrBreakoutRoom("BreakoutRoom")
                                .requestTime(reqTime)
                                .explanation("The orangatangs")
                                .solved(false)
                                .build();

                LocalDateTime reqTime2 = LocalDateTime.parse("2022-03-11T01:00:00");

                HelpRequests helpRequests2 = HelpRequests.builder()
                                .requesterEmail("monkey2@ucsb.edu")
                                .teamId("12345")
                                .tableOrBreakoutRoom("table")
                                .requestTime(reqTime2)
                                .explanation("The cows")
                                .solved(true)
                                .build();

                ArrayList<HelpRequests> helpreqs = new ArrayList<>();
                helpreqs.addAll(Arrays.asList(helpRequests1, helpRequests2));

                when(helpRequestsRepository.findAll()).thenReturn(helpreqs);

                // act
                MvcResult response = mockMvc.perform(get("/api/helprequests/all"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(helpRequestsRepository, times(1)).findAll();
                String expectedJson = mapper.writeValueAsString(helpreqs);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void an_admin_user_can_post_a_new_HelpRequests() throws Exception {
                // arrange

                LocalDateTime reqTime = LocalDateTime.parse("2022-01-03T00:00:00");

                HelpRequests HelpRequests1 = HelpRequests.builder()
                                .requesterEmail("monkey@ucsb.edu")
                                .teamId("12345")
                                .tableOrBreakoutRoom("table")
                                .requestTime(reqTime)
                                .explanation("The cows")
                                .solved(true)
                                .build();

                when(helpRequestsRepository.save(eq(HelpRequests1))).thenReturn(HelpRequests1);

                // act
                MvcResult response = mockMvc.perform(
                                post("/api/helprequests/post?requesterEmail=monkey@ucsb.edu&teamId=12345&tableOrBreakoutRoom=table&requestTime=2022-01-03T00:00:00&explanation=The cows&solved=true")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(helpRequestsRepository, times(1)).save(HelpRequests1);
                String expectedJson = mapper.writeValueAsString(HelpRequests1);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_delete_a_date() throws Exception {
                // arrange

                LocalDateTime reqTime = LocalDateTime.parse("2022-01-03T00:00:00");

                HelpRequests HelpRequests1 = HelpRequests.builder()
                                .requesterEmail("monkey@ucsb.edu")
                                .teamId("12345")
                                .tableOrBreakoutRoom("table")
                                .requestTime(reqTime)
                                .explanation("Thecows")
                                .solved(true)
                                .build();

                when(helpRequestsRepository.findById(eq(15L))).thenReturn(Optional.of(HelpRequests1));

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/helprequests?id=15")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(helpRequestsRepository, times(1)).findById(15L);
                verify(helpRequestsRepository, times(1)).delete(any());

                Map<String, Object> json = responseToJson(response);
                assertEquals("HelpRequests with id 15 deleted", json.get("message"));
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_tries_to_delete_non_existant_HelpRequests_and_gets_right_error_message()
                        throws Exception {
                // arrange

                when(helpRequestsRepository.findById(eq(15L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/helprequests?id=15")
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(helpRequestsRepository, times(1)).findById(15L);
                Map<String, Object> json = responseToJson(response);
                assertEquals("HelpRequests with id 15 not found", json.get("message"));
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_edit_an_existing_HelpRequests() throws Exception {
                // arrange

                LocalDateTime reqTime = LocalDateTime.parse("2022-01-03T11:00:00");

                HelpRequests HelpRequestsOrig = HelpRequests.builder()
                                .requesterEmail("monkey1@ucsb.edu")
                                .teamId("123456")
                                .tableOrBreakoutRoom("BreakoutRoom")
                                .requestTime(reqTime)
                                .explanation("The orangatangs")
                                .solved(false)
                                .build();

                LocalDateTime reqTime2 = LocalDateTime.parse("2022-03-11T01:00:00");

                HelpRequests HelpRequestsEdited = HelpRequests.builder()
                                .requesterEmail("monkey2@ucsb.edu")
                                .teamId("12345")
                                .tableOrBreakoutRoom("table")
                                .requestTime(reqTime2)
                                .explanation("The cows")
                                .solved(true)
                                .build();

                String requestBody = mapper.writeValueAsString(HelpRequestsEdited);

                when(helpRequestsRepository.findById(eq(67L))).thenReturn(Optional.of(HelpRequestsOrig));

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/helprequests?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(helpRequestsRepository, times(1)).findById(67L);
                verify(helpRequestsRepository, times(1)).save(HelpRequestsEdited); // should be saved with correct user
                String responseString = response.getResponse().getContentAsString();
                assertEquals(requestBody, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_cannot_edit_HelpRequests_that_does_not_exist() throws Exception {
                // arrange

                LocalDateTime reqTime = LocalDateTime.parse("2022-01-03T00:00:00");

                HelpRequests HelpRequestsOrig = HelpRequests.builder()
                                .requesterEmail("monkey1@ucsb.edu")
                                .teamId("123456")
                                .tableOrBreakoutRoom("BreakoutRoom")
                                .requestTime(reqTime)
                                .explanation("The orangatangs")
                                .solved(false)
                                .build();

                String requestBody = mapper.writeValueAsString(HelpRequestsOrig);

                when(helpRequestsRepository.findById(eq(67L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/helprequests?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(helpRequestsRepository, times(1)).findById(67L);
                Map<String, Object> json = responseToJson(response);
                assertEquals("HelpRequests with id 67 not found", json.get("message"));

        }

        // @WithMockUser(roles = { "USER" })
        // @Test
        // public void logged_in_users_can_return_all() throws Exception {
        //         LocalDateTime reqTime = LocalDateTime.parse("2024-10-22T12:10:56")
        //         HelpRequests HelpRequests = HelpRequests.builder()
        //                         .requesterEmail("monkey@ucsb.edu")
        //                         .teamId("12345")
        //                         .tableOrBreakoutRoom("table")
        //                         .requestTime(reqTime)
        //                         .explanation("The cows")
        //                         .solved(true)
        //                         .build();
        //         mockMvc.perform(get("/api/HelpRequests/all"))
        //                         .andExpect(status().is(200)); // logged
        //         when(HelpRequestsRepository.findById(eq(67L))).thenReturn(Optional.empty());
        // }
}
