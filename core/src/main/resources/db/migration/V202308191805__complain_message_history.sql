CREATE TABLE tb_complaint_message_history
(
    complaint_message_history_id              BIGINT auto_increment NOT NULL,
    sent_content                              TEXT NOT NULL,
    sent_phone_number                         VARCHAR(13) NOT NULL,
    sent_train_no                             VARCHAR(10) NOT NULL,
    sent_subway_line_id                       BIGINT NOT NULL,
    sent_member_id                            BIGINT NOT NULL,
    created_at                                TIMESTAMP NOT NULL,
    created_by                                VARCHAR(50) NOT NULL,
    updated_at                                TIMESTAMP NOT NULL,
    updated_by                                VARCHAR(50) NOT NULL,
    PRIMARY KEY (complaint_message_history_id)
);