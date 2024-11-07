CREATE TABLE tb_report
(
    report_id         BIGINT auto_increment NOT NULL,
    source_member_id  BIGINT NOT NULL,
    target_member_id  BIGINT NULL,
    community_post_id BIGINT NULL,
    lost_post_id      BIGINT NULL,
    created_at        TIMESTAMP NOT NULL,
    created_by        VARCHAR(50) NOT NULL,
    updated_at        TIMESTAMP NOT NULL,
    updated_by        VARCHAR(50) NOT NULL,
    PRIMARY KEY (report_id)
);
