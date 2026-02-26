CREATE TABLE tb_member_story
(
    member_story_id BIGINT NOT NULL AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    station_id BIGINT NULL,
    subway_line_id BIGINT NULL,
    image_url VARCHAR(512) NOT NULL,
    caption VARCHAR(280) NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'CREATED',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50) NOT NULL DEFAULT 'SYSTEM',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(50) NOT NULL DEFAULT 'SYSTEM',
    PRIMARY KEY (member_story_id),
    KEY idx_member_story_member_status_created_at (member_id, status, created_at DESC)
);
