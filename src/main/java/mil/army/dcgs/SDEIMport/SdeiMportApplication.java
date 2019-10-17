package mil.army.dcgs.SDEIMport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SdeiMportApplication {

    private static final Logger log = LoggerFactory.getLogger(SdeiMportApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SdeiMportApplication.class, args);
    }

    @Bean
    public CommandLineRunner loadData(FolderConfigRepository repo, SystemConfigRepository sysRepo) {
        return (args) -> {
//            repo.save(new FolderConfig("C:\\testdir", "sdePassword", "sdeDatabase","123.456.789",   "3306", "sdeuser","tableName2"));
//            repo.save(new FolderConfig("C:\\testdir2", "sdePassword2", "sdeDatabase2","123.456.789",   "3306", "sdeuser","tableName2"));
          
            if (sysRepo.findAll().size() < 1) {
                sysRepo.save(new SystemConfig("C:\\sdeimport.exe"));
            }
        };
    }

//        @PostConstruct
//        private void init(){
//            System.out.println("**** init()");
//            Importer importer = new Importer();
//            importer.watchFolders();
//        }
}
