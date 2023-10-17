drop table IF EXISTS joins;
drop table IF EXISTS room;
drop table IF EXISTS member;

create TABLE member (
  MEMBER_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(255) UNIQUE NOT NULL,
  username VARCHAR(30) NOT NULL,
  password VARCHAR(255),
  picture VARCHAR(255),
  role VARCHAR(255) NOT NULL,
  refresh_token VARCHAR(255),
  provider VARCHAR(255)
) default character set utf8 collate utf8_general_ci;

create TABLE room (
  ROOM_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  code VARCHAR(255) UNIQUE,
  capacity INT NOT NULL,
  MEMBER_ID BIGINT NOT NULL,
  FOREIGN KEY (MEMBER_ID) REFERENCES MEMBER (MEMBER_ID)
) default character set utf8 collate utf8_general_ci;

create TABLE joins (
  MEMBER_ID BIGINT,
  ROOM_ID BIGINT,
  CREATED_AT DATETIME NOT NULL,
  ENTERED_AT DATETIME NOT NULL,

  PRIMARY KEY (MEMBER_ID, ROOM_ID),

  FOREIGN KEY (MEMBER_ID) REFERENCES MEMBER (MEMBER_ID),
  FOREIGN KEY (ROOM_ID) REFERENCES ROOM (ROOM_ID)
) default character set utf8 collate utf8_general_ci;
