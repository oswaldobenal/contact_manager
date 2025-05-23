package com.juliandev.app;


import com.formdev.flatlaf.FlatLightLaf;
import com.juliandev.controller.ContactController;
import com.juliandev.model.ContactDAO;
import com.juliandev.view.ContactView;
import javax.swing.*;
import java.util.Locale;
import java.util.ResourceBundle;

public class ContactManagerApp {
    public static void main(String[] args) {
        // Configurar el Look and Feel moderno
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            System.err.println("Error al configurar FlatLaf: " + e.getMessage());
        }

        // Configurar idioma inicial
        Locale locale = Locale.getDefault();
        ResourceBundle messages = ResourceBundle.getBundle("messages", locale);

        // Crear y mostrar la ventana principal
        SwingUtilities.invokeLater(() -> {
            ContactView view = new ContactView(messages);
            ContactDAO dao = new ContactDAO();
            ContactController controller = new ContactController(view, dao, messages, locale);
            view.setVisible(true);
        });
    }
}