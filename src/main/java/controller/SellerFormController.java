package controller;

import db.DbException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;
import listeners.DataChangeListener;
import model.entities.Department;
import model.entities.Seller;
import model.exceptions.ValidationException;
import model.services.DepartmentService;
import model.services.SellerService;
import util.Alerts;
import util.Constraints;
import util.Utils;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class SellerFormController implements Initializable {

    private Seller entity;

    private SellerService service;

    private DepartmentService departmentService;

    private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

    @FXML
    private Label labelTitle;

    @FXML
    private TextField textFieldId;

    @FXML
    private TextField textFieldName;

    @FXML
    private TextField textFieldEmail;

    @FXML
    private DatePicker datePickerBirthDate;

    @FXML
    private TextField textFieldBaseSalary;

    @FXML
    private ComboBox<Department> comboBoxDepartment;
    private ObservableList<Department> observableList;

    @FXML
    private Label labelErrorName;

    @FXML
    private Label labelErrorEmail;

    @FXML
    private Label labelErrorBirthDate;

    @FXML
    private Label labelErrorBaseSalary;

    @FXML
    private Button buttonSave;

    @FXML
    private Button buttonCancel;

    public void setSeller(Seller entity) {
        this.entity = entity;
    }

    public void setServices (SellerService service, DepartmentService departmentService)  {

        this.service = service;
        this.departmentService = departmentService;

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
            Alerts.showAlert("Success!" , null , "Seller saved successful!", Alert.AlertType.INFORMATION);
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

    private Seller getFormData() {

        Seller seller = new Seller();

        ValidationException exception = new ValidationException("Validation Error");

        seller.setId(Utils.tryParseToInt(textFieldId.getText()));

        if (textFieldName.getText() == null || textFieldName.getText().trim().equals("")) {
            exception.addError("name","Field can't be empty");
        }
        seller.setName(textFieldName.getText());

        if (exception.getErrors().size() > 0) {
            throw exception;
        }

        return seller;
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
        Constraints.setTextFieldMaxLength(textFieldName,70);
        Constraints.setTextFieldDouble(textFieldBaseSalary);
        Constraints.setTextFieldMaxLength(textFieldEmail,60);
        Utils.formatDatePicker(datePickerBirthDate, "dd/MM/yyyy");

        initializeComboBoxDepartment();

    }

    public void updateFormData() {
        if (entity == null) {
            throw new IllegalStateException("Entity was null");
        }
        textFieldId.setText(String.valueOf(entity.getId()));
        textFieldName.setText(entity.getName());
        textFieldEmail.setText(entity.getEmail());
        if (entity.getBaseSalary() != null) {
            datePickerBirthDate.setValue(LocalDate.ofInstant(entity.getBirthDate().toInstant(), ZoneId.systemDefault()));
        }
        textFieldBaseSalary.setText(String.format("%.2f", entity.getBaseSalary()));
        if (entity.getDepartment() == null) {
            comboBoxDepartment.getSelectionModel().selectFirst();
        } else {
            comboBoxDepartment.setValue(entity.getDepartment());
        }
    }

    public void loadAssossiatedObjects() {
        if (departmentService == null) {
            throw new IllegalStateException("DepartmentService was null!");
        }

        List<Department> list = departmentService.findAll();
        observableList = FXCollections.observableArrayList(list);
        comboBoxDepartment.setItems(observableList);
    }

    private void setErrorMessages(Map<String,String> errors) {
        Set<String> fields = errors.keySet();

        if (fields.contains("name")) {
            labelErrorName.setText(errors.get("name"));
        }
    }

    private void initializeComboBoxDepartment() {
        Callback<ListView<Department>, ListCell<Department>> factory = lv -> new ListCell<Department>() {
            @Override
            protected void updateItem(Department item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getName());
            }
        };
        comboBoxDepartment.setCellFactory(factory);
        comboBoxDepartment.setButtonCell(factory.call(null));
    }
}
