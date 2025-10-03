package raisetech.student.management.controller.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static util.TestDataFactory.createApplicationStatus; // createApplicationStatusの定義を仮定

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import raisetech.student.management.data.ApplicationStatus;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourse;
import raisetech.student.management.domain.StudentDetail;

class StudentConverterTest {

  private StudentConverter sut;

  @BeforeEach
  void setUp() {
    sut = new StudentConverter();
  }


  @Test
  void convertStudentDetails_shouldMapCoursesAndStatusesToCorrectStudents() {
    Student student1 = new Student();
    student1.setId(1);
    student1.setName("テスト 一郎");
    student1.setKanaName("テスト イチロウ");
    student1.setEmail("ichiro@test.com");

    Student student2 = new Student();
    student2.setId(2);
    student2.setName("テスト 二郎");
    student2.setKanaName("テスト ニロウ");
    student2.setEmail("niro@test.com");

    List<Student> students = List.of(student1, student2);

    // 申込状況データを作成し、courseIdを設定 (students_courses.id と 1対1)
    ApplicationStatus status1 = createApplicationStatus(1, "仮申込");
    status1.setCourseId(101); // course1a.id に対応

    ApplicationStatus status2 = createApplicationStatus(2, "受講中");
    status2.setCourseId(102); // course1b.id に対応

    ApplicationStatus status3 = createApplicationStatus(3, "受講中"); // course2a用に新しいIDを使用
    status3.setCourseId(201); // course2a.id に対応

    List<ApplicationStatus> applicationStatuses = List.of(status1, status2, status3);

    // 受講生コース情報データを作成し、ID (これがマッピングのキー) を設定
    // student1 (id=1) には2つのコースを紐づける
    StudentCourse course1a = new StudentCourse();
    course1a.setId(101);
    course1a.setStudentId(1);
    course1a.setCourseName("Java");

    StudentCourse course1b = new StudentCourse();
    course1b.setId(102);
    course1b.setStudentId(1);
    course1b.setCourseName("AWS");

    // student2 (id=2) には1つのコースを紐づける
    StudentCourse course2a = new StudentCourse();
    course2a.setId(201);
    course2a.setStudentId(2);
    course2a.setCourseName("Git");

    // 申込状況IDがApplicationStatusリストに存在しないコース (ID 99は使わない)
    StudentCourse unknownStatusCourse = new StudentCourse();
    unknownStatusCourse.setId(103); // ID 103には対応するApplicationStatusがない
    unknownStatusCourse.setStudentId(1);
    unknownStatusCourse.setCourseName("Unknown");

    // どの受講生にも紐づかないコース情報（フィルタリングで除外されるはず）
    StudentCourse orphanCourse = new StudentCourse();
    orphanCourse.setId(900);
    orphanCourse.setStudentId(99);
    orphanCourse.setCourseName("PHP");

    List<StudentCourse> allCourses = List.of(course1a, course1b, course2a, orphanCourse, unknownStatusCourse);

    List<StudentDetail> actualDetails = sut.convertStudentDetails(students, allCourses, applicationStatuses);

    assertNotNull(actualDetails);
    assertEquals(2, actualDetails.size(), "受講生の人数と一致");

    // student1 の検証
    StudentDetail detail1 = actualDetails.stream()
            .filter(d -> d.getStudent().getId().equals(1))
            .findFirst()
            .orElseThrow();
    assertEquals("テスト 一郎", detail1.getStudent().getName());
    assertEquals(3, detail1.getStudentCourseList().size(), "student1のコースは3つ (Java, AWS, Unknown)");

    // Javaコースの検証 (status1: 仮申込)
    StudentCourse actualCourse1a = detail1.getStudentCourseList().stream()
            .filter(c -> c.getCourseName().equals("Java"))
            .findFirst()
            .orElseThrow();
    assertNotNull(actualCourse1a.getApplicationStatus(), "JavaコースにApplicationStatusがセットされている");
    assertEquals("仮申込", actualCourse1a.getApplicationStatus().getStatus(), "Javaコースのステータス名が正しい");
    assertEquals(101, actualCourse1a.getId(), "JavaコースのIDが正しい");

    // AWSコースの検証 (status2: 受講中)
    StudentCourse actualCourse1b = detail1.getStudentCourseList().stream()
            .filter(c -> c.getCourseName().equals("AWS"))
            .findFirst()
            .orElseThrow();
    assertNotNull(actualCourse1b.getApplicationStatus(), "AWSコースにApplicationStatusがセットされている");
    assertEquals("受講中", actualCourse1b.getApplicationStatus().getStatus(), "AWSコースのステータス名が正しい");
    assertEquals(102, actualCourse1b.getId(), "AWSコースのIDが正しい");


    // Unknown Statusコースの検証 (ID 103)
    StudentCourse actualUnknownStatusCourse = detail1.getStudentCourseList().stream()
            .filter(c -> c.getCourseName().equals("Unknown"))
            .findFirst()
            .orElseThrow();
    assertNull(actualUnknownStatusCourse.getApplicationStatus(), "対応するcourseIdを持つApplicationStatusがないためnull");


    // student2 の検証
    StudentDetail detail2 = actualDetails.stream()
            .filter(d -> d.getStudent().getId().equals(2))
            .findFirst()
            .orElseThrow();
    assertEquals("テスト 二郎", detail2.getStudent().getName());
    assertEquals(1, detail2.getStudentCourseList().size(), "student2のコースは1つ");

    // Gitコースの検証 (status3: 受講中)
    StudentCourse actualCourse2a = detail2.getStudentCourseList().getFirst();
    assertNotNull(actualCourse2a.getApplicationStatus(), "GitコースにApplicationStatusがセットされている");
    assertEquals("受講中", actualCourse2a.getApplicationStatus().getStatus(), "Gitコースのステータス名が正しい");
    assertEquals(201, actualCourse2a.getId(), "GitコースのIDが正しい");
  }

