package invox.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditService {

    private static final String AUDIT_FILE = "audit.csv";
    private static final DateTimeFormatter TS_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static AuditService instance;

    private AuditService() {
        File file = new File(AUDIT_FILE);
        if (!file.exists()) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("nume_actiune,timestamp" + System.lineSeparator());
            } catch (IOException e) {
                System.err.println("[AUDIT] Nu am putut crea " + AUDIT_FILE + ": " + e.getMessage());
            }
        }
    }

    public static synchronized AuditService getInstance() {
        if (instance == null) {
            instance = new AuditService();
        }
        return instance;
    }

    public synchronized void log(String actionName) {
        try (FileWriter writer = new FileWriter(AUDIT_FILE, true)) {
            String timestamp = LocalDateTime.now().format(TS_FORMAT);
            writer.write(actionName + "," + timestamp + System.lineSeparator());
        } catch (IOException e) {
            System.err.println("[AUDIT] Nu am putut scrie in " + AUDIT_FILE + ": " + e.getMessage());
        }
    }
}
