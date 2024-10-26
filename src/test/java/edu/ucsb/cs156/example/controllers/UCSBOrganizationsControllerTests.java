package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.UCSBOrganizations;
import edu.ucsb.cs156.example.repositories.UCSBOrganizationsRepository;

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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = UCSBOrganizationsController.class)
@Import(TestConfig.class)

public class UCSBOrganizationsControllerTests extends ControllerTestCase {

        @MockBean
        UCSBOrganizationsRepository ucsbOrganizationsRepository;

        @MockBean
        UserRepository userRepository;

        @Test
        public void logged_out_users_cannot_get_all() throws Exception {
                mockMvc.perform(get("/api/ucsborganizations/all"))
                                .andExpect(status().is(403)); // logged out users can't get all
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_users_can_get_all() throws Exception {
                mockMvc.perform(get("/api/ucsborganizations/all"))
                                .andExpect(status().is(200)); // logged
        }

        // Authorization tests for /api/ucsborganizations/post
        // (Perhaps should also have these for put and delete)

        @Test
        public void logged_out_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/ucsborganizations/post"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_regular_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/ucsborganizations/post"))
                                .andExpect(status().is(403)); // only admins can post
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void an_admin_user_can_post_a_new_organizations() throws Exception {
                // arrange

                UCSBOrganizations zpr = UCSBOrganizations.builder()
                                .orgCode("ZPR")
                                .orgTranslationShort("ZETA PHI RHO")
                                .orgTranslation("ZETA PHI RHO")
                                .inactive(false)
                                .build();

                when(ucsbOrganizationsRepository.save(eq(zpr))).thenReturn(zpr);

                // act
                MvcResult response = mockMvc.perform(
                                post("/api/ucsborganizations/post?orgCode=ZPR&orgTranslationShort=ZETA PHI RHO&orgTranslation=ZETA PHI RHO&inactive=false")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(ucsbOrganizationsRepository, times(1)).save(zpr);
                String expectedJson = mapper.writeValueAsString(zpr);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void an_admin_user_can_post_a_new_organization_with_inactive_true() throws Exception {
                // Arrange
                UCSBOrganizations org = UCSBOrganizations.builder()
                                .orgCode("XYZ")
                                .orgTranslationShort("XYZ SHORT")
                                .orgTranslation("XYZ Translation")
                                .inactive(true)
                                .build();

                when(ucsbOrganizationsRepository.save(eq(org))).thenReturn(org);

                // Act
                MvcResult response = mockMvc.perform(
                                post("/api/ucsborganizations/post")
                                                .param("orgCode", "XYZ")
                                                .param("orgTranslationShort", "XYZ SHORT")
                                                .param("orgTranslation", "XYZ Translation")
                                                .param("inactive", "true")
                                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andReturn();

                // Assert
                verify(ucsbOrganizationsRepository, times(1)).save(org);
                String expectedJson = mapper.writeValueAsString(org);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_can_get_all_ucsborganizations() throws Exception {

                // arrange

                UCSBOrganizations zpr = UCSBOrganizations.builder()
                                .orgCode("ZPR")
                                .orgTranslationShort("ZPR")
                                .orgTranslation("ZPR")
                                .inactive(false)
                                .build();

                UCSBOrganizations dlg = UCSBOrganizations.builder()
                                .orgCode("DLG")
                                .orgTranslationShort("de-la-guerra")
                                .orgTranslation("de-la-guerra")
                                .inactive(true)
                                .build();

                ArrayList<UCSBOrganizations> expectedOrganizations = new ArrayList<>();
                expectedOrganizations.addAll(Arrays.asList(zpr, dlg));

                when(ucsbOrganizationsRepository.findAll()).thenReturn(expectedOrganizations);

                // act
                MvcResult response = mockMvc.perform(get("/api/ucsborganizations/all"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(ucsbOrganizationsRepository, times(1)).findAll();
                String expectedJson = mapper.writeValueAsString(expectedOrganizations);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_can_get_organization_by_id_when_it_exists() throws Exception {
                // Arrange
                String orgCode = "TEST_ORG";
                UCSBOrganizations org = UCSBOrganizations.builder()
                                .orgCode(orgCode)
                                .orgTranslation("Test Organization")
                                .build();

                when(ucsbOrganizationsRepository.findById(orgCode)).thenReturn(Optional.of(org));

                // Act
                MvcResult response = mockMvc.perform(get("/api/ucsborganizations")
                                .param("orgCode", orgCode))
                                .andExpect(status().isOk())
                                .andReturn();

                // Assert
                verify(ucsbOrganizationsRepository, times(1)).findById(orgCode);
                String expectedJson = mapper.writeValueAsString(org);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_gets_404_when_organization_does_not_exist() throws Exception {
                // Arrange
                String orgCode = "NON_EXISTENT_ORG";

                when(ucsbOrganizationsRepository.findById(orgCode)).thenReturn(Optional.empty());

                // Act
                MvcResult response = mockMvc.perform(get("/api/ucsborganizations")
                                .param("orgCode", orgCode))
                                .andExpect(status().isNotFound()) // Expect 404
                                .andReturn();

                // Assert
                verify(ucsbOrganizationsRepository, times(1)).findById(orgCode);
                Map<String, Object> jsonResponse = responseToJson(response);
                assertEquals("UCSBOrganizations with id NON_EXISTENT_ORG not found", jsonResponse.get("message"));
        }

        // PUT endpoint tests

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_edit_an_existing_organization_true() throws Exception {
                // arrange

                UCSBOrganizations zpr = UCSBOrganizations.builder()
                                .orgCode("ZPR")
                                .orgTranslationShort("ZPR")
                                .orgTranslation("ZPR")
                                .inactive(false)
                                .build();

                UCSBOrganizations zprEdited = UCSBOrganizations.builder()
                                .orgCode("ZPR")
                                .orgTranslationShort("zetas")
                                .orgTranslation("zetas")
                                .inactive(true)
                                .build();

                String requestBody = mapper.writeValueAsString(zprEdited);

                when(ucsbOrganizationsRepository.findById(eq("ZPR"))).thenReturn(Optional.of(zpr));

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/ucsborganizations?orgCode=ZPR")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(ucsbOrganizationsRepository, times(1)).findById("ZPR");
                verify(ucsbOrganizationsRepository, times(1)).save(zprEdited); // should be saved with updated info
                String responseString = response.getResponse().getContentAsString();
                assertEquals(requestBody, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_edit_an_existing_organization() throws Exception {
                // arrange

                UCSBOrganizations zpr = UCSBOrganizations.builder()
                                .orgCode("ZPR")
                                .orgTranslationShort("ZPR")
                                .orgTranslation("ZPR")
                                .inactive(true)
                                .build();

                UCSBOrganizations zprEdited = UCSBOrganizations.builder()
                                .orgCode("ZPR")
                                .orgTranslationShort("zetas")
                                .orgTranslation("zetas")
                                .inactive(false)
                                .build();

                String requestBody = mapper.writeValueAsString(zprEdited);

                when(ucsbOrganizationsRepository.findById(eq("ZPR"))).thenReturn(Optional.of(zpr));

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/ucsborganizations?orgCode=ZPR")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(ucsbOrganizationsRepository, times(1)).findById("ZPR");
                verify(ucsbOrganizationsRepository, times(1)).save(zprEdited); // should be saved with updated info
                String responseString = response.getResponse().getContentAsString();
                assertEquals(requestBody, responseString);

        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_cannot_edit_Organizations_that_does_not_exist() throws Exception {
                // arrange

                UCSBOrganizations editedOrganizations = UCSBOrganizations.builder()
                                .orgCode("ZPR")
                                .orgTranslationShort("ZPR")
                                .orgTranslation("ZPR")
                                .inactive(true)
                                .build();

                String requestBody = mapper.writeValueAsString(editedOrganizations);

                when(ucsbOrganizationsRepository.findById(eq("ZPR"))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/ucsborganizations?orgCode=ZPR")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(ucsbOrganizationsRepository, times(1)).findById("ZPR");
                Map<String, Object> json = responseToJson(response);
                assertEquals("UCSBOrganizations with id ZPR not found", json.get("message"));

        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_delete_a_organization() throws Exception {
                // arrange

                UCSBOrganizations zpr = UCSBOrganizations.builder()
                                .orgCode("ZPR")
                                .orgTranslationShort("ZPR")
                                .orgTranslation("ZPR")
                                .inactive(false)
                                .build();

                when(ucsbOrganizationsRepository.findById(eq("ZPR"))).thenReturn(Optional.of(zpr));

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/ucsborganizations?orgCode=ZPR")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(ucsbOrganizationsRepository, times(1)).findById("ZPR");
                verify(ucsbOrganizationsRepository, times(1)).delete(any());

                Map<String, Object> json = responseToJson(response);
                assertEquals("UCSBOrganizations with id ZPR deleted", json.get("message"));
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_tries_to_delete_non_existant_organization_and_gets_right_error_message()
                        throws Exception {
                // arrange

                when(ucsbOrganizationsRepository.findById(eq("ZPR"))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/ucsborganizations?orgCode=ZPR")
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(ucsbOrganizationsRepository, times(1)).findById("ZPR");
                Map<String, Object> json = responseToJson(response);
                assertEquals("UCSBOrganizations with id ZPR not found", json.get("message"));
        }
}
