UPDATE books SET version=0;
UPDATE authors SET version=0;
UPDATE publishers SET version=0;

ALTER TABLE books ${alter-column} COLUMN version INTEGER NOT NULL;
ALTER TABLE authors ${alter-column} COLUMN version INTEGER NOT NULL;
ALTER TABLE publishers ${alter-column} COLUMN version INTEGER NOT NULL;
