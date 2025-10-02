package raisetech.student.management.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import raisetech.student.management.data.ApplicationStatus;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourse;

@MybatisTest
class StudentRepositoryTest {

  @Autowired
  private StudentRepository sut;

  @Test
// 受講生の全件検索が行えること
  void search_shouldFindAllStudentList() {
    List<Student> actual = sut.search();
    assertThat(actual).hasSize(5);
  }

  @Test
//  IDを指定して受講生の検索が行えること
  void searchStudent_shouldFindExactlyOneStudent() {
    Student actual = sut.searchStudent(1);
    assertThat(actual.getName()).isEqualTo("佐藤 太郎");
    assertThat(actual.getKanaName()).isEqualTo("サトウ タロウ");
    assertThat(actual.getEmail()).isEqualTo("taro.sato@example.com");
  }

  @Test
// 受講生コース情報の全件検索が行えること
  void searchStudentCourseList_shouldFindAllCourseList() {
    List<StudentCourse> actual = sut.searchStudentCourseList();
    assertThat(actual).hasSize(10);
  }

  @Test
  // 受講生IDを指定して、その受講コース一覧が正しく取得できることを確認するテスト
  void searchStudentCourse_shouldFindCoursesByStudentId() {
    List<StudentCourse> actual = sut.searchStudentCourse(1);
    assertThat(actual).hasSize(2);
    assertThat(actual).extracting(StudentCourse::getCourseName)
        .containsExactly("Java基礎", "Spring Boot入門");
  }

  @Test
    // 受講生の登録が行えること
  void registerStudent_shouldInsertNewStudent() {
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

    List<Student> actualList = sut.search();
    assertThat(actualList).hasSize(6);

    Student actual = sut.searchStudent(student.getId());
    assertThat(actual).isNotNull();
    assertThat(actual.getName()).isEqualTo("新規太郎");
    assertThat(actual.getKanaName()).isEqualTo("シンキタロウ");
    assertThat(actual.getEmail()).isEqualTo("taro@test.com");
  }

  @Test
//  受講生コース情報の登録が行えること
  void registerStudentCourse_shouldInsertNewStudentCourse() {
    StudentCourse studentCourse = new StudentCourse();
    // 受講生ID:5の伊藤さんに新しいコースを追加
    studentCourse.setStudentId(5);
    studentCourse.setCourseName("Go言語入門");
    studentCourse.setCourseStartAt(LocalDate.of(2025, 4, 1).atStartOfDay());
    studentCourse.setCourseEndAt(LocalDate.of(2026, 3, 31).atStartOfDay());

    sut.registerStudentCourse(studentCourse);

    List<StudentCourse> actual = sut.searchStudentCourse(5);
    assertThat(actual).hasSize(3);
    assertThat(actual).extracting(StudentCourse::getCourseName).contains("Go言語入門");
  }

  @Test
//   受講生情報の更新が行えること
  void updateStudent_shouldUpdateStudentInfo() {
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
//  受講生コース情報の更新が行えること
  void updateStudentCourse_shouldUpdateStudentCourseInfo() {
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

  @Test
    //  受講生IDのリストに紐づく受講生コース情報を検索できること
  void searchStudentCoursesByStudentIdList_shouldFindCoursesForMultipleStudents() {
    // 検索対象の受講生IDリストを作成 (ID: 1 の佐藤さんと ID: 3 の田中さん)
    List<Integer> studentIdList = List.of(1, 3);

    // 作成したIDリストを使って、コース情報を検索
    List<StudentCourse> actual = sut.searchStudentCoursesByStudentIdList(studentIdList);

    // 合計4件のコース情報が取得できることを確認 (佐藤さん:2件 + 田中さん:2件)
    assertThat(actual.size()).isEqualTo(4);

    // 取得したコース名に期待通りのものが含まれているか順不同で確認
    assertThat(actual).extracting(StudentCourse::getCourseName)
        .containsExactlyInAnyOrder("Java基礎", "Spring Boot入門", "JavaScript基礎", "React応用");
  }

  @Test
  //  存在しない受講生IDのリストを渡した場合に空のリストを返すこと
  void searchStudentCoursesByStudentIdList_shouldReturnEmptyListForNonExistentIds() {
    List<Integer> studentIdList = List.of(998, 999);
    List<StudentCourse> actual = sut.searchStudentCoursesByStudentIdList(studentIdList);

    assertThat(actual).isEmpty();
  }

  @Test
  // 申し込み状況の全件検索が行えること
  void searchApplicationStatusList_shouldFindAllStatuses() {
    List<ApplicationStatus> actual = sut.searchApplicationStatusList();
    assertThat(actual).hasSize(5);
    assertThat(actual).extracting(ApplicationStatus::getStatus)
            .contains("仮申込", "本申込", "受講中", "受講終了");
  }

  @Test
  // IDを指定して申し込み状況の検索が行えること
  void searchApplicationStatus_shouldFindExactlyOneStatus() {
    Integer targetId = 3;
    ApplicationStatus actual = sut.searchApplicationStatus(targetId);

    assertThat(actual.getId()).isEqualTo(targetId);
    assertThat(actual.getStatus()).isEqualTo("受講中");
    assertThat(actual.getCourseId()).isEqualTo(3);
  }
}
