package raisetech.student.management.controller.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourse;
import raisetech.student.management.domain.StudentDetail;

@ExtendWith(MockitoExtension.class)
class StudentConverterTest {

  private StudentConverter sut;

  @BeforeEach
  void setUp() {
    sut = new StudentConverter();
  }

  @Test
  void convertStudentDetails_shouldMapCoursesToCorrectStudents() {
    Student student1 = new Student();
    student1.setId(1);
    student1.setName("テスト 一郎");
    student1.setKanaName("テスト イチロウ");
    student1.setEmail("ichiro@test.com");

    Student student2 = new Student();
    student2.setId(2);
    student2.setName("テスト 二郎");
    student1.setKanaName("テスト ニロウ");
    student1.setEmail("niro@test.com");

    List<Student> students = List.of(student1, student2);

    // 受講生コース情報データを作成
    // student1 には2つのコースを紐づける
    StudentCourse course1a = new StudentCourse();
    course1a.setStudentId(1);
    course1a.setCourseName("Java");

    StudentCourse course1b = new StudentCourse();
    course1b.setStudentId(1);
    course1b.setCourseName("AWS");

    // student2 には1つのコースを紐づける
    StudentCourse course2a = new StudentCourse();
    course2a.setStudentId(2);
    course2a.setCourseName("Git");

    // どの受講生にも紐づかないコース情報（フィルタリングで除外されるはず）
    StudentCourse orphanCourse = new StudentCourse();
    orphanCourse.setStudentId(99);
    orphanCourse.setCourseName("PHP");

    List<StudentCourse> allCourses = List.of(course1a, course1b, course2a, orphanCourse);

    List<StudentDetail> actualDetails = sut.convertStudentDetails(students, allCourses);

    assertNotNull(actualDetails);
    assertEquals(2, actualDetails.size(), "受講生の人数と一致");

    // student1 の検証
    StudentDetail detail1 = actualDetails.stream()
        .filter(d -> d.getStudent().getId().equals(1))
        .findFirst()
        .orElseThrow();
    assertEquals("テスト 一郎", detail1.getStudent().getName());
    assertEquals(2, detail1.getStudentCourseList().size(), "student1のコースは2つ");
    assertTrue(detail1.getStudentCourseList().contains(course1a));
    assertTrue(detail1.getStudentCourseList().contains(course1b));

    // student2 の検証
    StudentDetail detail2 = actualDetails.stream()
        .filter(d -> d.getStudent().getId().equals(2))
        .findFirst()
        .orElseThrow();
    assertEquals("テスト 二郎", detail2.getStudent().getName());
    assertEquals(1, detail2.getStudentCourseList().size(), "student2のコースは1つ");
    assertTrue(detail2.getStudentCourseList().contains(course2a));
  }

  @Test
  void convertStudentDetails_shouldSetEmptyCourseList_whenStudentHasNoMatchingCourses() {
    // --- Arrange (準備) ---
    Student student1 = new Student();
    student1.setId(1);
    List<Student> students = List.of(student1);

    // student1に紐づかないコース情報のみを用意
    StudentCourse course2a = new StudentCourse();
    course2a.setStudentId(2);
    List<StudentCourse> allCourses = List.of(course2a);

    // --- Act (実行) ---
    List<StudentDetail> actualDetails = sut.convertStudentDetails(students, allCourses);

    // --- Assert (検証) ---
    assertEquals(1, actualDetails.size());
    StudentDetail detail1 = actualDetails.get(0);
    assertNotNull(detail1.getStudentCourseList());
    assertTrue(detail1.getStudentCourseList().isEmpty(), "コース情報リストは空のはず");
  }

  @Test
  void convertStudentDetails_shouldReturnEmptyList_whenStudentListIsEmpty() {
    // --- Arrange (準備) ---
    List<Student> emptyStudents = Collections.emptyList();
    List<StudentCourse> allCourses = List.of(new StudentCourse()); // コース情報があってもなくても結果は同じ

    // --- Act (実行) ---
    List<StudentDetail> actualDetails = sut.convertStudentDetails(emptyStudents, allCourses);

    // --- Assert (検証) ---
    assertNotNull(actualDetails);
    assertTrue(actualDetails.isEmpty(), "結果リストは空のはず");
  }
}
