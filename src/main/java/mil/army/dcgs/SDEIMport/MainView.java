package mil.army.dcgs.SDEIMport;

import com.vaadin.flow.component.Component;
import org.springframework.util.StringUtils;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;

/**
 *
 * @author Joel Harris
 * @version 1 October 11, 2019
 */
@Route
public class MainView extends VerticalLayout {
    
    private final FolderConfigRepository repo;
    
    private final FolderConfigEditor editor;
    
    final Grid<FolderConfig> grid;
    final TextField filter;
    
    private final Button addNewBtn;
    private final Button sysConfigBtn;
    
    public MainView(FolderConfigRepository repo, FolderConfigEditor folderEditor) {
        
        this.repo = repo;
        this.editor = folderEditor;
        this.grid = new Grid<>(FolderConfig.class);
        this.filter = new TextField();
        this.addNewBtn = new Button("New Configuration", VaadinIcon.PLUS.create());
        this.sysConfigBtn = new Button("System Configuration", VaadinIcon.COG.create());
        
        Dialog dialog = new Dialog();
        TextField sdeImportExePath = new TextField();
        sdeImportExePath.setLabel("path to sdeimport.exe");
        dialog.add(sdeImportExePath);
        dialog.add(new Label("Close me with the esc-key or an outside click"));
        dialog.setWidth("400px");
        dialog.setHeight("150px");
        sysConfigBtn.addClickListener(event -> dialog.open());
        HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn, sysConfigBtn);
        add(actions, grid, editor);
        
        grid.setHeight("200px");
        grid.setColumns("id", "directory", "tableName", "sdeDatabase", "sdePassword");
        grid.getColumnByKey("id").setWidth("50px").setFlexGrow(0);
        
        filter.setPlaceholder("Filter by directory");
        
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(e -> listConfigs(e.getValue()));
        
        grid.asSingleSelect().addValueChangeListener(e -> {
            editor.editConfig(e.getValue());
        });

        // Instantiate and edit new Customer the new button is clicked
        addNewBtn.addClickListener(e -> editor.editConfig(new FolderConfig("", "", "", "")));

        // Listen changes made by the editor, refresh data from backend
        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            listConfigs(filter.getValue());
        });

        // Initialize listing
        listConfigs(null);
    }
    
    void listConfigs(String filterText) {
        if (StringUtils.isEmpty(filterText)) {
            
            grid.setItems(repo.findAll());
        } else {
            grid.setItems(repo.findByDirectoryStartsWithIgnoreCase(filterText));
        }
    }
}
