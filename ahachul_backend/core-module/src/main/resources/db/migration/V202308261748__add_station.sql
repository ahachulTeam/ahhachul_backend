CREATE TABLE tb_station
(
    station_id             BIGINT auto_increment NOT NULL,
    station_name           VARCHAR(100) NOT NULL,
    identity               BIGINT NOT NULL,
    subway_line_id         BIGINT NOT NULL,
    created_at             TIMESTAMP NOT NULL,
    created_by             VARCHAR(50) NOT NULL,
    updated_at             TIMESTAMP NOT NULL,
    updated_by             VARCHAR(50) NOT NULL,
    PRIMARY KEY (station_id)
);

ALTER TABLE tb_subway_line ADD identity BIGINT NOT NULL;
