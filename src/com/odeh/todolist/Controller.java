package com.odeh.todolist;

import com.odeh.todolist.datamodel.TodoData;
import com.odeh.todolist.datamodel.TodoItem;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Controller {
    private List<TodoItem> items;

    @FXML
    private ListView<TodoItem> listViewTag;
    @FXML
    private TextArea area;
    @FXML
    private Label dateLabel;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private ContextMenu listContextMenu;
    @FXML
    private ToggleButton filterToggleButton;
    private FilteredList<TodoItem> todoItemFilteredList;
    private Predicate<TodoItem> wantAllItems;
    private Predicate<TodoItem> wantTodaysItems;


    public void initialize() {
        listContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                TodoItem item = listViewTag.getSelectionModel().getSelectedItem();
                deleteItem(item);
            }
        });
        listContextMenu.getItems().addAll(deleteMenuItem);
//        TodoItem item1 = new TodoItem("Mahmoud's Birthday","on his home",
//                LocalDate.of(2020, Month.APRIL,22));
//        TodoItem item2 = new TodoItem("Ramadan","homee",
//                LocalDate.of(2020, Month.APRIL,24));
//        TodoItem item3 = new TodoItem("Sarah Twjihi","dont forget to say great sarah",
//                LocalDate.of(2019, Month.NOVEMBER,21));
//        TodoItem item4 = new TodoItem("Mohammad tohowr","get him a nutaila gift",
//                LocalDate.of(1994, Month.NOVEMBER,21));
//        TodoItem item5 = new TodoItem("FIrst work days for team","ya rat ma yeeji bil marah",
//                LocalDate.of(2020, Month.JUNE,01));
//
//        items = new ArrayList<TodoItem>();
//        items.add(item1);
//        items.add(item2);
//        items.add(item3);
//        items.add(item4);
//        items.add(item5);
//        TodoData.getInstance().setTodoItems(items);
        listViewTag.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TodoItem>() {
            @Override
            public void changed(ObservableValue<? extends TodoItem> observableValue, TodoItem oldValue, TodoItem newValue) {
                if (newValue != null) {
                    TodoItem defultItem = listViewTag.getSelectionModel().getSelectedItem();
                    area.setText(defultItem.getDetails().toString());
                    DateTimeFormatter currentFormatedDate = DateTimeFormatter.ofPattern("dd-MMMM-YYYY");
                    dateLabel.setText(currentFormatedDate.format(defultItem.getDeadline()));
                }
            }
        });
        wantAllItems = new Predicate<TodoItem>() {
            @Override
            public boolean test(TodoItem item) {
                return true;
            }
        };
        wantTodaysItems = new Predicate<TodoItem>() {
            @Override
            public boolean test(TodoItem item) {
                return item.getDeadline().equals(LocalDate.now());
            }
        };
        todoItemFilteredList = new FilteredList<TodoItem>(TodoData.getInstance().getTodoItems(), wantAllItems);
        SortedList<TodoItem> sortedList = new SortedList<>(todoItemFilteredList,
                new Comparator<TodoItem>() {
                    @Override
                    public int compare(TodoItem o1, TodoItem o2) {
                        return o2.getDeadline().compareTo(o1.getDeadline());
                    }
                });
        //listViewTag.setItems(TodoData.getInstance().getTodoItems());
        listViewTag.setItems(sortedList);
        listViewTag.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listViewTag.getSelectionModel().selectFirst();
        listViewTag.setCellFactory(new Callback<ListView<TodoItem>, ListCell<TodoItem>>() {
            @Override
            public ListCell<TodoItem> call(ListView<TodoItem> todoItemListView) {
                ListCell<TodoItem> cell = new ListCell<>() {
                    @Override
                    protected void updateItem(TodoItem todoItem, boolean b) {
                        super.updateItem(todoItem, b);
                        if (b) {
                            setText(null);
                        } else {
                            setText(todoItem.getShortDescription());
                            if (todoItem.getDeadline().equals(LocalDate.now())) {
                                setTextFill(Color.RED);
                            } else if (todoItem.getDeadline().equals(LocalDate.now().plusDays(1))) {
                                setTextFill(Color.GREEN);
                            } else if (todoItem.getDeadline().isBefore(LocalDate.now().plusDays(1))) {
                                setTextFill(Color.BLACK);
                            }
                        }

                    }

                };
                cell.emptyProperty().addListener(
                        (obs, wasEmpty, isNoneEmpty) -> {
                            if (isNoneEmpty) {
                                cell.setContextMenu(null);
                            } else {
                                cell.setContextMenu(listContextMenu);
                            }
                        });
                return cell;
            }
        });
    }

    @FXML
    public void showNewItemDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add new Note :)");
        dialog.setHeaderText("another layer");
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("todoitemDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());

        } catch (IOException e) {
            System.out.println("Could not Load the data !");
            e.printStackTrace();
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            DialogController dialogController = fxmlLoader.getController();
            TodoItem defultSelectedItem = dialogController.processResult();
//            listViewTag.getItems().setAll(TodoData.getInstance().getTodoItems());
            listViewTag.getSelectionModel().select(defultSelectedItem);
//            System.out.println("Print Pressed");
//        }else{
//            System.out.println("Print Cancel");
        }
    }

    @FXML
    public void handleListViewChanges() {
//        TodoItem item = listViewTag.getSelectionModel().getSelectedItem();
//        area.setText(item.getDetails().toString());
//        dateLabel.setText(item.getDeadline().toString());
//        StringBuilder itemFormat = new StringBuilder();
//        itemFormat.append(item.getDetails());
//        itemFormat.append("\n\n\n\n\n");
//        itemFormat.append("Due to : " + item.getDeadline().toString());
//        area.setText(itemFormat.toString());
    }

    public void deleteItem(TodoItem item) {
        Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION);
        deleteAlert.setTitle("Delete Item");
        deleteAlert.getDialogPane().setPrefSize(300, 200);
        deleteAlert.setHeaderText("Delete Item: " + item.getShortDescription());
        deleteAlert.setContentText("Are you sure? please Press OK to confirm, \n or cancal to back out.");
        Optional<ButtonType> result = deleteAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            TodoData.getInstance().deleteItem(item);
        }
    }

    @FXML
    public void HanldeKeyPressed(KeyEvent keyEvent) {
        TodoItem selectedItem = listViewTag.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            if (keyEvent.getCode().equals(KeyCode.DELETE)) {
                deleteItem(selectedItem);
            }
        }
    }

    public void handleFilterToggleButton() {
        TodoItem selectedItem = listViewTag.getSelectionModel().getSelectedItem();
        if (filterToggleButton.isSelected()) {
            todoItemFilteredList.setPredicate(wantTodaysItems);
            if (todoItemFilteredList.isEmpty()) {
                area.clear();
                dateLabel.setText("");
            } else if (todoItemFilteredList.contains(selectedItem)) {
                listViewTag.getSelectionModel().select(selectedItem);
            } else {
                listViewTag.getSelectionModel().selectFirst();
            }
        } else {
            todoItemFilteredList.setPredicate(wantAllItems);
            listViewTag.getSelectionModel().select(selectedItem);
        }
    }

    public void handleExist() {
        Platform.exit();
    }
}
