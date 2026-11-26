package patient.repository;

import patient.config.Database;

public class PatientRepository {
    public int count() {
        return Database.jdbc()
            .queryForObject("SELECT COUNT(*) FROM patient", Integer.class);
    }

    public void create(String name) {
        Database.jdbc().update(
            "INSERT INTO patient (name) VALUES (?)", name
        );
    }
}