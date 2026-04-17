CREATE TABLE stocks
(
    product_id         VARCHAR(255) NOT NULL,
    created_by         VARCHAR(255),
    last_modified_by   VARCHAR(255),
    created_date       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_date TIMESTAMP WITHOUT TIME ZONE,
    quantity           INTEGER      NOT NULL,
    CONSTRAINT pk_stocks PRIMARY KEY (product_id)
);

INSERT INTO stocks (
    product_id,
    created_by,
    last_modified_by,
    created_date,
    last_modified_date,
    quantity
)
SELECT
    product_id,
    created_by,
    last_modified_by,
    created_date,
    last_modified_date,
    stock_quantity
FROM products;

ALTER TABLE products
    DROP COLUMN stock_quantity;
