CREATE TABLE tb_member_station_route
(
    member_station_route_id BIGINT NOT NULL AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    source_station_id BIGINT NOT NULL,
    destination_station_id BIGINT NOT NULL,
    title VARCHAR(50) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) NOT NULL DEFAULT 'SYSTEM',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50) NOT NULL DEFAULT 'SYSTEM',
    PRIMARY KEY (member_station_route_id),
    UNIQUE KEY uk_member_station_route_pair (member_id, source_station_id, destination_station_id)
);
