CREATE DATABASE IF NOT EXISTS cs_hu_310_final_project; 
USE cs_hu_310_final_project; 
DROP TABLE IF EXISTS class_registrations; 
DROP TABLE IF EXISTS grades; 
DROP TABLE IF EXISTS class_sections; 
DROP TABLE IF EXISTS instructors; 
DROP TABLE IF EXISTS academic_titles; 
DROP TABLE IF EXISTS students; 
DROP TABLE IF EXISTS classes;
DROP FUNCTION IF EXISTS convert_to_grade_point; 
 
CREATE TABLE IF NOT EXISTS classes( 
    class_id INT AUTO_INCREMENT, 
    name VARCHAR(50) NOT NULL, 
    description VARCHAR(1000), 
    code VARCHAR(10) UNIQUE, 
    maximum_students INT DEFAULT 10, 
    PRIMARY KEY(class_id) 
); 
 
CREATE TABLE IF NOT EXISTS students( 
    student_id INT AUTO_INCREMENT, 
    first_name VARCHAR(30) NOT NULL, 
    last_name VARCHAR(50) NOT NULL, 
    birthdate DATE, 
    PRIMARY KEY (student_id) 
); 

CREATE TABLE IF NOT EXISTS academic_titles (
	academic_title_id INT AUTO_INCREMENT NOT NULL, 
    title VARCHAR(255) NOT NULL,
    PRIMARY KEY(academic_title_id)
);

CREATE TABLE IF NOT EXISTS instructors(
	instructor_id INT AUTO_INCREMENT NOT NULL,
    first_name VARCHAR(80) NOT NULL,
    last_name VARCHAR(80) NOT NULL,
    academic_title_id INT,
    PRIMARY KEY(instructor_id),
    FOREIGN KEY(academic_title_id) REFERENCES academic_titles(academic_title_id)
);

CREATE TABLE IF NOT EXISTS terms (
	term_id INT AUTO_INCREMENT NOT NULL, 
    name VARCHAR(80) NOT NULL,
    PRIMARY KEY(term_id)
);

CREATE TABLE IF NOT EXISTS class_sections (
	class_section_id INT AUTO_INCREMENT NOT NULL, 
    class_id INT NOT NULL, 
    instructor_id INT NOT NULL, 
    term_id INT NOT NULL, 
    PRIMARY KEY(class_section_id),
    FOREIGN KEY(class_id) REFERENCES classes(class_id),
    FOREIGN KEY(instructor_id) REFERENCES instructors(instructor_id),
    FOREIGN KEY(term_id) REFERENCES terms(term_id)
);

CREATE TABLE IF NOT EXISTS grades (
	grade_id INT AUTO_INCREMENT NOT NULL, 
    letter_grade CHAR(2) NOT NULL,
    PRIMARY KEY(grade_id)
);

CREATE TABLE IF NOT EXISTS class_registrations  (
	class_registration_id INT AUTO_INCREMENT NOT NULL, 
    class_section_id INT NOT NULL, 
    student_id INT NOT NULL, 
    grade_id INT, 
    signup_timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY(class_registration_id),
    FOREIGN KEY(class_section_id) REFERENCES class_sections(class_section_id),
    FOREIGN KEY(student_id) REFERENCES students(student_id),
    FOREIGN KEY(grade_id) REFERENCES grades(grade_id)
);

DELIMITER $$
CREATE FUNCTION convert_to_grade_point(letter_grade char(2))
returns int 
deterministic
BEGIN
	declare res int;
	if(letter_grade = 'A') then 
		set res = 4;
	elseif(letter_grade = 'B') then
		set res = 3;
	elseif(letter_grade = 'C') then
		set res = 2;
	elseif(letter_grade = 'D') then
		set res = 1;
	elseif(letter_grade = 'F') then
		set res = 0;
	elseif(letter_grade = NULL) then
		set res = NULL;
	end if;
	return res;
END $$


-- Report 1
select students.first_name,students.last_name, count(class_registrations.student_id) as number_of_classes, 
sum(convert_to_grade_point(grades.letter_grade)) as total_grade_points_earned, (sum(convert_to_grade_point(grades.letter_grade)) / count(class_registrations.student_id)) as GPA
from class_registrations
inner join students
on class_registrations.student_id = students.student_id and students.student_id = 1
inner join grades
on class_registrations.grade_id = grades.grade_id and class_registrations.student_id = students.student_id
group by students.first_name,students.last_name;

-- Report 2
select students.first_name,students.last_name, count(class_registrations.student_id) as number_of_classes, 
sum(convert_to_grade_point(grades.letter_grade)) as total_grade_points_earned, (sum(convert_to_grade_point(grades.letter_grade)) / count(class_registrations.student_id)) as GPA
from class_registrations
inner join students
on class_registrations.student_id = students.student_id
inner join grades
on class_registrations.grade_id = grades.grade_id and class_registrations.student_id = students.student_id
group by students.first_name,students.last_name;

-- Report 3
select classes.code, classes.name, count(class_registrations.grade_id) as number_of_grades, 
sum(convert_to_grade_point(grades.letter_grade)) as total_grade_points_earned, (sum(convert_to_grade_point(grades.letter_grade)) / count(class_registrations.grade_id) ) as GPA
from class_registrations
inner join class_sections
on class_registrations.class_section_id = class_sections.class_section_id
inner join classes
on class_sections.class_id = classes.class_id
inner join grades
on class_registrations.grade_id = grades.grade_id and class_registrations.class_section_id = class_sections.class_section_id
group by classes.code, classes.name;

-- Report 4
select classes.code, classes.name, terms.name, count(class_registrations.grade_id) as number_of_grades, 
sum(convert_to_grade_point(grades.letter_grade)) as total_grade_points_earned, (sum(convert_to_grade_point(grades.letter_grade)) / count(class_registrations.grade_id) ) as GPA
from class_registrations
inner join class_sections
on class_registrations.class_section_id = class_sections.class_section_id
right join classes
on class_sections.class_id = classes.class_id
inner join terms
on class_sections.term_id = terms.term_id
inner join grades
on class_registrations.grade_id = grades.grade_id and class_registrations.class_section_id = class_sections.class_section_id
group by classes.code, classes.name,terms.name;

select * from class_registrations;
select * from class_sections;
select * from terms;