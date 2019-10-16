
package mil.army.dcgs.SDEIMport;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 *
 * @author joel
 */
@Entity
public class SystemConfig implements Serializable {
    @Id
    @GeneratedValue
    private Long id;
    private String pathToExe;

    public SystemConfig() {
    }

    public SystemConfig(String pathToExe) {
        this.pathToExe = pathToExe;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPathToExe() {
        return pathToExe;
    }

    public void setPathToExe(String pathToExe) {
        this.pathToExe = pathToExe;
    }
    
    
}
