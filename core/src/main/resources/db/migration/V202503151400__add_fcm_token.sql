CREATE TABLE tb_fcm_token
(
    token_id    BIGINT AUTO_INCREMENT NOT NULL,
    member_id   BIGINT NOT NULL,
    token       VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP NOT NULL,
    created_by  VARCHAR(50) NOT NULL,
    updated_at  TIMESTAMP NOT NULL,
    updated_by  VARCHAR(50) NOT NULL,
    PRIMARY KEY (token_id)
);
