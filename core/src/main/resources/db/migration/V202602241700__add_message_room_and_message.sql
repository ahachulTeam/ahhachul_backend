CREATE TABLE tb_message_room
(
    message_room_id      BIGINT NOT NULL auto_increment,
    member_a_id          BIGINT NOT NULL,
    member_b_id          BIGINT NOT NULL,
    last_message_content VARCHAR(500) NULL,
    last_message_at      TIMESTAMP NULL,
    created_at           TIMESTAMP NOT NULL,
    created_by           VARCHAR(50) NOT NULL,
    updated_at           TIMESTAMP NOT NULL,
    updated_by           VARCHAR(50) NOT NULL,
    PRIMARY KEY (message_room_id),
    UNIQUE KEY uq_message_room_members (member_a_id, member_b_id)
);

CREATE TABLE tb_message
(
    message_id        BIGINT NOT NULL auto_increment,
    message_room_id   BIGINT NOT NULL,
    sender_member_id  BIGINT NOT NULL,
    content           VARCHAR(1000) NOT NULL,
    read_yn           VARCHAR(1) NOT NULL,
    created_at        TIMESTAMP NOT NULL,
    created_by        VARCHAR(50) NOT NULL,
    updated_at        TIMESTAMP NOT NULL,
    updated_by        VARCHAR(50) NOT NULL,
    PRIMARY KEY (message_id)
);

CREATE INDEX idx_message_room_member_a
    ON tb_message_room (member_a_id);

CREATE INDEX idx_message_room_member_b
    ON tb_message_room (member_b_id);

CREATE INDEX idx_message_room_last_message_at
    ON tb_message_room (last_message_at);

CREATE INDEX idx_message_room_id_message_id
    ON tb_message (message_room_id, message_id);

CREATE INDEX idx_message_unread
    ON tb_message (message_room_id, read_yn, sender_member_id);
