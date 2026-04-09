CREATE TABLE cart_items
(
    cart_item_id VARCHAR(255) NOT NULL,
    product_id   VARCHAR(255) NOT NULL,
    quantity     INTEGER      NOT NULL,
    member_id    VARCHAR(255),
    CONSTRAINT pk_cart_items PRIMARY KEY (cart_item_id)
);

CREATE TABLE carts
(
    member_id VARCHAR(255) NOT NULL,
    CONSTRAINT pk_carts PRIMARY KEY (member_id)
);

CREATE TABLE members
(
    member_id          VARCHAR(255) NOT NULL,
    created_by         VARCHAR(255),
    last_modified_by   VARCHAR(255),
    created_date       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_date TIMESTAMP WITHOUT TIME ZONE,
    email              VARCHAR(255) NOT NULL,
    nickname           VARCHAR(255) NOT NULL,
    password           VARCHAR(255) NOT NULL,
    joined_at          TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    withdrawn_at       TIMESTAMP WITHOUT TIME ZONE,
    status             VARCHAR(255) NOT NULL,
    CONSTRAINT pk_members PRIMARY KEY (member_id)
);

CREATE TABLE order_items
(
    order_item_id      VARCHAR(255) NOT NULL,
    created_by         VARCHAR(255),
    last_modified_by   VARCHAR(255),
    created_date       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_date TIMESTAMP WITHOUT TIME ZONE,
    product_id         VARCHAR(255) NOT NULL,
    quantity           INTEGER      NOT NULL,
    price              DECIMAL(19, 0)      NOT NULL,
    order_id           VARCHAR(255),
    CONSTRAINT pk_order_items PRIMARY KEY (order_item_id)
);

CREATE TABLE orders
(
    order_id           VARCHAR(255) NOT NULL,
    created_by         VARCHAR(255),
    last_modified_by   VARCHAR(255),
    created_date       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_date TIMESTAMP WITHOUT TIME ZONE,
    member_id          VARCHAR(255) NOT NULL,
    status             VARCHAR(255) NOT NULL,
    ordered_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    name               VARCHAR(255),
    phone_number       VARCHAR(255),
    zip_code           VARCHAR(255),
    main_address       VARCHAR(255),
    detail_address     VARCHAR(255),
    CONSTRAINT pk_orders PRIMARY KEY (order_id)
);

CREATE TABLE product_images
(
    product_image_id   VARCHAR(255) NOT NULL,
    created_by         VARCHAR(255),
    last_modified_by   VARCHAR(255),
    created_date       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_date TIMESTAMP WITHOUT TIME ZONE,
    image_url          VARCHAR(255) NOT NULL,
    is_thumbnail       BOOLEAN      NOT NULL,
    product_id         VARCHAR(255),
    CONSTRAINT pk_product_images PRIMARY KEY (product_image_id)
);

CREATE TABLE products
(
    product_id         VARCHAR(255)  NOT NULL,
    created_by         VARCHAR(255),
    last_modified_by   VARCHAR(255),
    created_date       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_date TIMESTAMP WITHOUT TIME ZONE,
    seller_id          VARCHAR(255)  NOT NULL,
    name               VARCHAR(255)  NOT NULL,
    description        VARCHAR(2000) NOT NULL,
    price              DECIMAL       NOT NULL,
    stock_quantity     INTEGER       NOT NULL,
    status             VARCHAR(255)  NOT NULL,
    category           VARCHAR(255)  NOT NULL,
    CONSTRAINT pk_products PRIMARY KEY (product_id)
);

CREATE TABLE shipping_addresses
(
    shipping_address_id VARCHAR(255) NOT NULL,
    created_by          VARCHAR(255),
    last_modified_by    VARCHAR(255),
    created_date        TIMESTAMP WITHOUT TIME ZONE,
    last_modified_date  TIMESTAMP WITHOUT TIME ZONE,
    member_id           VARCHAR(255),
    is_default          BOOLEAN      NOT NULL,
    name                VARCHAR(255),
    phone_number        VARCHAR(255),
    zip_code            VARCHAR(255),
    main_address        VARCHAR(255),
    detail_address      VARCHAR(255),
    CONSTRAINT pk_shipping_addresses PRIMARY KEY (shipping_address_id)
);

ALTER TABLE members
    ADD CONSTRAINT uc_members_email UNIQUE (email);

ALTER TABLE members
    ADD CONSTRAINT uc_members_nickname UNIQUE (nickname);

ALTER TABLE cart_items
    ADD CONSTRAINT fk_cart_items_on_member FOREIGN KEY (member_id) REFERENCES carts (member_id);

ALTER TABLE order_items
    ADD CONSTRAINT fk_order_items_on_order FOREIGN KEY (order_id) REFERENCES orders (order_id);

ALTER TABLE product_images
    ADD CONSTRAINT fk_product_images_on_product FOREIGN KEY (product_id) REFERENCES products (product_id);

ALTER TABLE shipping_addresses
    ADD CONSTRAINT fk_shipping_addresses_on_member FOREIGN KEY (member_id) REFERENCES members (member_id);