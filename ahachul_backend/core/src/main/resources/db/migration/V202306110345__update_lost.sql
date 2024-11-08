ALTER TABLE tb_lost_post
    MODIFY member_id BIGINT NULL;

ALTER TABLE tb_lost_post
    ADD page_url VARCHAR(100) NULL;

ALTER TABLE tb_lost_post
    ADD received_date TIMESTAMP NULL;

ALTER TABLE tb_lost_post
    MODIFY storage_number VARCHAR (50) NULL;