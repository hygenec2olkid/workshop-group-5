package com.kampus.kbazaar.cart;

import com.kampus.kbazaar.cart.bodyReq.ProductDetailBody;
import com.kampus.kbazaar.cartItem.CartItem;
import com.kampus.kbazaar.cartItem.CartItemRepository;
import com.kampus.kbazaar.exceptions.NotFoundException;
import com.kampus.kbazaar.product.Product;
import com.kampus.kbazaar.product.ProductRepository;
import com.kampus.kbazaar.shopper.Shopper;
import com.kampus.kbazaar.shopper.ShopperRepository;
import java.math.BigDecimal;
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

    public CartResponse addProductToCart(String userName, ProductDetailBody productDetailBody) {
        Optional<Shopper> _shopper = this.shopperRepository.findByUsername(userName);
        Optional<Product> _product =
                this.productRepository.findById(productDetailBody.productSku());

        if (_shopper.isEmpty()) {
            throw new NotFoundException(userName + " is not member of Kbazaar");
        }
        if (_product.isEmpty()) {
            throw new NotFoundException(productDetailBody.productSku() + " is not have in product");
        }
        Shopper shopper = _shopper.get();
        Product product = _product.get();

        CartItem cartItem = new CartItem();

        Optional<Cart> _cartShopper = this.cartRepository.findByShopper_Id(shopper.getId());
        // Case shopper not have a cart
        if (_cartShopper.isEmpty()) {
            Cart cart = new Cart();
            cart.setShopper(shopper);
            BigDecimal total =
                    product.getPrice().multiply(BigDecimal.valueOf(productDetailBody.quantity()));
            cart.setTotal(total);
            cart.setFinalTotal(total);

            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(productDetailBody.quantity());
            cartItem.setSubTotal(
                    product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            cartItem.setProductSku(product.getSku());
            cartItem.setDiscount(BigDecimal.ZERO);
            cartItem.setPromotionCode("");
            cart.setCartItemList(List.of(cartItem));

            this.cartRepository.save(cart);
            this.cartItemRepository.save(cartItem);
            return cart.toResponse();
        }
        Cart cartShopper = _cartShopper.get();
        Optional<CartItem> _cartItemShopper =
                this.cartItemRepository.findByCartIdAndProductId(
                        cartShopper.getId(), product.getId());
        // Case shopper not have product in cart
        if (_cartItemShopper.isEmpty()) {
            cartItem.setProduct(product);
            cartItem.setCart(cartShopper);
            cartItem.setQuantity(productDetailBody.quantity());
            cartItem.setSubTotal(
                    product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            cartItem.setProductSku(product.getSku());
            cartItem.setDiscount(BigDecimal.ZERO);
            cartItem.setPromotionCode("");
            this.cartItemRepository.save(cartItem);
            updateTotal(cartShopper);
            return cartShopper.toResponse();
        }
        // Case shopper have product in cart will update a cart
        CartItem cartItemShopper = _cartItemShopper.get();
        cartItemShopper.setQuantity(cartItemShopper.getQuantity() + productDetailBody.quantity());
        cartItemShopper.setSubTotal(
                product.getPrice().multiply(BigDecimal.valueOf(cartItemShopper.getQuantity())));
        this.cartItemRepository.save(cartItemShopper);
        updateTotal(cartShopper);
        return cartShopper.toResponse();
    }

    public void updateTotal(Cart cart) {
        List<CartItem> cartItemList = this.cartItemRepository.findByCartId(cart.getId());

        BigDecimal total =
                cartItemList.stream()
                        .map(CartItem::getSubTotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotal(total);
        cart.setFinalTotal(total.subtract(cart.getTotalDiscount()));
        this.cartRepository.save(cart);
    }

    public CartResponse getCartByUsername(String username) {
        Cart cart =
                this.cartRepository
                        .findByShopper_name(username)
                        .orElseThrow(
                                () ->
                                        new NotFoundException(
                                                username + " not have any item in cart"));
        return cart.toResponse();
    }
}
