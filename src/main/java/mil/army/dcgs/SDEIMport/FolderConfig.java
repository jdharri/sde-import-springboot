
package mil.army.dcgs.SDEIMport;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
/**
 *
 * @author Joel Harris
 * @version 1
 * October 9, 2019
 */
@Entity
public class FolderConfig {
   @Id
   @GeneratedValue
   private Long id;
   private String directory;
   private boolean deleteFiles;
   private String keyFields;
   private String layerName;
   private String mainIdColumn;
   private String sdePassword;
   private String sdeDatabase;
   private String sdeHost;
   private String sdePort;
   private String sdeUsername;
   private String tableName;
   private boolean enabled = false;
//       public String CGWIN = "cgwyn";
//    public String DELETE_FILES = "deleteFiles";
//    public String DRIVER_SDE = "driverSDE";
//    public String IMPORT_COMMAND = "importCommand";
//    public String IN_DIR = "inDir";
//    public String KEY_FIELDS = "keyFields";
//    public String LAYER_NAME = "layerName";
//    public String MAIN_ID_COLUMN = "mainIDColumn";
//    public String MIN_FILE_LATENCY = "minFileLatency";
//    public String NUM_IMPORTS = "numImports";
//    public String PASSWD_SDE = "passwdSDE";
//    public String SDE_DATABASE = "SDEDatabase";
//    public String SDE_HOST = "SDEHost";
//    public String SDE_PORT = "SDEPort";
//    public String SLEEP_TIME = "sleepTime";
//    public String TABLE_NAME = "tableName";
//    public String URL_SDE = "urlSDE";
//    public String USER_SDE = "userSDE";
//   private String dbTable;

    public FolderConfig(boolean enabled, String directory, String sdePassword, String sdeDatabase, String sdeHost, String sdePort, String sdeUsername, String tableName) {
        this.enabled = enabled;
        this.directory = directory;
        this.sdePassword = sdePassword;
        this.sdeDatabase = sdeDatabase;
        this.sdeHost = sdeHost;
        this.sdePort = sdePort;
        this.sdeUsername = sdeUsername;
        this.tableName = tableName;
    }

   

    public FolderConfig() {
    }

    public Long getId() {
        return id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public boolean isDeleteFiles() {
        return deleteFiles;
    }

    public void setDeleteFiles(boolean deleteFiles) {
        this.deleteFiles = deleteFiles;
    }

    public String getKeyFields() {
        return keyFields;
    }

    public void setKeyFields(String keyFields) {
        this.keyFields = keyFields;
    }

    public String getLayerName() {
        return layerName;
    }

    public void setLayerName(String layerName) {
        this.layerName = layerName;
    }

    public String getMainIdColumn() {
        return mainIdColumn;
    }

    public void setMainIdColumn(String mainIdColumn) {
        this.mainIdColumn = mainIdColumn;
    }

    public String getSdePassword() {
        return sdePassword;
    }

    public void setSdePassword(String sdePassword) {
        this.sdePassword = sdePassword;
    }

    public String getSdeDatabase() {
        return sdeDatabase;
    }

    public void setSdeDatabase(String sdeDatabase) {
        this.sdeDatabase = sdeDatabase;
    }

    public String getSdeHost() {
        return sdeHost;
    }

    public void setSdeHost(String sdeHost) {
        this.sdeHost = sdeHost;
    }

    public String getSdePort() {
        return sdePort;
    }

    public void setSdePort(String sdePort) {
        this.sdePort = sdePort;
    }

    public String getSdeUsername() {
        return sdeUsername;
    }

    public void setSdeUsername(String sdeUsername) {
        this.sdeUsername = sdeUsername;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String toString() {
        return "FolderConfig{" + "id=" + id + ", directory=" + directory + ", deleteFiles=" + deleteFiles + ", keyFields=" + keyFields + ", layerName=" + layerName + ", mainIdColumn=" + mainIdColumn + ", sdePassword=" + sdePassword + ", sdeDatabase=" + sdeDatabase + ", sdeHost=" + sdeHost + ", sdePort=" + sdePort + ", sdeUsername=" + sdeUsername + ", tableName=" + tableName + '}';
    }
   
   
   
}
