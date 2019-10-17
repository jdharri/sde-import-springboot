package mil.army.dcgs.SDEIMport;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 * @author jdhar
 */
@Component
public class Importer {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(Importer.class);

    private final WatchService watcher;
    private final Map<WatchKey, FolderConfig> keys;
    private Map<Path, FolderConfig> configs;
    private final FolderConfigRepository repo;
    private boolean trace = false;
    final String pathToExe;

    private SystemConfigRepository sysRepo;

    public Importer(SystemConfigRepository systemRepo, FolderConfigRepository repo) throws IOException {
        System.out.println("***Importer");
        this.repo = repo;
        this.sysRepo = systemRepo;
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<WatchKey, FolderConfig>();

        if (sysRepo.findAll().size() < 1) {
            sysRepo.save(new SystemConfig("C:\\sdeimport.exe"));
        }
        this.pathToExe = sysRepo.findAll().get(0).getPathToExe();
        // this.pathToExe = "C:\\sdeimport.exe";
        //register(Paths.get(path));

    }

    public void remove(FolderConfig config) {
        System.out.println("size before removal: " + keys.size());

        keys.forEach((k, v) -> {
            if (v.equals(Paths.get(config.getDirectory()))) {
                k.cancel();
                keys.remove(k);
            }

        });
        System.out.println("size after removal: " + keys.size());
    }

//    @Scheduled(fixedRate = 3000)
//    protected void configurationPoller() {
//        System.out.println("********************just ran scheduled");
//        List<FolderConfig> folderConfigs = repo.findAll();
//
//    folderConfigs.forEach(c -> configs.put(key, c));
//      
//        configs.put(Paths.get(c.getDirectory()), c);
//
//    }
//     @PostConstruct
//    @Async
//    public void watchFolders() {
//        try {
//            //         if (sysRepo.findAll().size() < 1) {
////                sysRepo.save(new SystemConfig("C:\\sdeimport.exe"));
////            }
//            System.out.println("**** start watching folders");
//            configs = repo.findAll();
//            System.out.println(configs.size() + " configs found");
//            WatchService watchService = FileSystems.getDefault().newWatchService();
//
//            configs.forEach(c -> {
//                try {
//                    System.out.println("*** config directory: " + c.getDirectory());
//
//                    Path path = Paths.get(c.getDirectory());
//                    System.out.println("***register watch service for: " + path);
//                    path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
//                    WatchKey key;
////                if (Files.exists(Paths.get(c.getDirectory()))) {
////                    return;
////                }
//
//                    while ((key = watchService.take()) != null) {
//                        for (WatchEvent<?> event : key.pollEvents()) {
//                            System.out.println(
//                                    "Event kind:" + event.kind()
//                                    + ". File affected: " + event.context() + ".");
//                            insertIntoSDE(c, event);
//                        }
//                    }
//                } catch (IOException | InterruptedException ex) {
//                    Logger.getLogger(Importer.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            });
//        } catch (IOException ex) {
//            Logger.getLogger(Importer.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    @Async
    private void insertIntoSDE(FolderConfig c, WatchEvent event) {
        try {
//            if(sysRepo.findAll().size()<1)return;

            // WatchService watchService = FileSystems.getDefault().newWatchService();
//            Path path = Paths.get(c.getDirectory());
//            System.out.println("***register watch service for: " + path);
//            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
//            WatchKey key;
//            while ((key = watchService.take()) != null) {
//                for (WatchEvent<?> event : key.pollEvents()) {
//                    System.out.println(
//                            "Event kind:" + event.kind()
//                            + ". File affected: " + event.context() + ".");
            /**
             * sdeimport -o update_else_insert {-l <table,column> | -t
             * <table>} -K <key_columns> [-V <version_name>] -f
             * <{export_file | -}> [-q] [-v] [-c <commit_interval>] [-i
             * {<service> | <port#> | <direct connection>}] [-s
             * <server_name>] [-D <database_name>] -u <DB_user_name>
             * [-p <DB_user_password>]
             */
//                    fileQueue.add(event.context());
            Path fp = Paths.get(c.getDirectory().concat("/").concat(event.context().toString()));
            List<String> commands = new ArrayList<>();
            commands.add(pathToExe);
            commands.add("/c");
            commands.add("dir");
            commands.add("-o");
            commands.add("update_else_insert");
            commands.add("-t");
            commands.add(c.getTableName());
            commands.add("-f");
            commands.add(fp.toString());
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
            System.out.println("*** command: " + commands.toString().replace(",", " "));
            pb.redirectErrorStream(true);
            final Process p = pb.start();

            final int exitcode = p.waitFor();
            assert exitcode == 0;
            Files.delete(fp);
            //}
//            }
//
//            key.reset();
        } catch (IOException ex) {
            System.out.println("*** error in importer: " + ex);
            Logger.getLogger(Importer.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
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

    @Async
    public void register(FolderConfig config) throws IOException {
        Path dir = Paths.get(config.getDirectory());
        System.out.println("*** register: " + dir);
        WatchKey key = dir.register(watcher, ENTRY_CREATE);

        if (trace) {
            FolderConfig prev = keys.get(key);
            if (prev == null) {
                System.out.format("register: %s\n", dir);
            } else {
                if (!dir.equals(prev)) {
                    System.out.format("update: %s -> %s\n", prev, dir);
                }
            }
        }
        keys.put(key, config);
        processEvents();
    }

    /**
     * Process all events for keys queued to the watcher
     */
    void processEvents() {
        for (;;) {

            // wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            FolderConfig config = keys.get(key);
            Path dir = Paths.get(config.getDirectory());
            System.out.println("process events for path: " + dir);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();

                // TBD - provide example of how OVERFLOW event is handled
                if (kind == OVERFLOW) {
                    continue;
                }
                if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                    insertIntoSDE(config, event);
                }

            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);

                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }

}
