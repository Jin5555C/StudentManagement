package raisetech.student.management.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourse;

@MybatisTest
class StudentRepositoryTest {

  @Autowired
  private StudentRepository sut;

  @Test
// 受講生の全件検索が行えること
  void search_shouldFindAllStudent() {
    List<Student> actual = sut.search();
    assertThat(actual.size()).isEqualTo(5);
  }

  @Test
  void IDを指定して受講生の検索が行えること() {
    Student actual = sut.searchStudent(1);
    assertThat(actual.getName()).isEqualTo("佐藤 太郎");
    assertThat(actual.getKanaName()).isEqualTo("サトウ タロウ");
    assertThat(actual.getEmail()).isEqualTo("taro.sato@example.com");
  }

  @Test
  void 受講生コース情報の全件検索が行えること() {
    List<StudentCourse> actual = sut.searchStudentCourseList();
    assertThat(actual.size()).isEqualTo(10);
  }

  @Test
  void 受講生IDを指定してコース情報が検索できること() {
    List<StudentCourse> actual = sut.searchStudentCourse(1);
    assertThat(actual.size()).isEqualTo(2);
    assertThat(actual).extracting(StudentCourse::getCourseName)
        .containsExactly("Java基礎", "Spring Boot入門");
  }

  @Test
  void 受講生の登録が行えること() {
    Student student = new Student();
    student.setName("新規太郎");
    student.setKanaName("シンキタロウ");
    student.setNickname("たろー");
    student.setEmail("taro@test.com");
    student.setArea("東京都");
    student.setAge(36);
    student.setRemark("");
    student.setDeleted(false);

    sut.registerStudent(student);

    List<Student> actual = sut.search();

    assertThat(actual.size()).isEqualTo(6);
    assertThat(student.getId()).isNotNull();
  }

  @Test
  void 受講生コース情報の登録が行えること() {
    StudentCourse studentCourse = new StudentCourse();
    // 受講生ID:5の伊藤さんに新しいコースを追加
    studentCourse.setStudentId(5);
    studentCourse.setCourseName("Go言語入門");
    studentCourse.setCourseStartAt(LocalDate.of(2025, 4, 1).atStartOfDay());
    studentCourse.setCourseEndAt(LocalDate.of(2026, 3, 31).atStartOfDay());

    sut.registerStudentCourse(studentCourse);

    List<StudentCourse> actual = sut.searchStudentCourse(5);
    assertThat(actual.size()).isEqualTo(3);
    assertThat(actual).extracting(StudentCourse::getCourseName).contains("Go言語入門");
  }

  @Test
  void 受講生情報の更新が行えること() {
    // ID:2の鈴木さんの情報を更新する
    Student student = sut.searchStudent(2);
    student.setName("鈴木 愛子");
    student.setNickname("あいちゃん");
    student.setArea("京都");

    sut.updateStudent(student);

    Student actual = sut.searchStudent(2);
    assertThat(actual.getName()).isEqualTo("鈴木 愛子");
    assertThat(actual.getNickname()).isEqualTo("あいちゃん");
    assertThat(actual.getArea()).isEqualTo("京都");
  }

  @Test
  void 受講生コース情報の更新が行えること() {
    // 受講生ID:3の田中さんのコース情報を取得
    List<StudentCourse> courses = sut.searchStudentCourse(3);
    StudentCourse targetCourse = courses.get(0); // 最初のコース「JavaScript基礎」を取得
    assertThat(targetCourse.getCourseName()).isEqualTo("JavaScript基礎");

    // コース名を変更して更新
    targetCourse.setCourseName("JavaScript応用マスター");
    sut.updateStudentCourse(targetCourse);

    List<StudentCourse> actual = sut.searchStudentCourse(3);
    // コース名が更新されていることを確認
    assertThat(actual).extracting(StudentCourse::getCourseName)
        .contains("JavaScript応用マスター");
    // 古いコース名が存在しないことを確認
    assertThat(actual).extracting(StudentCourse::getCourseName)
        .doesNotContain("JavaScript基礎");
  }
}
