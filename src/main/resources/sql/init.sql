drop table IF EXISTS JOINS;
drop table IF EXISTS ROOM;
drop table IF EXISTS MEMBER;

create TABLE MEMBER (
  MEMBER_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(10) NOT NULL,
  password VARCHAR(255),
  email VARCHAR(255) NOT NULL,
  picture VARCHAR(255),
  role VARCHAR(255) NOT NULL,
  refresh_token VARCHAR(255),
  provider VARCHAR(255)
) default character set utf8 collate utf8_general_ci;

create TABLE ROOM (
  CLASSROOM_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  code VARCHAR(255) UNIQUE,
  capacity INT NOT NULL,
  MEMBER_ID BIGINT,
  FOREIGN KEY (MEMBER_ID) REFERENCES MEMBER (MEMBER_ID)
) default character set utf8 collate utf8_general_ci;

create TABLE JOINS (
  MEMBER_ID BIGINT,
  ROOM_ID BIGINT,
  CREATED_DATE DATETIME NOT NULL,
  ENTERED_AT DATETIME NOT NULL,

  PRIMARY KEY (MEMBER_ID, ROOM_ID),

  FOREIGN KEY (MEMBER_ID) REFERENCES MEMBER (MEMBER_ID),
  FOREIGN KEY (ROOM_ID) REFERENCES ROOM (CLASSROOM_ID)
) default character set utf8 collate utf8_general_ci;