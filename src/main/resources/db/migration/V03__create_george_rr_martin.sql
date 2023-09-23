INSERT INTO authors (id, first_name, last_name, created_date, update_date)
    VALUES ('f96172bd-01fe-4a33-b515-458c6b6b1872', 'George R.R.', 'Martin', now(), now());

INSERT INTO publishers (id, name, address, state, city, zip_code, created_date, update_date)
    VALUES ('71521505-7286-4a1c-babd-2dea2ad72f99', 'Penguin Random House', '1745 Broadway', 'NY', 'New York', '10019', now(), now());

INSERT INTO books (id, isbn, title, publisher_id, created_date, update_date)
    VALUES ('a652a514-4876-418f-99e5-882933d1ead5', '978-0553593716', 'A Game of Thrones', '71521505-7286-4a1c-babd-2dea2ad72f99', now(), now());
INSERT INTO books (id, isbn, title, publisher_id, created_date, update_date)
    VALUES ('9e4cecf0-69c9-4316-8a94-7919501287e6', '978-0553579901', 'A Clas of Kings', '71521505-7286-4a1c-babd-2dea2ad72f99', now(), now());
INSERT INTO books (id, isbn, title, publisher_id, created_date, update_date)
    VALUES ('d1be5aaf-19a6-48bc-b6b5-ed3541f5ab0f', '978-0345529053', 'A Storm of Swords', '71521505-7286-4a1c-babd-2dea2ad72f99', now(), now());
INSERT INTO books (id, isbn, title, publisher_id, created_date, update_date)
    VALUES ('707bfc00-5b08-43da-a4aa-f1b12eb873c2', '978-0553582024', 'A Feast for Crows', '71521505-7286-4a1c-babd-2dea2ad72f99', now(), now());
INSERT INTO books (id, isbn, title, publisher_id, created_date, update_date)
    VALUES ('73051f3c-9bcc-439d-8efa-21347b585f01', '978-0553801477', 'A Dance with Dragons', '71521505-7286-4a1c-babd-2dea2ad72f99', now(), now());

INSERT INTO author_book (book_id, author_id)
    VALUES ('a652a514-4876-418f-99e5-882933d1ead5', 'f96172bd-01fe-4a33-b515-458c6b6b1872');
INSERT INTO author_book (book_id, author_id)
    VALUES ('9e4cecf0-69c9-4316-8a94-7919501287e6', 'f96172bd-01fe-4a33-b515-458c6b6b1872');
INSERT INTO author_book (book_id, author_id)
    VALUES ('d1be5aaf-19a6-48bc-b6b5-ed3541f5ab0f', 'f96172bd-01fe-4a33-b515-458c6b6b1872');
INSERT INTO author_book (book_id, author_id)
    VALUES ('707bfc00-5b08-43da-a4aa-f1b12eb873c2', 'f96172bd-01fe-4a33-b515-458c6b6b1872');
INSERT INTO author_book (book_id, author_id)
    VALUES ('73051f3c-9bcc-439d-8efa-21347b585f01', 'f96172bd-01fe-4a33-b515-458c6b6b1872');
