import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This application will keep track of things like what classes are offered by
 * the school, and which students are registered for those classes and provide
 * basic reporting. This application interacts with a database to store and
 * retrieve data.
 */
public class SchoolManagementSystem {

    public static void getAllClassesByInstructor(String first_name, String last_name) {
        Connection connection = null;
        Statement sqlStatement = null;

        try {
        	connection = Database.getDatabaseConnection();
        	sqlStatement = connection.createStatement();
        	
        	String sql = String.format("select instructors.first_name, instructors.last_name, academic_titles.title, classes.code, classes.name as class_name, terms.name as term\r\n"
        			+ "from class_sections\r\n"
        			+ "left join terms\r\n"
        			+ "on class_sections.term_id = terms.term_id\r\n"
        			+ "inner join instructors\r\n"
        			+ "on class_sections.instructor_id = instructors.instructor_id and instructors.first_name = '%s' and instructors.last_name = '%s'\r\n"
        			+ "inner join academic_titles\r\n"
        			+ "on instructors.academic_title_id = academic_titles.academic_title_id\r\n"
        			+ "inner join classes\r\n"
        			+ "on class_sections.class_id = classes.class_id;", first_name,last_name);
        	
        	ResultSet resultSet = sqlStatement.executeQuery(sql);
        	

        	System.out.println("Firs Name | Last Name | Title | Code | Name | Term");
        	System.out.println("-".repeat(80));
        	while(resultSet.next()) {
        			
        			String firstName = resultSet.getString("first_name");
        			String lastName = resultSet.getString("last_name");
        			String title = resultSet.getString("title");
        			String code = resultSet.getString("code");
        			String className = resultSet.getString("class_name");
        			String term = resultSet.getString("term");
        			
        			String res = String.format("%s | %s | %s | %s | %s | %S", firstName, lastName, title , code,className, term);
        			
        			System.out.println(res);
        	}
            
        } catch (SQLException sqlException) {
            System.out.println("Failed to get class sections");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }

    }

