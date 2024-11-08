ALTER TABLE tb_subway_line_station
    MODIFY created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE tb_subway_line_station
    MODIFY updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE tb_subway_line_station
    MODIFY created_by VARCHAR(50) DEFAULT 'SYSTEM';

ALTER TABLE tb_subway_line_station
    MODIFY updated_by VARCHAR(50) DEFAULT 'SYSTEM';

ALTER TABLE tb_member_station
    MODIFY created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE tb_member_station
    MODIFY updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE tb_member_station
    MODIFY created_by VARCHAR(50) DEFAULT 'SYSTEM';

ALTER TABLE tb_member_station
    MODIFY updated_by VARCHAR(50) DEFAULT 'SYSTEM';
