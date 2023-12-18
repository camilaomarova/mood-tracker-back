ALTER TABLE tasks
RENAME COLUMN time TO start_time;

ALTER TABLE tasks
ADD COLUMN finish_time VARCHAR(255);
