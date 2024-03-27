package com.kampus.kbazaar.cart;

import com.kampus.kbazaar.cartItem.CartItem;
import com.kampus.kbazaar.cartItem.CartItemRepository;
import com.kampus.kbazaar.exceptions.NotFoundException;
import com.kampus.kbazaar.product.Product;
import com.kampus.kbazaar.product.ProductRepository;
import com.kampus.kbazaar.shopper.Shopper;
import com.kampus.kbazaar.shopper.ShopperRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final ShopperRepository shopperRepository;
    private final CartItemRepository cartItemRepository;

    public CartService(
            CartRepository cartRepository,
            ProductRepository productRepository,
            ShopperRepository shopperRepository,
            CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.shopperRepository = shopperRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Transactional
    public String addProductToCart(String userName, ProductDetailBody productDetailBody) {
        Optional<Shopper> _shopper = this.shopperRepository.findByUsername(userName);
        Optional<Product> _product =
                this.productRepository.findById(productDetailBody.productSku());

        if (_shopper.isEmpty()) {
            throw new NotFoundException(userName + " is not member of Kbazaar");
        }
        if (_product.isEmpty()) {
            throw new NotFoundException(productDetailBody.productSku() + " is not in product");
        }
        Shopper shopper = _shopper.get();
        Product product = _product.get();

        Cart cart = new Cart();
        CartItem cartItem = new CartItem();

        Optional<Cart> _cartShopper = this.cartRepository.findByShopper_Id(shopper.getId());

        if (_cartShopper.isEmpty()) {
            cartItem.setProduct(product);
            cart.setCartItemList(new ArrayList<>());
            cartItem.setCart(cart);
            cartItem.setQuantity(productDetailBody.quantity());
            cart.setShopper(shopper);
            cart.setTotal(BigDecimal.valueOf(100.00));
            cart.setDiscount(BigDecimal.valueOf(0));

            this.cartRepository.save(cart);
            this.cartItemRepository.save(cartItem);
            return "add product";
        }
        return "already have in cart";
    }
}
