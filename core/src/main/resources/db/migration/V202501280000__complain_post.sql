DROP TABLE IF EXISTS tb_complaint_message_history_file;
DROP TABLE IF EXISTS tb_complaint_message_history;

CREATE TABLE tb_complaint_post
(
    complaint_post_id  BIGINT auto_increment NOT NULL,
    complaint_type     VARCHAR(20)           NOT NULL,
    short_content_type VARCHAR(20)           NOT NULL,
    content            TEXT                  NOT NULL,
    phone_number       VARCHAR(13)           NULL,
    train_no           VARCHAR(10)           NULL,
    location           INT,
    status             VARCHAR(20)           NOT NULL,
    subway_line_id     BIGINT                NOT NULL,
    member_id          BIGINT,
    created_at         TIMESTAMP             NOT NULL,
    created_by         VARCHAR(50)           NOT NULL,
    updated_at         TIMESTAMP             NOT NULL,
    updated_by         VARCHAR(50)           NOT NULL,
    PRIMARY KEY (complaint_post_id)
);

CREATE TABLE tb_complaint_post_file
(
    complaint_post_file_id BIGINT auto_increment NOT NULL,
    complaint_post_id      BIGINT                NOT NULL,
    file_id                BIGINT                NOT NULL,
    created_at             TIMESTAMP             NOT NULL,
    created_by             VARCHAR(50)           NOT NULL,
    updated_at             TIMESTAMP             NOT NULL,
    updated_by             VARCHAR(50)           NOT NULL,
    PRIMARY KEY (complaint_post_file_id)
);