    public static void submitGrade(String studentId, String classSectionID, String grade) {
        Connection connection = null;
        Statement sqlStatement = null;

        try {
             /* Your logic goes here */
            throw new SQLException(); // REMOVE THIS (this is just to force it to compile)
        } catch (SQLException sqlException) {
            System.out.println("Failed to submit grade");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public static void registerStudent(String studentId, String classSectionID) {
        Connection connection = null;
        Statement sqlStatement = null;

        try {
        	connection = Database.getDatabaseConnection();
        	sqlStatement = connection.createStatement();
        	
        	String sql = String.format("insert into class_registrations(student_id, class_section_id)\r\n"
        			+ "values(%s,%s);",studentId,classSectionID);
        	
        	ResultSet rs = sqlStatement.executeQuery(sql);
        } catch (SQLException sqlException) {
            System.out.println("Failed to register student");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public static void deleteStudent(String studentId) {
        Connection connection = null;
        Statement sqlStatement = null;

        try {
        	connection = Database.getDatabaseConnection();
        	sqlStatement = connection.createStatement();
        	
        	String sql = String.format("SET SQL_SAFE_UPDATES = 0;"
        			+ "delete from students where students.student_id = %s;", studentId);
        	
        	sqlStatement.executeUpdate(sql);
        	
        } catch (SQLException sqlException) {
            System.out.println("Failed to delete student");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }


    public static void createNewStudent(String firstName, String lastName, String birthdate) {
        Connection connection = null;
        Statement sqlStatement = null;

        try {
        	connection = Database.getDatabaseConnection();
        	sqlStatement = connection.createStatement();
        	
        	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
        	LocalDate date = LocalDate.parse(birthdate, formatter);
        	
        	String sql = String.format("INSERT INTO students(first_name, last_name, birthdate)"
        			+ "VALUE (%s, %s, %s)",firstName,lastName, date);
        	
        	sqlStatement.executeUpdate(sql);
        	
        	
        	sqlStatement.close();
        	connection.close();
        } catch (SQLException sqlException) {
            System.out.println("Failed to create student");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }

    }

    public static void listAllClassRegistrations() {
        Connection connection = null;
        Statement sqlStatement = null;

        try {
        	connection = Database.getDatabaseConnection();
        	sqlStatement = connection.createStatement();
        	
        	String sql = "select students.student_id, class_registrations.class_section_id, students.first_name, students.last_name,\r\n"
        			+ " classes.code, classes.name, terms.name as term,grades.letter_grade\r\n"
        			+ "from class_registrations\r\n"
        			+ "left join class_sections\r\n"
        			+ "on class_registrations.class_section_id = class_sections.class_section_id\r\n"
        			+ "inner join terms\r\n"
        			+ "on class_sections.term_id = terms.term_id\r\n"
        			+ "inner join classes\r\n"
        			+ "on class_sections.class_id = classes.class_id\r\n"
        			+ "inner join students\r\n"
        			+ "on class_registrations.student_id = students.student_id\r\n"
        			+ "inner join grades\r\n"
        			+ "on class_registrations.grade_id = grades.grade_id;";
        	
        	ResultSet resultSet = sqlStatement.executeQuery(sql);
        	

        	System.out.println("Student ID | Class Section ID | First Name | Last Name | Code | Name | Term | Letter Grade");
        	System.out.println("-".repeat(80));
        	while(resultSet.next()) {
        		int studentId = resultSet.getInt("student_id");
    			int classSectionId = resultSet.getInt("class_section_id");
    			String firstName = resultSet.getString("first_name");
    			String lastName = resultSet.getString("last_name");
    			String code = resultSet.getString("code");
    			String name = resultSet.getString("name");
    			String term = resultSet.getString("term");
    			String letterChar = resultSet.getString("letter_grade");
    			
    			String res = String.format("%s | %s | %s | %s | %s | %s | %s | %s", studentId, classSectionId,firstName,lastName, 
    																				code,name, term, letterChar);
    			
    			System.out.println(res);
        	
        	}
        } catch (SQLException sqlException) {
            System.out.println("Failed to get class sections");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public static void listAllClassSections() {
        Connection connection = null;
        Statement sqlStatement = null;

        try {
        	connection = Database.getDatabaseConnection();
        	sqlStatement = connection.createStatement();
        	
        	String sql = "select class_sections.class_section_id, classes.code, classes.name, terms.name as term\r\n"
        			+ "from class_sections\r\n"
        			+ "inner join terms\r\n"
        			+ "on class_sections.term_id = terms.term_id\r\n"
        			+ "inner join classes\r\n"
        			+ "on class_sections.class_id = classes.class_id;";
        	
        	ResultSet resultSet = sqlStatement.executeQuery(sql);
        	

        	System.out.println("Class Section ID | Code | Name | term");
        	System.out.println("-".repeat(80));
        	while(resultSet.next()) {
        			
        			int classSectionId = resultSet.getInt("class_section_id");
        			String code = resultSet.getString("code");
        			String name = resultSet.getString("name");
        			String term = resultSet.getString("term");
        			
        			String res = String.format("%s | %s | %s | %s", classSectionId, code,name, term);
        			
        			System.out.println(res);
        	}
        } catch (SQLException sqlException) {
            System.out.println("Failed to get class sections");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    public static void listAllClasses(){
        Connection connection = null;
        Statement sqlStatement = null;

        try {
        	connection = Database.getDatabaseConnection();
        	sqlStatement = connection.createStatement();
        	
        	String sql = "SELECT * FROM classes;";
        	
        	ResultSet resultSet = sqlStatement.executeQuery(sql);
        	

        	System.out.println("Class ID | Code | Name | Description");
        	System.out.println("-".repeat(80));
        	while(resultSet.next()) {
        			
        			int classId = resultSet.getInt("class_id");
        			String code = resultSet.getString("code");
        			String name = resultSet.getString("name");
        			String descript = resultSet.getString("description");
        			
        			String res = String.format("%s | %s | %s | %s", classId, code,name, descript);
        			
        			System.out.println(res);
        	}
        	resultSet.close();
        	sqlStatement.close();
        	connection.close();
            
        } catch (SQLException sqlException) {
            System.out.println("Failed to get students");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }


    public static void listAllStudents() {
        Connection connection = null;
        Statement sqlStatement = null;

        try {
        	connection = Database.getDatabaseConnection();
        	sqlStatement = connection.createStatement();
        	
        	String sql = "SELECT * FROM students;";
        	
        	ResultSet resultSet = sqlStatement.executeQuery(sql);
        	

        	System.out.println("Student ID | First Name | Last Name | Birthdate");
        	System.out.println("-".repeat(80));
        	while(resultSet.next()) {
        			
        			int studentId = resultSet.getInt("student_id");
        			String firstName = resultSet.getString("first_name");
        			String lastName = resultSet.getString("last_name");
        			Date birthDay = resultSet.getDate("birthdate");
        			
        			String res = String.format("%s | %s | %s | %s", studentId, firstName,lastName, birthDay);
        			
        			System.out.println(res);
        	}
        	resultSet.close();
        	sqlStatement.close();
        	connection.close();
        } catch (SQLException sqlException) {
            System.out.println("Failed to get students");
            System.out.println(sqlException.getMessage());

        } finally {
            try {
                if (sqlStatement != null)
                    sqlStatement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    /***
     * Splits a string up by spaces. Spaces are ignored when wrapped in quotes.
     *
     * @param command - School Management System cli command
     * @return splits a string by spaces.
     */
    public static List<String> parseArguments(String command) {
        List<String> commandArguments = new ArrayList<String>();
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(command);
        while (m.find()) commandArguments.add(m.group(1).replace("\"", ""));
        return commandArguments;
    }

    public static void main(String[] args) throws SQLException {
        System.out.println("Welcome to the School Management System");
        System.out.println("-".repeat(80));

        Scanner scan = new Scanner(System.in);
        String command = "";

        do {
            System.out.print("Command: ");
            command = scan.nextLine();
            ;
            List<String> commandArguments = parseArguments(command);
            command = commandArguments.get(0);
            commandArguments.remove(0);

            if (command.equals("help")) {
                System.out.println("-".repeat(38) + "Help" + "-".repeat(38));
                System.out.println("test connection \n\tTests the database connection");

                System.out.println("list students \n\tlists all the students");
                System.out.println("list classes \n\tlists all the classes");
                System.out.println("list class_sections \n\tlists all the class_sections");
                System.out.println("list class_registrations \n\tlists all the class_registrations");
                System.out.println("list instructor <first_name> <last_name>\n\tlists all the classes taught by that instructor");


                System.out.println("delete student <studentId> \n\tdeletes the student");
                System.out.println("create student <first_name> <last_name> <birthdate> \n\tcreates a student");
                System.out.println("register student <student_id> <class_section_id>\n\tregisters the student to the class section");

                System.out.println("submit grade <studentId> <class_section_id> <letter_grade> \n\tcreates a student");
                System.out.println("help \n\tlists help information");
                System.out.println("quit \n\tExits the program");
            } else if (command.equals("test") && commandArguments.get(0).equals("connection")) {
                Database.testConnection();
            } else if (command.equals("list")) {
                if (commandArguments.get(0).equals("students")) listAllStudents();
                if (commandArguments.get(0).equals("classes")) listAllClasses();
                if (commandArguments.get(0).equals("class_sections")) listAllClassSections();
                if (commandArguments.get(0).equals("class_registrations")) listAllClassRegistrations();

                if (commandArguments.get(0).equals("instructor")) {
                    getAllClassesByInstructor(commandArguments.get(1), commandArguments.get(2));
                }
            } else if (command.equals("create")) {
                if (commandArguments.get(0).equals("student")) {
                    createNewStudent(commandArguments.get(1), commandArguments.get(2), commandArguments.get(3));
                }
            } else if (command.equals("register")) {
                if (commandArguments.get(0).equals("student")) {
                    registerStudent(commandArguments.get(1), commandArguments.get(2));
                }
            } else if (command.equals("submit")) {
                if (commandArguments.get(0).equals("grade")) {
                    submitGrade(commandArguments.get(1), commandArguments.get(2), commandArguments.get(3));
                }
            } else if (command.equals("delete")) {
                if (commandArguments.get(0).equals("student")) {
                    deleteStudent(commandArguments.get(1));
                }
            } else if (!(command.equals("quit") || command.equals("exit"))) {
                System.out.println(command);
                System.out.println("Command not found. Enter 'help' for list of commands");
            }
            System.out.println("-".repeat(80));
        } while (!(command.equals("quit") || command.equals("exit")));
        System.out.println("Bye!");
    }

}

