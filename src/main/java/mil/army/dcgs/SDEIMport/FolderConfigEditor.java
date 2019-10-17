package mil.army.dcgs.SDEIMport;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;

import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import java.io.File;
import java.nio.file.Paths;
import javax.swing.plaf.basic.BasicMenuUI;
import com.vaadin.flow.data.converter.Converter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    Importer importer;
    TextField directory = new TextField("directory");
    TextField sdeHost = new TextField("SDE Host");
    TextField sdePort = new TextField("SDE Port");
    TextField sdePassword = new TextField("SDE Password");
    TextField sdeUsername = new TextField("SDE Username");
    TextField sdeDatabase = new TextField("SDE Database");
    TextField tableName = new TextField("table name");
    
    Button save = new Button("Save", VaadinIcon.CHECK.create());
    Button cancel = new Button("Cancel");
    Button delete = new Button("Delete", VaadinIcon.TRASH.create());
    HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);
    
    Binder<FolderConfig> binder = new Binder<>(FolderConfig.class);
    
    private ChangeHandler changeHandler;
    
    @Autowired
    public FolderConfigEditor(FolderConfigRepository repository) {
        this.repo = repository;
//        Converter filePathConverter = new Converter() {
//            @Override
//            public Result convertToModel(Object value, ValueContext context) {
//             return Paths.get(value.toString()).normalize();
//            }
//
//            @Override
//            public Object convertToPresentation(Object value, ValueContext context) {
//                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//            }
//        }
//                .withValidator((t) -> validateFilePath(t), "not a valid path");
        sdeHost.setRequired(true);
        sdePort.setRequired(true);
        directory.setRequired(true);
        sdeUsername.setRequired(true);
        sdePassword.setRequired(true);
        sdeDatabase.setRequired(true);
        tableName.setRequired(true);
        
        add(sdeHost, sdePort, directory, sdeUsername, sdePassword, sdeDatabase, tableName, actions);
//        binder.forField(directory)
//                //                .withValidator(pathValidator)
//                .withValidator(v -> validateFilePath(v), "not a valid file path")
//                
//                .bind(FolderConfig::getDirectory, FolderConfig::setDirectory);
        binder.bindInstanceFields(this);

//        directory.setRequired(true);
        setSpacing(true);
        save.getElement().getThemeList().add("primary");
        delete.getElement().getThemeList().add("error");
        
        addKeyPressListener(Key.ENTER, e -> save());
        
        save.addClickListener(e -> save());
        delete.addClickListener(e -> delete());
        cancel.addClickListener(e -> editConfig(config));
        setVisible(false);
        
    }
    
    private String normalizePath(String p) {
        File path = new File(p);
        return path.getAbsolutePath();
    }
    
    private boolean isValidFilePath(String path) {
        System.out.println("*** validator");
        System.out.println("*** path: " + path);
        File folder = new File(path);
        if (!folder.exists()) {
            System.out.println("NOT VALID");
            return false;
        }
        
        System.out.println("VALID");
        return true;
    }
    
    void save() {
        try {
            System.out.println("trying to save: " + config.toString());
//        if (directory.isInvalid()) {
//            System.out.println("*** directory is invalid");
//            return;
//        }
//        String normalizedPath = config.getDirectory().replace("\\", "\\\\");
//        System.out.println("normalized path: "+normalizedPath);
//        config.setDirectory(normalizedPath);
//        try {
//            importer.register(Paths.get(config.getDirectory()));
//            importer.processEvents(config);
//          
//        } catch (IOException ex) {
//            log.error("problem saving folder configuration: " + config.getDirectory() + " with error: " + ex);
//        }
            repo.save(config);
            importer.register(config);
            changeHandler.onChange();
        } catch (IOException ex) {
            Logger.getLogger(FolderConfigEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
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
        System.out.println("*** persisted: " + persisted);
        if (persisted) {
            
            config = repo.findById(c.getId()).get();
        } else {
            config = c;
        }
        
        cancel.setVisible(persisted);
//        String normalizedPath = config.getDirectory().replace("\\", "\\\\");
//        System.out.println("normalized path: " + normalizedPath);
//        config.setDirectory(normalizedPath);
        binder.setBean(config);
        setVisible(true);
        sdeHost.focus();
    }
    
    public void setChangeHandler(ChangeHandler h) {
        changeHandler = h;
    }
}
