CREATE TABLE IF NOT EXISTS category_m (
    category_id INTEGER NOT NULL AUTO_INCREMENT,
    category_name VARCHAR(50) NOT NULL,

    PRIMARY KEY (category_id),
    UNIQUE (category_name)
);

CREATE TABLE IF NOT EXISTS product_m (
    product_id INTEGER NOT NULL AUTO_INCREMENT,
    product_name VARCHAR(50) NOT NULL,
    product_quantity INTEGER NOT NULL
        CHECK (product_quantity >= 0)
        CHECK (product_quantity <= 1000),
    product_image_url VARCHAR(255),
    product_updated_at TIMESTAMP NOT NULL
        DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,
    category_id INTEGER NOT NULL,

    PRIMARY KEY (product_id),
    FOREIGN KEY (category_id) REFERENCES category_m(category_id),

    UNIQUE (product_name)
);

CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    is_admin BOOL NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE (name),
    UNIQUE (email)
);
