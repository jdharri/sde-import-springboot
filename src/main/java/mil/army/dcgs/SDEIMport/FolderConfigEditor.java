package mil.army.dcgs.SDEIMport;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import java.io.File;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Joel Harris
 * @version 1 October 10, 2019
 */
@SpringComponent
@UIScope
public class FolderConfigEditor extends VerticalLayout implements KeyNotifier {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(FolderConfigEditor.class);
    private final FolderConfigRepository repo;

    private FolderConfig config;
    @Autowired
    private Importer importer;
    private TextField directory = new TextField("directory");
    private TextField sdeHost = new TextField("SDE Host");
    private TextField sdePort = new TextField("SDE Port");
    private TextField sdePassword = new TextField("SDE Password");
    private TextField sdeUsername = new TextField("SDE Username");
    private TextField sdeDatabase = new TextField("SDE Database");
    private TextField tableName = new TextField("table name");
    private Checkbox enabled = new Checkbox("enabled");

    private Button save = new Button("Save", VaadinIcon.CHECK.create());
    private Button cancel = new Button("Cancel");
    private Button delete = new Button("Delete", VaadinIcon.TRASH.create());
    private HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);

    private Binder<FolderConfig> binder = new Binder<>(FolderConfig.class);

    private ChangeHandler changeHandler;

    @Autowired
    public FolderConfigEditor(FolderConfigRepository repository) {
        this.repo = repository;

        sdeHost.setRequired(true);
        sdePort.setRequired(true);
        directory.setRequired(true);
        sdeUsername.setRequired(true);
        sdePassword.setRequired(true);
        sdeDatabase.setRequired(true);
        tableName.setRequired(true);

        add(enabled, sdeHost, sdePort, directory, sdeUsername, sdePassword, sdeDatabase, tableName, actions);

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

 
/**
 * @TODO
 * @param path
 * @return 
 */
    private boolean isValidFilePath(String path) {
     
        File folder = new File(path);
        if (!folder.exists()) {
           
            return false;
        }

       
        return true;
    }

    void save() {

        repo.save(config);
        if (!config.isEnabled()) {
            importer.remove(config);
        } else {
            importer.register(config);
        }

        changeHandler.onChange();

    }

    void delete() {
        importer.remove(config);
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
        sdeHost.focus();
    }

    public void setChangeHandler(ChangeHandler h) {
        changeHandler = h;
    }
}
