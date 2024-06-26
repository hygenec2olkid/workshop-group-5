package com.kampus.kbazaar.shopper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.kampus.kbazaar.cart.Cart;
import com.kampus.kbazaar.exceptions.NotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ShopperServiceTest {

    @Mock private ShopperRepository shopperRepository;

    @InjectMocks private ShopperService shopperService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("should be able to get all shoppers")
    void getAll_ShouldReturnListOfShoppers() {
        // Mock data
        Cart cart1 = new Cart();
        Cart cart2 = new Cart();

        Shopper shopper1 = new Shopper(1L, "TechNinja", "techninja@example.com", cart1);
        Shopper shopper2 = new Shopper(2L, "CodeMaster", "codemaster@example.com", cart2);
        List<Shopper> shoppers = Arrays.asList(shopper1, shopper2);

        // Mock repository method
        when(shopperRepository.findAll()).thenReturn(shoppers);

        // Call service method
        List<ShopperResponse> result = shopperService.getAll();

        // Assertions
        assertEquals(2, result.size());
        assertEquals("TechNinja", result.get(0).username());
        assertEquals("codemaster@example.com", result.get(1).email());
    }

    @Test
    @DisplayName("should be able to get shopper by id")
    void getShopperById_ShouldReturnShopper() {
        // Mock data
        Cart cart1 = new Cart();
        Shopper shopper = new Shopper(1L, "DataGuru", "dataguru@example.com", cart1);

        // Mock repository method
        when(shopperRepository.findById(1)).thenReturn(Optional.of(shopper));

        // Call service method
        ShopperResponse result = shopperService.getById("1");

        // Assertions
        assertEquals("DataGuru", result.username());
    }

    @Test
    @DisplayName("should throw NotFoundException when shopper not found")
    void getShopperById_ShouldThrowNotFoundException() {
        // Mock repository method returning empty optional
        when(shopperRepository.findById(1)).thenReturn(Optional.empty());

        // Assertions
        assertThrows(NotFoundException.class, () -> shopperService.getById("1"));
    }

    @Test
    @DisplayName("should be able to get shopper by username")
    void getShopperByUsername_ShouldReturnShopper() {
        // Mock data
        Cart cart1 = new Cart();
        Shopper shopper = new Shopper(1L, "DataGuru", "dataguru@example.com", cart1);

        // Mock repository method
        when(shopperRepository.findByUsername("DataGuru")).thenReturn(Optional.of(shopper));

        // Call service method
        ShopperResponse result = shopperService.getByUsername("DataGuru");

        // Assertions
        assertEquals("DataGuru", result.username());
    }

    @Test
    @DisplayName("should throw NotFoundException when shopper not found")
    void getByUsername_ShouldThrowNotFoundException() {
        // Mock repository method returning empty optional
        when(shopperRepository.findByUsername("DataGuru")).thenReturn(Optional.empty());

        // Assertions
        assertThrows(NotFoundException.class, () -> shopperService.getByUsername("DataGuru"));
    }

    @Test
    @DisplayName("should return shopper")
    void shouldReturnShopper() {
        Shopper shopper = new Shopper();
        shopper.setId(1L);

        when(shopperRepository.findByUsername("test")).thenReturn(Optional.of(shopper));

        Shopper actual = shopperService.getByUsernames("test");

        assertEquals(1l, actual.getId());
    }

    @Test
    @DisplayName("should throw not found shopper")
    void shouldThrowNotFoundShopper() {

        when(shopperRepository.findByUsername("test")).thenReturn(Optional.empty());

        Exception actual =
                assertThrows(NotFoundException.class, () -> shopperService.getByUsernames("test"));

        assertEquals("Shopper not found", actual.getMessage());
    }
}
