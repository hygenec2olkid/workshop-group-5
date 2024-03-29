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
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

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
        // shopper id not have cart
        if (_cartShopper.isEmpty()) {
            cartItem.setProduct(product);
            cart.setCartItemList(new ArrayList<>());
            cartItem.setCart(cart);
            cartItem.setQuantity(productDetailBody.quantity());
            cart.setShopper(shopper);
            BigDecimal total =
                    product.getPrice().multiply(BigDecimal.valueOf(productDetailBody.quantity()));
            cart.setTotal(total);
            cart.setDiscount(BigDecimal.valueOf(0));

            this.cartRepository.save(cart);
            this.cartItemRepository.save(cartItem);
            return "add product";
        }
        // shopper have cart
        Cart cartShopper = _cartShopper.get();
        Optional<CartItem> _cartItemShopper =
                this.cartItemRepository.findByCartIdAndProductId(
                        cartShopper.getId(), product.getId());
        if (_cartItemShopper.isEmpty()) {
            cartItem.setProduct(product);
            cartItem.setCart(cartShopper);
            cartItem.setQuantity(productDetailBody.quantity());
            this.cartItemRepository.save(cartItem);
            updateTotal(cartShopper);
            return "shopper not have cartitem of this product";
        }
        // shopper have cart should update quantity
        CartItem cartItemShopper = _cartItemShopper.get();
        cartItemShopper.setQuantity(cartItemShopper.getQuantity() + productDetailBody.quantity());
        this.cartItemRepository.save(cartItemShopper);
        updateTotal(cartShopper);
        return "shopper already have in cart";
    }

    private void updateTotal(Cart cart) {
        List<CartItem> cartItemList = this.cartItemRepository.findByCartId(cart.getId());
        BigDecimal total =
                cartItemList.stream()
                        .map(
                                cartItem -> {
                                    Product product =
                                            this.productRepository.findByProductId(
                                                    cartItem.getProduct().getId());
                                    return product.getPrice()
                                            .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
                                })
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotal(total);
        this.cartRepository.save(cart);
    }
}
