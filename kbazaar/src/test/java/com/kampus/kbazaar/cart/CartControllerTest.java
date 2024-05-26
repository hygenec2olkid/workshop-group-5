package com.kampus.kbazaar.cart;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kampus.kbazaar.cart.bodyReq.ProductDetailBody;
import com.kampus.kbazaar.promotion.PromotionService;
import com.kampus.kbazaar.security.JwtAuthFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(
        controllers = CartController.class,
        excludeFilters =
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = JwtAuthFilter.class))
public class CartControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private CartService cartService;

    @MockBean private PromotionService promotionService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getCart_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/v1/carts").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("should return status 201 after add product in cart")
    public void shouldReturnOkStatusAfterAddProduct() throws Exception {
        String mockProductSku = "mockUsername";
        ProductDetailBody mockProductDetailBody = new ProductDetailBody(1L, 2);
        String requestBody =
                """
                {
                      "productSku": 1,
                      "quantity": 2
                  }""";
        mockMvc.perform(
                        post("/api/v1/carts/" + mockProductSku + "/items")
                                .content(requestBody)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("should return status 400 if product detail is invalid")
    public void shouldReturnBadRequestStatus() throws Exception {
        String requestBody =
                """
                {
                      "productSku": 1,
                  }""";
        mockMvc.perform(
                        post("/api/v1/carts/mockUsername/items")
                                .content(requestBody)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("should return status 200 after call get cart of shopper")
    public void shouldReturn200AfterGetCartOfShopper() throws Exception {
        String mockUsername = "TEST";

        mockMvc.perform(
                        get("/api/v1/carts/" + mockUsername)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("should return status 200 after use promotion code specific")
    public void shouldReturn200AfterUsePromotionCodeSpecific() throws Exception {
        String username = "TEST";
        String requestBody =
                """
                {
                        "code": "FIXEDAMOUNT2",
                        "productSkus": "MOBILE-APPLE-IPHONE-12-PRO"
                  }""";

        mockMvc.perform(
                        post("/api/v1/carts/" + username + "/promotions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("should return status 200 after use promotion code")
    public void shouldReturn200AfterUsePromotionCode() throws Exception {
        String username = "TEST";
        String requestBody =
                """
                {
                        "code": "FIXEDAMOUNT2"
                  }""";

        mockMvc.perform(
                        post("/api/v1/carts/" + username + "/promotions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("should return status 200 after delete cartItem")
    public void shouldReturn200AfterDeleteCartItem() throws Exception {
        String username = "DataGuru";
        String productSku = "MOBILE-APPLE-IPHONE-12-PRO";

        mockMvc.perform(
                        delete("/api/v1/carts/" + username + "/items/" + productSku)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
