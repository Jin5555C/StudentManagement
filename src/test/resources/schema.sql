CREATE TABLE IF NOT EXISTS students (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL,
  kana_name VARCHAR(50) NOT NULL,
  nickname VARCHAR(50),
  email VARCHAR(50) NOT NULL,
  area VARCHAR(50),
  age INT,
  sex VARCHAR(10),
  remark VARCHAR(50),
  isDeleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS students_courses (
  id INT PRIMARY KEY AUTO_INCREMENT,
  studentId INT,
  courseName VARCHAR(255),
  courseStartAt TIMESTAMP,
  courseEndAt TIMESTAMP
);