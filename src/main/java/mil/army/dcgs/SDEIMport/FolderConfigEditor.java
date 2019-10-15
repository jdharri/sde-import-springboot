package mil.army.dcgs.SDEIMport;


import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import javax.swing.plaf.basic.BasicMenuUI;

import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Joel Harris
 * @version 1 October 10, 2019
 */
@SpringComponent
@UIScope
public class FolderConfigEditor extends VerticalLayout implements KeyNotifier {

    private final FolderConfigRepository repo;

    private FolderConfig config;
    Binder<FolderConfig> binder = new Binder<>(FolderConfig.class);
    private ChangeHandler changeHandler;
    TextField directory = new TextField("directory");
    TextField sdePassword = new TextField("SDE Password");
    TextField sdeDatabase = new TextField("SDE Database");
    TextField tableName = new TextField("table name");

    Button save = new Button("Save", VaadinIcon.CHECK.create());
    Button cancel = new Button("Cancel");
    Button delete = new Button("Delete", VaadinIcon.TRASH.create());

    @Autowired
    public FolderConfigEditor(FolderConfigRepository repository) {
        this.repo = repository;
        add(directory, sdePassword, sdeDatabase, tableName);
        binder.bindInstanceFields(this);
        setSpacing(true);
        save.getElement().getThemeList().add("primary");
        delete.getElement().getThemeList().add("error");

        addKeyPressListener(Key.ENTER, e -> save());

        save.addClickListener(e -> save());
        delete.addClickListener(e -> delete());
        cancel.addClickListener(e -> editConfig(config));
        setVisible(false);

    }
    void save() {
        repo.save(config);
        changeHandler.onChange();
    }

    void delete() {
        repo.delete(config);
        changeHandler.onChange();
    }

    public interface ChangeHandler {

        void onChange();
    }

    public final void editConfig(FolderConfig c) {
        if (c == null) {
            setVisible(false);
            return;
        }
        final boolean persisted = c.getId() != null;
        if (persisted) {
            config = repo.findById(c.getId()).get();
        } else {
            config = c;
        }

        cancel.setVisible(persisted);
        binder.setBean(config);
        setVisible(true);
        directory.focus();
    }

    public void setChangeHandler(ChangeHandler h) {
        changeHandler = h;
    }
}
