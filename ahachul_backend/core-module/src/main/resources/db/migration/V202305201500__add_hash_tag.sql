CREATE TABLE tb_hash_tag
(
    hash_tag_id BIGINT NOT NULL auto_increment,
    name        VARCHAR(30) NOT NULL,
    created_at  TIMESTAMP NOT NULL,
    created_by  VARCHAR(50) NOT NULL,
    updated_at  TIMESTAMP NOT NULL,
    updated_by  VARCHAR(50) NOT NULL,
    PRIMARY KEY (hash_tag_id)
);

CREATE TABLE tb_community_post_hash_tag
(
    community_post_hash_tag_id BIGINT NOT NULL auto_increment,
    community_post_id          BIGINT NOT NULL,
    hash_tag_id                BIGINT NOT NULL,
    created_at                 TIMESTAMP NOT NULL,
    created_by                 VARCHAR(50) NOT NULL,
    updated_at                 TIMESTAMP NOT NULL,
    updated_by                 VARCHAR(50) NOT NULL,
    PRIMARY KEY (community_post_hash_tag_id)
);