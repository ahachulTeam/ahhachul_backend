CREATE TABLE tb_station_social_meetup
(
    station_social_meetup_id BIGINT NOT NULL auto_increment,
    station_id               BIGINT NOT NULL,
    subway_line_id           BIGINT NOT NULL,
    host_member_id           BIGINT NOT NULL,
    title                    VARCHAR(120) NOT NULL,
    description              VARCHAR(2000) NOT NULL,
    meetup_at                TIMESTAMP NOT NULL,
    max_participants         INT NOT NULL,
    nationality_code         VARCHAR(16) NULL,
    same_nationality_only_yn VARCHAR(1) NOT NULL,
    status                   VARCHAR(20) NOT NULL,
    created_at               TIMESTAMP NOT NULL,
    created_by               VARCHAR(50) NOT NULL,
    updated_at               TIMESTAMP NOT NULL,
    updated_by               VARCHAR(50) NOT NULL,
    PRIMARY KEY (station_social_meetup_id)
);

CREATE TABLE tb_station_social_meetup_participant
(
    station_social_meetup_participant_id BIGINT NOT NULL auto_increment,
    station_social_meetup_id             BIGINT NOT NULL,
    member_id                            BIGINT NOT NULL,
    status                               VARCHAR(20) NOT NULL,
    introduction_message                 VARCHAR(500) NULL,
    nationality_code                     VARCHAR(16) NULL,
    match_open_yn                        VARCHAR(1) NOT NULL,
    created_at                           TIMESTAMP NOT NULL,
    created_by                           VARCHAR(50) NOT NULL,
    updated_at                           TIMESTAMP NOT NULL,
    updated_by                           VARCHAR(50) NOT NULL,
    PRIMARY KEY (station_social_meetup_participant_id),
    UNIQUE KEY uk_station_social_meetup_member (station_social_meetup_id, member_id)
);

CREATE INDEX idx_station_social_meetup_station_status_at
    ON tb_station_social_meetup (station_id, status, meetup_at);

CREATE INDEX idx_station_social_meetup_host
    ON tb_station_social_meetup (host_member_id);

CREATE INDEX idx_station_social_meetup_participant_meetup_status
    ON tb_station_social_meetup_participant (station_social_meetup_id, status);