  @Test
  void convertStudentDetails_shouldSetEmptyCourseList_whenStudentHasNoMatchingCourses() {
    Student student1 = new Student();
    student1.setId(1);
    List<Student> students = List.of(student1);

    // student1に紐づかないコース情報のみを用意
    StudentCourse course2a = new StudentCourse();
    course2a.setStudentId(2);
    course2a.setId(201); // IDを設定
    List<StudentCourse> allCourses = List.of(course2a);

    // ApplicationStatusのリストを準備
    ApplicationStatus status = createApplicationStatus(1, "仮申込");
    status.setCourseId(201); // course2aのIDに紐づく
    List<ApplicationStatus> applicationStatuses = List.of(status);

    List<StudentDetail> actualDetails = sut.convertStudentDetails(students, allCourses, applicationStatuses);

    assertEquals(1, actualDetails.size());
    StudentDetail detail1 = actualDetails.getFirst();
    assertNotNull(detail1.getStudentCourseList());
    assertTrue(detail1.getStudentCourseList().isEmpty(), "コース情報リストは空のはず");
  }

  @Test
  void convertStudentDetails_shouldReturnEmptyList_whenStudentListIsEmpty() {
    List<Student> emptyStudents = Collections.emptyList();

    // コース情報にもIDを設定する必要がある
    StudentCourse course = new StudentCourse();
    course.setId(1);
    List<StudentCourse> allCourses = List.of(course);

    // ApplicationStatusにもcourseIdを設定する必要がある
    ApplicationStatus status = createApplicationStatus(1, "仮申込");
    status.setCourseId(1);
    List<ApplicationStatus> applicationStatuses = List.of(status);

    List<StudentDetail> actualDetails = sut.convertStudentDetails(emptyStudents, allCourses, applicationStatuses);

    assertNotNull(actualDetails);
    assertTrue(actualDetails.isEmpty(), "結果リストは空のはず");
  }
}