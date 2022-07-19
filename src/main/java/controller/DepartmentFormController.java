package controller;

import db.DbException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import listeners.DataChangeListener;
import model.entities.Department;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import util.Alerts;
import util.Constraints;
import util.Utils;

import java.net.URL;
import java.util.*;

public class DepartmentFormController implements Initializable {

    private Department entity;

    private DepartmentService service;

    private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

    @FXML
    private Label labelTitle;

    @FXML
    private TextField textFieldId;

    @FXML
    private TextField textFieldName;

    @FXML
    private Label labelErrorName;

    @FXML
    private Button buttonSave;

    @FXML
    private Button buttonCancel;

    public void setDepartment(Department entity) {
        this.entity = entity;
    }

    public void setDepartmentService (DepartmentService service) {
        this.service = service;
    }

    public void subscribeDataChangeListener(DataChangeListener listener) {
        dataChangeListeners.add(listener);
    }

    @FXML
    public void onButtonSaveAction(ActionEvent event) {
        if (entity == null) {
            throw new IllegalStateException("Entity was null!");
        }
        if (service == null) {
            throw new IllegalStateException("Service was null!");
        }
        try {
            entity = getFormData();
            service.saveOrUpdate(entity);
            Alerts.showAlert("Success!" , null , "Department saved successful!", Alert.AlertType.INFORMATION);
            notifyDataChangeListeners();
            Utils.currentStage(event).close();
        } catch (DbException e) {
            Alerts.showAlert("Error saving object" , null , e.getMessage(), Alert.AlertType.ERROR);
        } catch (ValidationException e) {
            setErrorMessages(e.getErrors());
        }
    }

    private void notifyDataChangeListeners() {
        dataChangeListeners.forEach(x -> x.onDataChanged());
    }

    private Department getFormData() {

        Department dep = new Department();

        ValidationException exception = new ValidationException("Validation Error");

        dep.setId(Utils.tryParseToInt(textFieldId.getText()));

        if (textFieldName.getText() == null || textFieldName.getText().trim().equals("")) {
            exception.addError("name","Field can't be empty");
        }
        dep.setName(textFieldName.getText());

        if (exception.getErrors().size() > 0) {
            throw exception;
        }

        return dep;
    }

    @FXML
    public void onButtonCancelAction(ActionEvent event) {
        Utils.currentStage(event).close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeNodes();
    }

    private void initializeNodes(){
        Constraints.setTextFieldInteger(textFieldId);
        Constraints.setTextFieldMaxLength(textFieldName,30);

    }

    public void updateFormData() {
        if (entity == null) {
            throw new IllegalStateException("Entity was null");
        }
        textFieldId.setText(String.valueOf(entity.getId()));
        textFieldName.setText(entity.getName());
    }

    private void setErrorMessages(Map<String,String> errors) {
        Set<String> fields = errors.keySet();

        if (fields.contains("name")) {
            labelErrorName.setText(errors.get("name"));
        }
    }
}
