package raisetech.student.management.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import raisetech.student.management.controller.converter.StudentConverter;
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

  private  StudentService sut;

  @BeforeEach
  void before(){
    sut = new StudentService(repository,converter);

  }
//  void 受講生詳細の一覧検索_リポジトリとコンバーターの処理が適切に呼び出せていること()
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

  @Test
//void 受講生詳細の１件検索_単一の受講生Idに紐づく受講生情報とコース情報がレポジトリから適切に呼び出せていること()
  void findStudentDetailById_shouldRetrieveStudentAndCourseFromRepository() {
    Integer studentId = 1;
    Student student = new Student();
    student.setId(studentId);

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

//  void 受講生詳細検索_studentがnullの場合はnullを返すこと()
  @Test
  void searchStudentDetails_shouldReturnNullWhenStudentIsNull() {
    Integer studentId = 1;
    when(repository.searchStudent(studentId)).thenReturn(null);

    StudentDetail actual = sut.searchStudent(studentId);

    verify(repository, times(1)).searchStudent(studentId);
    verify(repository, never()).searchStudentCourse(anyInt());

    assertThat(actual).isNull();
  }

  @Test
//  void 受講生詳細登録_受講生と受講生コース情報を別々のテーブルに登録できること()
  void registerStudentDetail_shouldInsertStudentAndCourseSeparately() {
    Student student = new Student();
    StudentCourse course1 = new StudentCourse();
    StudentCourse course2 = new StudentCourse();

    List<StudentCourse> courseList = Arrays.asList(course1, course2);

    StudentDetail studentDetail = new StudentDetail(student, courseList);
    StudentDetail result = sut.registerStudent(studentDetail);

    verify(repository).registerStudent(student);
    verify(repository).registerStudentCourse(course1);
    verify(repository).registerStudentCourse(course2);

    assertThat(result).isEqualTo(studentDetail);
  }

  @Test
//  void 受講生詳細の更新ができる_コースも更新()
  void updateStudentDetail_shouldUpdateCourse() {
    Student student = new Student();

    StudentCourse newCourse = new StudentCourse(); // getId() == null → 新規追加
    StudentCourse existingCourse = new StudentCourse();
    existingCourse.setId(100); // getId() != null → 既存更新

    List<StudentCourse> courseList = Arrays.asList(newCourse, existingCourse);

    StudentDetail studentDetail = mock(StudentDetail.class);
    when(studentDetail.getStudent()).thenReturn(student);
    when(studentDetail.getStudentCourseList()).thenReturn(courseList);

    sut.updateStudent(studentDetail);

    verify(repository).updateStudent(student);
    verify(repository).registerStudentCourse(newCourse);
    verify(repository).updateStudentCourse(existingCourse);
  }

  @Test
//  void 受講生詳細の更新ができる_コースは新規登録()
  void updateStudentDetail_shouldRegisterNewCourse() {
    Student student = new Student();

    StudentDetail studentDetail = mock(StudentDetail.class);
    when(studentDetail.getStudent()).thenReturn(student);
    when(studentDetail.getStudentCourseList()).thenReturn(null);

    sut.updateStudent(studentDetail);

    verify(repository).updateStudent(student);
  }
}