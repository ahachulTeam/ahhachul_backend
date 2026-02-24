ALTER TABLE tb_complaint_post
    ADD COLUMN station_id BIGINT NULL;

CREATE INDEX idx_complaint_post_station_id
    ON tb_complaint_post (station_id);

ALTER TABLE tb_lost_post
    ADD COLUMN station_id BIGINT NULL;

CREATE INDEX idx_lost_post_station_id
    ON tb_lost_post (station_id);
