package mil.army.dcgs.SDEIMport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    public CommandLineRunner loadData(FolderConfigRepository repo, SystemConfigRepository sysRepo, Importer importer) {
        return (args) -> {
      
            if (sysRepo.findAll().size() < 1) {
                sysRepo.save(new SystemConfig("C:\\sdeimport.exe"));
            }

        };
    }


}
