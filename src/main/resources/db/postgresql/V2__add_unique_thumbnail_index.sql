CREATE UNIQUE INDEX ux_product_images_thumbnail
    ON product_images (product_id)
    WHERE is_thumbnail = TRUE;
