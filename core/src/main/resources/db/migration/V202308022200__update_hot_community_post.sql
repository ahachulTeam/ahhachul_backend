ALTER TABLE tb_community_post
    ADD COLUMN hot_post_yn            CHAR(1)   NOT NULL,
    ADD COLUMN hot_post_selected_date TIMESTAMP NULL;
