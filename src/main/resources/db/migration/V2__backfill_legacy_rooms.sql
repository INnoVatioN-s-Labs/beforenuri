ALTER TABLE rooms ADD COLUMN IF NOT EXISTS code INT NULL;
ALTER TABLE rooms ADD COLUMN IF NOT EXISTS category VARCHAR(60) NULL;

UPDATE rooms
SET code = 2,
    category = '평범함이 좋아'
WHERE title = '자유 대화실' AND (code IS NULL OR code = 0 OR category IS NULL OR category = '');

UPDATE rooms
SET code = 1,
    category = '평범함이 좋아'
WHERE title = '심야 잡담방' AND (code IS NULL OR code = 0 OR category IS NULL OR category = '');

UPDATE rooms
SET category = '평범함이 좋아'
WHERE category IS NULL OR category = '';

UPDATE rooms
SET code = id + 1000
WHERE code IS NULL OR code = 0;

ALTER TABLE rooms MODIFY code INT NOT NULL;
ALTER TABLE rooms MODIFY category VARCHAR(60) NOT NULL;

ALTER TABLE rooms ADD CONSTRAINT uk_rooms_code UNIQUE (code);
