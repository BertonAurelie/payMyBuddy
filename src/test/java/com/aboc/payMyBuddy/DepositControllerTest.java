package com.aboc.payMyBuddy;

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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class DepositControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    @WithMockUser
    public void givenUri_whenShowPage_thenReturnDepositPage() throws Exception {
        this.mockMvc
                .perform(get("/deposit"))
                .andExpect(status().isOk())
                .andExpect(view().name("deposit"))
                .andExpect(model().attributeExists("UpdatedUserDto"));
    }

    @Test
    @WithMockUser
    public void givenDepositPage_whenAddMoney_thenReturnSuccessfullyTransfertPage() throws Exception {
        when(userService.moneyDeposit(any(UpdatedUserDto.class), eq(0))).thenReturn(1);

        this.mockMvc
                .perform(post("/deposit")
                        .param("solde", "5")
                        .param("action", "DEPOSIT")
                        .with(csrf()))

                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/transfert"));
    }

}
