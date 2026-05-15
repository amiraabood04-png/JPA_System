/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import dao.CourseDAO;
import dao.EnrollmentDAO;
import dao.StudentDAO;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Course;
import models.Enrollment;
import models.Student;

/**
 * FXML Controller class
 *
 * @author HP
 */
public class EnrollmentController implements Initializable {

    @FXML
    private ComboBox<Integer> studentsCombobox;
    @FXML
    private ComboBox<Integer> coursesCombobox;
    @FXML
    private DatePicker enrollmentDate;
    @FXML
    private TableView<Enrollment> table;
    @FXML
    private TableColumn<Enrollment, Integer> enrollmentIdTC;
    @FXML
    private TableColumn<Enrollment, Integer> studentIdTC;
    @FXML
    private TableColumn<Enrollment, Integer> courseIdTC;
    @FXML
    private TableColumn<Enrollment, String> enrollmentDateTC;

    StudentDAO studentdao = new StudentDAO();
    CourseDAO coursedao = new CourseDAO();
    EnrollmentDAO enrollmentdao = new EnrollmentDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        enrollmentIdTC.setCellValueFactory(new PropertyValueFactory<>("enrollmentId"));
        studentIdTC.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        courseIdTC.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        enrollmentDateTC.setCellValueFactory(new PropertyValueFactory<>("enrollmentDate"));

        List<Integer> studentIds = studentdao.getAllStudentIds();
        studentsCombobox.getItems().addAll(studentIds);

        List<Integer> courseIds = coursedao.getAllCourseIds();
        coursesCombobox.getItems().addAll(courseIds);

        table.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {

                    if (newValue == null) {
                        return;
                    }

                    studentsCombobox.setValue(newValue.getStudentId());
                    coursesCombobox.setValue(newValue.getCourseId());
                    enrollmentDate.setValue(LocalDate.parse(newValue.getEnrollmentDate())
                    );
                });
    }

    @FXML
    private void addHandle(ActionEvent event) {
        if (studentsCombobox.getValue() == null
                || coursesCombobox.getValue() == null
                || enrollmentDate.getValue() == null) {

            showAlert("Missing Data", "Please select student, course, and date");
            return;
        }
        Integer studentId = studentsCombobox.getValue();
        Integer courseId = coursesCombobox.getValue();

        Student student = studentdao.findById(studentId);
        Course course = coursedao.findById(courseId);
        Enrollment e = new Enrollment(student, course, enrollmentDate.getValue().toString());

        boolean success = enrollmentdao.insertOne(e);

        if (success) {
            showAlert("Success", "Enrollment added successfully");
            clearHandle(event);
            viewHandle(event);
        } else {
            showAlert("Duplicate", "This student is already enrolled in this course");
        }
    }

    @FXML
    private void updateHandle(ActionEvent event) {
        Enrollment selected = table.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showWarningAlert(
                    "No Selection",
                    "No Record Selected",
                    "Please select an enrollment record from the table"
            );
            return;
        }

        if (studentsCombobox.getValue() == null
                || coursesCombobox.getValue() == null
                || enrollmentDate.getValue() == null) {

            showAlert("Missing Data", "Please fill all fields");
            return;
        }

        Student student = studentdao.findById(studentsCombobox.getValue());
        Course course = coursedao.findById(coursesCombobox.getValue());

        selected.setStudent(student);
        selected.setCourse(course);
        selected.setEnrollmentDate(enrollmentDate.getValue().toString());

        boolean success = enrollmentdao.updateOne(selected);

        if (success) {
            showAlert("Success", "Enrollment updated successfully");
            viewHandle(event);
            clearHandle(event);
        }
    }

    @FXML
    private void deleteHandle(ActionEvent event) {

        Enrollment e
                = table.getSelectionModel().getSelectedItem();

        if (e == null) {

            showWarningAlert(
                    "No Selection",
                    "No Record Selected",
                    "Please select an enrollment record from the table"
            );

        } else {

            if (showConfirmationAlert(
                    "Delete Confirmation",
                    "Are you sure",
                    "Do you want to delete this enrollment?"
            )) {

                enrollmentdao.deleteOne(e);

                viewHandle(event);

                clearHandle(event);
            }
        }
    }

    @FXML
    private void viewHandle(ActionEvent event) {
        List<Enrollment> enrollments = enrollmentdao.findAll();
        table.getItems().setAll(enrollments);
    }

    @FXML
    private void clearHandle(ActionEvent event) {
        studentsCombobox.setValue(null);
        coursesCombobox.setValue(null);
        enrollmentDate.setValue(null);
        table.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarningAlert(String title, String header, String message) {

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();

    }

    private boolean showConfirmationAlert(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            return true;
        }
        return false;
    }

}
