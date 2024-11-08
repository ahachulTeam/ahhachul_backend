CREATE TABLE tb_community_post
(
    community_post_id BIGINT auto_increment NOT NULL,
    member_id         BIGINT NOT NULL,
    title             VARCHAR(100) NOT NULL,
    content           TEXT NOT NULL,
    status            VARCHAR(20) NOT NULL,
    views             INT NOT NULL,
    category_type     VARCHAR(20) NOT NULL,
    region_type       VARCHAR(20) NOT NULL,
    created_at        TIMESTAMP NOT NULL,
    created_by        VARCHAR(50) NOT NULL,
    updated_at        TIMESTAMP NOT NULL,
    updated_by        VARCHAR(50) NOT NULL,
    PRIMARY KEY (community_post_id)
);

CREATE TABLE tb_community_comment
(
    community_comment_id BIGINT auto_increment NOT NULL,
    content              TEXT NOT NULL,
    status               VARCHAR(20) NOT NULL,
    member_id            BIGINT NOT NULL,
    upper_comment_id     BIGINT NULL,
    community_post_id    BIGINT NOT NULL,
    created_at           TIMESTAMP NOT NULL,
    created_by           VARCHAR(50) NOT NULL,
    updated_at           TIMESTAMP NOT NULL,
    updated_by           VARCHAR(50) NOT NULL,
    PRIMARY KEY (community_comment_id)
);

CREATE TABLE tb_community_post_file
(
    community_post_file_id BIGINT auto_increment NOT NULL,
    community_post_id      BIGINT NOT NULL,
    file_id                BIGINT NOT NULL,
    created_at             TIMESTAMP NOT NULL,
    created_by             VARCHAR(50) NOT NULL,
    updated_at             TIMESTAMP NOT NULL,
    updated_by             VARCHAR(50) NOT NULL,
    PRIMARY KEY (community_post_file_id)
);

CREATE TABLE tb_file
(
    file_id    BIGINT auto_increment NOT NULL,
    file_name  VARCHAR(100) NOT NULL,
    file_path  VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(50) NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    updated_by VARCHAR(50) NOT NULL,
    PRIMARY KEY (file_id)
);

CREATE TABLE tb_member
(
    member_id        BIGINT auto_increment NOT NULL,
    nickname         VARCHAR(100) NOT NULL,
    provider_user_id VARCHAR(255) NOT NULL,
    provider         VARCHAR(10) NOT NULL,
    region_type      VARCHAR(20) NOT NULL,
    email            VARCHAR(100) NULL,
    gender           VARCHAR(10) NULL,
    age_range        VARCHAR(10) NULL,
    status           VARCHAR(10) NULL,
    created_at       TIMESTAMP NOT NULL,
    created_by       VARCHAR(50) NOT NULL,
    updated_at       TIMESTAMP NOT NULL,
    updated_by       VARCHAR(50) NOT NULL,
    PRIMARY KEY (member_id)
);

CREATE TABLE tb_lost_post
(
    lost_post_id   BIGINT NOT NULL auto_increment,
    member_id      BIGINT NOT NULL,
    subway_line_id BIGINT NOT NULL,
    title          VARCHAR(100) NOT NULL,
    content        TEXT NOT NULL,
    status         VARCHAR(10) NOT NULL,
    origin         VARCHAR(10) NOT NULL,
    lost_category  VARCHAR(10) NOT NULL,
    type           VARCHAR(10) NOT NULL,
    lost_type      VARCHAR(10) NOT NULL,
    storage        VARCHAR(10) NOT NULL,
    storage_number VARCHAR(10),
    created_at     TIMESTAMP NOT NULL,
    created_by     VARCHAR(50) NOT NULL,
    updated_at     TIMESTAMP NOT NULL,
    updated_by     VARCHAR(50) NOT NULL,
    PRIMARY KEY (lost_post_id)
);

CREATE TABLE tb_lost_post_file
(
    lost_post_file_id BIGINT NOT NULL auto_increment,
    lost_post_id           BIGINT NOT NULL,
    file_id           BIGINT NOT NULL,
    created_at        TIMESTAMP NOT NULL,
    created_by        VARCHAR(50) NOT NULL,
    updated_at        TIMESTAMP NOT NULL,
    updated_by        VARCHAR(50) NOT NULL,
    PRIMARY KEY (lost_post_file_id)
);

CREATE TABLE tb_category_lost_post
(
    category_lost_post_id BIGINT NOT NULL auto_increment,
    lost_post_id     BIGINT NOT NULL,
    category_id      BIGINT NOT NULL,
    created_at       TIMESTAMP NOT NULL,
    created_by       VARCHAR(50) NOT NULL,
    updated_at       TIMESTAMP NOT NULL,
    updated_by       VARCHAR(50) NOT NULL,
    PRIMARY KEY (category_lost_post_id)
);

CREATE TABLE tb_category
(
    category_id BIGINT NOT NULL auto_increment,
    name        VARCHAR(10) NOT NULL,
    created_at  TIMESTAMP NOT NULL,
    created_by  VARCHAR(50) NOT NULL,
    updated_at  TIMESTAMP NOT NULL,
    updated_by  VARCHAR(50) NOT NULL,
    PRIMARY KEY (category_id)
);

CREATE TABLE tb_subway_line
(
    subway_line_id BIGINT NOT NULL auto_increment,
    name           VARCHAR(100) NOT NULL,
    region_type    VARCHAR(20) NOT NULL,
    created_at     TIMESTAMP NOT NULL,
    created_by     VARCHAR(50) NOT NULL,
    updated_at     TIMESTAMP NOT NULL,
    updated_by     VARCHAR(50) NOT NULL,
    PRIMARY KEY (subway_line_id)
);