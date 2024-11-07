ALTER TABLE tb_complaint_message_history
    MODIFY COLUMN sent_content TEXT NULL;

ALTER TABLE tb_complaint_message_history
    MODIFY COLUMN sent_phone_number VARCHAR(13) NULL;

ALTER TABLE tb_complaint_message_history
    MODIFY COLUMN sent_member_id BIGINT NOT NULL;