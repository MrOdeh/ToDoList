package com.odeh.todolist;

import com.odeh.todolist.datamodel.TodoData;
import com.odeh.todolist.datamodel.TodoItem;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class DialogController {
    @FXML
    private TextField shortDescriptionId;
    @FXML
    private TextArea detailsId;
    @FXML
    private DatePicker deadlineId;

    public TodoItem processResult() {
        String shortDescription = shortDescriptionId.getText().trim().toLowerCase();
        String details = detailsId.getText().trim().toLowerCase();
        LocalDate deadline = deadlineId.getValue();

        TodoItem newItem = new TodoItem(shortDescription, details, deadline);
        TodoData.getInstance().addToItem(newItem);
        return newItem;
    }
}
