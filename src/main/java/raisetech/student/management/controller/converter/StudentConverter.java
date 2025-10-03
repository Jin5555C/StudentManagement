package raisetech.student.management.controller.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import raisetech.student.management.data.ApplicationStatus;
import raisetech.student.management.data.Student;
import raisetech.student.management.data.StudentCourse;
import raisetech.student.management.domain.StudentDetail;

@Component
public class StudentConverter {

    public List<StudentDetail> convertStudentDetails(List<Student> students, List<StudentCourse> studentCourses,
                                                     List<ApplicationStatus> applicationStatuses) {
        List<StudentDetail> studentDetails = new ArrayList<>();

        Map<Integer, ApplicationStatus> applicationStatusMap = applicationStatuses.stream()
                .filter(as -> as.getCourseId() != null)
                .collect(Collectors.toMap(ApplicationStatus::getCourseId, Function.identity()));

        students.forEach(student -> {
            StudentDetail studentDetail = new StudentDetail();
            studentDetail.setStudent(student);

            // courseId と applicationIdは 1:1 対応で重複することはない。
            List<StudentCourse> convertStudentCourse = studentCourses.stream()
                    .filter(studentCourse -> student.getId().equals(studentCourse.getStudentId()))
                    .map(studentCourse -> {
                        ApplicationStatus status = applicationStatusMap.get(studentCourse.getId());

                        if (status != null) {
                            studentCourse.setApplicationStatus(status);
                        }

                        return studentCourse;
                    })
                    .collect(Collectors.toList());

            studentDetail.setStudentCourseList(convertStudentCourse);
            studentDetails.add(studentDetail);
        });
        return studentDetails;
    }
}