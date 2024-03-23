CREATE TABLE IF NOT EXISTS cart (
	shopper_id bigserial NOT NULL,
	product_id bigserial NOT NULL,
	quantity int4 NOT NULL,
	product_sku varchar(255) NOT NULL,
	CONSTRAINT cart_pk PRIMARY KEY (product_id, shopper_id)
);
CREATE  INDEX IF NOT EXISTS cart_shopper_id_idx ON cart USING btree (shopper_id);
