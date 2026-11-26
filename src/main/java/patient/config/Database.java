package patient.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public final class Database {
    private static final JdbcTemplate JDBC = createJdbcTemplate();

    private Database() {}

    private static JdbcTemplate createJdbcTemplate() {
        Properties local = loadConfiguration();
        DriverManagerDataSource source = new DriverManagerDataSource();
        source.setDriverClassName("org.postgresql.Driver");
        source.setUrl(setting(local, "DB_URL", false));
        source.setUsername(setting(local, "DB_USER", false));
        source.setPassword(setting(local, "DB_PASSWORD", true));

        return new JdbcTemplate(source);
    }

    private static Properties loadConfiguration() {
        Properties properties = new Properties();
        String configuredPath = System.getenv("DB_CONFIG_FILE");
        Path path = Path.of(configuredPath == null ? "src/main/.env" : configuredPath);
        if (configuredPath == null && !Files.exists(path)) return properties;
        try {
            for (String line : Files.readAllLines(path)) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                if (line.startsWith("export ")) line = line.substring(7).trim();
                int separator = line.indexOf('=');
                if (separator < 1) throw new IllegalStateException("Invalid configuration line in " + path);
                String value = line.substring(separator + 1).trim();
                if (value.length() >= 2 && ((value.startsWith("'") && value.endsWith("'"))
                        || (value.startsWith("\"") && value.endsWith("\"")))) {
                    value = value.substring(1, value.length() - 1);
                }
                properties.setProperty(line.substring(0, separator).trim(), value);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read database configuration: " + path, e);
        }
        return properties;
    }

    private static String setting(Properties local, String name, boolean allowEmpty) {
        String value = System.getenv(name);
        if (value == null) value = local.getProperty(name);
        if (value == null || (!allowEmpty && value.isBlank())) {
            throw new IllegalStateException("Missing database setting: " + name);
        }
        return value;
    }

    public static JdbcTemplate jdbc() {
        return JDBC;
    }
}
