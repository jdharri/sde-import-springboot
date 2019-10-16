package mil.army.dcgs.SDEIMport;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 *
 * @author jdhar
 */
@Service
public class Importer {

    @Autowired
    private FolderConfigRepository repo;

    ConcurrentLinkedQueue fileQueue;

    public void folderWatcher() {

        List<FolderConfig> configs = repo.findAll();

        configs.forEach(c -> {
            insertIntoSDE(c);
        });
    }

    @Async
    public void insertIntoSDE(FolderConfig c) {
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();

            Path path = Paths.get(c.getDirectory());
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
            WatchKey key;
            while ((key = watchService.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    System.out.println(
                            "Event kind:" + event.kind()
                            + ". File affected: " + event.context() + ".");
                    /**
                     * sdeimport -o update_else_insert {-l <table,column> | -t
                     * <table>} -K <key_columns> [-V <version_name>] -f
                     * <{export_file | -}> [-q] [-v] [-c <commit_interval>] [-i
                     * {<service> | <port#> | <direct connection>}] [-s
                     * <server_name>] [-D <database_name>] -u <DB_user_name>
                     * [-p <DB_user_password>]
                     */
                    fileQueue.add(event.context());

                    List<String> commands = new ArrayList<>();
                    commands.add("cmd.exe");
                    commands.add("/c");
                    commands.add("dir");
                    commands.add("-o");
                    commands.add("update_else_insert");
                    commands.add("-t");
                    commands.add(c.getTableName());
                    commands.add("-f");
                    commands.add(event.context().toString());
                    commands.add("-s");
                    commands.add(c.getSdeHost());
                    commands.add("-D");
                    commands.add(c.getSdeDatabase());
                    commands.add("-u");
                    commands.add(c.getSdeUsername());
                    commands.add("-p");
                    commands.add(c.getSdePassword());
                    commands.add("-i");
                    commands.add(c.getSdePort());
                    if (c.getKeyFields().length() > 0) {
                        commands.add("-K");
                        commands.add(c.getKeyFields());
                    }
                    ProcessBuilder pb = new ProcessBuilder(commands);
                    final Process p = pb.start();
                    final InputStream expout = p.getInputStream();
                    final int exitcode = p.waitFor();
                    assert exitcode == 0;
                }
            }
            key.reset();
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(Importer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
//    public void insertIntoSDE(){
//     try {
//            WatchService watchService = FileSystems.getDefault().newWatchService();
//            Path path = Paths.get(writeFile("test"));
//            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY);
//            WatchKey key;
//            while ((key = watchService.take()) != null) {
//                for (WatchEvent<?> event : key.pollEvents()) {
//                    System.out.println(
//                            "Event kind:" + event.kind()
//                            + ". File affected: " + event.context() + ".");
//                    if (event.kind().equals(StandardWatchEventKinds.ENTRY_CREATE)) {
//                        System.out.println("new file created");
//                        callSDEImport("stuff");
//                        
//                    }
//                }
//                key.reset();
//            }
//        } catch (IOException ex) {
//            Logger.getLogger(SDEImport.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(SDEImport.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
}
