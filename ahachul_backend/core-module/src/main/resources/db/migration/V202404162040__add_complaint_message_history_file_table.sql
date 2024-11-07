CREATE TABLE tb_complaint_message_history_file
(
    complaint_message_history_file_id BIGINT auto_increment NOT NULL,
    complaint_message_history_id      BIGINT NOT NULL,
    file_id                           BIGINT NOT NULL,
    created_at                        TIMESTAMP NOT NULL,
    created_by                        VARCHAR(50) NOT NULL,
    updated_at                        TIMESTAMP NOT NULL,
    updated_by                        VARCHAR(50) NOT NULL,
    PRIMARY KEY (complaint_message_history_file_id)
);