package com.kampus.kbazaar.cart.bodyReq;

import java.util.Optional;

public record RequestBodyCode(String code, Optional<String> productSkus) {}
