package raisetech.student.management.controller.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import raisetech.student.management.data.Student;

class SearchStudentRequestTest {

    @Test
        // リクエストDTOで設定されたフィールドが正しく検索条件のStudentオブジェクトに変換されること
    void toSearchCondition_shouldMapFieldsCorrectly() {
        // Arrange
        SearchStudentRequest request = new SearchStudentRequest();
        request.setId(10);
        request.setName("テスト太郎");
        request.setKanaName("テストタロウ");
        request.setEmail("test@example.com");
        request.setArea("北海道");
        request.setAge(25);
        request.setSex("男");

        // Act
        Student actualStudent = request.toSearchCondition();

        // Assert
        // DTOで設定したフィールドが正しくStudentにコピーされているか検証
        assertThat(actualStudent.getId()).isEqualTo(10);
        assertThat(actualStudent.getName()).isEqualTo("テスト太郎");
        assertThat(actualStudent.getKanaName()).isEqualTo("テストタロウ");
        assertThat(actualStudent.getEmail()).isEqualTo("test@example.com");
        assertThat(actualStudent.getArea()).isEqualTo("北海道");
        assertThat(actualStudent.getAge()).isEqualTo(25);
        assertThat(actualStudent.getSex()).isEqualTo("男");

        // DTOに設定されていないフィールド（例: nickname）はnullのままか検証
        assertThat(actualStudent.getNickname()).isNull();

        // 論理削除フラグなどのフィールドはDTOで設定していないため、デフォルト値（false）か検証
        assertThat(actualStudent.isDeleted()).isFalse();
    }

    @Test
        // 全てのフィールドがnullの場合、空のStudentオブジェクトが生成されること
    void toSearchCondition_shouldReturnEmptyStudentWhenAllFieldsAreNull() {
        // Arrange: 全てのフィールドがデフォルトのnullのまま
        SearchStudentRequest request = new SearchStudentRequest();

        // Act
        Student actualStudent = request.toSearchCondition();

        // Assert
        // IDや名前などが全てnullであること
        assertThat(actualStudent.getId()).isNull();
        assertThat(actualStudent.getName()).isNull();
        // boolean型のisDeletedはJavaのデフォルト値（false）になること
        assertThat(actualStudent.isDeleted()).isFalse();
    }
}