ALTER TABLE tb_daily_vote_poll
    ADD COLUMN member_id BIGINT NULL AFTER primary_yn;

ALTER TABLE tb_daily_vote_poll
    DROP INDEX uk_daily_vote_poll_unique;

CREATE INDEX idx_daily_vote_poll_lookup
    ON tb_daily_vote_poll (poll_date, poll_slot, poll_context, poll_kind, station_id, subway_line_id, primary_yn, status);

CREATE INDEX idx_daily_vote_poll_station_board
    ON tb_daily_vote_poll (poll_kind, station_id, subway_line_id, status, created_at);

CREATE INDEX idx_daily_vote_poll_member
    ON tb_daily_vote_poll (member_id);
