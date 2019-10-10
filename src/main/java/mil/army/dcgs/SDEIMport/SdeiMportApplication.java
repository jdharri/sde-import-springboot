package mil.army.dcgs.SDEIMport;

import interfaces.ConfigRepository;
import mil.army.dcgs.SDEIMport.entities.FolderConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;

@SpringBootApplication
public class SdeiMportApplication {

	public static void main(String[] args) {
		SpringApplication.run(SdeiMportApplication.class, args);
	}
        
        @Bean
        public CommandLineRunner loadData(ConfigRepository repo){
            return (args) -> {
                repo.save(new FolderConfig("directory", "sdePassword", "sdeDatabase", "tableName"));
            };
        }

}
