DROP TABLE IF EXISTS `MEMBER`;

CREATE TABLE MEMBER (
  MEMBER_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(10) NOT NULL,
  password VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  picture VARCHAR(255),
  role VARCHAR(255) NOT NULL,
  refresh_token VARCHAR(255),
  provider VARCHAR(255)
);
