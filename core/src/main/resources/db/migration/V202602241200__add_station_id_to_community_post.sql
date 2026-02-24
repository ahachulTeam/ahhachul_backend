ALTER TABLE tb_community_post
    ADD COLUMN station_id BIGINT NULL;

CREATE INDEX idx_community_post_station_id
    ON tb_community_post (station_id);
