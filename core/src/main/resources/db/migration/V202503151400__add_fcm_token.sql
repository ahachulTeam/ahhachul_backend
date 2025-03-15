CREATE TABLE tb_fcm_token
(
    token_id         BIGINT auto_increment NOT NULL,
    member_id        REFERENCES tb_member(member_id) NOT NULL,
    token            VARCHAR(255) NOT NULL,
    created_at       TIMESTAMP NOT NULL,
    created_by       VARCHAR(50) NOT NULL,
    updated_at       TIMESTAMP NOT NULL,
    updated_by       VARCHAR(50) NOT NULL,
    PRIMARY KEY (token_id)
);