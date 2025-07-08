package com.aboc.payMyBuddy;

import com.aboc.payMyBuddy.exception.RequestException;
import com.aboc.payMyBuddy.model.dto.request.UpdatedUserDto;
import com.aboc.payMyBuddy.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RelationControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    @WithMockUser
    public void givenUri_whenShowPage_thenReturnRelationPage() throws Exception {
        this.mockMvc.perform(get("/relationPage"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("UpdatedUserDto"))
                .andExpect(view().name("relationPage"));
    }

    @Test
    @WithMockUser
    public void test() throws Exception {
        doNothing().when(userService).addFriend(any(UpdatedUserDto.class));

        mockMvc.perform(post("/relation")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transfert"));
    }

    @Test
    @WithMockUser
    public void givenException_whenAddFriend_thenReturnRelationPageWithError() throws Exception {
        doThrow(new RequestException("Unable to add this friend"))
                .when(userService).addFriend(any(UpdatedUserDto.class));

        mockMvc.perform(post("/relation").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("relationPage"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "Unable to add this friend"));
    }

    @Test
    @WithMockUser
    public void givenInvalidForm_whenPostRelation_thenReturnRelationPage() throws Exception {
        mockMvc.perform(post("/relation")
                        .param("email", "") //email invalid car vide
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("relationPage"));

        //Puisque if result.hasError alors, on n'appelle jamais la methode du service
        verify(userService, never()).addFriend(any(UpdatedUserDto.class));
    }
}
