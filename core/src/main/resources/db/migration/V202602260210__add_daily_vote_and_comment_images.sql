ALTER TABLE tb_comment
    ADD COLUMN image_urls TEXT NULL AFTER content;

CREATE TABLE tb_daily_vote_poll
(
    daily_vote_poll_id BIGINT auto_increment NOT NULL,
    poll_date          DATE NOT NULL,
    poll_slot          VARCHAR(20) NOT NULL,
    poll_context       VARCHAR(20) NOT NULL,
    poll_kind          VARCHAR(20) NOT NULL,
    status             VARCHAR(20) NOT NULL,
    station_id         BIGINT NOT NULL,
    subway_line_id     BIGINT NOT NULL,
    question           VARCHAR(255) NOT NULL,
    primary_yn         VARCHAR(1) NOT NULL,
    created_at         TIMESTAMP NOT NULL,
    created_by         VARCHAR(50) NOT NULL,
    updated_at         TIMESTAMP NOT NULL,
    updated_by         VARCHAR(50) NOT NULL,
    PRIMARY KEY (daily_vote_poll_id),
    UNIQUE KEY uk_daily_vote_poll_unique (
        poll_date,
        poll_slot,
        poll_context,
        poll_kind,
        station_id,
        subway_line_id,
        primary_yn
    )
);

CREATE TABLE tb_daily_vote_response
(
    daily_vote_response_id BIGINT auto_increment NOT NULL,
    daily_vote_poll_id     BIGINT NOT NULL,
    member_id              BIGINT NOT NULL,
    option_code            VARCHAR(30) NOT NULL,
    created_at             TIMESTAMP NOT NULL,
    created_by             VARCHAR(50) NOT NULL,
    updated_at             TIMESTAMP NOT NULL,
    updated_by             VARCHAR(50) NOT NULL,
    PRIMARY KEY (daily_vote_response_id),
    UNIQUE KEY uk_daily_vote_response_unique (daily_vote_poll_id, member_id)
);

CREATE TABLE tb_daily_vote_comment
(
    daily_vote_comment_id BIGINT auto_increment NOT NULL,
    daily_vote_poll_id    BIGINT NOT NULL,
    member_id             BIGINT NOT NULL,
    content               TEXT NOT NULL,
    image_urls            TEXT NULL,
    status                VARCHAR(20) NOT NULL,
    created_at            TIMESTAMP NOT NULL,
    created_by            VARCHAR(50) NOT NULL,
    updated_at            TIMESTAMP NOT NULL,
    updated_by            VARCHAR(50) NOT NULL,
    PRIMARY KEY (daily_vote_comment_id)
);

CREATE TABLE tb_daily_vote_comment_like
(
    daily_vote_comment_like_id BIGINT auto_increment NOT NULL,
    daily_vote_comment_id      BIGINT NOT NULL,
    member_id                  BIGINT NOT NULL,
    created_at                 TIMESTAMP NOT NULL,
    created_by                 VARCHAR(50) NOT NULL,
    updated_at                 TIMESTAMP NOT NULL,
    updated_by                 VARCHAR(50) NOT NULL,
    PRIMARY KEY (daily_vote_comment_like_id),
    UNIQUE KEY uk_daily_vote_comment_like_unique (daily_vote_comment_id, member_id)
);

CREATE INDEX idx_daily_vote_poll_station_line ON tb_daily_vote_poll (station_id, subway_line_id, poll_date);
CREATE INDEX idx_daily_vote_comment_poll ON tb_daily_vote_comment (daily_vote_poll_id, status);
CREATE INDEX idx_daily_vote_comment_like_comment ON tb_daily_vote_comment_like (daily_vote_comment_id);
