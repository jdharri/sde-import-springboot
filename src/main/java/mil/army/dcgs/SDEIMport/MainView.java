package mil.army.dcgs.SDEIMport;

import org.springframework.util.StringUtils;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

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

    public MainView(FolderConfigRepository repo, FolderConfigEditor editor) {

        this.repo = repo;
        this.editor = editor;
        this.grid = new Grid<>(FolderConfig.class);
        this.filter = new TextField();
        this.addNewBtn = new Button("New Configuration", VaadinIcon.PLUS.create());

        HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn);
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

    }

    void listConfigs(String filterText) {
        if (StringUtils.isEmpty(filterText)) {

            grid.setItems(repo.findAll());
        } else {
            grid.setItems(repo.findByDirectoryStartsWithIgnoreCase(filterText));
        }
    }
}
