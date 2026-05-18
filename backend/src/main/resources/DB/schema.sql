CREATE TABLE IF NOT EXISTS product_m (
    product_id INTEGER NOT NULL AUTO_INCREMENT,
    product_name VARCHAR(50) NOT NULL,
    product_quantity INTEGER NOT NULL
        CHECK (product_quantity >= 0)
        CHECK (product_quantity <= 1000),
    product_imageUrl VARCHAR(255),
    product_updateAt TIMESTAMP NOT NULL
        DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    category_id INTEGER NOT NULL,

    PRIMARY KEY (product_id),
    FOREIGN KEY (category_id) REFERENCES category_m(category_id),

    UNIQUE (product_name)
);

CREATE TABLE IF NOT EXISTS category_m (
    category_id INTEGER NOT NULL AUTO_INCREMENT,
    category_name VARCHAR(50) NOT NULL,

    PRIMARY KEY (category_id),
    UNIQUE (category_name)
);