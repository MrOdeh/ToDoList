package com.odeh.todolist.datamodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

// here we are wokring on singelton to enusre there's no instanse of class
public class TodoData {
    private static TodoData instance = new TodoData();
    private static String fileName = new String("todoListItems.txt");

    private ObservableList<TodoItem> todoItems;
    private DateTimeFormatter formatter;

    private TodoData() {
        formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    }

    public static TodoData getInstance() {
        return instance;
    }

    public ObservableList<TodoItem> getTodoItems() {
        return todoItems;
    }

    public void deleteItem(TodoItem item) {
        todoItems.remove(item);
    }

    public void setTodoItems(ObservableList<TodoItem> todoItems) {
        this.todoItems = todoItems;
    }

    public void addToItem(TodoItem item) {
        todoItems.add(item);
    }

    public void loadItems() throws IOException {
        todoItems = FXCollections.observableArrayList();
        BufferedReader fileBufferReader = Files.newBufferedReader(Paths.get(fileName));
        String input;

        try {
            while ((input = fileBufferReader.readLine()) != null) {
                String[] rowData = input.split("\t");

                String shortDescription = rowData[0];
                String details = rowData[1];
                LocalDate date = LocalDate.parse(rowData[2], formatter);

                TodoItem newItem = new TodoItem(shortDescription, details, date);
                todoItems.add(newItem);
            }
        } finally {
            if (fileBufferReader != null) {
                fileBufferReader.close();
            }
        }
    }

    public void storeTodoItems() throws IOException {
        Path path = Paths.get(fileName);
        BufferedWriter fileBufferWriter = Files.newBufferedWriter(path);
        try {
            Iterator<TodoItem> itemIterator = todoItems.iterator();
            while (itemIterator.hasNext()) {
                TodoItem nextItem = itemIterator.next();
                fileBufferWriter.write(String.format("%s\t%s\t%s",
                        nextItem.getShortDescription(),
                        nextItem.getDetails(),
                        nextItem.getDeadline().format(formatter)));
                fileBufferWriter.newLine();
            }

        } finally {
            if (fileBufferWriter != null) {
                fileBufferWriter.close();
            }
        }
    }
}
