ALTER TABLE tb_community_comment
    MODIFY COLUMN community_post_id BIGINT NULL;

ALTER TABLE tb_community_comment
    ADD COLUMN visibility VARCHAR(20) NOT NULL AFTER status;

ALTER TABLE tb_community_comment
    ADD COLUMN lost_post_id BIGINT NULL AFTER community_post_id;

RENAME TABLE tb_community_comment TO tb_comment;