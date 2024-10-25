package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import lombok.With;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.UCSBDate;
import edu.ucsb.cs156.example.entities.UCSBDiningCommonsMenuItem;
import edu.ucsb.cs156.example.repositories.UCSBDiningCommonsMenuItemRepository;

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

@WebMvcTest(controllers = UCSBDiningCommonsMenuItemsController.class)
@Import(TestConfig.class)
public class UCSBDiningCommonsMenuItemControllerTests extends ControllerTestCase {

        @MockBean
        UCSBDiningCommonsMenuItemRepository ucsbDiningCommonsMenuItemRepository;

        @MockBean
        UserRepository userRepository;

        // Authorization tests for /api/ucsbdiningcommonsmenuitems/all

        @Test
        public void logged_out_users_cannot_get_all() throws Exception {
                mockMvc.perform(get("/api/ucsbdiningcommonsmenuitems/all"))
                                .andExpect(status().is(403)); // logged out users can't get all
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_users_can_get_all() throws Exception {
                mockMvc.perform(get("/api/ucsbdiningcommonsmenuitems/all"))
                                .andExpect(status().is(200)); // logged
        }
        
        // Authorization tests for /api/ucsbdates/post
        // (Perhaps should also have these for put and delete)

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_create_new_menu_item() throws Exception {

                UCSBDiningCommonsMenuItem menuItem = new UCSBDiningCommonsMenuItem();
                menuItem.setDiningCommonsCode("foo");
                menuItem.setName("ber");
                menuItem.setStation("basdf");
                when(ucsbDiningCommonsMenuItemRepository.save(menuItem)).thenReturn(menuItem);

                MvcResult response = mockMvc.perform(post("/api/ucsbdiningcommonsmenuitems/post?diningCommonsCode=foo&name=ber&station=basdf")
                                .with(csrf())).andExpect(status().is(200)).andReturn();

                verify (ucsbDiningCommonsMenuItemRepository, times(1)).save(any());  
                String expectedJson = "{\"id\":0,\"diningCommonsCode\":\"foo\",\"name\":\"ber\",\"station\":\"basdf\"}";
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);

        }

        @Test
        public void logged_out_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/ucsbdiningcommonsmenuitems/post"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_regular_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/ucsbdiningcommonsmenuitems/post"))
                                .andExpect(status().is(403)); // only admins can post
        }


        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void testNonEmptyReturns() throws Exception {
                // act
                UCSBDiningCommonsMenuItem menuItem = new UCSBDiningCommonsMenuItem();
                menuItem.setDiningCommonsCode("foo");
                menuItem.setName("bar");
                menuItem.setStation("baz");

                when(ucsbDiningCommonsMenuItemRepository.findAll()).thenReturn(
                                Arrays.asList(menuItem));

                MvcResult getResponse = mockMvc.perform(get("/api/ucsbdiningcommonsmenuitems/all"))
                                .andExpect(status().isOk()).andReturn();

                String getContent = getResponse.getResponse().getContentAsString();
                assertEquals(getContent, "[{\"id\":0,\"diningCommonsCode\":\"foo\",\"name\":\"bar\",\"station\":\"baz\"}]");
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void testGetIdHappyPath() throws Exception {
                UCSBDiningCommonsMenuItem mockItem = new UCSBDiningCommonsMenuItem();
                mockItem.setId(63L);
                mockItem.setDiningCommonsCode("foo");
                mockItem.setName("bar");
                mockItem.setStation("baz");
                when(ucsbDiningCommonsMenuItemRepository.findById(63L)).thenReturn(Optional.of(mockItem));
                MvcResult response = mockMvc.perform(get("/api/ucsbdiningcommonsmenuitems?id=63"))
                                .andExpect(status().isOk()).andReturn();

                String expected = "{\"id\":63,\"diningCommonsCode\":\"foo\",\"name\":\"bar\",\"station\":\"baz\"}";
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expected, responseString);
        }


        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void testGetIdNotFound() throws Exception {
                when(ucsbDiningCommonsMenuItemRepository.findById(63L)).thenReturn(Optional.empty());
                mockMvc.perform(get("/api/ucsbdiningcommonsmenuitems?id=63"))
                                .andExpect(status().isNotFound()).andReturn();

        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void testPutHappyPath() throws Exception {
                UCSBDiningCommonsMenuItem mockItem = new UCSBDiningCommonsMenuItem();
                mockItem.setId(63L);
                mockItem.setDiningCommonsCode("foo");
                mockItem.setName("bar");
                mockItem.setStation("baz");

                when(ucsbDiningCommonsMenuItemRepository.findById(63L)).thenReturn(Optional.of(mockItem));
                MvcResult response = mockMvc.perform(delete("/api/ucsbdiningcommonsmenuitems?id=63").with(csrf()))
                                .andExpect(status().isOk()).andReturn();
                verify(ucsbDiningCommonsMenuItemRepository, times(1)).delete(any());
                assertEquals("{\"message\":\"UCSBDiningCommonsMenuItem with id 63 deleted\"}", response.getResponse().getContentAsString());
        }
        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void testPutNotFound() throws Exception {
                UCSBDiningCommonsMenuItem mockItem = new UCSBDiningCommonsMenuItem();
                mockItem.setId(63L);
                mockItem.setDiningCommonsCode("foo");
                mockItem.setName("bar");
                mockItem.setStation("baz");
                when(ucsbDiningCommonsMenuItemRepository.findById(63L)).thenReturn(Optional.empty());
                String body = "{\"id\":63,\"diningCommonsCode\":\"foo\",\"name\":\"bar\",\"station\":\"baz\"}";
                mockMvc.perform(put("/api/ucsbdiningcommonsmenuitems?id=63")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("utf-8")
                                .content(body)
                                .with(csrf())).andExpect(status().isNotFound()).andReturn();
                verify(ucsbDiningCommonsMenuItemRepository, times(0)).save(mockItem);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void testDeleteHappyPath() throws Exception {
                UCSBDiningCommonsMenuItem mockItem = new UCSBDiningCommonsMenuItem();
                mockItem.setId(63L);
                mockItem.setDiningCommonsCode("foo");
                mockItem.setName("bar");
                mockItem.setStation("baz");

                UCSBDiningCommonsMenuItem oldMockItem = new UCSBDiningCommonsMenuItem();
                oldMockItem.setId(63L);
                oldMockItem.setDiningCommonsCode("old_foo");
                oldMockItem.setName("old_bar");
                oldMockItem.setStation("old_baz");

                when(ucsbDiningCommonsMenuItemRepository.findById(63L)).thenReturn(Optional.of(oldMockItem));
                when(ucsbDiningCommonsMenuItemRepository.save(mockItem)).thenReturn(mockItem);
                String body = "{\"id\":63,\"diningCommonsCode\":\"foo\",\"name\":\"bar\",\"station\":\"baz\"}";
                MvcResult response = mockMvc.perform(put("/api/ucsbdiningcommonsmenuitems?id=63")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("utf-8")
                                .content(body)
                                .with(csrf())).andExpect(status().isOk()).andReturn();
                verify(ucsbDiningCommonsMenuItemRepository, times(1)).save(mockItem);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(body, responseString);

        }
        
        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void testDeleteNotFound() throws Exception {
                when(ucsbDiningCommonsMenuItemRepository.findById(63L)).thenReturn(Optional.empty());
                mockMvc.perform(delete("/api/ucsbdiningcommonsmenuitems?id=63").with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();
        }
}
