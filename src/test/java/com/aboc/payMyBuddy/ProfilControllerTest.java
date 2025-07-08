package com.aboc.payMyBuddy;

import com.aboc.payMyBuddy.exception.RequestException;
import com.aboc.payMyBuddy.model.CustomUserDetails;
import com.aboc.payMyBuddy.model.UserDb;
import com.aboc.payMyBuddy.model.dto.request.UpdatedUserDto;
import com.aboc.payMyBuddy.repository.UserRepository;
import com.aboc.payMyBuddy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProfilControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    @BeforeEach
    public void setupAuthenticatedUser() {
        UserDb userDb = new UserDb();
        userDb.setId(1);
        userDb.setUsername("testuser");
        userDb.setEmail("test@example.com");
        userDb.setPassword("password");
        userDb.setRole("USER");

        CustomUserDetails userDetails = new CustomUserDetails(userDb, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userRepository.findById(1)).thenReturn(Optional.of(userDb));
    }

    @Test
    public void givenAuthenticatedUser_whenGetProfil_thenReturnsProfilPage() throws Exception {
        mockMvc.perform(get("/profil"))
                .andExpect(status().isOk())
                .andExpect(view().name("profil"))
                .andExpect(model().attributeExists("UpdatedUserDto"));
    }

    @Test
    public void givenAuthenticatedUser_whenGetProfil_thenReturnProfilViewWithDto() throws Exception {
        mockMvc.perform(get("/profil"))
                .andExpect(status().isOk())
                .andExpect(view().name("profil"))
                .andExpect(model().attributeExists("UpdatedUserDto"));
    }

    @Test
    @WithMockUser
    public void givenValidUserDto_whenPostProfil_thenRedirect() throws Exception {
        UpdatedUserDto dto = new UpdatedUserDto();
        dto.setId(1);
        dto.setEmail("new@email.com");

        when(userService.updateUser(any(UpdatedUserDto.class))).thenReturn(1);

        mockMvc.perform(post("/profil")
                        .param("id", "1")
                        .param("email", "new@email.com")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transfert"));
    }

    @Test
    @WithMockUser
    public void givenInvalidUserDto_whenPostProfil_thenReturnProfilPage() throws Exception {
        mockMvc.perform(post("/profil")
                        .param("email", "") // email vide = invalide
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("profil"))
                .andExpect(model().attributeExists("UpdatedUserDto"));
    }


    @Test
    @WithMockUser
    public void givenServiceThrowsRequestException_whenPostProfil_thenShowError() throws Exception {
        doThrow(new RequestException("error updating user"))
                .when(userService).updateUser(any(UpdatedUserDto.class));

        mockMvc.perform(post("/profil")
                        .param("email", "test@email.com")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("profil"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "error updating user"));
    }
}
