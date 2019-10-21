package mil.army.dcgs.SDEIMport;

import java.io.File;
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
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 * @author Joel Harris
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
;
        this.repo = repo;
        this.sysRepo = systemRepo;
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<WatchKey, FolderConfig>();

        if (sysRepo.findAll().size() < 1) {
            sysRepo.save(new SystemConfig("C:\\sdeimport.exe"));
        }
        this.pathToExe = sysRepo.findAll().get(0).getPathToExe();
  

    }

   

    public void remove(FolderConfig config) {
       

        keys.forEach((k, v) -> {
            Path pathtoremove = Paths.get(config.getDirectory());
          
            if (Paths.get(v.getDirectory()).equals(Paths.get(config.getDirectory()))) {

                k.cancel();
           
            }

        });
      
    }

    @Scheduled(initialDelay = 1000 * 30, fixedDelay = Long.MAX_VALUE)
    public void loadRegisteredWatchers() {
      
        List<FolderConfig> folderConfigs = repo.findAll();
   
        folderConfigs.forEach((FolderConfig c) -> {
            if(c.isEnabled())
            register(c);

        });
    }


    @Async
    private void insertIntoSDE(FolderConfig c, String fileName) {
        try {
          
            /**
             * sdeimport -o update_else_insert {-l <table,column> | -t
             * <table>} -K <key_columns> [-V <version_name>] -f
             * <{export_file | -}> [-q] [-v] [-c <commit_interval>] [-i
             * {<service> | <port#> | <direct connection>}] [-s
             * <server_name>] [-D <database_name>] -u <DB_user_name>
             * [-p <DB_user_password>]
             */
            Path fp = Paths.get(c.getDirectory().concat("/").concat(fileName));
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
            commands.add("'"+c.getSdePort()+"'");
            if (null != c.getKeyFields()) {
                commands.add("-K");
                commands.add(c.getKeyFields());
            }
            System.out.println(commands.toString().replace(",", " "));
            ProcessBuilder pb = new ProcessBuilder(commands);
          
            pb.redirectErrorStream(true);
            final Process p = pb.start();

            final int exitcode = p.waitFor();
            assert exitcode == 0;
            Files.delete(fp);
   
        } catch (IOException ex) {
         
            Logger.getLogger(Importer.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Importer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    @Async
    public void register(FolderConfig config) {
        try {
        
            Path dir = Paths.get(config.getDirectory());
        
            // HashSet<String> files = Stream.of(new File(dir).listFiles())
            Set<String> files = Stream.of(new File(dir.toString()).listFiles())
                    .filter(file -> !file.isDirectory())
                    .map(File::getName)
                    .collect(Collectors.toSet());
            if (files.size() > 0) {
                files.forEach(f -> insertIntoSDE(config, f));

            }
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
        } catch (IOException ex) {
        
            Logger.getLogger(Importer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Process all events for keys queued to the watcher
     */
    @Async
    void processEvents() {
        for (;;) {
            
            // wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                log.error("interrupted exception: " + x);
                return;
            }

            FolderConfig config = keys.get(key);
            Path dir = Paths.get(config.getDirectory());
          
            if (dir == null) {
               
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();

                // TBD - provide example of how OVERFLOW event is handled
                if (kind == OVERFLOW) {
                    continue;
                }
                if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                    insertIntoSDE(config, event.context().toString());
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
