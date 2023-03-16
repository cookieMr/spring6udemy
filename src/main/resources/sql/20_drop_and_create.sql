DROP TABLE IF EXISTS author_book;
DROP TABLE IF EXISTS authors;
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS publishers;

CREATE TABLE author_book
(
    book_id   VARCHAR(36) NOT NULL,
    author_id VARCHAR(36) NOT NULL,
    PRIMARY KEY (book_id, author_id)
) ENGINE = MyISAM;

CREATE TABLE authors
(
    id         VARCHAR(36) NOT NULL,
    first_name VARCHAR(64) NOT NULL,
    last_name  VARCHAR(64) NOT NULL,
    version    INTEGER,
    PRIMARY KEY (id)
) ENGINE = MyISAM;

CREATE TABLE books
(
    id           VARCHAR(36)  NOT NULL,
    isbn         VARCHAR(14)  NOT NULL,
    title        VARCHAR(128) NOT NULL,
    version      INTEGER,
    publisher_id VARCHAR(36),
    PRIMARY KEY (id)
) ENGINE = MyISAM;

CREATE TABLE publishers
(
    id       VARCHAR(36)  NOT NULL,
    address  VARCHAR(128) NOT NULL,
    city     VARCHAR(64)  NOT NULL,
    name     VARCHAR(128) NOT NULL,
    state    VARCHAR(64)  NOT NULL,
    version  INTEGER,
    zip_code VARCHAR(64)  NOT NULL,
    PRIMARY KEY (id)
) ENGINE = MyISAM;

ALTER TABLE books
    ADD CONSTRAINT UK_kibbepcitr0a3cpk3rfr7nihn UNIQUE (isbn);

ALTER TABLE author_book
    ADD CONSTRAINT FK7cqs8nb7l859jcwwqoawcokqf
        FOREIGN KEY (author_id)
            REFERENCES authors (id);

ALTER TABLE author_book
    ADD CONSTRAINT FKmeehr164a2cpxegeiawuv40a3
        FOREIGN KEY (book_id)
            REFERENCES books (id);

ALTER TABLE books
    ADD CONSTRAINT FKayy5edfrqnegqj3882nce6qo8
        FOREIGN KEY (publisher_id)
            REFERENCES publishers (id);
