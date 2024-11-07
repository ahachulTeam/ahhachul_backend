CREATE TABLE tb_community_post_like
(
    community_post_like_id BIGINT auto_increment NOT NULL,
    like_yn                VARCHAR(2) NOT NULL,
    member_id              BIGINT NOT NULL,
    community_post_id      BIGINT NOT NULL,
    created_at             TIMESTAMP NOT NULL,
    created_by             VARCHAR(50) NOT NULL,
    updated_at             TIMESTAMP NOT NULL,
    updated_by             VARCHAR(50) NOT NULL,
    PRIMARY KEY (community_post_like_id)
);