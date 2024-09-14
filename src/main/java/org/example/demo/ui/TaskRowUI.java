package org.example.demo.ui;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.example.demo.enums.Status;
import org.example.demo.models.Task;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.List;

public class TaskRowUI {
    private final int index;
    private final Task task;

    private FontIcon deleteIcon;
    private CheckBox checkBox;

    public TaskRowUI(int index, Task task){
        this.index = index;
        this.task = task;
        setCheckBox();
        setDeleteIcon();
    }

    public Label getTaskLabel(){
        return new Label(index + ". " + task.getName());
    }
    public HBox getLayout1(){
        // will be added to column 1 of left pane
        FontIcon habitIcon = new FontIcon(task.getHabit().getIcon());

        FontIcon timeIcon = new FontIcon();
        Label timeLabel = new Label();
        if(task.getTaskTime() > 0){
            timeIcon.setIconCode(FontAwesomeSolid.CLOCK);
            timeIcon.setIconSize(20);
            timeLabel.setText(task.getTaskTime() + "h");
        }

        Label streakLabel = new Label(Integer.toString(task.getHabit().getActiveDays()));

        habitIcon.setIconSize(20);

        // create the layout for column 1 include habit icon and streak number
        return createColumnLayout(List.of(timeIcon, timeLabel, habitIcon, streakLabel));
    }

    public HBox getLayout2(){
        // create a checkbox and delete icon layout
        return createColumnLayout(List.of(this.checkBox, this.deleteIcon));
    }

    private HBox createColumnLayout(List<Node> nodes){
        HBox layout = new HBox();
        layout.setSpacing(20);
        layout.setAlignment(Pos.TOP_RIGHT);
        nodes.forEach(node -> layout.getChildren().add(node));
        return layout;
    }
    private void setDeleteIcon(){
        FontIcon deleteIcon = new FontIcon(FontAwesomeSolid.TRASH);
        deleteIcon.setIconSize(20);
        this.deleteIcon = deleteIcon;
    }
    private void setCheckBox(){
        CheckBox taskCheckbox = new CheckBox();
        if(task.getStatus() == Status.COMPLETE){
            taskCheckbox.setSelected(true);
        }
        this.checkBox = taskCheckbox;
    }

    public FontIcon getDeleteIcon() {
        return deleteIcon;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }
}
