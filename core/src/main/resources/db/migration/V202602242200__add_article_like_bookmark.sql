CREATE TABLE tb_article_like
(
    article_like_id BIGINT auto_increment NOT NULL,
    article_type    VARCHAR(20)           NOT NULL,
    article_id      BIGINT                NOT NULL,
    member_id       BIGINT                NOT NULL,
    created_at      TIMESTAMP             NOT NULL,
    created_by      VARCHAR(50)           NOT NULL,
    updated_at      TIMESTAMP             NOT NULL,
    updated_by      VARCHAR(50)           NOT NULL,
    PRIMARY KEY (article_like_id)
);

CREATE UNIQUE INDEX uk_article_like_member_target
    ON tb_article_like (member_id, article_type, article_id);

CREATE INDEX idx_article_like_target
    ON tb_article_like (article_type, article_id);

CREATE INDEX idx_article_like_member_created
    ON tb_article_like (member_id, created_at);

CREATE TABLE tb_article_bookmark
(
    article_bookmark_id BIGINT auto_increment NOT NULL,
    article_type        VARCHAR(20)           NOT NULL,
    article_id          BIGINT                NOT NULL,
    member_id           BIGINT                NOT NULL,
    created_at          TIMESTAMP             NOT NULL,
    created_by          VARCHAR(50)           NOT NULL,
    updated_at          TIMESTAMP             NOT NULL,
    updated_by          VARCHAR(50)           NOT NULL,
    PRIMARY KEY (article_bookmark_id)
);

CREATE UNIQUE INDEX uk_article_bookmark_member_target
    ON tb_article_bookmark (member_id, article_type, article_id);

CREATE INDEX idx_article_bookmark_target
    ON tb_article_bookmark (article_type, article_id);

CREATE INDEX idx_article_bookmark_member_created
    ON tb_article_bookmark (member_id, created_at);
