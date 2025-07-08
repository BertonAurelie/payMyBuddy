package com.aboc.payMyBuddy;

import com.aboc.payMyBuddy.exception.RequestException;
import com.aboc.payMyBuddy.model.dto.request.TransactionDto;
import com.aboc.payMyBuddy.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TransfertControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    @WithMockUser
    public void givenUri_whenShowPage_thenReturnTransfertPage() throws Exception {
        mockMvc.perform(get("/transfert"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("TransactionDto"))
                .andExpect(model().attributeExists("options"))
                .andExpect(model().attributeExists("transaction"))
                .andExpect(view().name("transfert"));
    }

    @Test
    @WithMockUser
    public void givenTransfertPage_whenSendMoney_thenReturnSuccessfullyTransfertPage() throws Exception {
        when(userService.showListFriends()).thenReturn(List.of());
        when(userService.showTransaction()).thenReturn(List.of());

        mockMvc.perform(post("/transfert").with(csrf())
                        .param("receiver", "receiver")
                        .param("description", "receiver")
                        .param("amount", "10.00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transfert"));

    }

    @Test
    @WithMockUser
    public void givenException_whenSendMoney_thenReturnTransfertPageWithError() throws Exception {
        doThrow(new RequestException("error message")).when(userService).sendMoney(any(TransactionDto.class));

        when(userService.showListFriends()).thenReturn(List.of());
        when(userService.showTransaction()).thenReturn(List.of());

        mockMvc.perform(post("/transfert").with(csrf())
                        .param("receiver", "receiver")
                        .param("description", "receiver")
                        .param("amount", "10.00"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("transaction"))
                .andExpect(model().attributeExists("options"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "error message"))
                .andExpect(view().name("transfert"));
    }

    @Test
    @WithMockUser
    public void givenResultError_whenSendMoney_thenReturnTransfertPageWithError() throws Exception {


        mockMvc.perform(post("/transfert").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("transaction"))
                .andExpect(model().attributeExists("options"))
                .andExpect(view().name("transfert"));
    }
}
