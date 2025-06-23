  -- 1) 맴버 테이블
CREATE TABLE `member` (
  `mno` INT NOT NULL AUTO_INCREMENT,
  `id` VARCHAR(45) NOT NULL unique,
  `name` VARCHAR(45) NOT NULL,
  `email` VARCHAR(45) NOT NULL unique,
  `pw` VARCHAR(100) NOT NULL,
  `role` VARCHAR(45) NULL,
  PRIMARY KEY (`mno`));
  
  -- 2) 여행 계획 테이블
CREATE TABLE travelplans (
  plan_id        INT AUTO_INCREMENT PRIMARY KEY,
  user_id           INT                NOT NULL,
  description   VARCHAR(500),
  routes   JSON               NOT NULL COMMENT '[{title, content_id, place_id, latitude, longitude}, …]',
  start_day     DATETIME           DEFAULT CURRENT_TIMESTAMP,
  end_day     DATETIME           DEFAULT CURRENT_TIMESTAMP 
                                   ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES member(mno)
);

-- 3) 사용자 정의 장소 테이블
CREATE TABLE userplaces (
  place_id   INT AUTO_INCREMENT PRIMARY KEY,
  user_id    INT                NOT NULL,
  name       VARCHAR(100)       NOT NULL COMMENT 'ex: 집, 회사 등',
  latitude   DECIMAL(20,17)     NOT NULL,
  longitude  DECIMAL(20,17)     NOT NULL,
  created    DATETIME           DEFAULT CURRENT_TIMESTAMP,
  updated    DATETIME           DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES member(mno)
);

-- 4) 게시글 테이블
CREATE TABLE posts (
  post_id   INT AUTO_INCREMENT PRIMARY KEY,
  user_id   INT                NOT NULL,
  title     VARCHAR(150)       NOT NULL,
  content   VARCHAR(150)       NOT NULL,
  tags      VARCHAR(100)       NOT NULL COMMENT '카테고리 태그(콤마 구분)',
  -- likes     INT                DEFAULT 0, 매번 집계 방식으로 대체할 예정
  created   DATETIME           DEFAULT CURRENT_TIMESTAMP,
  updated   DATETIME           DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES member(mno)
);

-- 5) 댓글 테이블
CREATE TABLE comments (
  comment_id INT AUTO_INCREMENT PRIMARY KEY,
  post_id    INT                NOT NULL,
  user_id    INT                NOT NULL,
  content    VARCHAR(50)        NOT NULL,
  created    DATETIME           DEFAULT CURRENT_TIMESTAMP,
  updated    DATETIME           DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (post_id)  REFERENCES posts(post_id) ON DELETE CASCADE,
  FOREIGN KEY (user_id)  REFERENCES member(mno)
);

-- 6) 포스트 좋아요 테이블
CREATE TABLE postlikes (
  post_id    INT      NOT NULL,
  user_id    INT      NOT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (post_id, user_id),
  FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE,
  FOREIGN KEY (user_id) REFERENCES member(mno)
);

-- 7) 댓글 좋아요 테이블
CREATE TABLE commentlikes (
  comment_id INT      NOT NULL,
  user_id    INT      NOT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (comment_id, user_id),
  FOREIGN KEY (comment_id) REFERENCES comments(comment_id) ON DELETE CASCADE,
  FOREIGN KEY (user_id)    REFERENCES member(mno)
);

-- View 
-- 1) post view
CREATE OR REPLACE VIEW post AS
SELECT
  p.post_id,
  p.title,
  p.content,
  p.tags,
  p.created,
  p.updated,
  m.mno       AS id,
  m.id        AS author_id,
  m.name      AS author_name,
  COUNT(pl.user_id) AS likes,
  -- 댓글 개수 컬럼 추가
  (SELECT COUNT(*) 
     FROM comments c 
    WHERE c.post_id = p.post_id
  ) AS comment_count
FROM posts p
JOIN member m 
  ON m.mno = p.user_id
LEFT JOIN postlikes pl
  ON pl.post_id = p.post_id
GROUP BY
  p.post_id,
  p.title,
  p.content,
  p.tags,
  p.created,
  p.updated,
  m.mno,
  m.id,
  m.name;

-- 2) comment view
CREATE OR REPLACE VIEW comment AS
SELECT
  c.post_id     AS post_id,     
  m.mno         AS user_id,
  m.id          AS author_id,
  m.name        AS author_name,
  c.comment_id,
  c.content,
  c.created,
  c.updated,
  COUNT(likes.comment_id) AS likes
FROM comments AS c
JOIN member AS m
  ON m.mno = c.user_id
LEFT JOIN commentlikes AS likes
  ON likes.comment_id = c.comment_id
GROUP BY
  c.post_id,        
  c.comment_id,
  c.content,
  c.created,
  c.updated,
  m.mno,
  m.id,
  m.name;

-- 3) metrics view
CREATE OR REPLACE VIEW metrics AS
SELECT
  (SELECT COUNT(*) FROM member)      AS member_count,
  (SELECT COUNT(*) FROM posts)       AS posts_count,
  (SELECT COUNT(*) FROM comments)    AS comments_count,
  (SELECT COUNT(*) FROM postlikes)   AS postlikes_count,
  (SELECT COUNT(*) FROM commentlikes)AS commentlikes_count,
  (SELECT COUNT(*) FROM userplaces)  AS userplaces_count,
  (SELECT COUNT(*) FROM travelplans) AS travelplans_count;
 