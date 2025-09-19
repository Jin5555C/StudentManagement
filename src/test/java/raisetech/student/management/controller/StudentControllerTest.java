package raisetech.student.management.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import raisetech.student.management.domain.StudentDetail;
import raisetech.student.management.service.StudentService;

class StudentControllerTest {

  private MockMvc mockMvc;
  private StudentService service;
  private StudentController controller;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setup() {
    service = Mockito.mock(StudentService.class);
    controller = new StudentController(service);
    mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    objectMapper = new ObjectMapper();
  }

  @Test
  void getStudentList_shouldReturnEmptyList() throws Exception {
    when(service.searchStudentList()).thenReturn(Collections.emptyList());

    mockMvc.perform(MockMvcRequestBuilders.get("/studentList"))
        .andExpect(status().isOk())
        .andExpect(content().json("[]")); // 空リストを期待

    verify(service, times(1)).searchStudentList();
  }

  @Test
  void getStudent_shouldReturnStudentDetail() throws Exception {
    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(null);
    studentDetail.setStudentCourseList(Collections.emptyList());

    when(service.searchStudent(1)).thenReturn(studentDetail);

    mockMvc.perform(MockMvcRequestBuilders.get("/student/1"))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(studentDetail)));

    verify(service, times(1)).searchStudent(1);
  }

  @Test
  void getStudent_shouldReturn404WhenNotFound() throws Exception {
    when(service.searchStudent(99)).thenReturn(null);

    mockMvc.perform(MockMvcRequestBuilders.get("/student/99"))
        .andExpect(status().isNotFound());

    verify(service, times(1)).searchStudent(99);
  }

  @Test
  void registerStudent_shouldReturnRegisteredStudent() throws Exception {
    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(null);
    studentDetail.setStudentCourseList(Collections.emptyList());

    when(service.registerStudent(any(StudentDetail.class))).thenReturn(studentDetail);

    mockMvc.perform(MockMvcRequestBuilders.post("/registerStudent")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(studentDetail)))
        .andExpect(status().isOk())
        .andExpect(content().json(objectMapper.writeValueAsString(studentDetail)));

    verify(service, times(1)).registerStudent(any(StudentDetail.class));
  }


  @Test
  void updateStudent_shouldReturnSuccessMessage() throws Exception {
    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(null);
    studentDetail.setStudentCourseList(Collections.emptyList());

    mockMvc.perform(MockMvcRequestBuilders.put("/updateStudent")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(studentDetail)))
        .andExpect(status().isOk())
        .andExpect(content().string("success updating"));

    verify(service, times(1)).updateStudent(any(StudentDetail.class));
  }
}
