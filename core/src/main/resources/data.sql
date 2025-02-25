INSERT INTO tb_member(nickname, provider_user_id, provider, region_type, email, gender, age_range) values('세미', '22012412', 'KAKAO', 'METROPOLITAN', 'ss@naver.com', 'MALE', '20');

INSERT INTO tb_category(name) values('가방');
INSERT INTO tb_category(name) values('귀금속');
INSERT INTO tb_category(name) values('도서용품');
INSERT INTO tb_category(name) values('서류');
INSERT INTO tb_category(name) values('산업용품');
INSERT INTO tb_category(name) values('쇼핑백');
INSERT INTO tb_category(name) values('스포츠용품');
INSERT INTO tb_category(name) values('악기');
INSERT INTO tb_category(name) values('의류');
INSERT INTO tb_category(name) values('자동차');
INSERT INTO tb_category(name) values('전자기기');
INSERT INTO tb_category(name) values('지갑');
INSERT INTO tb_category(name) values('증명서');
INSERT INTO tb_category(name) values('컴퓨터');
INSERT INTO tb_category(name) values('카드');
INSERT INTO tb_category(name) values('현금');
INSERT INTO tb_category(name) values('휴대폰');
INSERT INTO tb_category(name) values('기타물품');

-- # WARNING
-- Don't touch the contents below.

-- ## 지하철 노선

INSERT INTO tb_subway_line(subway_line_id, name, region_type, phone_number, identity) VALUES (1, '1호선', 'METROPOLITAN', '02-000-0000', 1001);
INSERT INTO tb_subway_line(subway_line_id, name, region_type, phone_number, identity) VALUES (2, '2호선', 'METROPOLITAN', '02-000-0000', 1002);
INSERT INTO tb_subway_line(subway_line_id, name, region_type, phone_number, identity) VALUES (3, '3호선', 'METROPOLITAN', '02-000-0000', 1003);
INSERT INTO tb_subway_line(subway_line_id, name, region_type, phone_number, identity) VALUES (4, '4호선', 'METROPOLITAN', '02-000-0000', 1004);
INSERT INTO tb_subway_line(subway_line_id, name, region_type, phone_number, identity) VALUES (5, '5호선', 'METROPOLITAN', '02-000-0000', 1005);
INSERT INTO tb_subway_line(subway_line_id, name, region_type, phone_number, identity) VALUES (6, '6호선', 'METROPOLITAN', '02-000-0000', 1006);
INSERT INTO tb_subway_line(subway_line_id, name, region_type, phone_number, identity) VALUES (7, '7호선', 'METROPOLITAN', '02-000-0000', 1007);
INSERT INTO tb_subway_line(subway_line_id, name, region_type, phone_number, identity) VALUES (8, '8호선', 'METROPOLITAN', '02-000-0000', 1008);
INSERT INTO tb_subway_line(subway_line_id, name, region_type, phone_number, identity) VALUES (9, '9호선', 'METROPOLITAN', '02-000-0000', 1009);
INSERT INTO tb_subway_line(subway_line_id, name, region_type, phone_number, identity) VALUES (10, '경강선', 'METROPOLITAN', '02-000-0000', 1081);
INSERT INTO tb_subway_line(subway_line_id, name, region_type, phone_number, identity) VALUES (11, '경의중앙선', 'METROPOLITAN', '02-000-0000', 1063);
INSERT INTO tb_subway_line(subway_line_id, name, region_type, phone_number, identity) VALUES (12, '경춘선', 'METROPOLITAN', '02-000-0000', 1067);
INSERT INTO tb_subway_line(subway_line_id, name, region_type, phone_number, identity) VALUES (13, '공항철도', 'METROPOLITAN', '02-000-0000', 1065);
INSERT INTO tb_subway_line(subway_line_id, name, region_type, phone_number, identity) VALUES (15, '서해선', 'METROPOLITAN', '02-000-0000', 1093);
INSERT INTO tb_subway_line(subway_line_id, name, region_type, phone_number, identity) VALUES (16, '수인분당선', 'METROPOLITAN', '02-000-0000', 1075);
INSERT INTO tb_subway_line(subway_line_id, name, region_type, phone_number, identity) VALUES (18, '신분당선', 'METROPOLITAN', '02-000-0000', 1077);
INSERT INTO tb_subway_line(subway_line_id, name, region_type, phone_number, identity) VALUES (20, '우이신설경전철', 'METROPOLITAN', '02-000-0000', 1092);

