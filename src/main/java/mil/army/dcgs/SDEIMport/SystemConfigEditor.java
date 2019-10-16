/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mil.army.dcgs.SDEIMport;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author joel
 */
@SpringComponent
@UIScope
public class SystemConfigEditor extends VerticalLayout implements KeyNotifier {

    private SystemConfig config;
    private final SystemConfigRepository repo;
    Button save = new Button("Save", VaadinIcon.CHECK.create());
    Button cancel = new Button("Cancel");
    Dialog dialog;
    TextField pathToExe = new TextField("Path to exe");

    HorizontalLayout actions = new HorizontalLayout(save, cancel);
    Binder<SystemConfig> binder = new Binder<>(SystemConfig.class);
    private ChangeHandler changeHandler;

    @Autowired
    public SystemConfigEditor(SystemConfigRepository repo) {
        this.dialog = new Dialog();
        
        this.repo = repo;
        binder.bindInstanceFields(this);
        pathToExe.setLabel("path to sdeimport.exe");
        addKeyPressListener(Key.ENTER, e -> save());
        // sdeImportExePath.setValue(config.getPathToExe());
//        Button saveSdeLocationButton = new Button("Save", VaadinIcon.DISC.create());
//        saveSdeLocationButton.addClickListener(e ->)
        VerticalLayout dialogContents = new VerticalLayout(pathToExe, actions);
        dialog.add(dialogContents);

        dialog.setWidth("400px");
        dialog.setHeight("150px");
        save.addClickListener(e -> save());
        cancel.addClickListener(e -> this.dialog.close());
    }

    void save() {
      
        repo.save(config);
//        changeHandler.onChange();
        this.dialog.close();
    }

    public interface ChangeHandler {

        void onChange();
    }

    public final void editConfig() {
        List<SystemConfig> currentConfigs = repo.findAll();
        SystemConfig currentConfig = currentConfigs.get(0);
        config = currentConfig;
//        pathToExe.setValue(config.getPathToExe());
        binder.setBean(config);
        dialog.open();

    }

    public void setChangeHandler(SystemConfigEditor.ChangeHandler h) {
        changeHandler = h;
    }
}
