UPDATE cosmere.books SET version=0;
UPDATE cosmere.authors SET version=0;
UPDATE cosmere.publishers SET version=0;

ALTER TABLE cosmere.books MODIFY version INTEGER NOT NULL;
ALTER TABLE cosmere.authors MODIFY version INTEGER NOT NULL;
ALTER TABLE cosmere.publishers MODIFY version INTEGER NOT NULL;
