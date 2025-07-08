package com.aboc.payMyBuddy;

import com.aboc.payMyBuddy.controller.RegistrationController;
import com.aboc.payMyBuddy.exception.RequestException;
import com.aboc.payMyBuddy.model.dto.request.CreatedUserDto;
import com.aboc.payMyBuddy.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ControllersTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void showRegistrationFormTest() throws Exception {
        this.mockMvc
                .perform(get("/registration"))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().attributeExists("CreatedUserDto"));
    }

    @Test
    void processRegistrationTest() throws Exception {

        when(userService.createUser(any(CreatedUserDto.class))).thenReturn(1);

        this.mockMvc
                .perform(post("/registration")
                        .param("username", "test")
                        .param("email", "test@test.com")
                        .param("password", "test")
                        .with(csrf()))

                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/login"));
    }

    @Test
    void failRegistrationTest() throws Exception {

        CreatedUserDto userDto = new CreatedUserDto();
        userDto.setUsername("test");
        userDto.setEmail("test@test.com");
        userDto.setPassword("test");

        RegistrationController controller = new RegistrationController(userService);

        when(userService.createUser(any(CreatedUserDto.class)))
                .thenThrow(new RequestException("email already existing"));

        BindingResult bindingResult = new MapBindingResult(new HashMap<>(), "CreatedUserDto");
        Model model = new ExtendedModelMap();

        String viewName = controller.processRegistration(userDto, bindingResult, model);

        assertEquals("registration", viewName);
        assertTrue(model.containsAttribute("error"));
        assertEquals("email already existing", model.getAttribute("error"));

    }
}
