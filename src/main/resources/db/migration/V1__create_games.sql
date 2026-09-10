CREATE TABLE games (
    id INTEGER PRIMARY KEY,
    title TEXT NOT NULL CHECK (length(trim(title)) > 0),
    added_at INTEGER NOT NULL
);
