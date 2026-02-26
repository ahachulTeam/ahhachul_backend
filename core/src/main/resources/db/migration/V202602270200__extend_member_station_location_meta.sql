ALTER TABLE tb_member_station
    ADD COLUMN location_name VARCHAR(120) NULL AFTER label,
    ADD COLUMN road_address VARCHAR(255) NULL AFTER location_name,
    ADD COLUMN jibun_address VARCHAR(255) NULL AFTER road_address,
    ADD COLUMN latitude DECIMAL(10, 7) NULL AFTER jibun_address,
    ADD COLUMN longitude DECIMAL(10, 7) NULL AFTER latitude,
    ADD COLUMN walking_minutes INT NULL AFTER longitude,
    ADD COLUMN walking_source VARCHAR(20) NULL AFTER walking_minutes,
    ADD COLUMN walking_updated_at TIMESTAMP NULL AFTER walking_source;
