
package mil.army.dcgs.SDEIMport;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Joel Harris
 * @version 1
 * October 9, 2019
 */
public interface FolderConfigRepository extends JpaRepository<FolderConfig, Long> {
    List<FolderConfig> findAll();
    
    
    List<FolderConfig> findByDirectoryStartsWithIgnoreCase(String filterText);
}
