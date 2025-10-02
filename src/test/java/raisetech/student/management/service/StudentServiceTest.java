package raisetech.student.management.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static util.TestDataFactory.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import raisetech.student.management.controller.converter.StudentConverter;
import raisetech.student.management.data.ApplicationStatus;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourse;
import raisetech.student.management.domain.StudentDetail;
import raisetech.student.management.repository.StudentRepository;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

  @Mock
  private StudentRepository repository;

  @Mock
  private StudentConverter converter;

  private StudentService sut;

  @BeforeEach
  void before() {
    sut = new StudentService(repository, converter);

  }

  // 受講生詳細の一覧検索_リポジトリとコンバーターの処理が適切に呼び出せていること
  @Test
  void searchStudentDetails_shouldCallRepositoryAndConverterProperly() {
    List<Student> studentList = new ArrayList<>();
    List<StudentCourse> studentCourseList = new ArrayList<>();
    when(repository.search()).thenReturn(studentList);
    when(repository.searchStudentCourseList()).thenReturn(studentCourseList);

    sut.searchStudentList();

    verify(repository, times(1)).search();
    verify(repository, times(1)).searchStudentCourseList();
    verify(converter, times(1)).convertStudentDetails(studentList, studentCourseList);
  }

  // 受講生詳細の１件検索_単一の受講生Idに紐づく受講生情報とコース情報がレポジトリから適切に呼び出せていること
  @Test
  void findStudentDetailById_shouldRetrieveStudentAndCourseFromRepository() {
    Integer studentId = 1;
    Student student = createStudent(studentId);

    List<StudentCourse> studentCourseList = new ArrayList<>();

    when(repository.searchStudent(studentId)).thenReturn(student);
    when(repository.searchStudentCourse(studentId)).thenReturn(studentCourseList);

    StudentDetail actual = sut.searchStudent(studentId);

    verify(repository, times(1)).searchStudent(studentId);
    verify(repository, times(1)).searchStudentCourse(studentId);

    assertThat(actual.getStudent())
        .as("受講生情報が一致していること")
        .isEqualTo(student);
    assertThat(actual.getStudentCourseList())
        .as("コース情報が一致していること")
        .isEqualTo(studentCourseList);
  }

  //  受講生詳細検索_studentがnullの場合はnullを返すこと
  @Test
  void searchStudentDetails_shouldReturnNullWhenStudentIsNull() {
    Integer studentId = 1;
    when(repository.searchStudent(studentId)).thenReturn(null);

    StudentDetail actual = sut.searchStudent(studentId);

    verify(repository, times(1)).searchStudent(studentId);
    verify(repository, never()).searchStudentCourse(anyInt());

    assertThat(actual).isNull();
  }

  //  受講生条件検索_条件に当てはまる受講生一覧を表示する
  @Test
  void searchStudentList_shouldReturnStudentListWhenStudentsAreFound() {
    Student searchCondition = new Student();
    searchCondition.setName("テスト太郎");

    Student student1 = createStudent(1);
    Student student2 = createStudent(2);
    List<Student> studentList = List.of(student1, student2);
    when(repository.searchStudentList(searchCondition)).thenReturn(studentList);

    List<Integer> studentIdList = List.of(1, 2);
    List<StudentCourse> studentCourseList = List.of(createCourse(101), createCourse(102));
    when(repository.searchStudentCoursesByStudentIdList(studentIdList)).thenReturn(
        studentCourseList);

    List<StudentDetail> expectedStudentDetails = List.of(new StudentDetail(), new StudentDetail());
    when(converter.convertStudentDetails(studentList, studentCourseList)).thenReturn(
        expectedStudentDetails);

    List<StudentDetail> actual = sut.searchStudentList(searchCondition);

    verify(repository, times(1)).searchStudentList(searchCondition);
    verify(repository, times(1)).searchStudentCoursesByStudentIdList(studentIdList);
    verify(converter, times(1)).convertStudentDetails(studentList, studentCourseList);

    assertThat(actual).isEqualTo(expectedStudentDetails);
  }

  //  受講生条件検索_条件に当てはまる受講生が見つからない場合に空のリストを返すこと
  @Test
  void searchStudentList_shouldReturnEmptyListWhenNoStudentsAreFound() {
    Student searchCondition = new Student();
    searchCondition.setName("存在しない名前");

    when(repository.searchStudentList(searchCondition)).thenReturn(new ArrayList<>());

    List<StudentDetail> actual = sut.searchStudentList(searchCondition);

    verify(repository, times(1)).searchStudentList(searchCondition);
    verify(repository, never()).searchStudentCoursesByStudentIdList(anyList());
    verify(converter, never()).convertStudentDetails(anyList(), anyList());

    assertThat(actual).isEmpty();
  }

  // 受講生詳細登録_受講生と受講生コース情報を別々のテーブルに登録できること
  @Test
  void registerStudentDetail_shouldInsertStudentAndCourseSeparately() {
    Student student = createStudent(1);
    StudentCourse course1 = createCourse(null);
    StudentCourse course2 = createCourse(null);

    StudentDetail studentDetail = createStudentDetail(student, course1, course2);
    StudentDetail result = sut.registerStudent(studentDetail);

    verify(repository).registerStudent(student);
    verify(repository).registerStudentCourse(course1);
    verify(repository).registerStudentCourse(course2);

    assertThat(result).isEqualTo(studentDetail);
  }

  //  受講生詳細の更新ができる_コースも更新
  @Test
  void updateStudentDetail_shouldUpdateCourse() {
    Student student = createStudent(1);

    StudentCourse newCourse = createCourse(null); //  新規追加
    StudentCourse existingCourse = createCourse(100); // 既存更新

    StudentDetail studentDetail = createStudentDetail(student, newCourse, existingCourse);
    sut.updateStudent(studentDetail);

    verify(repository).updateStudent(student);
    verify(repository).registerStudentCourse(newCourse);
    verify(repository).updateStudentCourse(existingCourse);
  }

  // 受講生詳細の更新ができる_コースは新規登録
  @Test
  void updateStudentDetail_shouldRegisterNewCourse() {
    Student student = createStudent(1);

    StudentDetail studentDetail = createStudentDetail(student, (StudentCourse[]) null);

    sut.updateStudent(studentDetail);

    verify(repository).updateStudent(student);
  }

  // 申込状況の全件検索が行えること
  @Test
  void searchApplicationStatusList_shouldReturnListFromRepository(){
    List<ApplicationStatus> expectedList = List.of(
            createApplicationStatus(1, "仮申込"),
            createApplicationStatus(2, "本申込"),
            createApplicationStatus(3, "受講中")
    );
    when(repository.searchApplicationStatusList()).thenReturn(expectedList);

    List<ApplicationStatus> actualList = sut.searchApplicationStatusList();

    assertThat(actualList).isEqualTo(expectedList);
    verify(repository,times(1)).searchApplicationStatusList();
  }

  // IDを指定して申し込み状況の検索が行えること
  @Test
  void searchApplicationStatus_shouldReturnStatusFromRepository() {
    Integer targetId = 2;
    ApplicationStatus expectedStatus = createApplicationStatus(targetId, "本申込");
    when(repository.searchApplicationStatus(targetId)).thenReturn(expectedStatus);

    ApplicationStatus actualStatus = sut.searchApplicationStatus(targetId);

    assertThat(actualStatus).isEqualTo(expectedStatus);
    verify(repository, times(1)).searchApplicationStatus(targetId);
  }

}
