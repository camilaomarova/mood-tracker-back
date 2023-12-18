CREATE TABLE moods (
   id SERIAL PRIMARY KEY,
   user_id NUMERIC,
   mood_type TEXT,
   description TEXT
);
