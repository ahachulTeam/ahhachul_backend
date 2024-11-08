CREATE TABLE tb_member_station
(
    member_station_id BIGINT NOT NULL auto_increment,
    member_id      BIGINT NOT NULL,
    station_id     BIGINT NOT NULL,
    created_at     TIMESTAMP NOT NULL,
    created_by     VARCHAR(50) NOT NULL,
    updated_at     TIMESTAMP NOT NULL,
    updated_by     VARCHAR(50) NOT NULL,
    PRIMARY KEY (member_station_id)
);
