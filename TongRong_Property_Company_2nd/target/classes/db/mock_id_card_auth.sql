-- TongRong_Property_Company_2nd mock data
-- 100 fake identities (names + valid-format ID numbers). Test/demo only.

CREATE TABLE IF NOT EXISTS id_card_auth (
  id INT UNSIGNED NOT NULL AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL COMMENT '姓名',
  id_card CHAR(18) NOT NULL COMMENT '身份证号',
  gender TINYINT NOT NULL COMMENT '1-男 2-女',
  birth_date DATE NOT NULL COMMENT '出生日期',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_id_card (id_card)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实名认证测试数据';

INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('武翠花', '450103198605116562', 2, '1986-05-11');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('李伟东', '500107197410196166', 2, '1974-10-19');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('秦源', '420102198801086995', 1, '1988-01-08');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('易凯', '120101198806027604', 2, '1988-06-02');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('龙秀', '370202197001112194', 1, '1970-01-11');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('金睿聪', '110105199112095723', 2, '1991-12-09');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('廖嘉怡', '410105198805258624', 2, '1988-05-25');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('程悦', '530111199707040620', 2, '1997-07-04');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('龙明轩', '110105196907184873', 1, '1969-07-18');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('程伟东', '220102198111099812', 1, '1981-11-09');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('宋娜娜', '110105196605180842', 2, '1966-05-18');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('张大伟', '410105200310088037', 1, '2003-10-08');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('何磊', '350102196508216319', 1, '1965-08-21');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('黎小军', '450103198904114428', 2, '1989-04-11');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('程琪', '110101199811158978', 1, '1998-11-15');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('汪雅婷', '370202199402100249', 2, '1994-02-10');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('康轩', '220102196609127498', 1, '1966-09-12');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('贺梅', '350102198507257732', 1, '1985-07-25');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('殷一鸣', '320102199512230867', 2, '1995-12-23');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('郭娜', '420102198607133700', 2, '1986-07-13');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('徐强', '510104197302026293', 1, '1973-02-02');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('邱静静', '320505199101134089', 2, '1991-01-13');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('林静静', '420102197912240030', 1, '1979-12-24');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('尹永强', '500107198505023852', 1, '1985-05-02');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('傅淑芬', '500107196601122560', 2, '1966-01-12');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('吕小军', '310101197709136439', 1, '1977-09-13');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('钟雨欣', '410105199205244143', 2, '1992-05-24');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('孔洋', '43010219801019676X', 2, '1980-10-19');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('文艳', '320102196812293524', 2, '1968-12-29');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('侯军军', '230102197810284483', 2, '1978-10-28');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('尹淑芬', '110101196602072779', 1, '1966-02-07');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('彭娜娜', '110105199308268032', 1, '1993-08-26');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('王雨欣', '140102197312065008', 2, '1973-12-06');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('陆建华', '640104197808180502', 2, '1978-08-18');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('黎悦', '140102197807265570', 1, '1978-07-26');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('江玉兰', '320102199301145574', 1, '1993-01-14');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('石桂芳', '500107197403294796', 1, '1974-03-29');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('蒋泽', '110101197301010421', 2, '1973-01-01');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('阎梦瑶', '310101197504103107', 2, '1975-04-10');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('尹杰', '710000198202264279', 1, '1982-02-26');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('侯娟', '330106198407085947', 2, '1984-07-08');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('林建华', '310101198707073459', 1, '1987-07-07');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('毛琳', '330106197806258484', 2, '1978-06-25');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('陆丽', '650102197803301861', 2, '1978-03-30');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('孟敏', '440106199601139566', 2, '1996-01-13');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('王晓明', '310115199109112334', 1, '1991-09-11');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('钟嘉怡', '510104200311139745', 2, '2003-11-13');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('钟天佑', '330102197206171320', 2, '1972-06-17');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('贺宇', '440103200004081833', 1, '2000-04-08');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('许诗', '310115200111066950', 1, '2001-11-06');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('魏丽', '610102197702093875', 1, '1977-02-09');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('秦春', '320505196606029539', 1, '1966-06-02');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('赵思', '330102198712157921', 2, '1987-12-15');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('蔡婷婷', '11010519710718380X', 2, '1971-07-18');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('韩小军', '330102197907031996', 1, '1979-07-03');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('熊思琪', '44010619810811802X', 2, '1981-08-11');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('阎思源', '61010219971023143X', 1, '1997-10-23');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('戴军军', '320505199607120426', 2, '1996-07-12');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('郝远', '37020219801209589X', 1, '1980-12-09');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('沈昊', '620102199301019800', 2, '1993-01-01');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('阎泽', '310115197809217322', 2, '1978-09-21');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('杜俊杰', '410105199105079435', 1, '1991-05-07');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('顾欣怡', '120101197909180906', 2, '1979-09-18');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('吴婷', '330102200310021351', 1, '2003-10-02');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('林逸飞', '640104199109100744', 2, '1991-09-10');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('孙平平', '11010119700503914X', 2, '1970-05-03');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('常辉', '120101199311045442', 2, '1993-11-04');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('周平', '500107197101030066', 2, '1971-01-03');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('戴佳', '230102200212255601', 2, '2002-12-25');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('苏翠花', '71000019690928432X', 2, '1969-09-28');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('郭建', '420111197005031178', 1, '1970-05-03');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('吕涛', '330106197307231412', 1, '1973-07-23');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('乔娜娜', '350102197405253744', 2, '1974-05-25');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('宋梅', '500107199006021064', 2, '1990-06-02');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('孔洋', '500107198901266004', 2, '1989-01-26');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('毛诗', '330106200112296253', 1, '2001-12-29');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('蔡洋', '330102197303082047', 2, '1973-03-08');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('康泽', '140102197307305054', 1, '1973-07-30');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('邹雨欣', '430102199102011448', 2, '1991-02-01');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('崔春', '500107199804183500', 2, '1998-04-18');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('曹芳', '110101197904066029', 2, '1979-04-06');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('蔡宇', '620102196906280987', 2, '1969-06-28');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('朱志刚', '500103199504165276', 1, '1995-04-16');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('孙琳', '120101199805313785', 2, '1998-05-31');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('杨华', '320505197407295193', 1, '1974-07-29');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('曾家豪', '420111199612116586', 2, '1996-12-11');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('文紫萱', '53011119930118048X', 2, '1993-01-18');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('高芳', '430102200210115688', 2, '2002-10-11');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('唐平', '440106197408076760', 2, '1974-08-07');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('罗梅', '330106196504028590', 1, '1965-04-02');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('高涛', '350102196808293033', 1, '1968-08-29');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('白文博', '410105200306166987', 2, '2003-06-16');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('罗杰', '320102197406238735', 1, '1974-06-23');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('曹子豪', '43010219881201493X', 1, '1988-12-01');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('毛浩然', '350102197311096303', 2, '1973-11-09');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('孔逸飞', '33010219810924916X', 2, '1981-09-24');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('顾春华', '320505198005216231', 1, '1980-05-21');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('文凯文', '62010219760607270X', 2, '1976-06-07');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('王建军', '350102199906141264', 2, '1999-06-14');
INSERT INTO id_card_auth (name, id_card, gender, birth_date) VALUES ('康思', '140102198207265715', 1, '1982-07-26');

SELECT COUNT(*) AS total FROM id_card_auth;