-- ### 제외 데이터
-- INSERT INTO tb_subway_line(subway_line_id, name, region_type, phone_number, identity) VALUES (14, '김포도시철도', 'METROPOLITAN', '02-000-0000');
-- INSERT INTO tb_subway_line(subway_line_id, name, region_type, phone_number, identity) VALUES (17, '신림선', 'METROPOLITAN', '02-000-0000');
-- INSERT INTO tb_subway_line(subway_line_id, name, region_type, phone_number, identity) VALUES (19, '용인경전철', 'METROPOLITAN', '02-000-0000');
-- INSERT INTO tb_subway_line(subway_line_id, name, region_type, phone_number, identity) VALUES (21, '의정부경전철', 'METROPOLITAN', '02-000-0000');
-- INSERT INTO tb_subway_line(subway_line_id, name, region_type, phone_number, identity) VALUES (22, '인천2호선', 'METROPOLITAN', '02-000-0000');
-- INSERT INTO tb_subway_line(subway_line_id, name, region_type, phone_number, identity VALUES (23, '인천선', 'METROPOLITAN', '02-000-0000');


-- ## 지하철 번호

-- # 1호선
INSERT INTO tb_train(prefix_train_no, subway_line_id) values('0', 1);
INSERT INTO tb_train(prefix_train_no, subway_line_id) values('K', 1);
INSERT INTO tb_train(prefix_train_no, subway_line_id) values('S', 1);
INSERT INTO tb_train(prefix_train_no, subway_line_id) values('1', 1);
INSERT INTO tb_train(prefix_train_no, subway_line_id) values('311', 1);
INSERT INTO tb_train(prefix_train_no, subway_line_id) values('312', 1);
INSERT INTO tb_train(prefix_train_no, subway_line_id) values('319', 1);
INSERT INTO tb_train(prefix_train_no, subway_line_id) values('341', 1);

-- # 2 ~ 9호선
INSERT INTO tb_train(prefix_train_no, subway_line_id) values('2', 2);
INSERT INTO tb_train(prefix_train_no, subway_line_id) values('3', 3);
INSERT INTO tb_train(prefix_train_no, subway_line_id) values('4', 4);
INSERT INTO tb_train(prefix_train_no, subway_line_id) values('5', 5);
INSERT INTO tb_train(prefix_train_no, subway_line_id) values('6', 6);
INSERT INTO tb_train(prefix_train_no, subway_line_id) values('7', 7);
INSERT INTO tb_train(prefix_train_no, subway_line_id) values('8', 8);
INSERT INTO tb_train(prefix_train_no, subway_line_id) values('9', 9);

-- # 신분당선
INSERT INTO tb_train(prefix_train_no, subway_line_id) values('D01', 18);
INSERT INTO tb_train(prefix_train_no, subway_line_id) values('D02', 18);
INSERT INTO tb_train(prefix_train_no, subway_line_id) values('D03', 18);
INSERT INTO tb_train(prefix_train_no, subway_line_id) values('D04', 18);
INSERT INTO tb_train(prefix_train_no, subway_line_id) values('D05', 18);
INSERT INTO tb_train(prefix_train_no, subway_line_id) values('D06', 18);
INSERT INTO tb_train(prefix_train_no, subway_line_id) values('D07', 18);
INSERT INTO tb_train(prefix_train_no, subway_line_id) values('D08', 18);
INSERT INTO tb_train(prefix_train_no, subway_line_id) values('D09', 18);
INSERT INTO tb_train(prefix_train_no, subway_line_id) values('D10', 18);
INSERT INTO tb_train(prefix_train_no, subway_line_id) values('D11', 18);
INSERT INTO tb_train(prefix_train_no, subway_line_id) values('D12', 18);
INSERT INTO tb_train(prefix_train_no, subway_line_id) values('D13', 18);
INSERT INTO tb_train(prefix_train_no, subway_line_id) values('D14', 18);
INSERT INTO tb_train(prefix_train_no, subway_line_id) values('D15', 18);
INSERT INTO tb_train(prefix_train_no, subway_line_id) values('D16', 18);
INSERT INTO tb_train(prefix_train_no, subway_line_id) values('D17', 18);
INSERT INTO tb_train(prefix_train_no, subway_line_id) values('D18', 18);
INSERT INTO tb_train(prefix_train_no, subway_line_id) values('D19', 18);
INSERT INTO tb_train(prefix_train_no, subway_line_id) values('D20', 18);
INSERT INTO tb_train(prefix_train_no, subway_line_id) values('D21', 18);
INSERT INTO tb_train(prefix_train_no, subway_line_id) values('D22', 18);
INSERT INTO tb_train(prefix_train_no, subway_line_id) values('D23', 18);

-- # 수인분당선
INSERT INTO tb_train(prefix_train_no, subway_line_id) values('351', 16);

-- # 경의중앙선
INSERT INTO tb_train(prefix_train_no, subway_line_id) values('321', 11);
INSERT INTO tb_train(prefix_train_no, subway_line_id) values('331', 11);

-- # 우이신설선
INSERT INTO tb_train(prefix_train_no, subway_line_id) values('UL', 20);

-- ## 지하철 노선 정류장

INSERT INTO tb_station(station_id, name) VALUES (1, '청명');
INSERT INTO tb_station(station_id, name) VALUES (2, '고색');
INSERT INTO tb_station(station_id, name) VALUES (3, '성복');
INSERT INTO tb_station(station_id, name) VALUES (4, '솔샘');
INSERT INTO tb_station(station_id, name) VALUES (5, '운동장.송담대');
INSERT INTO tb_station(station_id, name) VALUES (6, '삼성중앙');
INSERT INTO tb_station(station_id, name) VALUES (7, '구의');
INSERT INTO tb_station(station_id, name) VALUES (8, '구로');
INSERT INTO tb_station(station_id, name) VALUES (9, '남동구청');
INSERT INTO tb_station(station_id, name) VALUES (10, '굽은다리');
INSERT INTO tb_station(station_id, name) VALUES (11, '임학');
INSERT INTO tb_station(station_id, name) VALUES (12, '팔당');
INSERT INTO tb_station(station_id, name) VALUES (13, '약수');
INSERT INTO tb_station(station_id, name) VALUES (14, '석남');
INSERT INTO tb_station(station_id, name) VALUES (15, '능곡');
INSERT INTO tb_station(station_id, name) VALUES (16, '신림');
INSERT INTO tb_station(station_id, name) VALUES (17, '문래');
INSERT INTO tb_station(station_id, name) VALUES (18, '이태원');
INSERT INTO tb_station(station_id, name) VALUES (19, '인천시청');
INSERT INTO tb_station(station_id, name) VALUES (20, '답십리');
INSERT INTO tb_station(station_id, name) VALUES (21, '화곡');
INSERT INTO tb_station(station_id, name) VALUES (22, '곤제');
INSERT INTO tb_station(station_id, name) VALUES (23, '마두');
INSERT INTO tb_station(station_id, name) VALUES (24, '마전');
INSERT INTO tb_station(station_id, name) VALUES (25, '운서');
INSERT INTO tb_station(station_id, name) VALUES (26, '우장산');
INSERT INTO tb_station(station_id, name) VALUES (27, '역곡');
INSERT INTO tb_station(station_id, name) VALUES (28, '공덕');
INSERT INTO tb_station(station_id, name) VALUES (29, '고촌');
INSERT INTO tb_station(station_id, name) VALUES (30, '서정리');
INSERT INTO tb_station(station_id, name) VALUES (31, '풍무');
INSERT INTO tb_station(station_id, name) VALUES (32, '신풍');
INSERT INTO tb_station(station_id, name) VALUES (33, '까치울');
INSERT INTO tb_station(station_id, name) VALUES (34, '동백');
INSERT INTO tb_station(station_id, name) VALUES (35, '명동');
INSERT INTO tb_station(station_id, name) VALUES (36, '동두천');
INSERT INTO tb_station(station_id, name) VALUES (37, '개화산');
INSERT INTO tb_station(station_id, name) VALUES (38, '학동');
INSERT INTO tb_station(station_id, name) VALUES (39, '평내호평');
INSERT INTO tb_station(station_id, name) VALUES (40, '장암');
INSERT INTO tb_station(station_id, name) VALUES (41, '종로3가');
INSERT INTO tb_station(station_id, name) VALUES (42, '완정');
INSERT INTO tb_station(station_id, name) VALUES (43, '원덕');
INSERT INTO tb_station(station_id, name) VALUES (44, '강촌');
INSERT INTO tb_station(station_id, name) VALUES (45, '퇴계원');
INSERT INTO tb_station(station_id, name) VALUES (46, '공항시장');
INSERT INTO tb_station(station_id, name) VALUES (47, '광나루');
INSERT INTO tb_station(station_id, name) VALUES (48, '사평');
INSERT INTO tb_station(station_id, name) VALUES (49, '신답');
INSERT INTO tb_station(station_id, name) VALUES (50, '화전');
INSERT INTO tb_station(station_id, name) VALUES (51, '송파나루');
INSERT INTO tb_station(station_id, name) VALUES (52, '국제업무지구');
INSERT INTO tb_station(station_id, name) VALUES (53, '이수');
INSERT INTO tb_station(station_id, name) VALUES (54, '4·19민주묘지');
INSERT INTO tb_station(station_id, name) VALUES (55, '상계');
INSERT INTO tb_station(station_id, name) VALUES (56, '흥선');
INSERT INTO tb_station(station_id, name) VALUES (57, '선바위');
INSERT INTO tb_station(station_id, name) VALUES (58, '강매');
INSERT INTO tb_station(station_id, name) VALUES (59, '화계');
INSERT INTO tb_station(station_id, name) VALUES (60, '숭의');
INSERT INTO tb_station(station_id, name) VALUES (61, '종로5가');
INSERT INTO tb_station(station_id, name) VALUES (62, '시민공원');
INSERT INTO tb_station(station_id, name) VALUES (63, '경찰병원');
INSERT INTO tb_station(station_id, name) VALUES (64, '연수');
INSERT INTO tb_station(station_id, name) VALUES (65, '서초');
INSERT INTO tb_station(station_id, name) VALUES (66, '뚝섬');
INSERT INTO tb_station(station_id, name) VALUES (67, '고잔');
INSERT INTO tb_station(station_id, name) VALUES (68, '춘천');
INSERT INTO tb_station(station_id, name) VALUES (69, '인천');
INSERT INTO tb_station(station_id, name) VALUES (70, '신연수');
INSERT INTO tb_station(station_id, name) VALUES (71, '발산');
INSERT INTO tb_station(station_id, name) VALUES (72, '달월');
INSERT INTO tb_station(station_id, name) VALUES (73, '당곡');
INSERT INTO tb_station(station_id, name) VALUES (74, '센트럴파크');
INSERT INTO tb_station(station_id, name) VALUES (75, '회룡');
INSERT INTO tb_station(station_id, name) VALUES (76, '잠실');
INSERT INTO tb_station(station_id, name) VALUES (77, '거여');
INSERT INTO tb_station(station_id, name) VALUES (78, '압구정로데오');
INSERT INTO tb_station(station_id, name) VALUES (79, '원당');
INSERT INTO tb_station(station_id, name) VALUES (80, '보산');
INSERT INTO tb_station(station_id, name) VALUES (81, '매봉');
INSERT INTO tb_station(station_id, name) VALUES (82, '관악');
INSERT INTO tb_station(station_id, name) VALUES (83, '오이도');
INSERT INTO tb_station(station_id, name) VALUES (84, '초당');
INSERT INTO tb_station(station_id, name) VALUES (85, '소요산');
INSERT INTO tb_station(station_id, name) VALUES (86, '시흥시청');
INSERT INTO tb_station(station_id, name) VALUES (87, '봉천');
INSERT INTO tb_station(station_id, name) VALUES (88, '학여울');
INSERT INTO tb_station(station_id, name) VALUES (89, '시청.용인대');
INSERT INTO tb_station(station_id, name) VALUES (90, '미아사거리');
INSERT INTO tb_station(station_id, name) VALUES (91, '춘의');
INSERT INTO tb_station(station_id, name) VALUES (92, '금호');
INSERT INTO tb_station(station_id, name) VALUES (93, '구파발');
INSERT INTO tb_station(station_id, name) VALUES (94, '별내별가람');
INSERT INTO tb_station(station_id, name) VALUES (95, '송탄');
INSERT INTO tb_station(station_id, name) VALUES (96, '온수');
INSERT INTO tb_station(station_id, name) VALUES (97, '홍대입구');
INSERT INTO tb_station(station_id, name) VALUES (98, '모래내시장');
INSERT INTO tb_station(station_id, name) VALUES (99, '수유');
INSERT INTO tb_station(station_id, name) VALUES (100, '사가정');
INSERT INTO tb_station(station_id, name) VALUES (101, '오빈');
INSERT INTO tb_station(station_id, name) VALUES (102, '임진강');
INSERT INTO tb_station(station_id, name) VALUES (103, '일산');
INSERT INTO tb_station(station_id, name) VALUES (104, '사리');
INSERT INTO tb_station(station_id, name) VALUES (105, '북한산우이');
INSERT INTO tb_station(station_id, name) VALUES (106, '산곡');
INSERT INTO tb_station(station_id, name) VALUES (107, '한티');
INSERT INTO tb_station(station_id, name) VALUES (108, '신도림');
INSERT INTO tb_station(station_id, name) VALUES (109, '봉은사');
INSERT INTO tb_station(station_id, name) VALUES (110, '선릉');
INSERT INTO tb_station(station_id, name) VALUES (111, '송파');
INSERT INTO tb_station(station_id, name) VALUES (112, '대성리');
INSERT INTO tb_station(station_id, name) VALUES (113, '노량진');
INSERT INTO tb_station(station_id, name) VALUES (114, '종각');
INSERT INTO tb_station(station_id, name) VALUES (115, '양정');
INSERT INTO tb_station(station_id, name) VALUES (116, '석천사거리');
INSERT INTO tb_station(station_id, name) VALUES (117, '올림픽공원');
INSERT INTO tb_station(station_id, name) VALUES (118, '돌곶이');
INSERT INTO tb_station(station_id, name) VALUES (119, '동작');
INSERT INTO tb_station(station_id, name) VALUES (120, '양원');
INSERT INTO tb_station(station_id, name) VALUES (121, '가천대');
INSERT INTO tb_station(station_id, name) VALUES (122, '서현');
INSERT INTO tb_station(station_id, name) VALUES (123, '백석');
INSERT INTO tb_station(station_id, name) VALUES (124, '충정로');
INSERT INTO tb_station(station_id, name) VALUES (125, '서울숲');
INSERT INTO tb_station(station_id, name) VALUES (126, '인천공항1터미널');
INSERT INTO tb_station(station_id, name) VALUES (127, '교대');
INSERT INTO tb_station(station_id, name) VALUES (128, '송산');
INSERT INTO tb_station(station_id, name) VALUES (129, '달미');
INSERT INTO tb_station(station_id, name) VALUES (130, '오목천');
INSERT INTO tb_station(station_id, name) VALUES (131, '인천대공원');
INSERT INTO tb_station(station_id, name) VALUES (132, '광화문');
INSERT INTO tb_station(station_id, name) VALUES (133, '대흥');
INSERT INTO tb_station(station_id, name) VALUES (134, '광흥창');
INSERT INTO tb_station(station_id, name) VALUES (135, '삼각지');
INSERT INTO tb_station(station_id, name) VALUES (136, '개포동');
INSERT INTO tb_station(station_id, name) VALUES (137, '새말');
INSERT INTO tb_station(station_id, name) VALUES (138, '방배');
INSERT INTO tb_station(station_id, name) VALUES (139, '매탄권선');
INSERT INTO tb_station(station_id, name) VALUES (140, '상록수');
INSERT INTO tb_station(station_id, name) VALUES (141, '지평');
INSERT INTO tb_station(station_id, name) VALUES (142, '삼가');
INSERT INTO tb_station(station_id, name) VALUES (143, '망우');
INSERT INTO tb_station(station_id, name) VALUES (144, '한성대입구');
INSERT INTO tb_station(station_id, name) VALUES (145, '수원시청');
INSERT INTO tb_station(station_id, name) VALUES (146, '시흥대야');
INSERT INTO tb_station(station_id, name) VALUES (147, '신금호');
INSERT INTO tb_station(station_id, name) VALUES (148, '삼동');
INSERT INTO tb_station(station_id, name) VALUES (149, '성신여대입구');
INSERT INTO tb_station(station_id, name) VALUES (150, '정왕');
INSERT INTO tb_station(station_id, name) VALUES (151, '안암');
INSERT INTO tb_station(station_id, name) VALUES (152, '강동구청');
INSERT INTO tb_station(station_id, name) VALUES (153, '노들');
INSERT INTO tb_station(station_id, name) VALUES (154, '장기');
INSERT INTO tb_station(station_id, name) VALUES (155, '야목');
INSERT INTO tb_station(station_id, name) VALUES (156, '영등포');
INSERT INTO tb_station(station_id, name) VALUES (157, '제물포');
INSERT INTO tb_station(station_id, name) VALUES (158, '성균관대');
INSERT INTO tb_station(station_id, name) VALUES (159, '백마');
INSERT INTO tb_station(station_id, name) VALUES (160, '남동인더스파크');
INSERT INTO tb_station(station_id, name) VALUES (161, '정자');
INSERT INTO tb_station(station_id, name) VALUES (162, '서원');
INSERT INTO tb_station(station_id, name) VALUES (163, '흑석');
INSERT INTO tb_station(station_id, name) VALUES (164, '보문');
INSERT INTO tb_station(station_id, name) VALUES (165, '서울지방병무청');
INSERT INTO tb_station(station_id, name) VALUES (166, '버티고개');
INSERT INTO tb_station(station_id, name) VALUES (167, '보정');
INSERT INTO tb_station(station_id, name) VALUES (168, '서빙고');
INSERT INTO tb_station(station_id, name) VALUES (169, '대모산입구');
INSERT INTO tb_station(station_id, name) VALUES (170, '온양온천');
INSERT INTO tb_station(station_id, name) VALUES (171, '망포');
INSERT INTO tb_station(station_id, name) VALUES (172, '수원');
INSERT INTO tb_station(station_id, name) VALUES (173, '청담');
INSERT INTO tb_station(station_id, name) VALUES (174, '경기광주');
INSERT INTO tb_station(station_id, name) VALUES (175, '오류동');
INSERT INTO tb_station(station_id, name) VALUES (176, '상수');
INSERT INTO tb_station(station_id, name) VALUES (177, '동춘');
INSERT INTO tb_station(station_id, name) VALUES (178, '영종');
INSERT INTO tb_station(station_id, name) VALUES (179, '동수');
INSERT INTO tb_station(station_id, name) VALUES (180, '서울대입구');
INSERT INTO tb_station(station_id, name) VALUES (181, '사릉');
INSERT INTO tb_station(station_id, name) VALUES (182, '응암');
INSERT INTO tb_station(station_id, name) VALUES (183, '고덕');
INSERT INTO tb_station(station_id, name) VALUES (184, '청량리');
INSERT INTO tb_station(station_id, name) VALUES (185, '탑석');
INSERT INTO tb_station(station_id, name) VALUES (186, '영등포구청');
INSERT INTO tb_station(station_id, name) VALUES (187, '죽전');
INSERT INTO tb_station(station_id, name) VALUES (188, '마곡');
INSERT INTO tb_station(station_id, name) VALUES (189, '서동탄');
INSERT INTO tb_station(station_id, name) VALUES (190, '철산');
INSERT INTO tb_station(station_id, name) VALUES (191, '양수');
INSERT INTO tb_station(station_id, name) VALUES (192, '기흥');
INSERT INTO tb_station(station_id, name) VALUES (193, '강변');
INSERT INTO tb_station(station_id, name) VALUES (194, '언주');
INSERT INTO tb_station(station_id, name) VALUES (195, '영통');
INSERT INTO tb_station(station_id, name) VALUES (196, '한대앞');
INSERT INTO tb_station(station_id, name) VALUES (197, '곤지암');
INSERT INTO tb_station(station_id, name) VALUES (198, '숙대입구');
INSERT INTO tb_station(station_id, name) VALUES (199, '석촌고분');
INSERT INTO tb_station(station_id, name) VALUES (200, '작전');
INSERT INTO tb_station(station_id, name) VALUES (201, '디지털미디어시티');
INSERT INTO tb_station(station_id, name) VALUES (202, '풍산');
INSERT INTO tb_station(station_id, name) VALUES (203, '마포구청');
INSERT INTO tb_station(station_id, name) VALUES (204, '가오리');
INSERT INTO tb_station(station_id, name) VALUES (205, '신대방');
INSERT INTO tb_station(station_id, name) VALUES (206, '아신');
INSERT INTO tb_station(station_id, name) VALUES (207, '한남');
INSERT INTO tb_station(station_id, name) VALUES (208, '가정');
INSERT INTO tb_station(station_id, name) VALUES (209, '상왕십리');
INSERT INTO tb_station(station_id, name) VALUES (210, '발곡');
INSERT INTO tb_station(station_id, name) VALUES (211, '중앙보훈병원');
INSERT INTO tb_station(station_id, name) VALUES (212, '삼산체육관');
INSERT INTO tb_station(station_id, name) VALUES (213, '양재');
INSERT INTO tb_station(station_id, name) VALUES (214, '석촌');
INSERT INTO tb_station(station_id, name) VALUES (215, '외대앞');
INSERT INTO tb_station(station_id, name) VALUES (216, '천왕');
INSERT INTO tb_station(station_id, name) VALUES (217, '정발산');
INSERT INTO tb_station(station_id, name) VALUES (218, '간석');
INSERT INTO tb_station(station_id, name) VALUES (219, '왕십리');
INSERT INTO tb_station(station_id, name) VALUES (220, '가양');
INSERT INTO tb_station(station_id, name) VALUES (221, '미사');
INSERT INTO tb_station(station_id, name) VALUES (222, '안산');
INSERT INTO tb_station(station_id, name) VALUES (223, '보평');
INSERT INTO tb_station(station_id, name) VALUES (224, '일원');
INSERT INTO tb_station(station_id, name) VALUES (225, '건대입구');
INSERT INTO tb_station(station_id, name) VALUES (226, '낙성대');
INSERT INTO tb_station(station_id, name) VALUES (227, '서부여성회관');
INSERT INTO tb_station(station_id, name) VALUES (228, '어정');
INSERT INTO tb_station(station_id, name) VALUES (229, '호구포');
INSERT INTO tb_station(station_id, name) VALUES (230, '옥수');
INSERT INTO tb_station(station_id, name) VALUES (231, '반포');
INSERT INTO tb_station(station_id, name) VALUES (232, '모란');
INSERT INTO tb_station(station_id, name) VALUES (233, '양평');
INSERT INTO tb_station(station_id, name) VALUES (234, '광교');
INSERT INTO tb_station(station_id, name) VALUES (235, '강남대');
INSERT INTO tb_station(station_id, name) VALUES (236, '신창');
INSERT INTO tb_station(station_id, name) VALUES (237, '노원');
INSERT INTO tb_station(station_id, name) VALUES (238, '신촌');
INSERT INTO tb_station(station_id, name) VALUES (239, '지석');
INSERT INTO tb_station(station_id, name) VALUES (240, '구반포');
INSERT INTO tb_station(station_id, name) VALUES (241, '증산');
INSERT INTO tb_station(station_id, name) VALUES (242, '응봉');
INSERT INTO tb_station(station_id, name) VALUES (243, '태평');
INSERT INTO tb_station(station_id, name) VALUES (244, '대청');
INSERT INTO tb_station(station_id, name) VALUES (245, '배방');
INSERT INTO tb_station(station_id, name) VALUES (246, '영등포시장');
INSERT INTO tb_station(station_id, name) VALUES (247, '신현');
INSERT INTO tb_station(station_id, name) VALUES (248, '길동');
INSERT INTO tb_station(station_id, name) VALUES (249, '동암');
INSERT INTO tb_station(station_id, name) VALUES (250, '선학');
INSERT INTO tb_station(station_id, name) VALUES (251, '몽촌토성');
INSERT INTO tb_station(station_id, name) VALUES (252, '야탑');
INSERT INTO tb_station(station_id, name) VALUES (253, '원흥');
INSERT INTO tb_station(station_id, name) VALUES (254, '간석오거리');
INSERT INTO tb_station(station_id, name) VALUES (255, '검바위');
INSERT INTO tb_station(station_id, name) VALUES (256, '양천구청');
INSERT INTO tb_station(station_id, name) VALUES (257, '굴포천');
INSERT INTO tb_station(station_id, name) VALUES (258, '마석');
INSERT INTO tb_station(station_id, name) VALUES (259, '금천구청');
INSERT INTO tb_station(station_id, name) VALUES (260, '관악산');
INSERT INTO tb_station(station_id, name) VALUES (261, '종합운동장');
INSERT INTO tb_station(station_id, name) VALUES (262, '평촌');
INSERT INTO tb_station(station_id, name) VALUES (263, '한강진');
INSERT INTO tb_station(station_id, name) VALUES (264, '아시아드경기장');
INSERT INTO tb_station(station_id, name) VALUES (265, '동막');
INSERT INTO tb_station(station_id, name) VALUES (266, '신용산');
INSERT INTO tb_station(station_id, name) VALUES (267, '화랑대');
INSERT INTO tb_station(station_id, name) VALUES (268, '세종대왕릉');
INSERT INTO tb_station(station_id, name) VALUES (269, '구로디지털단지');
INSERT INTO tb_station(station_id, name) VALUES (270, '행신');
INSERT INTO tb_station(station_id, name) VALUES (271, '여의나루');
INSERT INTO tb_station(station_id, name) VALUES (272, '광교중앙');
INSERT INTO tb_station(station_id, name) VALUES (273, '덕소');
INSERT INTO tb_station(station_id, name) VALUES (274, '총신대입구');
INSERT INTO tb_station(station_id, name) VALUES (275, '둔전');
INSERT INTO tb_station(station_id, name) VALUES (276, '문학경기장');
INSERT INTO tb_station(station_id, name) VALUES (277, '부천시청');
INSERT INTO tb_station(station_id, name) VALUES (278, '양천향교');
INSERT INTO tb_station(station_id, name) VALUES (279, '등촌');
INSERT INTO tb_station(station_id, name) VALUES (280, '월계');
INSERT INTO tb_station(station_id, name) VALUES (281, '운연');
INSERT INTO tb_station(station_id, name) VALUES (282, '남부터미널');
INSERT INTO tb_station(station_id, name) VALUES (283, '초지');
INSERT INTO tb_station(station_id, name) VALUES (284, '용마산');
INSERT INTO tb_station(station_id, name) VALUES (285, '덕계');
INSERT INTO tb_station(station_id, name) VALUES (286, '범골');
INSERT INTO tb_station(station_id, name) VALUES (287, '개롱');
INSERT INTO tb_station(station_id, name) VALUES (288, '천마산');
INSERT INTO tb_station(station_id, name) VALUES (289, '매교');
INSERT INTO tb_station(station_id, name) VALUES (290, '오목교');
INSERT INTO tb_station(station_id, name) VALUES (291, '광운대');
INSERT INTO tb_station(station_id, name) VALUES (292, '양주');
INSERT INTO tb_station(station_id, name) VALUES (293, '잠원');
INSERT INTO tb_station(station_id, name) VALUES (294, '상갈');
INSERT INTO tb_station(station_id, name) VALUES (295, '동인천');
INSERT INTO tb_station(station_id, name) VALUES (296, '아차산');
INSERT INTO tb_station(station_id, name) VALUES (297, '부개');
INSERT INTO tb_station(station_id, name) VALUES (298, '귤현');
INSERT INTO tb_station(station_id, name) VALUES (299, '사우');
INSERT INTO tb_station(station_id, name) VALUES (300, '소래포구');
INSERT INTO tb_station(station_id, name) VALUES (301, '별내');
INSERT INTO tb_station(station_id, name) VALUES (302, '동천');
INSERT INTO tb_station(station_id, name) VALUES (303, '목동');
INSERT INTO tb_station(station_id, name) VALUES (304, '경복궁');
INSERT INTO tb_station(station_id, name) VALUES (305, '진접');
INSERT INTO tb_station(station_id, name) VALUES (306, '주안');
INSERT INTO tb_station(station_id, name) VALUES (307, '개봉');
INSERT INTO tb_station(station_id, name) VALUES (308, '대공원');
INSERT INTO tb_station(station_id, name) VALUES (309, '서대문');
INSERT INTO tb_station(station_id, name) VALUES (310, '월롱');
INSERT INTO tb_station(station_id, name) VALUES (311, '소사');
INSERT INTO tb_station(station_id, name) VALUES (312, '마포');
INSERT INTO tb_station(station_id, name) VALUES (313, '하남시청');
INSERT INTO tb_station(station_id, name) VALUES (314, '오산');
INSERT INTO tb_station(station_id, name) VALUES (315, '예술회관');
INSERT INTO tb_station(station_id, name) VALUES (316, '상현');
INSERT INTO tb_station(station_id, name) VALUES (317, '의정부시청');
INSERT INTO tb_station(station_id, name) VALUES (318, '녹사평');
INSERT INTO tb_station(station_id, name) VALUES (319, '여의도');
INSERT INTO tb_station(station_id, name) VALUES (320, '어천');
INSERT INTO tb_station(station_id, name) VALUES (321, '남위례');
INSERT INTO tb_station(station_id, name) VALUES (322, '마산');
INSERT INTO tb_station(station_id, name) VALUES (323, '천안');
INSERT INTO tb_station(station_id, name) VALUES (324, '남성');
INSERT INTO tb_station(station_id, name) VALUES (325, '용답');
INSERT INTO tb_station(station_id, name) VALUES (326, '구산');
INSERT INTO tb_station(station_id, name) VALUES (327, '갈산');
INSERT INTO tb_station(station_id, name) VALUES (328, '독립문');
INSERT INTO tb_station(station_id, name) VALUES (329, '북한산보국문');
INSERT INTO tb_station(station_id, name) VALUES (330, '삼성');
INSERT INTO tb_station(station_id, name) VALUES (331, '혜화');
INSERT INTO tb_station(station_id, name) VALUES (332, '당고개');
INSERT INTO tb_station(station_id, name) VALUES (333, '상월곡');
INSERT INTO tb_station(station_id, name) VALUES (334, '도봉');
INSERT INTO tb_station(station_id, name) VALUES (335, '동대입구');
INSERT INTO tb_station(station_id, name) VALUES (336, '시흥능곡');
INSERT INTO tb_station(station_id, name) VALUES (337, '정릉');
INSERT INTO tb_station(station_id, name) VALUES (338, '원시');
INSERT INTO tb_station(station_id, name) VALUES (339, '중동');
INSERT INTO tb_station(station_id, name) VALUES (340, '미아');
INSERT INTO tb_station(station_id, name) VALUES (341, '선부');
INSERT INTO tb_station(station_id, name) VALUES (342, '잠실나루');
INSERT INTO tb_station(station_id, name) VALUES (343, '신이문');
INSERT INTO tb_station(station_id, name) VALUES (344, '계양');
INSERT INTO tb_station(station_id, name) VALUES (345, '직산');
INSERT INTO tb_station(station_id, name) VALUES (346, '신대방삼거리');
INSERT INTO tb_station(station_id, name) VALUES (347, '구리');
INSERT INTO tb_station(station_id, name) VALUES (348, '이촌');
INSERT INTO tb_station(station_id, name) VALUES (349, '금촌');
INSERT INTO tb_station(station_id, name) VALUES (350, '장한평');
INSERT INTO tb_station(station_id, name) VALUES (351, '개화');
INSERT INTO tb_station(station_id, name) VALUES (352, '용두');
INSERT INTO tb_station(station_id, name) VALUES (353, '한양대');
INSERT INTO tb_station(station_id, name) VALUES (354, '시청');
INSERT INTO tb_station(station_id, name) VALUES (355, '세마');
INSERT INTO tb_station(station_id, name) VALUES (356, '탄현');
INSERT INTO tb_station(station_id, name) VALUES (357, '어린이대공원');
INSERT INTO tb_station(station_id, name) VALUES (358, '굴봉산');
INSERT INTO tb_station(station_id, name) VALUES (359, '야당');
INSERT INTO tb_station(station_id, name) VALUES (360, '고속터미널');
INSERT INTO tb_station(station_id, name) VALUES (361, '어룡');
INSERT INTO tb_station(station_id, name) VALUES (362, '고려대');
INSERT INTO tb_station(station_id, name) VALUES (363, '부천종합운동장');
INSERT INTO tb_station(station_id, name) VALUES (364, '신반포');
INSERT INTO tb_station(station_id, name) VALUES (365, '남춘천');
INSERT INTO tb_station(station_id, name) VALUES (366, '길음');
INSERT INTO tb_station(station_id, name) VALUES (367, '장승배기');
INSERT INTO tb_station(station_id, name) VALUES (368, '천호');
INSERT INTO tb_station(station_id, name) VALUES (369, '신원');
INSERT INTO tb_station(station_id, name) VALUES (370, '문정');
INSERT INTO tb_station(station_id, name) VALUES (371, '김유정');
INSERT INTO tb_station(station_id, name) VALUES (372, '박촌');
INSERT INTO tb_station(station_id, name) VALUES (373, '중화');
INSERT INTO tb_station(station_id, name) VALUES (374, '인천터미널');
INSERT INTO tb_station(station_id, name) VALUES (375, '하남풍산');
INSERT INTO tb_station(station_id, name) VALUES (376, '병점');
INSERT INTO tb_station(station_id, name) VALUES (377, '창동');
INSERT INTO tb_station(station_id, name) VALUES (378, '화서');
INSERT INTO tb_station(station_id, name) VALUES (379, '의정부');
INSERT INTO tb_station(station_id, name) VALUES (380, '검암');
INSERT INTO tb_station(station_id, name) VALUES (381, '송도');
INSERT INTO tb_station(station_id, name) VALUES (382, '금정');
INSERT INTO tb_station(station_id, name) VALUES (383, '진위');
INSERT INTO tb_station(station_id, name) VALUES (384, '갈매');
INSERT INTO tb_station(station_id, name) VALUES (385, '계산');
INSERT INTO tb_station(station_id, name) VALUES (386, '원인재');
INSERT INTO tb_station(station_id, name) VALUES (387, '화정');
INSERT INTO tb_station(station_id, name) VALUES (388, '염창');
INSERT INTO tb_station(station_id, name) VALUES (389, '김량장');
INSERT INTO tb_station(station_id, name) VALUES (390, '월곡');
INSERT INTO tb_station(station_id, name) VALUES (391, '신논현');
INSERT INTO tb_station(station_id, name) VALUES (392, '가락시장');
INSERT INTO tb_station(station_id, name) VALUES (393, '명일');
INSERT INTO tb_station(station_id, name) VALUES (394, '곡산');
INSERT INTO tb_station(station_id, name) VALUES (395, '과천');
INSERT INTO tb_station(station_id, name) VALUES (396, '구룡');
INSERT INTO tb_station(station_id, name) VALUES (397, '제기동');
INSERT INTO tb_station(station_id, name) VALUES (398, '신설동');
INSERT INTO tb_station(station_id, name) VALUES (399, '행당');
INSERT INTO tb_station(station_id, name) VALUES (400, '마천');
INSERT INTO tb_station(station_id, name) VALUES (401, '신길');
INSERT INTO tb_station(station_id, name) VALUES (402, '탕정');
INSERT INTO tb_station(station_id, name) VALUES (403, '사당');
INSERT INTO tb_station(station_id, name) VALUES (404, '초월');
INSERT INTO tb_station(station_id, name) VALUES (405, '송내');
INSERT INTO tb_station(station_id, name) VALUES (406, '아현');
INSERT INTO tb_station(station_id, name) VALUES (407, '망원');
INSERT INTO tb_station(station_id, name) VALUES (408, '동대문');
INSERT INTO tb_station(station_id, name) VALUES (409, '인천논현');
INSERT INTO tb_station(station_id, name) VALUES (410, '아산');
INSERT INTO tb_station(station_id, name) VALUES (411, '청평');
INSERT INTO tb_station(station_id, name) VALUES (412, '대방');
INSERT INTO tb_station(station_id, name) VALUES (413, '청계산입구');
INSERT INTO tb_station(station_id, name) VALUES (414, '도봉산');
INSERT INTO tb_station(station_id, name) VALUES (415, '신길온천');
INSERT INTO tb_station(station_id, name) VALUES (416, '논현');
INSERT INTO tb_station(station_id, name) VALUES (417, '왕길');
INSERT INTO tb_station(station_id, name) VALUES (418, '상동');
INSERT INTO tb_station(station_id, name) VALUES (419, '송도달빛축제공원');
INSERT INTO tb_station(station_id, name) VALUES (420, '공항화물청사');
INSERT INTO tb_station(station_id, name) VALUES (421, '오리');
INSERT INTO tb_station(station_id, name) VALUES (422, '광명사거리');
INSERT INTO tb_station(station_id, name) VALUES (423, '상봉');
INSERT INTO tb_station(station_id, name) VALUES (424, '석바위시장');
INSERT INTO tb_station(station_id, name) VALUES (425, '청구');
INSERT INTO tb_station(station_id, name) VALUES (426, '인천가좌');
INSERT INTO tb_station(station_id, name) VALUES (427, '운천');
INSERT INTO tb_station(station_id, name) VALUES (428, '전대.에버랜드');
INSERT INTO tb_station(station_id, name) VALUES (429, '암사');
INSERT INTO tb_station(station_id, name) VALUES (430, '까치산');
INSERT INTO tb_station(station_id, name) VALUES (431, '방화');
INSERT INTO tb_station(station_id, name) VALUES (432, '송정');
INSERT INTO tb_station(station_id, name) VALUES (433, '삼전');
INSERT INTO tb_station(station_id, name) VALUES (434, '당정');
INSERT INTO tb_station(station_id, name) VALUES (435, '구래');
INSERT INTO tb_station(station_id, name) VALUES (436, '홍제');
INSERT INTO tb_station(station_id, name) VALUES (437, '숭실대입구');
INSERT INTO tb_station(station_id, name) VALUES (438, '도화');
INSERT INTO tb_station(station_id, name) VALUES (439, '여주');
INSERT INTO tb_station(station_id, name) VALUES (440, '이대');
INSERT INTO tb_station(station_id, name) VALUES (441, '당산');
INSERT INTO tb_station(station_id, name) VALUES (442, '둔촌오륜');
INSERT INTO tb_station(station_id, name) VALUES (443, '남영');
INSERT INTO tb_station(station_id, name) VALUES (444, '석수');
INSERT INTO tb_station(station_id, name) VALUES (445, '용문');
INSERT INTO tb_station(station_id, name) VALUES (446, '원종');
INSERT INTO tb_station(station_id, name) VALUES (447, '동묘앞');
INSERT INTO tb_station(station_id, name) VALUES (448, '내방');
INSERT INTO tb_station(station_id, name) VALUES (449, '단대오거리');
INSERT INTO tb_station(station_id, name) VALUES (450, '운길산');
INSERT INTO tb_station(station_id, name) VALUES (451, '녹번');
INSERT INTO tb_station(station_id, name) VALUES (452, '백운');
INSERT INTO tb_station(station_id, name) VALUES (453, '캠퍼스타운');
INSERT INTO tb_station(station_id, name) VALUES (454, '남태령');
INSERT INTO tb_station(station_id, name) VALUES (455, '가정중앙시장');
INSERT INTO tb_station(station_id, name) VALUES (456, '구성');
INSERT INTO tb_station(station_id, name) VALUES (457, '이천');
INSERT INTO tb_station(station_id, name) VALUES (458, '명지대');
INSERT INTO tb_station(station_id, name) VALUES (459, '신흥');
INSERT INTO tb_station(station_id, name) VALUES (460, '구일');
INSERT INTO tb_station(station_id, name) VALUES (461, '대야미');
INSERT INTO tb_station(station_id, name) VALUES (462, '보라매병원');
INSERT INTO tb_station(station_id, name) VALUES (463, '오금');
INSERT INTO tb_station(station_id, name) VALUES (464, '수색');
INSERT INTO tb_station(station_id, name) VALUES (465, '광명');
INSERT INTO tb_station(station_id, name) VALUES (466, '보라매공원');
INSERT INTO tb_station(station_id, name) VALUES (467, '소새울');
INSERT INTO tb_station(station_id, name) VALUES (468, '서구청');
INSERT INTO tb_station(station_id, name) VALUES (469, '태릉입구');
INSERT INTO tb_station(station_id, name) VALUES (470, '잠실새내');
INSERT INTO tb_station(station_id, name) VALUES (471, '공릉');
INSERT INTO tb_station(station_id, name) VALUES (472, '금릉');
INSERT INTO tb_station(station_id, name) VALUES (473, '중곡');
INSERT INTO tb_station(station_id, name) VALUES (474, '세류');
INSERT INTO tb_station(station_id, name) VALUES (475, '중계');
INSERT INTO tb_station(station_id, name) VALUES (476, '용산');
INSERT INTO tb_station(station_id, name) VALUES (477, '주엽');
INSERT INTO tb_station(station_id, name) VALUES (478, '무악재');
INSERT INTO tb_station(station_id, name) VALUES (479, '남한산성입구');
INSERT INTO tb_station(station_id, name) VALUES (480, '서강대');
INSERT INTO tb_station(station_id, name) VALUES (481, '동두천중앙');
INSERT INTO tb_station(station_id, name) VALUES (482, '녹양');
INSERT INTO tb_station(station_id, name) VALUES (483, '평택지제');
INSERT INTO tb_station(station_id, name) VALUES (484, '강일');
INSERT INTO tb_station(station_id, name) VALUES (485, '오산대');
INSERT INTO tb_station(station_id, name) VALUES (486, '쌍용');
INSERT INTO tb_station(station_id, name) VALUES (487, '신방화');
INSERT INTO tb_station(station_id, name) VALUES (488, '독바위');
INSERT INTO tb_station(station_id, name) VALUES (489, '부천');
INSERT INTO tb_station(station_id, name) VALUES (490, '수진');
INSERT INTO tb_station(station_id, name) VALUES (491, '독정');
INSERT INTO tb_station(station_id, name) VALUES (492, '먹골');
INSERT INTO tb_station(station_id, name) VALUES (493, '정부과천청사');
INSERT INTO tb_station(station_id, name) VALUES (494, '수지구청');
INSERT INTO tb_station(station_id, name) VALUES (495, '신정네거리');
INSERT INTO tb_station(station_id, name) VALUES (496, '두정');
INSERT INTO tb_station(station_id, name) VALUES (497, '불광');
INSERT INTO tb_station(station_id, name) VALUES (498, '금곡');
INSERT INTO tb_station(station_id, name) VALUES (499, '회현');
INSERT INTO tb_station(station_id, name) VALUES (500, '인덕원');
INSERT INTO tb_station(station_id, name) VALUES (501, '선유도');
INSERT INTO tb_station(station_id, name) VALUES (502, '방학');
INSERT INTO tb_station(station_id, name) VALUES (503, '동대문역사문화공원');
INSERT INTO tb_station(station_id, name) VALUES (504, '검단오류');
INSERT INTO tb_station(station_id, name) VALUES (505, '신포');
INSERT INTO tb_station(station_id, name) VALUES (506, '도심');
INSERT INTO tb_station(station_id, name) VALUES (507, '수락산');
INSERT INTO tb_station(station_id, name) VALUES (508, '부평시장');
INSERT INTO tb_station(station_id, name) VALUES (509, '테크노파크');
INSERT INTO tb_station(station_id, name) VALUES (510, '부발');
INSERT INTO tb_station(station_id, name) VALUES (511, '서울대벤처타운');
INSERT INTO tb_station(station_id, name) VALUES (512, '삼송');
INSERT INTO tb_station(station_id, name) VALUES (513, '고진');
INSERT INTO tb_station(station_id, name) VALUES (514, '역촌');
INSERT INTO tb_station(station_id, name) VALUES (515, '검단사거리');
INSERT INTO tb_station(station_id, name) VALUES (516, '연신내');
INSERT INTO tb_station(station_id, name) VALUES (517, '삼양');
INSERT INTO tb_station(station_id, name) VALUES (518, '경인교대입구');
INSERT INTO tb_station(station_id, name) VALUES (519, '명학');
INSERT INTO tb_station(station_id, name) VALUES (520, '상일동');
INSERT INTO tb_station(station_id, name) VALUES (521, '마곡나루');
INSERT INTO tb_station(station_id, name) VALUES (522, '쌍문');
INSERT INTO tb_station(station_id, name) VALUES (523, '석계');
INSERT INTO tb_station(station_id, name) VALUES (524, '대치');
INSERT INTO tb_station(station_id, name) VALUES (525, '둔촌동');
INSERT INTO tb_station(station_id, name) VALUES (526, '봉명');
INSERT INTO tb_station(station_id, name) VALUES (527, '월곶');
INSERT INTO tb_station(station_id, name) VALUES (528, '효자');
INSERT INTO tb_station(station_id, name) VALUES (529, '가산디지털단지');
INSERT INTO tb_station(station_id, name) VALUES (530, '인천대입구');
INSERT INTO tb_station(station_id, name) VALUES (531, '운양');
INSERT INTO tb_station(station_id, name) VALUES (532, '수내');
INSERT INTO tb_station(station_id, name) VALUES (533, '압구정');
INSERT INTO tb_station(station_id, name) VALUES (534, '신내');
INSERT INTO tb_station(station_id, name) VALUES (535, '반월');
INSERT INTO tb_station(station_id, name) VALUES (536, '도농');
INSERT INTO tb_station(station_id, name) VALUES (537, '가재울');
INSERT INTO tb_station(station_id, name) VALUES (538, '신정');
INSERT INTO tb_station(station_id, name) VALUES (539, '마장');
INSERT INTO tb_station(station_id, name) VALUES (540, '도곡');
INSERT INTO tb_station(station_id, name) VALUES (541, '범계');
INSERT INTO tb_station(station_id, name) VALUES (542, '한성백제');
INSERT INTO tb_station(station_id, name) VALUES (543, '삼양사거리');
INSERT INTO tb_station(station_id, name) VALUES (544, '지식정보단지');
INSERT INTO tb_station(station_id, name) VALUES (545, '덕정');
INSERT INTO tb_station(station_id, name) VALUES (546, '애오개');
INSERT INTO tb_station(station_id, name) VALUES (547, '국회의사당');
INSERT INTO tb_station(station_id, name) VALUES (548, '신갈');
INSERT INTO tb_station(station_id, name) VALUES (549, '문산');
INSERT INTO tb_station(station_id, name) VALUES (550, '도림천');
INSERT INTO tb_station(station_id, name) VALUES (551, '이매');
INSERT INTO tb_station(station_id, name) VALUES (552, '성수');
INSERT INTO tb_station(station_id, name) VALUES (553, '하계');
INSERT INTO tb_station(station_id, name) VALUES (554, '동오');
INSERT INTO tb_station(station_id, name) VALUES (555, '회기');
INSERT INTO tb_station(station_id, name) VALUES (556, '안국');
INSERT INTO tb_station(station_id, name) VALUES (557, '강남');
INSERT INTO tb_station(station_id, name) VALUES (558, '증미');
INSERT INTO tb_station(station_id, name) VALUES (559, '군자');
INSERT INTO tb_station(station_id, name) VALUES (560, '대림');
INSERT INTO tb_station(station_id, name) VALUES (561, '대화');
INSERT INTO tb_station(station_id, name) VALUES (562, '신중동');
INSERT INTO tb_station(station_id, name) VALUES (563, '운정');
INSERT INTO tb_station(station_id, name) VALUES (564, '걸포북변');
INSERT INTO tb_station(station_id, name) VALUES (565, '양재시민의숲');
INSERT INTO tb_station(station_id, name) VALUES (566, '새절');
INSERT INTO tb_station(station_id, name) VALUES (567, '청라국제도시');
INSERT INTO tb_station(station_id, name) VALUES (568, '안양');
INSERT INTO tb_station(station_id, name) VALUES (569, '대곡');
INSERT INTO tb_station(station_id, name) VALUES (570, '성환');
INSERT INTO tb_station(station_id, name) VALUES (571, '합정');
INSERT INTO tb_station(station_id, name) VALUES (572, '지축');
INSERT INTO tb_station(station_id, name) VALUES (573, '인천공항2터미널');
INSERT INTO tb_station(station_id, name) VALUES (574, '부평삼거리');
INSERT INTO tb_station(station_id, name) VALUES (575, '가능');
INSERT INTO tb_station(station_id, name) VALUES (576, '신당');
INSERT INTO tb_station(station_id, name) VALUES (577, '을지로3가');
INSERT INTO tb_station(station_id, name) VALUES (578, '산본');
INSERT INTO tb_station(station_id, name) VALUES (579, '도원');
INSERT INTO tb_station(station_id, name) VALUES (580, '경전철의정부');
INSERT INTO tb_station(station_id, name) VALUES (581, '신둔도예촌');
INSERT INTO tb_station(station_id, name) VALUES (582, '주안국가산단');
INSERT INTO tb_station(station_id, name) VALUES (583, '군포');
INSERT INTO tb_station(station_id, name) VALUES (584, '역삼');
INSERT INTO tb_station(station_id, name) VALUES (585, '상도');
INSERT INTO tb_station(station_id, name) VALUES (586, '보라매');
INSERT INTO tb_station(station_id, name) VALUES (587, '양촌');
INSERT INTO tb_station(station_id, name) VALUES (588, '중랑');
INSERT INTO tb_station(station_id, name) VALUES (589, '미금');
INSERT INTO tb_station(station_id, name) VALUES (590, '경마공원');
INSERT INTO tb_station(station_id, name) VALUES (591, '솔밭공원');
INSERT INTO tb_station(station_id, name) VALUES (592, '부평구청');
INSERT INTO tb_station(station_id, name) VALUES (593, '장지');
INSERT INTO tb_station(station_id, name) VALUES (594, '가평');
INSERT INTO tb_station(station_id, name) VALUES (595, '상천');
INSERT INTO tb_station(station_id, name) VALUES (596, '하남검단산');
INSERT INTO tb_station(station_id, name) VALUES (597, '남구로');
INSERT INTO tb_station(station_id, name) VALUES (598, '가좌');
INSERT INTO tb_station(station_id, name) VALUES (599, '강남구청');
INSERT INTO tb_station(station_id, name) VALUES (600, '봉화산');
INSERT INTO tb_station(station_id, name) VALUES (601, '면목');
INSERT INTO tb_station(station_id, name) VALUES (602, '국수');
INSERT INTO tb_station(station_id, name) VALUES (603, '신천');
INSERT INTO tb_station(station_id, name) VALUES (604, '부평');
INSERT INTO tb_station(station_id, name) VALUES (605, '을지로입구');
INSERT INTO tb_station(station_id, name) VALUES (606, '창신');
INSERT INTO tb_station(station_id, name) VALUES (607, '독산');
INSERT INTO tb_station(station_id, name) VALUES (608, '판교');
INSERT INTO tb_station(station_id, name) VALUES (609, '복정');
INSERT INTO tb_station(station_id, name) VALUES (610, '수리산');
INSERT INTO tb_station(station_id, name) VALUES (611, '지행');
INSERT INTO tb_station(station_id, name) VALUES (612, '녹천');
INSERT INTO tb_station(station_id, name) VALUES (613, '시우');
INSERT INTO tb_station(station_id, name) VALUES (614, '만수');
INSERT INTO tb_station(station_id, name) VALUES (615, '충무로');
INSERT INTO tb_station(station_id, name) VALUES (616, '마들');
INSERT INTO tb_station(station_id, name) VALUES (617, '의왕');
INSERT INTO tb_station(station_id, name) VALUES (618, '선정릉');
INSERT INTO tb_station(station_id, name) VALUES (619, '파주');
INSERT INTO tb_station(station_id, name) VALUES (620, '의정부중앙');
INSERT INTO tb_station(station_id, name) VALUES (621, '방이');
INSERT INTO tb_station(station_id, name) VALUES (622, '수서');
INSERT INTO tb_station(station_id, name) VALUES (623, '김포공항');
INSERT INTO tb_station(station_id, name) VALUES (624, '효창공원앞');
INSERT INTO tb_station(station_id, name) VALUES (625, '샛강');
INSERT INTO tb_station(station_id, name) VALUES (626, '을지로4가');
INSERT INTO tb_station(station_id, name) VALUES (627, '뚝섬유원지');
INSERT INTO tb_station(station_id, name) VALUES (628, '인하대');
INSERT INTO tb_station(station_id, name) VALUES (629, '중앙');
INSERT INTO tb_station(station_id, name) VALUES (630, '서울역');
INSERT INTO tb_station(station_id, name) VALUES (631, '신목동');
INSERT INTO tb_station(station_id, name) VALUES (632, '백양리');
INSERT INTO tb_station(station_id, name) VALUES (633, '망월사');
INSERT INTO tb_station(station_id, name) VALUES (634, '월드컵경기장');
INSERT INTO tb_station(station_id, name) VALUES (635, '산성');
INSERT INTO tb_station(station_id, name) VALUES (636, '평택');
INSERT INTO tb_station(station_id, name) VALUES (637, '신사');
INSERT INTO tb_station(station_id, name) VALUES (638, '경기도청북부청사');
INSERT INTO tb_station(station_id, name) VALUES (639, '강동');
INSERT INTO tb_station(station_id, name) VALUES (640, '오남');

-- ## 지하철 노선 - 정류장 연관 정보
/*
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 408);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 280);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 612);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 377);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 61);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 41);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 114);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 307);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 175);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 27);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 489);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 405);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 604);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 452);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 157);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 295);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 69);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 460);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 311);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 297);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 218);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 579);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 96);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 339);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 438);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 85);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 36);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 481);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 611);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 285);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 292);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 482);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 575);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 379);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 75);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 633);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 414);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 334);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 354);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 630);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 401);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 80);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 545);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 502);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 306);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 189);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 314);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 249);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 382);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 443);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 476);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 113);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 412);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 526);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 486);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 410);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 402);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 245);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 170);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 236);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 156);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 108);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 383);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 184);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 397);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 447);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 8);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 529);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 259);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 444);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 82);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 568);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 519);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 583);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 617);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 158);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 378);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 172);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 607);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 474);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 376);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 355);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 485);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 95);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 30);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 483);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 636);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 570);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 345);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 496);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 323);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 434);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 465);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 398);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 555);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 215);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 343);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 523);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(1, 291);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 560);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 238);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 97);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 269);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 403);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 605);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 261);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 352);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 49);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 66);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 626);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 571);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 7);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 495);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 441);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 225);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 330);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 205);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 186);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 256);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 16);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 552);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 577);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 17);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 108);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 406);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 325);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 353);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 219);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 180);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 226);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 138);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 65);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 127);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 557);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 584);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 110);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 76);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 342);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 193);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 209);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 576);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 503);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 354);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 430);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 87);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 124);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 550);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 470);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 398);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(2, 440);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(3, 436);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(3, 577);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(3, 63);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(3, 512);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(3, 88);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(3, 451);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(3, 497);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(3, 516);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(3, 93);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(3, 572);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(3, 253);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(3, 533);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(3, 561);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(3, 477);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(3, 213);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(3, 217);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(3, 23);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(3, 615);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(3, 81);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(3, 569);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(3, 387);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(3, 478);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(3, 637);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(3, 41);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(3, 79);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(3, 392);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(3, 622);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(3, 224);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(3, 282);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(3, 127);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(3, 360);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(3, 540);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(3, 13);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(3, 123);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(3, 556);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(3, 244);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(3, 92);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(3, 304);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(3, 335);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(3, 524);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(3, 463);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(3, 328);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(3, 230);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(3, 293);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 541);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 237);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 377);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 522);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 99);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 340);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 90);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 366);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 149);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 144);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 331);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 408);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 503);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 615);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 35);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 499);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 630);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 198);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 135);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 266);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 348);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 119);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 274);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 403);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 454);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 55);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 332);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 94);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 640);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 305);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 57);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 590);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 308);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 395);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 493);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 500);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 262);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 382);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 578);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 461);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 535);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 140);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 196);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 629);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 67);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 283);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 222);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 415);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 150);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 83);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(4, 610);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 503);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 626);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 425);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 41);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 132);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 309);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 124);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 28);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 312);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 271);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 319);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 401);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 246);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 147);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 399);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 219);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 539);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 186);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 233);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 290);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 303);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 538);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 430);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 21);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 26);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 71);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 350);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 559);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 296);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 47);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 368);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 639);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 248);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 10);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 393);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 183);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 525);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 117);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 621);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 287);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 77);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 400);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 313);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 596);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 623);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 20);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 375);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 546);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 520);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 463);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 484);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 221);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 188);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 432);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 431);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(5, 37);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(6, 362);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(6, 182);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(6, 514);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(6, 497);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(6, 488);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(6, 516);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(6, 326);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(6, 566);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(6, 241);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(6, 201);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(6, 634);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(6, 203);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(6, 407);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(6, 571);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(6, 176);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(6, 134);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(6, 133);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(6, 28);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(6, 624);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(6, 135);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(6, 318);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(6, 18);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(6, 263);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(6, 166);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(6, 13);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(6, 576);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(6, 447);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(6, 606);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(6, 164);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(6, 151);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(6, 390);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(6, 333);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(6, 118);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(6, 523);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(6, 469);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(6, 600);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(6, 534);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(6, 267);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(6, 425);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 373);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 601);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 284);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 473);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 559);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 357);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 225);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 627);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 173);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 599);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 416);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 360);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 448);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 53);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 277);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 324);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 437);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 585);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 367);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 346);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 586);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 32);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 560);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 597);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 529);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 422);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 216);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 96);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 14);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 592);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 106);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 190);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 38);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 212);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 40);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 418);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 414);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 507);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 616);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 237);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 475);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 553);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 471);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 469);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 492);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 423);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 562);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 231);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 100);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 91);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 363);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 33);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(7, 257);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(8, 429);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(8, 368);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(8, 152);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(8, 251);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(8, 76);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(8, 111);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(8, 593);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(8, 214);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(8, 370);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(8, 392);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(8, 321);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(8, 232);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(8, 490);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(8, 459);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(8, 449);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(8, 479);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(8, 635);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(8, 609);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(9, 364);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(9, 623);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(9, 46);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(9, 487);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(9, 521);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(9, 278);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(9, 220);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(9, 558);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(9, 351);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(9, 279);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(9, 388);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(9, 631);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(9, 501);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(9, 547);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(9, 319);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(9, 625);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(9, 113);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(9, 163);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(9, 119);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(9, 240);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(9, 48);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(9, 391);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(9, 211);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(9, 433);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(9, 442);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(9, 109);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(9, 214);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(9, 6);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(9, 618);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(9, 117);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(9, 542);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(9, 51);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(9, 199);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(9, 261);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(9, 360);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(9, 153);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(9, 194);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(9, 441);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(10, 510);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(10, 197);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(10, 457);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(10, 439);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(10, 581);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(10, 551);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(10, 148);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(10, 174);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(10, 268);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(10, 404);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(10, 608);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 28);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 168);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 219);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 450);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 191);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 369);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 602);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 206);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 101);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 233);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 43);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 445);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 141);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 630);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 238);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 624);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 480);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 97);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 598);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 201);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 464);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 58);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 270);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 394);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 159);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 202);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 103);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 356);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 359);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 563);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 472);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 349);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 310);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 619);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 549);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 102);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 427);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 555);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 184);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 242);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 230);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 207);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 476);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 569);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 143);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 15);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 348);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 506);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 273);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 115);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 536);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 347);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 120);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 423);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 588);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 50);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(11, 12);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(12, 555);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(12, 39);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(12, 411);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(12, 112);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(12, 288);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(12, 68);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(12, 44);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(12, 258);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(12, 365);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(12, 371);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(12, 632);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(12, 358);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(12, 45);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(12, 594);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(12, 291);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(12, 184);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(12, 588);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(12, 423);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(12, 143);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(12, 534);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(12, 384);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(12, 301);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(12, 181);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(12, 498);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(12, 595);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(13, 567);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(13, 573);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(13, 178);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(13, 521);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(13, 28);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(13, 97);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(13, 201);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(13, 623);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(13, 126);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(13, 420);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(13, 25);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(13, 630);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(13, 344);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(13, 380);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(15, 363);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(15, 103);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(15, 336);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(15, 446);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(15, 311);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(15, 467);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(15, 146);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(15, 603);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(15, 247);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(15, 86);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(15, 15);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(15, 394);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(15, 159);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(15, 129);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(15, 569);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(15, 202);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(15, 341);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(15, 283);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(15, 613);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(15, 338);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(15, 623);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 229);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 160);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 386);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 64);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 381);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 628);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 505);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 69);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 110);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 540);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 396);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 136);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 219);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 599);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 78);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 622);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 104);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 121);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 243);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 169);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 107);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 618);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 72);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 421);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 551);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 167);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 187);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 456);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 548);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 192);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 294);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 1);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 171);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 139);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 145);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 289);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 2);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 130);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 320);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 155);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 527);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 300);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 409);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 252);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 60);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 195);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 609);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 122);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 532);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 161);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 589);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 232);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 83);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 196);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 629);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 67);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 283);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 222);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 415);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 150);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 184);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 172);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(16, 125);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(18, 565);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(18, 161);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(18, 637);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(18, 557);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(18, 391);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(18, 589);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(18, 494);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(18, 302);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(18, 272);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(18, 234);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(18, 416);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(18, 213);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(18, 413);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(18, 3);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(18, 316);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(18, 608);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(20, 105);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(20, 398);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(20, 164);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(20, 149);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(20, 337);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(20, 329);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(20, 591);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(20, 59);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(20, 517);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(20, 4);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(20, 54);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(20, 204);
INSERT INTO tb_subway_line_station(subway_line_id, station_id) VALUES(20, 543);
*/

/* 지하철 데이터 없는 목록
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 0, '1918') ON DUPLICATE KEY UPDATE subway_line_station_code = '1918'; -- 전곡 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 0, '0409') ON DUPLICATE KEY UPDATE subway_line_station_code = '0409'; -- 불암산 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 0, '2730') ON DUPLICATE KEY UPDATE subway_line_station_code = '2730'; -- 자양 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (8, 0, '2807') ON DUPLICATE KEY UPDATE subway_line_station_code = '2807'; -- 동구릉 8호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 0, '1919') ON DUPLICATE KEY UPDATE subway_line_station_code = '1919'; -- 연천 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 0, '1917') ON DUPLICATE KEY UPDATE subway_line_station_code = '1917'; -- 청산 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (8, 0, '2809') ON DUPLICATE KEY UPDATE subway_line_station_code = '2809'; -- 장자호수공원 8호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (8, 0, '2810') ON DUPLICATE KEY UPDATE subway_line_station_code = '2810'; -- 암사역사공원 8호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (8, 0, '2806') ON DUPLICATE KEY UPDATE subway_line_station_code = '2806'; -- 다산 8호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 0, '1268') ON DUPLICATE KEY UPDATE subway_line_station_code = '1268'; -- 한국항공대 경의중앙선
*/

INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (20, 54, '4703') ON DUPLICATE KEY UPDATE subway_line_station_code = '4703'; -- 4?19민주묘지 우이신설경전철
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 1, '1867') ON DUPLICATE KEY UPDATE subway_line_station_code = '1867'; -- 청명 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 2, '1873') ON DUPLICATE KEY UPDATE subway_line_station_code = '1873'; -- 고색 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (18, 3, '4316') ON DUPLICATE KEY UPDATE subway_line_station_code = '4316'; -- 성복 신분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (20, 4, '4708') ON DUPLICATE KEY UPDATE subway_line_station_code = '4708'; -- 솔샘 우이신설경전철
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (9, 6, '4128') ON DUPLICATE KEY UPDATE subway_line_station_code = '4128'; -- 삼성중앙 9호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 7, '0213') ON DUPLICATE KEY UPDATE subway_line_station_code = '0213'; -- 구의 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 8, '1701') ON DUPLICATE KEY UPDATE subway_line_station_code = '1701'; -- 구로 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 10, '2551') ON DUPLICATE KEY UPDATE subway_line_station_code = '2551'; -- 굽은다리 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 12, '1210') ON DUPLICATE KEY UPDATE subway_line_station_code = '1210'; -- 팔당 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (6, 13, '2634') ON DUPLICATE KEY UPDATE subway_line_station_code = '2634'; -- 약수 6호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (3, 13, '0323') ON DUPLICATE KEY UPDATE subway_line_station_code = '0323'; -- 약수 3호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 14, '3763') ON DUPLICATE KEY UPDATE subway_line_station_code = '3763'; -- 석남 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 15, '1271') ON DUPLICATE KEY UPDATE subway_line_station_code = '1271'; -- 능곡 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (15, 15, '104C') ON DUPLICATE KEY UPDATE subway_line_station_code = '104C'; -- 능곡 서해선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 16, '0230') ON DUPLICATE KEY UPDATE subway_line_station_code = '0230'; -- 신림 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 17, '0235') ON DUPLICATE KEY UPDATE subway_line_station_code = '0235'; -- 문래 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (6, 18, '2631') ON DUPLICATE KEY UPDATE subway_line_station_code = '2631'; -- 이태원 6호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 20, '2543') ON DUPLICATE KEY UPDATE subway_line_station_code = '2543'; -- 답십리 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 21, '2518') ON DUPLICATE KEY UPDATE subway_line_station_code = '2518'; -- 화곡 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (3, 23, '1955') ON DUPLICATE KEY UPDATE subway_line_station_code = '1955'; -- 마두 3호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (13, 25, '4211') ON DUPLICATE KEY UPDATE subway_line_station_code = '4211'; -- 운서 공항철도
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 26, '2517') ON DUPLICATE KEY UPDATE subway_line_station_code = '2517'; -- 우장산 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 27, '1803') ON DUPLICATE KEY UPDATE subway_line_station_code = '1803'; -- 역곡 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (13, 28, '4202') ON DUPLICATE KEY UPDATE subway_line_station_code = '4202'; -- 공덕 공항철도
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (6, 28, '2627') ON DUPLICATE KEY UPDATE subway_line_station_code = '2627'; -- 공덕 6호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 28, '1262') ON DUPLICATE KEY UPDATE subway_line_station_code = '1262'; -- 공덕 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 28, '2530') ON DUPLICATE KEY UPDATE subway_line_station_code = '2530'; -- 공덕 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 30, '1722') ON DUPLICATE KEY UPDATE subway_line_station_code = '1722'; -- 서정리 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 32, '2745') ON DUPLICATE KEY UPDATE subway_line_station_code = '2745'; -- 신풍 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 33, '3753') ON DUPLICATE KEY UPDATE subway_line_station_code = '3753'; -- 까치울 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 35, '0424') ON DUPLICATE KEY UPDATE subway_line_station_code = '0424'; -- 명동 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 36, '1915') ON DUPLICATE KEY UPDATE subway_line_station_code = '1915'; -- 동두천 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 37, '2512') ON DUPLICATE KEY UPDATE subway_line_station_code = '2512'; -- 개화산 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 38, '2733') ON DUPLICATE KEY UPDATE subway_line_station_code = '2733'; -- 학동 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (12, 39, '1317') ON DUPLICATE KEY UPDATE subway_line_station_code = '1317'; -- 평내호평 경춘선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 40, '2711') ON DUPLICATE KEY UPDATE subway_line_station_code = '2711'; -- 장암 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (3, 41, '0319') ON DUPLICATE KEY UPDATE subway_line_station_code = '0319'; -- 종로3가 3호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 41, '2535') ON DUPLICATE KEY UPDATE subway_line_station_code = '2535'; -- 종로3가 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 41, '0153') ON DUPLICATE KEY UPDATE subway_line_station_code = '0153'; -- 종로3가 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 43, '1218') ON DUPLICATE KEY UPDATE subway_line_station_code = '1218'; -- 원덕 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (12, 44, '1326') ON DUPLICATE KEY UPDATE subway_line_station_code = '1326'; -- 강촌 경춘선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (12, 45, '1314') ON DUPLICATE KEY UPDATE subway_line_station_code = '1314'; -- 퇴계원 경춘선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (9, 46, '4103') ON DUPLICATE KEY UPDATE subway_line_station_code = '4103'; -- 공항시장 9호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 47, '2547') ON DUPLICATE KEY UPDATE subway_line_station_code = '2547'; -- 광나루 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (9, 48, '4124') ON DUPLICATE KEY UPDATE subway_line_station_code = '4124'; -- 사평 9호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 49, '0245') ON DUPLICATE KEY UPDATE subway_line_station_code = '0245'; -- 신답 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (9, 51, '4134') ON DUPLICATE KEY UPDATE subway_line_station_code = '4134'; -- 송파나루 9호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 53, '2738') ON DUPLICATE KEY UPDATE subway_line_station_code = '2738'; -- 이수 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 55, '0410') ON DUPLICATE KEY UPDATE subway_line_station_code = '0410'; -- 상계 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 57, '1450') ON DUPLICATE KEY UPDATE subway_line_station_code = '1450'; -- 선바위 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 58, '1269') ON DUPLICATE KEY UPDATE subway_line_station_code = '1269'; -- 강매 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (20, 59, '4705') ON DUPLICATE KEY UPDATE subway_line_station_code = '4705'; -- 화계 우이신설경전철
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 60, '1889') ON DUPLICATE KEY UPDATE subway_line_station_code = '1889'; -- 숭의 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 61, '0154') ON DUPLICATE KEY UPDATE subway_line_station_code = '0154'; -- 종로5가 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (3, 63, '0341') ON DUPLICATE KEY UPDATE subway_line_station_code = '0341'; -- 경찰병원 3호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 64, '1885') ON DUPLICATE KEY UPDATE subway_line_station_code = '1885'; -- 연수 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 65, '0224') ON DUPLICATE KEY UPDATE subway_line_station_code = '0224'; -- 서초 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 66, '0210') ON DUPLICATE KEY UPDATE subway_line_station_code = '0210'; -- 뚝섬 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 67, '1757') ON DUPLICATE KEY UPDATE subway_line_station_code = '1757'; -- 고잔 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 67, '1832') ON DUPLICATE KEY UPDATE subway_line_station_code = '1832'; -- 고잔 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (12, 68, '1329') ON DUPLICATE KEY UPDATE subway_line_station_code = '1329'; -- 춘천 경춘선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 69, '1812') ON DUPLICATE KEY UPDATE subway_line_station_code = '1812'; -- 인천 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 69, '1891') ON DUPLICATE KEY UPDATE subway_line_station_code = '1891'; -- 인천 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 71, '2516') ON DUPLICATE KEY UPDATE subway_line_station_code = '2516'; -- 발산 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 72, '1878') ON DUPLICATE KEY UPDATE subway_line_station_code = '1878'; -- 달월 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 75, '1905') ON DUPLICATE KEY UPDATE subway_line_station_code = '1905'; -- 회룡 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 76, '0216') ON DUPLICATE KEY UPDATE subway_line_station_code = '0216'; -- 잠실 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (8, 76, '2815') ON DUPLICATE KEY UPDATE subway_line_station_code = '2815'; -- 잠실 8호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 77, '2560') ON DUPLICATE KEY UPDATE subway_line_station_code = '2560'; -- 거여 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 78, '1848') ON DUPLICATE KEY UPDATE subway_line_station_code = '1848'; -- 압구정로데오 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (3, 79, '1951') ON DUPLICATE KEY UPDATE subway_line_station_code = '1951'; -- 원당 3호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 80, '1914') ON DUPLICATE KEY UPDATE subway_line_station_code = '1914'; -- 보산 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (3, 81, '0333') ON DUPLICATE KEY UPDATE subway_line_station_code = '0333'; -- 매봉 3호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 82, '1705') ON DUPLICATE KEY UPDATE subway_line_station_code = '1705'; -- 관악 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 83, '1800') ON DUPLICATE KEY UPDATE subway_line_station_code = '1800'; -- 오이도 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 83, '1762') ON DUPLICATE KEY UPDATE subway_line_station_code = '1762'; -- 오이도 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 85, '1916') ON DUPLICATE KEY UPDATE subway_line_station_code = '1916'; -- 소요산 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (15, 86, '4809') ON DUPLICATE KEY UPDATE subway_line_station_code = '4809'; -- 시흥시청 서해선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 87, '0229') ON DUPLICATE KEY UPDATE subway_line_station_code = '0229'; -- 봉천 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (3, 88, '0336') ON DUPLICATE KEY UPDATE subway_line_station_code = '0336'; -- 학여울 3호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 90, '0416') ON DUPLICATE KEY UPDATE subway_line_station_code = '0416'; -- 미아사거리 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 91, '3755') ON DUPLICATE KEY UPDATE subway_line_station_code = '3755'; -- 춘의 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (3, 92, '0324') ON DUPLICATE KEY UPDATE subway_line_station_code = '0324'; -- 금호 3호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (3, 93, '0310') ON DUPLICATE KEY UPDATE subway_line_station_code = '0310'; -- 구파발 3호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 94, '0408') ON DUPLICATE KEY UPDATE subway_line_station_code = '0408'; -- 별내별가람 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 95, '1721') ON DUPLICATE KEY UPDATE subway_line_station_code = '1721'; -- 송탄 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 96, '2752') ON DUPLICATE KEY UPDATE subway_line_station_code = '2752'; -- 온수 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 96, '1821') ON DUPLICATE KEY UPDATE subway_line_station_code = '1821'; -- 온수 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 97, '0239') ON DUPLICATE KEY UPDATE subway_line_station_code = '0239'; -- 홍대입구 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 97, '1264') ON DUPLICATE KEY UPDATE subway_line_station_code = '1264'; -- 홍대입구 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (13, 97, '4203') ON DUPLICATE KEY UPDATE subway_line_station_code = '4203'; -- 홍대입구 공항철도
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 99, '0414') ON DUPLICATE KEY UPDATE subway_line_station_code = '0414'; -- 수유 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 100, '2724') ON DUPLICATE KEY UPDATE subway_line_station_code = '2724'; -- 사가정 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 101, '1216') ON DUPLICATE KEY UPDATE subway_line_station_code = '1216'; -- 오빈 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 102, '1285') ON DUPLICATE KEY UPDATE subway_line_station_code = '1285'; -- 임진강 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 103, '1275') ON DUPLICATE KEY UPDATE subway_line_station_code = '1275'; -- 일산 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (15, 103, '108C') ON DUPLICATE KEY UPDATE subway_line_station_code = '108C'; -- 일산 서해선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 104, '1877') ON DUPLICATE KEY UPDATE subway_line_station_code = '1877'; -- 사리 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (20, 105, '4701') ON DUPLICATE KEY UPDATE subway_line_station_code = '4701'; -- 북한산우이 우이신설경전철
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 106, '3762') ON DUPLICATE KEY UPDATE subway_line_station_code = '3762'; -- 산곡 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 107, '1024') ON DUPLICATE KEY UPDATE subway_line_station_code = '1024'; -- 한티 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 108, '1007') ON DUPLICATE KEY UPDATE subway_line_station_code = '1007'; -- 신도림 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 108, '0234') ON DUPLICATE KEY UPDATE subway_line_station_code = '0234'; -- 신도림 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (9, 109, '4129') ON DUPLICATE KEY UPDATE subway_line_station_code = '4129'; -- 봉은사 9호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 110, '0220') ON DUPLICATE KEY UPDATE subway_line_station_code = '0220'; -- 선릉 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 110, '1023') ON DUPLICATE KEY UPDATE subway_line_station_code = '1023'; -- 선릉 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (8, 111, '2817') ON DUPLICATE KEY UPDATE subway_line_station_code = '2817'; -- 송파 8호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (12, 112, '1320') ON DUPLICATE KEY UPDATE subway_line_station_code = '1320'; -- 대성리 경춘선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 113, '1004') ON DUPLICATE KEY UPDATE subway_line_station_code = '1004'; -- 노량진 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (9, 113, '4117') ON DUPLICATE KEY UPDATE subway_line_station_code = '4117'; -- 노량진 9호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 114, '0152') ON DUPLICATE KEY UPDATE subway_line_station_code = '0152'; -- 종각 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 115, '1207') ON DUPLICATE KEY UPDATE subway_line_station_code = '1207'; -- 양정 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (9, 117, '4136') ON DUPLICATE KEY UPDATE subway_line_station_code = '4136'; -- 올림픽공원 9호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 117, '2556') ON DUPLICATE KEY UPDATE subway_line_station_code = '2556'; -- 올림픽공원 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (6, 118, '2644') ON DUPLICATE KEY UPDATE subway_line_station_code = '2644'; -- 돌곶이 6호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 119, '0431') ON DUPLICATE KEY UPDATE subway_line_station_code = '0431'; -- 동작 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (9, 119, '4120') ON DUPLICATE KEY UPDATE subway_line_station_code = '4120'; -- 동작 9호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 120, '1204') ON DUPLICATE KEY UPDATE subway_line_station_code = '1204'; -- 양원 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 121, '1851') ON DUPLICATE KEY UPDATE subway_line_station_code = '1851'; -- 가천대 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 122, '1855') ON DUPLICATE KEY UPDATE subway_line_station_code = '1855'; -- 서현 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (3, 123, '1954') ON DUPLICATE KEY UPDATE subway_line_station_code = '1954'; -- 백석 3호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 124, '0243') ON DUPLICATE KEY UPDATE subway_line_station_code = '0243'; -- 충정로 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 124, '2532') ON DUPLICATE KEY UPDATE subway_line_station_code = '2532'; -- 충정로 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 125, '1847') ON DUPLICATE KEY UPDATE subway_line_station_code = '1847'; -- 서울숲 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (13, 126, '4213') ON DUPLICATE KEY UPDATE subway_line_station_code = '4213'; -- 인천공항1터미널 공항철도
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 127, '0223') ON DUPLICATE KEY UPDATE subway_line_station_code = '0223'; -- 교대 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (3, 127, '0330') ON DUPLICATE KEY UPDATE subway_line_station_code = '0330'; -- 교대 3호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (15, 129, '4811') ON DUPLICATE KEY UPDATE subway_line_station_code = '4811'; -- 달미 서해선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 130, '1874') ON DUPLICATE KEY UPDATE subway_line_station_code = '1874'; -- 오목천 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 132, '2534') ON DUPLICATE KEY UPDATE subway_line_station_code = '2534'; -- 광화문 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (6, 133, '2626') ON DUPLICATE KEY UPDATE subway_line_station_code = '2626'; -- 대흥 6호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (6, 134, '2625') ON DUPLICATE KEY UPDATE subway_line_station_code = '2625'; -- 광흥창 6호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (6, 135, '2629') ON DUPLICATE KEY UPDATE subway_line_station_code = '2629'; -- 삼각지 6호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 135, '0428') ON DUPLICATE KEY UPDATE subway_line_station_code = '0428'; -- 삼각지 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 136, '1027') ON DUPLICATE KEY UPDATE subway_line_station_code = '1027'; -- 개포동 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 138, '0225') ON DUPLICATE KEY UPDATE subway_line_station_code = '0225'; -- 방배 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 139, '1870') ON DUPLICATE KEY UPDATE subway_line_station_code = '1870'; -- 매탄권선 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 140, '1754') ON DUPLICATE KEY UPDATE subway_line_station_code = '1754'; -- 상록수 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 141, '1220') ON DUPLICATE KEY UPDATE subway_line_station_code = '1220'; -- 지평 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 143, '1203') ON DUPLICATE KEY UPDATE subway_line_station_code = '1203'; -- 망우 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (12, 143, '1310') ON DUPLICATE KEY UPDATE subway_line_station_code = '1310'; -- 망우 경춘선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 144, '0419') ON DUPLICATE KEY UPDATE subway_line_station_code = '0419'; -- 한성대입구 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 145, '1871') ON DUPLICATE KEY UPDATE subway_line_station_code = '1871'; -- 수원시청 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (15, 146, '4806') ON DUPLICATE KEY UPDATE subway_line_station_code = '4806'; -- 시흥대야 서해선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 147, '2539') ON DUPLICATE KEY UPDATE subway_line_station_code = '2539'; -- 신금호 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (10, 148, '1503') ON DUPLICATE KEY UPDATE subway_line_station_code = '1503'; -- 삼동 경강선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 149, '0418') ON DUPLICATE KEY UPDATE subway_line_station_code = '0418'; -- 성신여대입구 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (20, 149, '4711') ON DUPLICATE KEY UPDATE subway_line_station_code = '4711'; -- 성신여대입구 우이신설경전철
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 150, '1761') ON DUPLICATE KEY UPDATE subway_line_station_code = '1761'; -- 정왕 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 150, '1836') ON DUPLICATE KEY UPDATE subway_line_station_code = '1836'; -- 정왕 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (6, 151, '2640') ON DUPLICATE KEY UPDATE subway_line_station_code = '2640'; -- 안암 6호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (8, 152, '2813') ON DUPLICATE KEY UPDATE subway_line_station_code = '2813'; -- 강동구청 8호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (9, 153, '4118') ON DUPLICATE KEY UPDATE subway_line_station_code = '4118'; -- 노들 9호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 155, '1876') ON DUPLICATE KEY UPDATE subway_line_station_code = '1876'; -- 야목 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 156, '1006') ON DUPLICATE KEY UPDATE subway_line_station_code = '1006'; -- 영등포 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 157, '1810') ON DUPLICATE KEY UPDATE subway_line_station_code = '1810'; -- 제물포 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 158, '1711') ON DUPLICATE KEY UPDATE subway_line_station_code = '1711'; -- 성균관대 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (15, 159, '106C') ON DUPLICATE KEY UPDATE subway_line_station_code = '106C'; -- 백마 서해선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 159, '1273') ON DUPLICATE KEY UPDATE subway_line_station_code = '1273'; -- 백마 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 160, '1883') ON DUPLICATE KEY UPDATE subway_line_station_code = '1883'; -- 남동인더스파크 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 161, '1857') ON DUPLICATE KEY UPDATE subway_line_station_code = '1857'; -- 정자 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (18, 161, '4312') ON DUPLICATE KEY UPDATE subway_line_station_code = '4312'; -- 정자 신분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (9, 163, '4119') ON DUPLICATE KEY UPDATE subway_line_station_code = '4119'; -- 흑석 9호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (20, 164, '4712') ON DUPLICATE KEY UPDATE subway_line_station_code = '4712'; -- 보문 우이신설경전철
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (6, 164, '2639') ON DUPLICATE KEY UPDATE subway_line_station_code = '2639'; -- 보문 6호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (6, 166, '2633') ON DUPLICATE KEY UPDATE subway_line_station_code = '2633'; -- 버티고개 6호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 167, '1861') ON DUPLICATE KEY UPDATE subway_line_station_code = '1861'; -- 보정 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 168, '1009') ON DUPLICATE KEY UPDATE subway_line_station_code = '1009'; -- 서빙고 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 169, '1028') ON DUPLICATE KEY UPDATE subway_line_station_code = '1028'; -- 대모산입구 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 170, '1407') ON DUPLICATE KEY UPDATE subway_line_station_code = '1407'; -- 온양온천 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 171, '1869') ON DUPLICATE KEY UPDATE subway_line_station_code = '1869'; -- 망포 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 172, '1713') ON DUPLICATE KEY UPDATE subway_line_station_code = '1713'; -- 수원 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 172, '1846') ON DUPLICATE KEY UPDATE subway_line_station_code = '1846'; -- 수원 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 173, '2731') ON DUPLICATE KEY UPDATE subway_line_station_code = '2731'; -- 청담 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (10, 174, '1504') ON DUPLICATE KEY UPDATE subway_line_station_code = '1504'; -- 경기광주 경강선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 175, '1802') ON DUPLICATE KEY UPDATE subway_line_station_code = '1802'; -- 오류동 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (6, 176, '2624') ON DUPLICATE KEY UPDATE subway_line_station_code = '2624'; -- 상수 6호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (13, 178, '4217') ON DUPLICATE KEY UPDATE subway_line_station_code = '4217'; -- 영종 공항철도
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 180, '0228') ON DUPLICATE KEY UPDATE subway_line_station_code = '0228'; -- 서울대입구 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (12, 181, '1315') ON DUPLICATE KEY UPDATE subway_line_station_code = '1315'; -- 사릉 경춘선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (6, 182, '2611') ON DUPLICATE KEY UPDATE subway_line_station_code = '2611'; -- 응암 6호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 183, '2553') ON DUPLICATE KEY UPDATE subway_line_station_code = '2553'; -- 고덕 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 184, '1845') ON DUPLICATE KEY UPDATE subway_line_station_code = '1845'; -- 청량리 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 184, '0158') ON DUPLICATE KEY UPDATE subway_line_station_code = '0158'; -- 청량리 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 184, '1014') ON DUPLICATE KEY UPDATE subway_line_station_code = '1014'; -- 청량리 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (12, 184, '1306') ON DUPLICATE KEY UPDATE subway_line_station_code = '1306'; -- 청량리 경춘선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 186, '0236') ON DUPLICATE KEY UPDATE subway_line_station_code = '0236'; -- 영등포구청 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 186, '2524') ON DUPLICATE KEY UPDATE subway_line_station_code = '2524'; -- 영등포구청 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 187, '1862') ON DUPLICATE KEY UPDATE subway_line_station_code = '1862'; -- 죽전 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 188, '2515') ON DUPLICATE KEY UPDATE subway_line_station_code = '2515'; -- 마곡 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 189, '1749') ON DUPLICATE KEY UPDATE subway_line_station_code = '1749'; -- 서동탄 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 190, '2749') ON DUPLICATE KEY UPDATE subway_line_station_code = '2749'; -- 철산 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 191, '1212') ON DUPLICATE KEY UPDATE subway_line_station_code = '1212'; -- 양수 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 192, '1865') ON DUPLICATE KEY UPDATE subway_line_station_code = '1865'; -- 기흥 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 193, '0214') ON DUPLICATE KEY UPDATE subway_line_station_code = '0214'; -- 강변 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (9, 194, '4126') ON DUPLICATE KEY UPDATE subway_line_station_code = '4126'; -- 언주 9호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 195, '1868') ON DUPLICATE KEY UPDATE subway_line_station_code = '1868'; -- 영통 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 196, '1755') ON DUPLICATE KEY UPDATE subway_line_station_code = '1755'; -- 한대앞 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 196, '1830') ON DUPLICATE KEY UPDATE subway_line_station_code = '1830'; -- 한대앞 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (10, 197, '1506') ON DUPLICATE KEY UPDATE subway_line_station_code = '1506'; -- 곤지암 경강선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 198, '0427') ON DUPLICATE KEY UPDATE subway_line_station_code = '0427'; -- 숙대입구 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (9, 199, '4132') ON DUPLICATE KEY UPDATE subway_line_station_code = '4132'; -- 석촌고분 9호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (13, 201, '4204') ON DUPLICATE KEY UPDATE subway_line_station_code = '4204'; -- 디지털미디어시티 공항철도
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (6, 201, '2619') ON DUPLICATE KEY UPDATE subway_line_station_code = '2619'; -- 디지털미디어시티 6호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 201, '1266') ON DUPLICATE KEY UPDATE subway_line_station_code = '1266'; -- 디지털미디어시티 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (15, 202, '107C') ON DUPLICATE KEY UPDATE subway_line_station_code = '107C'; -- 풍산 서해선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 202, '1274') ON DUPLICATE KEY UPDATE subway_line_station_code = '1274'; -- 풍산 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (6, 203, '2621') ON DUPLICATE KEY UPDATE subway_line_station_code = '2621'; -- 마포구청 6호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (20, 204, '4704') ON DUPLICATE KEY UPDATE subway_line_station_code = '4704'; -- 가오리 우이신설경전철
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 205, '0231') ON DUPLICATE KEY UPDATE subway_line_station_code = '0231'; -- 신대방 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 206, '1215') ON DUPLICATE KEY UPDATE subway_line_station_code = '1215'; -- 아신 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 207, '1010') ON DUPLICATE KEY UPDATE subway_line_station_code = '1010'; -- 한남 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 209, '0207') ON DUPLICATE KEY UPDATE subway_line_station_code = '0207'; -- 상왕십리 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (9, 211, '4138') ON DUPLICATE KEY UPDATE subway_line_station_code = '4138'; -- 중앙보훈병원 9호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 212, '3759') ON DUPLICATE KEY UPDATE subway_line_station_code = '3759'; -- 삼산체육관 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (3, 213, '0332') ON DUPLICATE KEY UPDATE subway_line_station_code = '0332'; -- 양재 3호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (18, 213, '4308') ON DUPLICATE KEY UPDATE subway_line_station_code = '4308'; -- 양재 신분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (9, 214, '4133') ON DUPLICATE KEY UPDATE subway_line_station_code = '4133'; -- 석촌 9호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (8, 214, '2816') ON DUPLICATE KEY UPDATE subway_line_station_code = '2816'; -- 석촌 8호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 215, '1016') ON DUPLICATE KEY UPDATE subway_line_station_code = '1016'; -- 외대앞 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 216, '2751') ON DUPLICATE KEY UPDATE subway_line_station_code = '2751'; -- 천왕 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (3, 217, '1956') ON DUPLICATE KEY UPDATE subway_line_station_code = '1956'; -- 정발산 3호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 218, '1816') ON DUPLICATE KEY UPDATE subway_line_station_code = '1816'; -- 간석 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 219, '102C') ON DUPLICATE KEY UPDATE subway_line_station_code = '102C'; -- 왕십리 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 219, '1013') ON DUPLICATE KEY UPDATE subway_line_station_code = '1013'; -- 왕십리 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 219, '2541') ON DUPLICATE KEY UPDATE subway_line_station_code = '2541'; -- 왕십리 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 219, '0208') ON DUPLICATE KEY UPDATE subway_line_station_code = '0208'; -- 왕십리 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (9, 220, '4107') ON DUPLICATE KEY UPDATE subway_line_station_code = '4107'; -- 가양 9호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 221, '2563') ON DUPLICATE KEY UPDATE subway_line_station_code = '2563'; -- 미사 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 222, '1759') ON DUPLICATE KEY UPDATE subway_line_station_code = '1759'; -- 안산 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 222, '1834') ON DUPLICATE KEY UPDATE subway_line_station_code = '1834'; -- 안산 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (3, 224, '0338') ON DUPLICATE KEY UPDATE subway_line_station_code = '0338'; -- 일원 3호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 225, '2729') ON DUPLICATE KEY UPDATE subway_line_station_code = '2729'; -- 건대입구 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 225, '0212') ON DUPLICATE KEY UPDATE subway_line_station_code = '0212'; -- 건대입구 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 226, '0227') ON DUPLICATE KEY UPDATE subway_line_station_code = '0227'; -- 낙성대 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 229, '1882') ON DUPLICATE KEY UPDATE subway_line_station_code = '1882'; -- 호구포 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 230, '1011') ON DUPLICATE KEY UPDATE subway_line_station_code = '1011'; -- 옥수 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (3, 230, '0325') ON DUPLICATE KEY UPDATE subway_line_station_code = '0325'; -- 옥수 3호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 231, '2735') ON DUPLICATE KEY UPDATE subway_line_station_code = '2735'; -- 반포 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 232, '1853') ON DUPLICATE KEY UPDATE subway_line_station_code = '1853'; -- 모란 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (8, 232, '2827') ON DUPLICATE KEY UPDATE subway_line_station_code = '2827'; -- 모란 8호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 233, '2523') ON DUPLICATE KEY UPDATE subway_line_station_code = '2523'; -- 양평 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 233, '1217') ON DUPLICATE KEY UPDATE subway_line_station_code = '1217'; -- 양평 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (18, 234, '4319') ON DUPLICATE KEY UPDATE subway_line_station_code = '4319'; -- 광교 신분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 236, '1408') ON DUPLICATE KEY UPDATE subway_line_station_code = '1408'; -- 신창 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 237, '0411') ON DUPLICATE KEY UPDATE subway_line_station_code = '0411'; -- 노원 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 237, '2715') ON DUPLICATE KEY UPDATE subway_line_station_code = '2715'; -- 노원 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 238, '1252') ON DUPLICATE KEY UPDATE subway_line_station_code = '1252'; -- 신촌 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 238, '0240') ON DUPLICATE KEY UPDATE subway_line_station_code = '0240'; -- 신촌 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (9, 240, '4121') ON DUPLICATE KEY UPDATE subway_line_station_code = '4121'; -- 구반포 9호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (6, 241, '2618') ON DUPLICATE KEY UPDATE subway_line_station_code = '2618'; -- 증산 6호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 242, '1012') ON DUPLICATE KEY UPDATE subway_line_station_code = '1012'; -- 응봉 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 243, '1852') ON DUPLICATE KEY UPDATE subway_line_station_code = '1852'; -- 태평 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (3, 244, '0337') ON DUPLICATE KEY UPDATE subway_line_station_code = '0337'; -- 대청 3호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 245, '1405') ON DUPLICATE KEY UPDATE subway_line_station_code = '1405'; -- 배방 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 246, '2525') ON DUPLICATE KEY UPDATE subway_line_station_code = '2525'; -- 영등포시장 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (15, 247, '4808') ON DUPLICATE KEY UPDATE subway_line_station_code = '4808'; -- 신현 서해선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 248, '2550') ON DUPLICATE KEY UPDATE subway_line_station_code = '2550'; -- 길동 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 249, '1808') ON DUPLICATE KEY UPDATE subway_line_station_code = '1808'; -- 동암 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (8, 251, '2814') ON DUPLICATE KEY UPDATE subway_line_station_code = '2814'; -- 몽촌토성 8호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 252, '1854') ON DUPLICATE KEY UPDATE subway_line_station_code = '1854'; -- 야탑 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (3, 253, '1948') ON DUPLICATE KEY UPDATE subway_line_station_code = '1948'; -- 원흥 3호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 256, '0248') ON DUPLICATE KEY UPDATE subway_line_station_code = '0248'; -- 양천구청 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 257, '3760') ON DUPLICATE KEY UPDATE subway_line_station_code = '3760'; -- 굴포천 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (12, 258, '1319') ON DUPLICATE KEY UPDATE subway_line_station_code = '1319'; -- 마석 경춘선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 259, '1703') ON DUPLICATE KEY UPDATE subway_line_station_code = '1703'; -- 금천구청 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (9, 261, '4130') ON DUPLICATE KEY UPDATE subway_line_station_code = '4130'; -- 종합운동장 9호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 261, '0218') ON DUPLICATE KEY UPDATE subway_line_station_code = '0218'; -- 종합운동장 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 262, '1456') ON DUPLICATE KEY UPDATE subway_line_station_code = '1456'; -- 평촌 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (6, 263, '2632') ON DUPLICATE KEY UPDATE subway_line_station_code = '2632'; -- 한강진 6호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 266, '0429') ON DUPLICATE KEY UPDATE subway_line_station_code = '0429'; -- 신용산 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (6, 267, '2647') ON DUPLICATE KEY UPDATE subway_line_station_code = '2647'; -- 화랑대 6호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (10, 268, '1510') ON DUPLICATE KEY UPDATE subway_line_station_code = '1510'; -- 세종대왕릉 경강선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 269, '0232') ON DUPLICATE KEY UPDATE subway_line_station_code = '0232'; -- 구로디지털단지 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 270, '1270') ON DUPLICATE KEY UPDATE subway_line_station_code = '1270'; -- 행신 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 271, '2528') ON DUPLICATE KEY UPDATE subway_line_station_code = '2528'; -- 여의나루 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (18, 272, '4318') ON DUPLICATE KEY UPDATE subway_line_station_code = '4318'; -- 광교중앙 신분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 273, '1208') ON DUPLICATE KEY UPDATE subway_line_station_code = '1208'; -- 덕소 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 274, '0432') ON DUPLICATE KEY UPDATE subway_line_station_code = '0432'; -- 총신대입구 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 277, '3757') ON DUPLICATE KEY UPDATE subway_line_station_code = '3757'; -- 부천시청 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (9, 278, '4106') ON DUPLICATE KEY UPDATE subway_line_station_code = '4106'; -- 양천향교 9호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (9, 279, '4109') ON DUPLICATE KEY UPDATE subway_line_station_code = '4109'; -- 등촌 9호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 280, '1020') ON DUPLICATE KEY UPDATE subway_line_station_code = '1020'; -- 월계 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (3, 282, '0331') ON DUPLICATE KEY UPDATE subway_line_station_code = '0331'; -- 남부터미널 3호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (15, 283, '4813') ON DUPLICATE KEY UPDATE subway_line_station_code = '4813'; -- 초지 서해선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 283, '1758') ON DUPLICATE KEY UPDATE subway_line_station_code = '1758'; -- 초지 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 283, '1833') ON DUPLICATE KEY UPDATE subway_line_station_code = '1833'; -- 초지 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 284, '2725') ON DUPLICATE KEY UPDATE subway_line_station_code = '2725'; -- 용마산 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 285, '1910') ON DUPLICATE KEY UPDATE subway_line_station_code = '1910'; -- 덕계 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 287, '2559') ON DUPLICATE KEY UPDATE subway_line_station_code = '2559'; -- 개롱 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (12, 288, '1318') ON DUPLICATE KEY UPDATE subway_line_station_code = '1318'; -- 천마산 경춘선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 289, '1872') ON DUPLICATE KEY UPDATE subway_line_station_code = '1872'; -- 매교 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 290, '2522') ON DUPLICATE KEY UPDATE subway_line_station_code = '2522'; -- 오목교 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 291, '1019') ON DUPLICATE KEY UPDATE subway_line_station_code = '1019'; -- 광운대 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (12, 291, '1305') ON DUPLICATE KEY UPDATE subway_line_station_code = '1305'; -- 광운대 경춘선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 292, '1909') ON DUPLICATE KEY UPDATE subway_line_station_code = '1909'; -- 양주 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (3, 293, '0328') ON DUPLICATE KEY UPDATE subway_line_station_code = '0328'; -- 잠원 3호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 294, '1866') ON DUPLICATE KEY UPDATE subway_line_station_code = '1866'; -- 상갈 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 295, '1811') ON DUPLICATE KEY UPDATE subway_line_station_code = '1811'; -- 동인천 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 296, '2546') ON DUPLICATE KEY UPDATE subway_line_station_code = '2546'; -- 아차산 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 297, '1815') ON DUPLICATE KEY UPDATE subway_line_station_code = '1815'; -- 부개 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 300, '1880') ON DUPLICATE KEY UPDATE subway_line_station_code = '1880'; -- 소래포구 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (12, 301, '1313') ON DUPLICATE KEY UPDATE subway_line_station_code = '1313'; -- 별내 경춘선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (8, 301, '2805') ON DUPLICATE KEY UPDATE subway_line_station_code = '2805'; -- 별내 8호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (18, 302, '4314') ON DUPLICATE KEY UPDATE subway_line_station_code = '4314'; -- 동천 신분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 303, '2521') ON DUPLICATE KEY UPDATE subway_line_station_code = '2521'; -- 목동 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (3, 304, '0317') ON DUPLICATE KEY UPDATE subway_line_station_code = '0317'; -- 경복궁 3호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 305, '0405') ON DUPLICATE KEY UPDATE subway_line_station_code = '0405'; -- 진접 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 306, '1809') ON DUPLICATE KEY UPDATE subway_line_station_code = '1809'; -- 주안 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 307, '1801') ON DUPLICATE KEY UPDATE subway_line_station_code = '1801'; -- 개봉 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 308, '1452') ON DUPLICATE KEY UPDATE subway_line_station_code = '1452'; -- 대공원 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 309, '2533') ON DUPLICATE KEY UPDATE subway_line_station_code = '2533'; -- 서대문 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 310, '1282') ON DUPLICATE KEY UPDATE subway_line_station_code = '1282'; -- 월롱 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (15, 311, '4804') ON DUPLICATE KEY UPDATE subway_line_station_code = '4804'; -- 소사 서해선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 311, '1814') ON DUPLICATE KEY UPDATE subway_line_station_code = '1814'; -- 소사 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 312, '2529') ON DUPLICATE KEY UPDATE subway_line_station_code = '2529'; -- 마포 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 313, '2565') ON DUPLICATE KEY UPDATE subway_line_station_code = '2565'; -- 하남시청 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 314, '1719') ON DUPLICATE KEY UPDATE subway_line_station_code = '1719'; -- 오산 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (18, 316, '4317') ON DUPLICATE KEY UPDATE subway_line_station_code = '4317'; -- 상현 신분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (6, 318, '2630') ON DUPLICATE KEY UPDATE subway_line_station_code = '2630'; -- 녹사평 6호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 319, '2527') ON DUPLICATE KEY UPDATE subway_line_station_code = '2527'; -- 여의도 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (9, 319, '4115') ON DUPLICATE KEY UPDATE subway_line_station_code = '4115'; -- 여의도 9호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 320, '1875') ON DUPLICATE KEY UPDATE subway_line_station_code = '1875'; -- 어천 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (8, 321, '2828') ON DUPLICATE KEY UPDATE subway_line_station_code = '2828'; -- 남위례 8호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 323, '1728') ON DUPLICATE KEY UPDATE subway_line_station_code = '1728'; -- 천안 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 324, '2739') ON DUPLICATE KEY UPDATE subway_line_station_code = '2739'; -- 남성 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 325, '0244') ON DUPLICATE KEY UPDATE subway_line_station_code = '0244'; -- 용답 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (6, 326, '2616') ON DUPLICATE KEY UPDATE subway_line_station_code = '2616'; -- 구산 6호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (3, 328, '0316') ON DUPLICATE KEY UPDATE subway_line_station_code = '0316'; -- 독립문 3호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (20, 329, '4709') ON DUPLICATE KEY UPDATE subway_line_station_code = '4709'; -- 북한산보국문 우이신설경전철
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 330, '0219') ON DUPLICATE KEY UPDATE subway_line_station_code = '0219'; -- 삼성 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 331, '0420') ON DUPLICATE KEY UPDATE subway_line_station_code = '0420'; -- 혜화 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (6, 333, '2643') ON DUPLICATE KEY UPDATE subway_line_station_code = '2643'; -- 상월곡 6호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 334, '1902') ON DUPLICATE KEY UPDATE subway_line_station_code = '1902'; -- 도봉 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (3, 335, '0322') ON DUPLICATE KEY UPDATE subway_line_station_code = '0322'; -- 동대입구 3호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (15, 336, '4810') ON DUPLICATE KEY UPDATE subway_line_station_code = '4810'; -- 시흥능곡 서해선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (20, 337, '4710') ON DUPLICATE KEY UPDATE subway_line_station_code = '4710'; -- 정릉 우이신설경전철
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (15, 338, '4815') ON DUPLICATE KEY UPDATE subway_line_station_code = '4815'; -- 원시 서해선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 339, '1822') ON DUPLICATE KEY UPDATE subway_line_station_code = '1822'; -- 중동 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 340, '0415') ON DUPLICATE KEY UPDATE subway_line_station_code = '0415'; -- 미아 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (15, 341, '4812') ON DUPLICATE KEY UPDATE subway_line_station_code = '4812'; -- 선부 서해선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 342, '0215') ON DUPLICATE KEY UPDATE subway_line_station_code = '0215'; -- 잠실나루 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 343, '1017') ON DUPLICATE KEY UPDATE subway_line_station_code = '1017'; -- 신이문 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (13, 344, '4208') ON DUPLICATE KEY UPDATE subway_line_station_code = '4208'; -- 계양 공항철도
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 345, '1726') ON DUPLICATE KEY UPDATE subway_line_station_code = '1726'; -- 직산 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 346, '2743') ON DUPLICATE KEY UPDATE subway_line_station_code = '2743'; -- 신대방삼거리 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 347, '1205') ON DUPLICATE KEY UPDATE subway_line_station_code = '1205'; -- 구리 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (8, 347, '2808') ON DUPLICATE KEY UPDATE subway_line_station_code = '2808'; -- 구리 8호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 348, '1008') ON DUPLICATE KEY UPDATE subway_line_station_code = '1008'; -- 이촌 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 348, '0430') ON DUPLICATE KEY UPDATE subway_line_station_code = '0430'; -- 이촌 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 349, '1280') ON DUPLICATE KEY UPDATE subway_line_station_code = '1280'; -- 금촌 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 350, '2544') ON DUPLICATE KEY UPDATE subway_line_station_code = '2544'; -- 장한평 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (9, 351, '4101') ON DUPLICATE KEY UPDATE subway_line_station_code = '4101'; -- 개화 9호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 352, '0250') ON DUPLICATE KEY UPDATE subway_line_station_code = '0250'; -- 용두 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 353, '0209') ON DUPLICATE KEY UPDATE subway_line_station_code = '0209'; -- 한양대 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 354, '0201') ON DUPLICATE KEY UPDATE subway_line_station_code = '0201'; -- 시청 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 354, '0151') ON DUPLICATE KEY UPDATE subway_line_station_code = '0151'; -- 시청 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 355, '1717') ON DUPLICATE KEY UPDATE subway_line_station_code = '1717'; -- 세마 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 356, '1276') ON DUPLICATE KEY UPDATE subway_line_station_code = '1276'; -- 탄현 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 357, '2728') ON DUPLICATE KEY UPDATE subway_line_station_code = '2728'; -- 어린이대공원 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (12, 358, '1324') ON DUPLICATE KEY UPDATE subway_line_station_code = '1324'; -- 굴봉산 경춘선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 359, '1277') ON DUPLICATE KEY UPDATE subway_line_station_code = '1277'; -- 야당 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (3, 360, '0329') ON DUPLICATE KEY UPDATE subway_line_station_code = '0329'; -- 고속터미널 3호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (9, 360, '4123') ON DUPLICATE KEY UPDATE subway_line_station_code = '4123'; -- 고속터미널 9호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 360, '2736') ON DUPLICATE KEY UPDATE subway_line_station_code = '2736'; -- 고속터미널 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (6, 362, '2641') ON DUPLICATE KEY UPDATE subway_line_station_code = '2641'; -- 고려대 6호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 363, '3754') ON DUPLICATE KEY UPDATE subway_line_station_code = '3754'; -- 부천종합운동장 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (15, 363, '1982') ON DUPLICATE KEY UPDATE subway_line_station_code = '1982'; -- 부천종합운동장 서해선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (9, 364, '4122') ON DUPLICATE KEY UPDATE subway_line_station_code = '4122'; -- 신반포 9호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (12, 365, '1328') ON DUPLICATE KEY UPDATE subway_line_station_code = '1328'; -- 남춘천 경춘선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 366, '0417') ON DUPLICATE KEY UPDATE subway_line_station_code = '0417'; -- 길음 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 367, '2742') ON DUPLICATE KEY UPDATE subway_line_station_code = '2742'; -- 장승배기 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 368, '2548') ON DUPLICATE KEY UPDATE subway_line_station_code = '2548'; -- 천호 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (8, 368, '2812') ON DUPLICATE KEY UPDATE subway_line_station_code = '2812'; -- 천호 8호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 369, '1213') ON DUPLICATE KEY UPDATE subway_line_station_code = '1213'; -- 신원 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (8, 370, '2819') ON DUPLICATE KEY UPDATE subway_line_station_code = '2819'; -- 문정 8호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (12, 371, '1327') ON DUPLICATE KEY UPDATE subway_line_station_code = '1327'; -- 김유정 경춘선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 373, '2721') ON DUPLICATE KEY UPDATE subway_line_station_code = '2721'; -- 중화 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 375, '2564') ON DUPLICATE KEY UPDATE subway_line_station_code = '2564'; -- 하남풍산 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 376, '1716') ON DUPLICATE KEY UPDATE subway_line_station_code = '1716'; -- 병점 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 377, '1022') ON DUPLICATE KEY UPDATE subway_line_station_code = '1022'; -- 창동 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 377, '0412') ON DUPLICATE KEY UPDATE subway_line_station_code = '0412'; -- 창동 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 378, '1712') ON DUPLICATE KEY UPDATE subway_line_station_code = '1712'; -- 화서 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 379, '1906') ON DUPLICATE KEY UPDATE subway_line_station_code = '1906'; -- 의정부 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (13, 380, '4209') ON DUPLICATE KEY UPDATE subway_line_station_code = '4209'; -- 검암 공항철도
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 381, '1886') ON DUPLICATE KEY UPDATE subway_line_station_code = '1886'; -- 송도 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 382, '1458') ON DUPLICATE KEY UPDATE subway_line_station_code = '1458'; -- 금정 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 382, '1708') ON DUPLICATE KEY UPDATE subway_line_station_code = '1708'; -- 금정 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 383, '1720') ON DUPLICATE KEY UPDATE subway_line_station_code = '1720'; -- 진위 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (12, 384, '1312') ON DUPLICATE KEY UPDATE subway_line_station_code = '1312'; -- 갈매 경춘선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 386, '1884') ON DUPLICATE KEY UPDATE subway_line_station_code = '1884'; -- 원인재 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (3, 387, '1952') ON DUPLICATE KEY UPDATE subway_line_station_code = '1952'; -- 화정 3호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (9, 388, '4110') ON DUPLICATE KEY UPDATE subway_line_station_code = '4110'; -- 염창 9호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (6, 390, '2642') ON DUPLICATE KEY UPDATE subway_line_station_code = '2642'; -- 월곡 6호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (18, 391, '4306') ON DUPLICATE KEY UPDATE subway_line_station_code = '4306'; -- 신논현 신분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (9, 391, '4125') ON DUPLICATE KEY UPDATE subway_line_station_code = '4125'; -- 신논현 9호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (3, 392, '0340') ON DUPLICATE KEY UPDATE subway_line_station_code = '0340'; -- 가락시장 3호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (8, 392, '2818') ON DUPLICATE KEY UPDATE subway_line_station_code = '2818'; -- 가락시장 8호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 393, '2552') ON DUPLICATE KEY UPDATE subway_line_station_code = '2552'; -- 명일 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 394, '1272') ON DUPLICATE KEY UPDATE subway_line_station_code = '1272'; -- 곡산 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (15, 394, '105C') ON DUPLICATE KEY UPDATE subway_line_station_code = '105C'; -- 곡산 서해선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 395, '1453') ON DUPLICATE KEY UPDATE subway_line_station_code = '1453'; -- 과천 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 396, '1026') ON DUPLICATE KEY UPDATE subway_line_station_code = '1026'; -- 구룡 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 397, '0157') ON DUPLICATE KEY UPDATE subway_line_station_code = '0157'; -- 제기동 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 398, '0246') ON DUPLICATE KEY UPDATE subway_line_station_code = '0246'; -- 신설동 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (20, 398, '4713') ON DUPLICATE KEY UPDATE subway_line_station_code = '4713'; -- 신설동 우이신설경전철
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 398, '0156') ON DUPLICATE KEY UPDATE subway_line_station_code = '0156'; -- 신설동 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 399, '2540') ON DUPLICATE KEY UPDATE subway_line_station_code = '2540'; -- 행당 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 400, '2561') ON DUPLICATE KEY UPDATE subway_line_station_code = '2561'; -- 마천 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 401, '2526') ON DUPLICATE KEY UPDATE subway_line_station_code = '2526'; -- 신길 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 401, '1032') ON DUPLICATE KEY UPDATE subway_line_station_code = '1032'; -- 신길 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 402, '1404') ON DUPLICATE KEY UPDATE subway_line_station_code = '1404'; -- 탕정 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 403, '0226') ON DUPLICATE KEY UPDATE subway_line_station_code = '0226'; -- 사당 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 403, '0433') ON DUPLICATE KEY UPDATE subway_line_station_code = '0433'; -- 사당 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (10, 404, '1505') ON DUPLICATE KEY UPDATE subway_line_station_code = '1505'; -- 초월 경강선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 405, '1805') ON DUPLICATE KEY UPDATE subway_line_station_code = '1805'; -- 송내 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 406, '0242') ON DUPLICATE KEY UPDATE subway_line_station_code = '0242'; -- 아현 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (6, 407, '2622') ON DUPLICATE KEY UPDATE subway_line_station_code = '2622'; -- 망원 6호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 408, '0155') ON DUPLICATE KEY UPDATE subway_line_station_code = '0155'; -- 동대문 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 408, '0421') ON DUPLICATE KEY UPDATE subway_line_station_code = '0421'; -- 동대문 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 409, '1881') ON DUPLICATE KEY UPDATE subway_line_station_code = '1881'; -- 인천논현 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 410, '1403') ON DUPLICATE KEY UPDATE subway_line_station_code = '1403'; -- 아산 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (12, 411, '1321') ON DUPLICATE KEY UPDATE subway_line_station_code = '1321'; -- 청평 경춘선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 412, '1005') ON DUPLICATE KEY UPDATE subway_line_station_code = '1005'; -- 대방 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (18, 413, '4310') ON DUPLICATE KEY UPDATE subway_line_station_code = '4310'; -- 청계산입구 신분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 414, '2712') ON DUPLICATE KEY UPDATE subway_line_station_code = '2712'; -- 도봉산 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 414, '1903') ON DUPLICATE KEY UPDATE subway_line_station_code = '1903'; -- 도봉산 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 415, '1760') ON DUPLICATE KEY UPDATE subway_line_station_code = '1760'; -- 신길온천 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 415, '1835') ON DUPLICATE KEY UPDATE subway_line_station_code = '1835'; -- 신길온천 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 416, '2734') ON DUPLICATE KEY UPDATE subway_line_station_code = '2734'; -- 논현 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (18, 416, '4305') ON DUPLICATE KEY UPDATE subway_line_station_code = '4305'; -- 논현 신분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 418, '3758') ON DUPLICATE KEY UPDATE subway_line_station_code = '3758'; -- 상동 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (13, 420, '4212') ON DUPLICATE KEY UPDATE subway_line_station_code = '4212'; -- 공항화물청사 공항철도
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 421, '1859') ON DUPLICATE KEY UPDATE subway_line_station_code = '1859'; -- 오리 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 422, '2750') ON DUPLICATE KEY UPDATE subway_line_station_code = '2750'; -- 광명사거리 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (12, 423, '1309') ON DUPLICATE KEY UPDATE subway_line_station_code = '1309'; -- 상봉 경춘선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 423, '2722') ON DUPLICATE KEY UPDATE subway_line_station_code = '2722'; -- 상봉 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 423, '1202') ON DUPLICATE KEY UPDATE subway_line_station_code = '1202'; -- 상봉 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 425, '2538') ON DUPLICATE KEY UPDATE subway_line_station_code = '2538'; -- 청구 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (6, 425, '2635') ON DUPLICATE KEY UPDATE subway_line_station_code = '2635'; -- 청구 6호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 427, '1286') ON DUPLICATE KEY UPDATE subway_line_station_code = '1286'; -- 운천 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (8, 429, '2811') ON DUPLICATE KEY UPDATE subway_line_station_code = '2811'; -- 암사 8호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 430, '2519') ON DUPLICATE KEY UPDATE subway_line_station_code = '2519'; -- 까치산 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 430, '0200') ON DUPLICATE KEY UPDATE subway_line_station_code = '0200'; -- 까치산 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 431, '2511') ON DUPLICATE KEY UPDATE subway_line_station_code = '2511'; -- 방화 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 432, '2514') ON DUPLICATE KEY UPDATE subway_line_station_code = '2514'; -- 송정 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (9, 433, '4131') ON DUPLICATE KEY UPDATE subway_line_station_code = '4131'; -- 삼전 9호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 434, '1729') ON DUPLICATE KEY UPDATE subway_line_station_code = '1729'; -- 당정 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (3, 436, '0314') ON DUPLICATE KEY UPDATE subway_line_station_code = '0314'; -- 홍제 3호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 437, '2740') ON DUPLICATE KEY UPDATE subway_line_station_code = '2740'; -- 숭실대입구 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 438, '1823') ON DUPLICATE KEY UPDATE subway_line_station_code = '1823'; -- 도화 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (10, 439, '1511') ON DUPLICATE KEY UPDATE subway_line_station_code = '1511'; -- 여주 경강선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 440, '0241') ON DUPLICATE KEY UPDATE subway_line_station_code = '0241'; -- 이대 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (9, 441, '4113') ON DUPLICATE KEY UPDATE subway_line_station_code = '4113'; -- 당산 9호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 441, '0237') ON DUPLICATE KEY UPDATE subway_line_station_code = '0237'; -- 당산 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (9, 442, '4137') ON DUPLICATE KEY UPDATE subway_line_station_code = '4137'; -- 둔촌오륜 9호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 443, '1002') ON DUPLICATE KEY UPDATE subway_line_station_code = '1002'; -- 남영 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 444, '1704') ON DUPLICATE KEY UPDATE subway_line_station_code = '1704'; -- 석수 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 445, '1219') ON DUPLICATE KEY UPDATE subway_line_station_code = '1219'; -- 용문 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (15, 446, '1981') ON DUPLICATE KEY UPDATE subway_line_station_code = '1981'; -- 원종 서해선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 447, '0159') ON DUPLICATE KEY UPDATE subway_line_station_code = '0159'; -- 동묘앞 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (6, 447, '2637') ON DUPLICATE KEY UPDATE subway_line_station_code = '2637'; -- 동묘앞 6호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 448, '2737') ON DUPLICATE KEY UPDATE subway_line_station_code = '2737'; -- 내방 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (8, 449, '2824') ON DUPLICATE KEY UPDATE subway_line_station_code = '2824'; -- 단대오거리 8호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 450, '1211') ON DUPLICATE KEY UPDATE subway_line_station_code = '1211'; -- 운길산 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (3, 451, '0313') ON DUPLICATE KEY UPDATE subway_line_station_code = '0313'; -- 녹번 3호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 452, '1807') ON DUPLICATE KEY UPDATE subway_line_station_code = '1807'; -- 백운 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 454, '0434') ON DUPLICATE KEY UPDATE subway_line_station_code = '0434'; -- 남태령 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 456, '1863') ON DUPLICATE KEY UPDATE subway_line_station_code = '1863'; -- 구성 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (10, 457, '1508') ON DUPLICATE KEY UPDATE subway_line_station_code = '1508'; -- 이천 경강선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (8, 459, '2825') ON DUPLICATE KEY UPDATE subway_line_station_code = '2825'; -- 신흥 8호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 460, '1813') ON DUPLICATE KEY UPDATE subway_line_station_code = '1813'; -- 구일 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 461, '1752') ON DUPLICATE KEY UPDATE subway_line_station_code = '1752'; -- 대야미 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 463, '2558') ON DUPLICATE KEY UPDATE subway_line_station_code = '2558'; -- 오금 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (3, 463, '0342') ON DUPLICATE KEY UPDATE subway_line_station_code = '0342'; -- 오금 3호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 464, '1267') ON DUPLICATE KEY UPDATE subway_line_station_code = '1267'; -- 수색 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 465, '1750') ON DUPLICATE KEY UPDATE subway_line_station_code = '1750'; -- 광명 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (15, 467, '4805') ON DUPLICATE KEY UPDATE subway_line_station_code = '4805'; -- 소새울 서해선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 469, '2719') ON DUPLICATE KEY UPDATE subway_line_station_code = '2719'; -- 태릉입구 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (6, 469, '2646') ON DUPLICATE KEY UPDATE subway_line_station_code = '2646'; -- 태릉입구 6호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 470, '0217') ON DUPLICATE KEY UPDATE subway_line_station_code = '0217'; -- 잠실새내 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 471, '2718') ON DUPLICATE KEY UPDATE subway_line_station_code = '2718'; -- 공릉 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 472, '1279') ON DUPLICATE KEY UPDATE subway_line_station_code = '1279'; -- 금릉 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 473, '2726') ON DUPLICATE KEY UPDATE subway_line_station_code = '2726'; -- 중곡 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 474, '1715') ON DUPLICATE KEY UPDATE subway_line_station_code = '1715'; -- 세류 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 475, '2716') ON DUPLICATE KEY UPDATE subway_line_station_code = '2716'; -- 중계 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 476, '1003') ON DUPLICATE KEY UPDATE subway_line_station_code = '1003'; -- 용산 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 476, '100C') ON DUPLICATE KEY UPDATE subway_line_station_code = '100C'; -- 용산 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (3, 477, '1957') ON DUPLICATE KEY UPDATE subway_line_station_code = '1957'; -- 주엽 3호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (3, 478, '0315') ON DUPLICATE KEY UPDATE subway_line_station_code = '0315'; -- 무악재 3호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (8, 479, '2823') ON DUPLICATE KEY UPDATE subway_line_station_code = '2823'; -- 남한산성입구 8호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 480, '1263') ON DUPLICATE KEY UPDATE subway_line_station_code = '1263'; -- 서강대 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 481, '1913') ON DUPLICATE KEY UPDATE subway_line_station_code = '1913'; -- 동두천중앙 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 482, '1908') ON DUPLICATE KEY UPDATE subway_line_station_code = '1908'; -- 녹양 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 483, '1723') ON DUPLICATE KEY UPDATE subway_line_station_code = '1723'; -- 평택지제 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 484, '2562') ON DUPLICATE KEY UPDATE subway_line_station_code = '2562'; -- 강일 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 485, '1718') ON DUPLICATE KEY UPDATE subway_line_station_code = '1718'; -- 오산대 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 486, '1402') ON DUPLICATE KEY UPDATE subway_line_station_code = '1402'; -- 쌍용 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (9, 487, '4104') ON DUPLICATE KEY UPDATE subway_line_station_code = '4104'; -- 신방화 9호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (6, 488, '2614') ON DUPLICATE KEY UPDATE subway_line_station_code = '2614'; -- 독바위 6호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 489, '1804') ON DUPLICATE KEY UPDATE subway_line_station_code = '1804'; -- 부천 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (8, 490, '2826') ON DUPLICATE KEY UPDATE subway_line_station_code = '2826'; -- 수진 8호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 492, '2720') ON DUPLICATE KEY UPDATE subway_line_station_code = '2720'; -- 먹골 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 493, '1454') ON DUPLICATE KEY UPDATE subway_line_station_code = '1454'; -- 정부과천청사 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (18, 494, '4315') ON DUPLICATE KEY UPDATE subway_line_station_code = '4315'; -- 수지구청 신분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 495, '0249') ON DUPLICATE KEY UPDATE subway_line_station_code = '0249'; -- 신정네거리 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 496, '1727') ON DUPLICATE KEY UPDATE subway_line_station_code = '1727'; -- 두정 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (3, 497, '0312') ON DUPLICATE KEY UPDATE subway_line_station_code = '0312'; -- 불광 3호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (6, 497, '2613') ON DUPLICATE KEY UPDATE subway_line_station_code = '2613'; -- 불광 6호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (12, 498, '1316') ON DUPLICATE KEY UPDATE subway_line_station_code = '1316'; -- 금곡 경춘선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 499, '0425') ON DUPLICATE KEY UPDATE subway_line_station_code = '0425'; -- 회현 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 500, '1455') ON DUPLICATE KEY UPDATE subway_line_station_code = '1455'; -- 인덕원 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (9, 501, '4112') ON DUPLICATE KEY UPDATE subway_line_station_code = '4112'; -- 선유도 9호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 502, '1901') ON DUPLICATE KEY UPDATE subway_line_station_code = '1901'; -- 방학 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 503, '0422') ON DUPLICATE KEY UPDATE subway_line_station_code = '0422'; -- 동대문역사문화공원 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 503, '0205') ON DUPLICATE KEY UPDATE subway_line_station_code = '0205'; -- 동대문역사문화공원 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 503, '2537') ON DUPLICATE KEY UPDATE subway_line_station_code = '2537'; -- 동대문역사문화공원 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 505, '1890') ON DUPLICATE KEY UPDATE subway_line_station_code = '1890'; -- 신포 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 506, '1209') ON DUPLICATE KEY UPDATE subway_line_station_code = '1209'; -- 도심 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 507, '2713') ON DUPLICATE KEY UPDATE subway_line_station_code = '2713'; -- 수락산 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (10, 510, '1509') ON DUPLICATE KEY UPDATE subway_line_station_code = '1509'; -- 부발 경강선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (3, 512, '1950') ON DUPLICATE KEY UPDATE subway_line_station_code = '1950'; -- 삼송 3호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (6, 514, '2612') ON DUPLICATE KEY UPDATE subway_line_station_code = '2612'; -- 역촌 6호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (3, 516, '0311') ON DUPLICATE KEY UPDATE subway_line_station_code = '0311'; -- 연신내 3호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (6, 516, '2615') ON DUPLICATE KEY UPDATE subway_line_station_code = '2615'; -- 연신내 6호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (20, 517, '4706') ON DUPLICATE KEY UPDATE subway_line_station_code = '4706'; -- 삼양 우이신설경전철
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 519, '1707') ON DUPLICATE KEY UPDATE subway_line_station_code = '1707'; -- 명학 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 520, '2554') ON DUPLICATE KEY UPDATE subway_line_station_code = '2554'; -- 상일동 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (13, 521, '4206') ON DUPLICATE KEY UPDATE subway_line_station_code = '4206'; -- 마곡나루 공항철도
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (9, 521, '4105') ON DUPLICATE KEY UPDATE subway_line_station_code = '4105'; -- 마곡나루 9호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 522, '0413') ON DUPLICATE KEY UPDATE subway_line_station_code = '0413'; -- 쌍문 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (6, 523, '2645') ON DUPLICATE KEY UPDATE subway_line_station_code = '2645'; -- 석계 6호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 523, '1018') ON DUPLICATE KEY UPDATE subway_line_station_code = '1018'; -- 석계 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (3, 524, '0335') ON DUPLICATE KEY UPDATE subway_line_station_code = '0335'; -- 대치 3호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 525, '2555') ON DUPLICATE KEY UPDATE subway_line_station_code = '2555'; -- 둔촌동 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 526, '1401') ON DUPLICATE KEY UPDATE subway_line_station_code = '1401'; -- 봉명 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 527, '1879') ON DUPLICATE KEY UPDATE subway_line_station_code = '1879'; -- 월곶 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 529, '1702') ON DUPLICATE KEY UPDATE subway_line_station_code = '1702'; -- 가산디지털단지 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 529, '2748') ON DUPLICATE KEY UPDATE subway_line_station_code = '2748'; -- 가산디지털단지 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 532, '1856') ON DUPLICATE KEY UPDATE subway_line_station_code = '1856'; -- 수내 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (3, 533, '0326') ON DUPLICATE KEY UPDATE subway_line_station_code = '0326'; -- 압구정 3호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (12, 534, '1311') ON DUPLICATE KEY UPDATE subway_line_station_code = '1311'; -- 신내 경춘선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (6, 534, '2649') ON DUPLICATE KEY UPDATE subway_line_station_code = '2649'; -- 신내 6호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 535, '1753') ON DUPLICATE KEY UPDATE subway_line_station_code = '1753'; -- 반월 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 536, '1206') ON DUPLICATE KEY UPDATE subway_line_station_code = '1206'; -- 도농 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 538, '2520') ON DUPLICATE KEY UPDATE subway_line_station_code = '2520'; -- 신정 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 539, '2542') ON DUPLICATE KEY UPDATE subway_line_station_code = '2542'; -- 마장 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (3, 540, '0334') ON DUPLICATE KEY UPDATE subway_line_station_code = '0334'; -- 도곡 3호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 540, '1025') ON DUPLICATE KEY UPDATE subway_line_station_code = '1025'; -- 도곡 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 541, '1457') ON DUPLICATE KEY UPDATE subway_line_station_code = '1457'; -- 범계 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (9, 542, '4135') ON DUPLICATE KEY UPDATE subway_line_station_code = '4135'; -- 한성백제 9호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (20, 543, '4707') ON DUPLICATE KEY UPDATE subway_line_station_code = '4707'; -- 삼양사거리 우이신설경전철
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 545, '1911') ON DUPLICATE KEY UPDATE subway_line_station_code = '1911'; -- 덕정 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 546, '2531') ON DUPLICATE KEY UPDATE subway_line_station_code = '2531'; -- 애오개 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (9, 547, '4114') ON DUPLICATE KEY UPDATE subway_line_station_code = '4114'; -- 국회의사당 9호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 548, '1864') ON DUPLICATE KEY UPDATE subway_line_station_code = '1864'; -- 신갈 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 549, '1284') ON DUPLICATE KEY UPDATE subway_line_station_code = '1284'; -- 문산 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 550, '0247') ON DUPLICATE KEY UPDATE subway_line_station_code = '0247'; -- 도림천 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (10, 551, '1502') ON DUPLICATE KEY UPDATE subway_line_station_code = '1502'; -- 이매 경강선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 551, '1860') ON DUPLICATE KEY UPDATE subway_line_station_code = '1860'; -- 이매 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 552, '0211') ON DUPLICATE KEY UPDATE subway_line_station_code = '0211'; -- 성수 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 553, '2717') ON DUPLICATE KEY UPDATE subway_line_station_code = '2717'; -- 하계 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 555, '1015') ON DUPLICATE KEY UPDATE subway_line_station_code = '1015'; -- 회기 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 555, '101C') ON DUPLICATE KEY UPDATE subway_line_station_code = '101C'; -- 회기 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (12, 555, '1307') ON DUPLICATE KEY UPDATE subway_line_station_code = '1307'; -- 회기 경춘선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (3, 556, '0318') ON DUPLICATE KEY UPDATE subway_line_station_code = '0318'; -- 안국 3호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (18, 557, '4307') ON DUPLICATE KEY UPDATE subway_line_station_code = '4307'; -- 강남 신분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 557, '0222') ON DUPLICATE KEY UPDATE subway_line_station_code = '0222'; -- 강남 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (9, 558, '4108') ON DUPLICATE KEY UPDATE subway_line_station_code = '4108'; -- 증미 9호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 559, '2727') ON DUPLICATE KEY UPDATE subway_line_station_code = '2727'; -- 군자 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 559, '2545') ON DUPLICATE KEY UPDATE subway_line_station_code = '2545'; -- 군자 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 560, '0233') ON DUPLICATE KEY UPDATE subway_line_station_code = '0233'; -- 대림 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 560, '2746') ON DUPLICATE KEY UPDATE subway_line_station_code = '2746'; -- 대림 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (3, 561, '1958') ON DUPLICATE KEY UPDATE subway_line_station_code = '1958'; -- 대화 3호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 562, '3756') ON DUPLICATE KEY UPDATE subway_line_station_code = '3756'; -- 신중동 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 563, '1278') ON DUPLICATE KEY UPDATE subway_line_station_code = '1278'; -- 운정 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (18, 565, '4309') ON DUPLICATE KEY UPDATE subway_line_station_code = '4309'; -- 양재시민의숲 신분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (6, 566, '2617') ON DUPLICATE KEY UPDATE subway_line_station_code = '2617'; -- 새절 6호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (13, 567, '4210') ON DUPLICATE KEY UPDATE subway_line_station_code = '4210'; -- 청라국제도시 공항철도
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 568, '1706') ON DUPLICATE KEY UPDATE subway_line_station_code = '1706'; -- 안양 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 569, '0300') ON DUPLICATE KEY UPDATE subway_line_station_code = '0300'; -- 대곡 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (3, 569, '1953') ON DUPLICATE KEY UPDATE subway_line_station_code = '1953'; -- 대곡 3호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (15, 569, '103C') ON DUPLICATE KEY UPDATE subway_line_station_code = '103C'; -- 대곡 서해선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 570, '1725') ON DUPLICATE KEY UPDATE subway_line_station_code = '1725'; -- 성환 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 571, '0238') ON DUPLICATE KEY UPDATE subway_line_station_code = '0238'; -- 합정 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (6, 571, '2623') ON DUPLICATE KEY UPDATE subway_line_station_code = '2623'; -- 합정 6호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (3, 572, '0309') ON DUPLICATE KEY UPDATE subway_line_station_code = '0309'; -- 지축 3호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (13, 573, '4215') ON DUPLICATE KEY UPDATE subway_line_station_code = '4215'; -- 인천공항2터미널 공항철도
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 575, '1907') ON DUPLICATE KEY UPDATE subway_line_station_code = '1907'; -- 가능 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 576, '0206') ON DUPLICATE KEY UPDATE subway_line_station_code = '0206'; -- 신당 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (6, 576, '2636') ON DUPLICATE KEY UPDATE subway_line_station_code = '2636'; -- 신당 6호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 577, '0203') ON DUPLICATE KEY UPDATE subway_line_station_code = '0203'; -- 을지로3가 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (3, 577, '0320') ON DUPLICATE KEY UPDATE subway_line_station_code = '0320'; -- 을지로3가 3호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 578, '1751') ON DUPLICATE KEY UPDATE subway_line_station_code = '1751'; -- 산본 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 579, '1817') ON DUPLICATE KEY UPDATE subway_line_station_code = '1817'; -- 도원 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (10, 581, '1507') ON DUPLICATE KEY UPDATE subway_line_station_code = '1507'; -- 신둔도예촌 경강선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 583, '1709') ON DUPLICATE KEY UPDATE subway_line_station_code = '1709'; -- 군포 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 584, '0221') ON DUPLICATE KEY UPDATE subway_line_station_code = '0221'; -- 역삼 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 585, '2741') ON DUPLICATE KEY UPDATE subway_line_station_code = '2741'; -- 상도 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 586, '2744') ON DUPLICATE KEY UPDATE subway_line_station_code = '2744'; -- 보라매 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 588, '1201') ON DUPLICATE KEY UPDATE subway_line_station_code = '1201'; -- 중랑 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (12, 588, '1308') ON DUPLICATE KEY UPDATE subway_line_station_code = '1308'; -- 중랑 경춘선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 589, '1858') ON DUPLICATE KEY UPDATE subway_line_station_code = '1858'; -- 미금 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (18, 589, '4313') ON DUPLICATE KEY UPDATE subway_line_station_code = '4313'; -- 미금 신분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 590, '1451') ON DUPLICATE KEY UPDATE subway_line_station_code = '1451'; -- 경마공원 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (20, 591, '4702') ON DUPLICATE KEY UPDATE subway_line_station_code = '4702'; -- 솔밭공원 우이신설경전철
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 592, '3761') ON DUPLICATE KEY UPDATE subway_line_station_code = '3761'; -- 부평구청 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (8, 593, '2820') ON DUPLICATE KEY UPDATE subway_line_station_code = '2820'; -- 장지 8호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (12, 594, '1323') ON DUPLICATE KEY UPDATE subway_line_station_code = '1323'; -- 가평 경춘선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (12, 595, '1322') ON DUPLICATE KEY UPDATE subway_line_station_code = '1322'; -- 상천 경춘선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 596, '2566') ON DUPLICATE KEY UPDATE subway_line_station_code = '2566'; -- 하남검단산 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 597, '2747') ON DUPLICATE KEY UPDATE subway_line_station_code = '2747'; -- 남구로 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 598, '1265') ON DUPLICATE KEY UPDATE subway_line_station_code = '1265'; -- 가좌 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 599, '1849') ON DUPLICATE KEY UPDATE subway_line_station_code = '1849'; -- 강남구청 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 599, '2732') ON DUPLICATE KEY UPDATE subway_line_station_code = '2732'; -- 강남구청 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (6, 600, '2648') ON DUPLICATE KEY UPDATE subway_line_station_code = '2648'; -- 봉화산 6호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 601, '2723') ON DUPLICATE KEY UPDATE subway_line_station_code = '2723'; -- 면목 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 602, '1214') ON DUPLICATE KEY UPDATE subway_line_station_code = '1214'; -- 국수 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (15, 603, '4807') ON DUPLICATE KEY UPDATE subway_line_station_code = '4807'; -- 신천 서해선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 604, '1806') ON DUPLICATE KEY UPDATE subway_line_station_code = '1806'; -- 부평 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 605, '0202') ON DUPLICATE KEY UPDATE subway_line_station_code = '0202'; -- 을지로입구 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (6, 606, '2638') ON DUPLICATE KEY UPDATE subway_line_station_code = '2638'; -- 창신 6호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 607, '1714') ON DUPLICATE KEY UPDATE subway_line_station_code = '1714'; -- 독산 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (18, 608, '4311') ON DUPLICATE KEY UPDATE subway_line_station_code = '4311'; -- 판교 신분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (10, 608, '1501') ON DUPLICATE KEY UPDATE subway_line_station_code = '1501'; -- 판교 경강선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (8, 609, '2821') ON DUPLICATE KEY UPDATE subway_line_station_code = '2821'; -- 복정 8호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 609, '1031') ON DUPLICATE KEY UPDATE subway_line_station_code = '1031'; -- 복정 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 610, '1763') ON DUPLICATE KEY UPDATE subway_line_station_code = '1763'; -- 수리산 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 611, '1912') ON DUPLICATE KEY UPDATE subway_line_station_code = '1912'; -- 지행 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 612, '1021') ON DUPLICATE KEY UPDATE subway_line_station_code = '1021'; -- 녹천 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (15, 613, '4814') ON DUPLICATE KEY UPDATE subway_line_station_code = '4814'; -- 시우 서해선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 615, '0423') ON DUPLICATE KEY UPDATE subway_line_station_code = '0423'; -- 충무로 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (3, 615, '0321') ON DUPLICATE KEY UPDATE subway_line_station_code = '0321'; -- 충무로 3호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (7, 616, '2714') ON DUPLICATE KEY UPDATE subway_line_station_code = '2714'; -- 마들 7호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 617, '1710') ON DUPLICATE KEY UPDATE subway_line_station_code = '1710'; -- 의왕 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (9, 618, '4127') ON DUPLICATE KEY UPDATE subway_line_station_code = '4127'; -- 선정릉 9호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 618, '1850') ON DUPLICATE KEY UPDATE subway_line_station_code = '1850'; -- 선정릉 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 619, '1283') ON DUPLICATE KEY UPDATE subway_line_station_code = '1283'; -- 파주 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 621, '2557') ON DUPLICATE KEY UPDATE subway_line_station_code = '2557'; -- 방이 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 622, '1030') ON DUPLICATE KEY UPDATE subway_line_station_code = '1030'; -- 수서 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (3, 622, '0339') ON DUPLICATE KEY UPDATE subway_line_station_code = '0339'; -- 수서 3호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (15, 623, '1980') ON DUPLICATE KEY UPDATE subway_line_station_code = '1980'; -- 김포공항 서해선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (9, 623, '4102') ON DUPLICATE KEY UPDATE subway_line_station_code = '4102'; -- 김포공항 9호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (13, 623, '4207') ON DUPLICATE KEY UPDATE subway_line_station_code = '4207'; -- 김포공항 공항철도
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 623, '2513') ON DUPLICATE KEY UPDATE subway_line_station_code = '2513'; -- 김포공항 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 624, '1261') ON DUPLICATE KEY UPDATE subway_line_station_code = '1261'; -- 효창공원앞 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (6, 624, '2628') ON DUPLICATE KEY UPDATE subway_line_station_code = '2628'; -- 효창공원앞 6호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (9, 625, '4116') ON DUPLICATE KEY UPDATE subway_line_station_code = '4116'; -- 샛강 9호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (2, 626, '0204') ON DUPLICATE KEY UPDATE subway_line_station_code = '0204'; -- 을지로4가 2호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 626, '2536') ON DUPLICATE KEY UPDATE subway_line_station_code = '2536'; -- 을지로4가 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 628, '1888') ON DUPLICATE KEY UPDATE subway_line_station_code = '1888'; -- 인하대 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (16, 629, '1831') ON DUPLICATE KEY UPDATE subway_line_station_code = '1831'; -- 중앙 수인분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 629, '1756') ON DUPLICATE KEY UPDATE subway_line_station_code = '1756'; -- 중앙 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (13, 630, '4201') ON DUPLICATE KEY UPDATE subway_line_station_code = '4201'; -- 서울역 공항철도
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (11, 630, '1251') ON DUPLICATE KEY UPDATE subway_line_station_code = '1251'; -- 서울역 경의중앙선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 630, '0426') ON DUPLICATE KEY UPDATE subway_line_station_code = '0426'; -- 서울역 4호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 630, '0150') ON DUPLICATE KEY UPDATE subway_line_station_code = '0150'; -- 서울역 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (9, 631, '4111') ON DUPLICATE KEY UPDATE subway_line_station_code = '4111'; -- 신목동 9호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (12, 632, '1325') ON DUPLICATE KEY UPDATE subway_line_station_code = '1325'; -- 백양리 경춘선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 633, '1904') ON DUPLICATE KEY UPDATE subway_line_station_code = '1904'; -- 망월사 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (6, 634, '2620') ON DUPLICATE KEY UPDATE subway_line_station_code = '2620'; -- 월드컵경기장 6호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (8, 635, '2822') ON DUPLICATE KEY UPDATE subway_line_station_code = '2822'; -- 산성 8호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (1, 636, '1724') ON DUPLICATE KEY UPDATE subway_line_station_code = '1724'; -- 평택 1호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (18, 637, '4304') ON DUPLICATE KEY UPDATE subway_line_station_code = '4304'; -- 신사 신분당선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (3, 637, '0327') ON DUPLICATE KEY UPDATE subway_line_station_code = '0327'; -- 신사 3호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (5, 639, '2549') ON DUPLICATE KEY UPDATE subway_line_station_code = '2549'; -- 강동 5호선
INSERT INTO tb_subway_line_station (subway_line_id, station_id, subway_line_station_code) VALUES (4, 640, '0406') ON DUPLICATE KEY UPDATE subway_line_station_code = '0406'; -- 오남 4호선