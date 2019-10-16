package mil.army.dcgs.SDEIMport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;

@SpringBootApplication
public class SdeiMportApplication {
    
private static final Logger log = LoggerFactory.getLogger(SdeiMportApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SdeiMportApplication.class, args);
	}
        
        @Bean
        public CommandLineRunner loadData(FolderConfigRepository repo, SystemConfigRepository sysRepo){
            return (args) -> {
                repo.save(new FolderConfig("directory", "sdePassword", "sdeDatabase", "tableName"));
                repo.save(new FolderConfig("directory2", "sdePassword2", "sdeDatabase2", "tableName2"));
                sysRepo.save(new SystemConfig("C:\\sdeimport.exe"));
            };
        }

}
