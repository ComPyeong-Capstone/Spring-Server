CREATE DATABASE IF NOT EXISTS AI_VIDEO;
USE AI_VIDEO;

-- 1️⃣ 테이블 삭제 순서 조정 (외래 키 참조 방지)
SET FOREIGN_KEY_CHECKS = 0;  -- 🚨 외래 키 검사 비활성화

DROP TABLE IF EXISTS `Comment_Like`;
DROP TABLE IF EXISTS `post_hash_tag`;
DROP TABLE IF EXISTS `Post_Like`;
DROP TABLE IF EXISTS `Post_Comment`;
DROP TABLE IF EXISTS `Notification`;
DROP TABLE IF EXISTS `post`;
DROP TABLE IF EXISTS `hash_tag`;
DROP TABLE IF EXISTS `User`;

SET FOREIGN_KEY_CHECKS = 1;  -- ✅ 외래 키 검사 다시 활성화


-- 2️⃣ 사용자 테이블 (User)
CREATE TABLE `User` (
	User_ID INT AUTO_INCREMENT PRIMARY KEY,
	User_Name VARCHAR(100) UNIQUE NOT NULL,
	Profile_Image VARCHAR(2083),  -- URL 최대 길이 고려
	Email VARCHAR(255) UNIQUE NOT NULL,
	Password VARCHAR(255) NOT NULL
);

-- 3️⃣ 게시물 테이블 (Post)
CREATE TABLE `post` (
	Post_ID INT AUTO_INCREMENT PRIMARY KEY,
	title VARCHAR(100) NOT NULL,  -- 너무 긴 제목 방지
	User_ID INT NOT NULL,
	Update_Time DATETIME DEFAULT CURRENT_TIMESTAMP,
    videoURL VARCHAR(2083),
	FOREIGN KEY (User_ID) REFERENCES User(User_ID) ON DELETE CASCADE
);

-- 4️⃣ 게시물 좋아요 테이블 (Post_Like)
CREATE TABLE `Post_Like` (
	Like_ID INT AUTO_INCREMENT PRIMARY KEY,
	User_ID INT NOT NULL,
	Post_ID INT NOT NULL,
	FOREIGN KEY (User_ID) REFERENCES User(User_ID) ON DELETE CASCADE,
	FOREIGN KEY (Post_ID) REFERENCES post(Post_ID) ON DELETE CASCADE,
	UNIQUE (User_ID, Post_ID)  -- 한 사용자는 같은 게시물에 한 번만 좋아요 가능
);

-- 5️⃣ 게시물 댓글 테이블 (Post_Comment)
CREATE TABLE `Post_Comment` (
	Comment_ID INT AUTO_INCREMENT PRIMARY KEY,
	User_ID INT NOT NULL,
	Post_ID INT NOT NULL,
	content TEXT NOT NULL,
    Parent_Comment_ID INT DEFAULT NULL,
    Created_At DATETIME DEFAULT CURRENT_TIMESTAMP,
	FOREIGN KEY (User_ID) REFERENCES User(User_ID) ON DELETE CASCADE,
	FOREIGN KEY (Post_ID) REFERENCES post(Post_ID) ON DELETE CASCADE,
    FOREIGN KEY (Parent_Comment_ID) REFERENCES Post_Comment(Comment_ID) ON DELETE CASCADE
);

-- ✅ 댓글 좋아요 테이블 (새로 추가)
CREATE TABLE `Comment_Like` (
	Like_ID INT AUTO_INCREMENT PRIMARY KEY,
	User_ID INT NOT NULL,
	Comment_ID INT NOT NULL,
	FOREIGN KEY (User_ID) REFERENCES User(User_ID) ON DELETE CASCADE,
	FOREIGN KEY (Comment_ID) REFERENCES Post_Comment(Comment_ID) ON DELETE CASCADE,
	UNIQUE (User_ID, Comment_ID) -- 중복 좋아요 방지
);

-- 6️⃣ 해시태그 테이블 (HashTag)
CREATE TABLE `hash_tag` (
	Hash_ID INT AUTO_INCREMENT PRIMARY KEY,
	Hash_Name VARCHAR(100) UNIQUE NOT NULL -- 중복 방지를 위해 UNIQUE 설정
);

-- 7️⃣ 게시물 해시태그 테이블 (Post_HashTag) - N:M 관계 해결을 위한 연결 테이블
CREATE TABLE `post_hash_tag` (
	post_hash_id INT AUTO_INCREMENT PRIMARY KEY,
	post_id INT NOT NULL,
	hash_id INT NOT NULL,
	FOREIGN KEY (post_id) REFERENCES post(post_id) ON DELETE CASCADE,
	FOREIGN KEY (hash_id) REFERENCES hash_tag(hash_id) ON DELETE CASCADE,
	UNIQUE (post_id, hash_id) -- 같은 해시태그가 중복 삽입되지 않도록 UNIQUE 설정
);

-- 8️⃣️ 알림 테이블 (Notification)
CREATE TABLE `Notification` (
	Noti_ID INT PRIMARY KEY AUTO_INCREMENT,
	Sender_ID INT NOT NULL,  -- 알림을 보낸 사용자 (좋아요/댓글 작성자)
	Receiver_ID INT NOT NULL,  -- 알림을 받은 사용자 (게시물 작성자)
	Post_ID INT NOT NULL,  -- 알림 유형에 따라 게시물이 없을 수도 있음
	Noti_Type ENUM('LIKE', 'COMMENT', 'COMMENT_LIKE') NOT NULL,  -- 알림 유형
	Noti_Read BOOLEAN DEFAULT FALSE,  -- 알림을 읽음 여부 (선택 사항)
	Noti_Time DATETIME DEFAULT CURRENT_TIMESTAMP,  -- 알림 생성 시간
	FOREIGN KEY (Sender_ID) REFERENCES User(User_ID) ON DELETE CASCADE,
	FOREIGN KEY (Receiver_ID) REFERENCES User(User_ID) ON DELETE CASCADE,
	FOREIGN KEY (Post_ID) REFERENCES post(Post_ID) ON DELETE CASCADE
);