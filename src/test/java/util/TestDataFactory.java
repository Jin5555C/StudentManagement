package util;

import java.util.Arrays;
import java.util.List;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourse;
import raisetech.student.management.domain.StudentDetail;

public class TestDataFactory {

  public static Student createStudent(Integer id) {
    Student student = new Student();
    student.setId(id);
    return student;
  }

  public static StudentCourse createCourse(Integer id) {
    StudentCourse course = new StudentCourse();
    course.setId(id);
    return course;
  }

  public static StudentDetail createStudentDetail(Student student, StudentCourse... courses) {
    List<StudentCourse> courseList = (courses == null) ? null : Arrays.asList(courses);
    return new StudentDetail(student, courseList);
  }
}
