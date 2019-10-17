package mil.army.dcgs.SDEIMport;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
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
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;
/**
 *
 * @author jdhar
 */
@Service
public class Importer {

    private FolderConfigRepository repo;
    private SystemConfigRepository sysRepo;

    ConcurrentLinkedQueue fileQueue;
    private List<FolderConfig> configs;

    @Autowired
    public Importer(FolderConfigRepository repo, SystemConfigRepository systemRepo) {
        this.repo = repo;
        this.sysRepo = systemRepo;
    }

    @PostConstruct
    @Async
    public void watchFolders() {
//         if (sysRepo.findAll().size() < 1) {
//                sysRepo.save(new SystemConfig("C:\\sdeimport.exe"));
//            }
        System.out.println("**** start watching folders");
        configs = repo.findAll();
        System.out.println(configs.size() + " configs found");
        configs.forEach(c -> {
            System.out.println("*** config directory: " + c.getDirectory());
            //   if (Files.exists(Paths.get(c.getDirectory()))) return;
            insertIntoSDE(c);

        });
    }

    @Async
    public void insertIntoSDE(FolderConfig c) {
        try {
//            if(sysRepo.findAll().size()<1)return;
            final String pathToExe = sysRepo.findAll().get(0).getPathToExe();
            WatchService watchService = FileSystems.getDefault().newWatchService();

            Path path = Paths.get(c.getDirectory());
            System.out.println("***register watch service for: " + path);
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
//                    fileQueue.add(event.context());

                    List<String> commands = new ArrayList<>();
                    commands.add(pathToExe);
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
                    if (null != c.getKeyFields()) {
                        commands.add("-K");
                        commands.add(c.getKeyFields());
                    }
                    ProcessBuilder pb = new ProcessBuilder(commands);
                    System.out.println("*** command: "+commands.toString().replace(","," "));
                    pb.redirectErrorStream(true);
                    final Process p = pb.start();

                    final int exitcode = p.waitFor();
                    assert exitcode == 0;
                    Files.delete(Paths.get(c.getDirectory().concat(File.pathSeparator).concat(event.context().toString())));
                }
            }
            key.reset();
        } catch (IOException | InterruptedException ex) {
            System.out.println("*** error in importer: " + ex);
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
