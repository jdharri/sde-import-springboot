
package interfaces;

import java.util.List;
import mil.army.dcgs.SDEIMport.entities.FolderConfig;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Joel Harris
 * @version 1
 * October 9, 2019
 */
public interface ConfigRepository extends JpaRepository<FolderConfig, Long> {
    List<FolderConfig> findAll();
    
}
