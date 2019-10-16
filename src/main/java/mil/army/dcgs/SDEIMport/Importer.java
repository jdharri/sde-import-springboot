
package mil.army.dcgs.SDEIMport;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jdhar
 */
public class Importer {
     try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            Path path = Paths.get(writeFile("test"));
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);
            WatchKey key;
            while ((key = watchService.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    System.out.println(
                            "Event kind:" + event.kind()
                            + ". File affected: " + event.context() + ".");
                    if (event.kind().equals(StandardWatchEventKinds.ENTRY_CREATE)) {
                        System.out.println("new file created");
                        callSDEImport("stuff");
                        
                    }
                }
                key.reset();
            }
        } catch (IOException ex) {
            Logger.getLogger(SDEImport.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(SDEImport.class.getName()).log(Level.SEVERE, null, ex);
        }
}
