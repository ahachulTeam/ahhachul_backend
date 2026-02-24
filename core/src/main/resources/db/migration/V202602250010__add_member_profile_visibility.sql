ALTER TABLE tb_member
    ADD COLUMN profile_public_yn VARCHAR(1) NOT NULL DEFAULT 'Y',
    ADD COLUMN email_public_yn VARCHAR(1) NOT NULL DEFAULT 'N',
    ADD COLUMN gender_age_public_yn VARCHAR(1) NOT NULL DEFAULT 'N',
    ADD COLUMN activity_posts_public_yn VARCHAR(1) NOT NULL DEFAULT 'Y',
    ADD COLUMN activity_comments_public_yn VARCHAR(1) NOT NULL DEFAULT 'Y';
