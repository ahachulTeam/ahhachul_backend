CREATE TABLE tb_train
(
    train_id               BIGINT auto_increment NOT NULL,
    prefix_train_no        VARCHAR(10) NOT NULL,
    subway_line_id         BIGINT NOT NULL,
    created_at             TIMESTAMP NOT NULL,
    created_by             VARCHAR(50) NOT NULL,
    updated_at             TIMESTAMP NOT NULL,
    updated_by             VARCHAR(50) NOT NULL,
    PRIMARY KEY (train_id)
);