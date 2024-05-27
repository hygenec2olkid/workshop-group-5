CREATE TABLE IF NOT EXISTS shopper (
	id SERIAL PRIMARY KEY,
	username VARCHAR (255) UNIQUE NOT NULL,
	email VARCHAR (255) NOT NULL
);

CREATE TABLE IF NOT EXISTS cart (
	    id SERIAL PRIMARY KEY,
    	user_id INT UNIQUE NOT NULL,
    	discount_id INT,
    	total DECIMAL(10, 2) NOT NULL,
    	discount DECIMAL(10, 2) DEFAULT 0.00,
    	total_discount DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    	final_total DECIMAL(10, 2) NOT NULL,
    	promotion_code VARCHAR(255) NOT NULL DEFAULT '',
    	fee INT NOT NULL DEFAULT 0,
    	CONSTRAINT cart_promotion_fk FOREIGN KEY (discount_id) REFERENCES promotion(promotion_id),
    	CONSTRAINT cart_shopper_fk FOREIGN KEY (user_id) REFERENCES shopper(id)
);

CREATE TABLE IF NOT EXISTS cart_item (
    id SERIAL PRIMARY KEY,
    cart_session INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    sub_total DECIMAL(10, 2) NOT NULL,
    product_sku VARCHAR(255) NOT NULL UNIQUE,
    discount DECIMAL(10, 2) NOT NULL,
    promotion_code VARCHAR(255) NOT NULL ,
    free_product INT NOT NULL DEFAULT 0,
    CONSTRAINT cart_item_cart_fk FOREIGN KEY (cart_session) REFERENCES cart(id),
    CONSTRAINT cart_item_product_fk FOREIGN KEY (product_id) REFERENCES product(id)
);
