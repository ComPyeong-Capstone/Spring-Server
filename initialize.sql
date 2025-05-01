CREATE DATABASE IF NOT EXISTS AI_VIDEO;
USE AI_VIDEO;

-- 1️⃣ 테이블 삭제 순서 조정 (외래 키 참조 방지)
SET FOREIGN_KEY_CHECKS = 0;  -- 🚨 외래 키 검사 비활성화

DROP TABLE IF EXISTS `comment_like`;
DROP TABLE IF EXISTS `post_hash_tag`;
DROP TABLE IF EXISTS `post_like`;
DROP TABLE IF EXISTS `post_comment`;
DROP TABLE IF EXISTS `notification`;
DROP TABLE IF EXISTS `post`;
DROP TABLE IF EXISTS `hash_tag`;
DROP TABLE IF EXISTS `user`;

SET FOREIGN_KEY_CHECKS = 1;  -- ✅ 외래 키 검사 다시 활성화

-- 2️⃣ 사용자 테이블 (User)
CREATE TABLE `user` (
                        userId INT AUTO_INCREMENT PRIMARY KEY,
                        userName VARCHAR(100) UNIQUE NOT NULL,
                        profileImage VARCHAR(2083),  -- URL 최대 길이 고려
                        email VARCHAR(255) UNIQUE NOT NULL,
                        password VARCHAR(255) NOT NULL
);

-- 3️⃣ 게시물 테이블 (Post)
CREATE TABLE `post` (
                        postId INT AUTO_INCREMENT PRIMARY KEY,
                        title VARCHAR(100) NOT NULL,  -- 너무 긴 제목 방지
                        user_Id INT NOT NULL,
                        updateTime DATETIME DEFAULT CURRENT_TIMESTAMP,
                        thumbnailURL VARCHAR(2083),
                        videoURL VARCHAR(2083),
                        FOREIGN KEY (user_Id) REFERENCES user(userId) ON DELETE CASCADE
);



-- 4️⃣ 게시물 좋아요 테이블 (Post_Like)
CREATE TABLE `post_like` (
                             likeId INT AUTO_INCREMENT PRIMARY KEY,
                             user_id INT NOT NULL,
                             post_id INT NOT NULL,
                             FOREIGN KEY (user_id) REFERENCES user(userId) ON DELETE CASCADE,
                             FOREIGN KEY (post_id) REFERENCES post(postId) ON DELETE CASCADE,
                             UNIQUE (user_id, post_id)  -- 한 사용자는 같은 게시물에 한 번만 좋아요 가능
);

-- 5️⃣ 게시물 댓글 테이블 (Post_Comment)
CREATE TABLE `post_comment` (
                                commentId INT AUTO_INCREMENT PRIMARY KEY,
                                user_id INT NOT NULL,
                                post_id INT NOT NULL,
                                content TEXT NOT NULL,
                                Parent_Comment_ID INT DEFAULT NULL,
                                Created_At DATETIME DEFAULT CURRENT_TIMESTAMP,
                                FOREIGN KEY (user_id) REFERENCES user(userId) ON DELETE CASCADE,
                                FOREIGN KEY (post_id) REFERENCES post(postId) ON DELETE CASCADE,
                                FOREIGN KEY (Parent_Comment_ID) REFERENCES post_comment(commentId) ON DELETE CASCADE
);

-- ✅ 댓글 좋아요 테이블 (새로 추가)
CREATE TABLE `comment_like` (
                                likeId INT AUTO_INCREMENT PRIMARY KEY,
                                user_id INT NOT NULL,
                                comment_id INT NOT NULL,
                                FOREIGN KEY (user_id) REFERENCES user(userId) ON DELETE CASCADE,
                                FOREIGN KEY (comment_id) REFERENCES post_comment(commentId) ON DELETE CASCADE,
                                UNIQUE (user_id, comment_id) -- 중복 좋아요 방지
);

-- 6️⃣ 해시태그 테이블 (HashTag)
CREATE TABLE `hash_tag` (
                            hashId INT AUTO_INCREMENT PRIMARY KEY,
                            hashName VARCHAR(100) UNIQUE NOT NULL -- 중복 방지를 위해 UNIQUE 설정
);

-- 7️⃣ 게시물 해시태그 테이블 (Post_HashTag) - N:M 관계 해결을 위한 연결 테이블
CREATE TABLE `post_hash_tag` (
                                 postHashId INT AUTO_INCREMENT PRIMARY KEY,
                                 post_id INT NOT NULL,
                                 hash_id INT NOT NULL,
                                 FOREIGN KEY (post_id) REFERENCES post(postId) ON DELETE CASCADE,
                                 FOREIGN KEY (hash_id) REFERENCES hash_tag(hashId) ON DELETE CASCADE,
                                 UNIQUE (post_id, hash_id) -- 같은 해시태그가 중복 삽입되지 않도록 UNIQUE 설정
);

-- 8️⃣️ 알림 테이블 (Notification)
CREATE TABLE `notification` (
                                notiId INT PRIMARY KEY AUTO_INCREMENT,
                                sender_id INT NOT NULL,         -- 알림을 보낸 사용자 (좋아요/댓글 작성자)
                                receiver_id INT NOT NULL,       -- 알림을 받은 사용자 (게시물 작성자)
                                post_id INT NOT NULL,           -- 알림 유형에 따라 게시물이 없을 수도 있음
                                Noti_Type ENUM('LIKE', 'COMMENT', 'COMMENT_LIKE') NOT NULL,  -- 알림 유형
                                Noti_Read BOOLEAN DEFAULT FALSE,    -- 알림을 읽음 여부 (선택 사항)
                                notiTime DATETIME DEFAULT CURRENT_TIMESTAMP,  -- 알림 생성 시간
                                FOREIGN KEY (sender_id) REFERENCES user(userId) ON DELETE CASCADE,
                                FOREIGN KEY (receiver_id) REFERENCES user(userId) ON DELETE CASCADE,
                                FOREIGN KEY (post_id) REFERENCES post(postId) ON DELETE CASCADE
);