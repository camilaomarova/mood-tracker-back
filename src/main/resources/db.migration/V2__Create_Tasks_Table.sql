CREATE TABLE tasks (
   id SERIAL PRIMARY KEY,
   user_id NUMERIC,
   mood TEXT,
   title TEXT,
   description TEXT,
   priority TEXT
);
