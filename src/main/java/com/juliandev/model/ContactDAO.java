package com.juliandev.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ContactDAO {
    private static final String DB_URL = "jdbc:sqlite:contacts.db";

    public ContactDAO() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            String sql = """
                    CREATE TABLE IF NOT EXISTS contacts (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        email TEXT NOT NULL UNIQUE,
                        phone TEXT,
                        contact_type TEXT,
                        favorite BOOLEAN NOT NULL
                    )
                    """;
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Error initializing database: " + e.getMessage());
        }
    }

    public void saveContact(Contact contact) {
        String sql = "INSERT INTO contacts (name, email, phone, contact_type, favorite) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, contact.getName());
            pstmt.setString(2, contact.getEmail());
            pstmt.setString(3, contact.getPhone());
            pstmt.setString(4, contact.getContactType());
            pstmt.setBoolean(5, contact.isFavorite());
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                contact.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving contact: " + e.getMessage());
        }
    }

    public void updateContact(Contact contact) {
        String sql = "UPDATE contacts SET name = ?, email = ?, phone = ?, contact_type = ?, favorite = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, contact.getName());
            pstmt.setString(2, contact.getEmail());
            pstmt.setString(3, contact.getPhone());
            pstmt.setString(4, contact.getContactType());
            pstmt.setBoolean(5, contact.isFavorite());
            pstmt.setInt(6, contact.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating contact: " + e.getMessage());
        }
    }

    public void deleteContact(int id) {
        String sql = "DELETE FROM contacts WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting contact: " + e.getMessage());
        }
    }

    public List<Contact> getAllContacts() {
        List<Contact> contacts = new ArrayList<>();
        String sql = "SELECT * FROM contacts";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Contact contact = new Contact(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("contact_type"),
                        rs.getBoolean("favorite")
                );
                contacts.add(contact);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving contacts: " + e.getMessage());
        }
        return contacts;
    }

    public List<Contact> searchContacts(String query) {
        List<Contact> contacts = new ArrayList<>();
        String sql = "SELECT * FROM contacts WHERE name LIKE ? OR email LIKE ? OR phone LIKE ? OR contact_type LIKE ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String searchQuery = "%" + query + "%";
            pstmt.setString(1, searchQuery);
            pstmt.setString(2, searchQuery);
            pstmt.setString(3, searchQuery);
            pstmt.setString(4, searchQuery);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Contact contact = new Contact(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("contact_type"),
                        rs.getBoolean("favorite")
                );
                contacts.add(contact);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching contacts: " + e.getMessage());
        }
        return contacts;
    }

    public boolean emailExists(String email, int excludeId) {
        String sql = "SELECT COUNT(*) FROM contacts WHERE email = ? AND id != ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setInt(2, excludeId);
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking email: " + e.getMessage());
        }
    }

    public void serializeToJson(File file) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            List<Contact> contacts = getAllContacts();
            mapper.writeValue(file, contacts);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing contacts to JSON: " + e.getMessage());
        }
    }

    public void deserializeFromJson(File file) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Contact[] contacts = mapper.readValue(file, Contact[].class);
            for (Contact contact : contacts) {
                contact.setId(0); // Reset ID to let SQLite assign new IDs
                if (!emailExists(contact.getEmail(), 0)) {
                    saveContact(contact);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error deserializing contacts from JSON: " + e.getMessage());
        }
    }
}