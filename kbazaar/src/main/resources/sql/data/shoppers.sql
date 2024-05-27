INSERT INTO shopper (username, email) VALUES ('TechNinja', 'techninja@example.com') ON CONFLICT DO NOTHING;
INSERT INTO shopper (username, email) VALUES ('CodeMaster', 'codemaster@example.com') ON CONFLICT DO NOTHING;
INSERT INTO shopper (username, email) VALUES ('DataGuru', 'dataguru@example.com') ON CONFLICT DO NOTHING;
INSERT INTO shopper (username, email) VALUES ('CyberSavvy', 'cybersavvy@example.com') ON CONFLICT DO NOTHING;
INSERT INTO shopper (username, email) VALUES ('GeekChic', 'geekchic@example.com') ON CONFLICT DO NOTHING;

--INSERT INTO cart (user_id, discount_id, total,final_total) VALUES(3,NULL,21010.5,21010.5) ON CONFLICT DO NOTHING;
--
--INSERT INTO cart_item(cart_session ,product_id ,quantity ,sub_total ,product_sku ,discount ,promotion_code )
--VALUES (1,1,1,20990.25,'MOBILE-APPLE-IPHONE-12-PRO',0,'') ON CONFLICT DO NOTHING;
--
--INSERT INTO cart_item(cart_session ,product_id ,quantity ,sub_total ,product_sku ,discount ,promotion_code )
--VALUES (1,28,1,20.25,'STATIONERY-STAPLER-SWINGLINE',0,'') ON CONFLICT DO NOTHING;
