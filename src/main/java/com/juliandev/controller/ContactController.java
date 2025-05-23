package com.juliandev.controller;

import com.juliandev.model.Contact;
import com.juliandev.model.ContactDAO;
import com.juliandev.view.ContactView;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

public class ContactController {
    private ContactView view;
    private ContactDAO dao;
    private ResourceBundle messages;
    private Locale locale;
    private final ReentrantLock exportLock = new ReentrantLock();
    private Contact selectedContact;

    public ContactController(ContactView view, ContactDAO dao, ResourceBundle messages, Locale locale) {
        this.view = view;
        this.dao = dao;
        this.messages = messages;
        this.locale = locale;
        initController();
    }

    private void initController() {
        view.getSaveButton().addActionListener(e -> saveContact());
        view.getEditButton().addActionListener(e -> editContact());
        view.getDeleteButton().addActionListener(e -> deleteContact());
        view.getClearButton().addActionListener(e -> clearForm());
        view.getFavoriteButton().addActionListener(e -> toggleFavorite());
        view.getSearchField().addKeyListener(new KeyAdapter() {
            private Timer timer = new Timer(300, e -> searchContacts());
            { timer.setRepeats(false); }

            @Override
            public void keyReleased(KeyEvent e) {
                timer.restart();
            }
        });
        view.getLanguageComboBox().addActionListener(e -> changeLanguage());
        view.getExportMenuItem().addActionListener(e -> exportToCSV());
        view.getImportJsonMenuItem().addActionListener(e -> importFromJson());
        view.getContactTable().getSelectionModel().addListSelectionListener(e -> selectContact());
        loadContacts();
    }

    private void saveContact() {
        String name = view.getNameField().getText().trim();
        String email = view.getEmailField().getText().trim();
        String phone = view.getPhoneField().getText().trim();
        String contactType = (String) view.getContactTypeComboBox().getSelectedItem();

        if (name.isEmpty()) {
            showError(messages.getString("error.name.empty"));
            return;
        }
        if (!isValidEmail(email)) {
            showError(messages.getString("error.email.invalid"));
            return;
        }
        if (contactType == null) {
            showError(messages.getString("error.type.empty"));
            return;
        }

        view.getSaveButton().setEnabled(false);
        view.getStatusLabel().setText(messages.getString("status.saving"));

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                return dao.emailExists(email, 0);
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        showError(messages.getString("error.email.exists"));
                    } else {
                        Contact contact = new Contact(0, name, email, phone, contactType, false);
                        dao.saveContact(contact);
                        loadContacts();
                        clearForm();
                        view.getStatusLabel().setText(messages.getString("status.saved"));
                    }
                } catch (Exception ex) {
                    showError(messages.getString("error.database"));
                } finally {
                    view.getSaveButton().setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void editContact() {
        if (selectedContact == null) {
            showError(messages.getString("error.no.contact.selected"));
            return;
        }

        String name = view.getNameField().getText().trim();
        String email = view.getEmailField().getText().trim();
        String phone = view.getPhoneField().getText().trim();
        String contactType = (String) view.getContactTypeComboBox().getSelectedItem();

        if (name.isEmpty()) {
            showError(messages.getString("error.name.empty"));
            return;
        }
        if (!isValidEmail(email)) {
            showError(messages.getString("error.email.invalid"));
            return;
        }
        if (contactType == null) {
            showError(messages.getString("error.type.empty"));
            return;
        }

        view.getEditButton().setEnabled(false);
        view.getStatusLabel().setText(messages.getString("status.saving"));

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                return dao.emailExists(email, selectedContact.getId());
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        showError(messages.getString("error.email.exists"));
                    } else {
                        selectedContact.setName(name);
                        selectedContact.setEmail(email);
                        selectedContact.setPhone(phone);
                        selectedContact.setContactType(contactType);
                        dao.updateContact(selectedContact);
                        loadContacts();
                        clearForm();
                        view.getStatusLabel().setText(messages.getString("status.updated"));
                    }
                } catch (Exception ex) {
                    showError(messages.getString("error.database"));
                } finally {
                    view.getEditButton().setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void deleteContact() {
        if (selectedContact == null) {
            showError(messages.getString("error.no.contact.selected"));
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(view,
                messages.getString("confirm.delete"),
                messages.getString("confirm.title"),
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        view.getDeleteButton().setEnabled(false);
        view.getStatusLabel().setText(messages.getString("status.deleting"));

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                dao.deleteContact(selectedContact.getId());
                return null;
            }

            @Override
            protected void done() {
                try {
                    loadContacts();
                    clearForm();
                    view.getStatusLabel().setText(messages.getString("status.deleted"));
                } catch (Exception ex) {
                    showError(messages.getString("error.database"));
                } finally {
                    view.getDeleteButton().setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void toggleFavorite() {
        if (selectedContact == null) {
            showError(messages.getString("error.no.contact.selected"));
            return;
        }

        view.getFavoriteButton().setEnabled(false);
        view.getStatusLabel().setText(messages.getString("status.updating"));

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                selectedContact.setFavorite(!selectedContact.isFavorite());
                dao.updateContact(selectedContact);
                return null;
            }

            @Override
            protected void done() {
                try {
                    loadContacts();
                    updateFavoriteButton();
                    view.getStatusLabel().setText(messages.getString("status.updated"));
                } catch (Exception ex) {
                    showError(messages.getString("error.database"));
                } finally {
                    view.getFavoriteButton().setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void loadContacts() {
        SwingWorker<List<Contact>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Contact> doInBackground() {
                return dao.getAllContacts();
            }

            @Override
            protected void done() {
                try {
                    DefaultTableModel model = view.getTableModel();
                    model.setRowCount(0);
                    for (Contact contact : get()) {
                        model.addRow(new Object[]{
                                contact.getId(),
                                contact.getName(),
                                contact.getEmail(),
                                contact.getPhone(),
                                contact.getContactType(),
                                contact.isFavorite()
                        });
                    }
                    view.getStatusLabel().setText(messages.getString("status.loaded"));
                } catch (Exception e) {
                    showError(messages.getString("error.database"));
                }
            }
        };
        worker.execute();
    }

    private void searchContacts() {
        String query = view.getSearchField().getText().trim();
        SwingWorker<List<Contact>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Contact> doInBackground() {
                return dao.searchContacts(query);
            }

            @Override
            protected void done() {
                try {
                    DefaultTableModel model = view.getTableModel();
                    model.setRowCount(0);
                    for (Contact contact : get()) {
                        model.addRow(new Object[]{
                                contact.getId(),
                                contact.getName(),
                                contact.getEmail(),
                                contact.getPhone(),
                                contact.getContactType(),
                                contact.isFavorite()
                        });
                    }
                    view.getStatusLabel().setText(messages.getString("status.search.completed"));
                } catch (Exception e) {
                    showError(messages.getString("error.database"));
                }
            }
        };
        worker.execute();
    }

    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("contacts.csv"));
        if (fileChooser.showSaveDialog(view) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = fileChooser.getSelectedFile();
        view.getStatusLabel().setText(messages.getString("status.exporting"));

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                exportLock.lock();
                try {
                    List<Contact> contacts = dao.getAllContacts();
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                        writer.write("ID,Name,Email,Phone,Type,Favorite\n");
                        for (Contact contact : contacts) {
                            writer.write(String.format("%d,%s,%s,%s,%s,%b\n",
                                    contact.getId(),
                                    escapeCSV(contact.getName()),
                                    escapeCSV(contact.getEmail()),
                                    escapeCSV(contact.getPhone()),
                                    escapeCSV(contact.getContactType()),
                                    contact.isFavorite()));
                        }
                    }
                    File jsonFile = new File(file.getParent(), "contacts.json");
                    dao.serializeToJson(jsonFile);
                } finally {
                    exportLock.unlock();
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    view.getStatusLabel().setText(messages.getString("status.exported"));
                } catch (Exception e) {
                    showError(messages.getString("error.export"));
                }
            }
        };
        worker.execute();
    }

    private void importFromJson() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".json");
            }

            @Override
            public String getDescription() {
                return "JSON Files (*.json)";
            }
        });
        if (fileChooser.showOpenDialog(view) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = fileChooser.getSelectedFile();
        view.getStatusLabel().setText(messages.getString("status.importing"));

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                dao.deserializeFromJson(file);
                return null;
            }

            @Override
            protected void done() {
                try {
                    loadContacts();
                    view.getStatusLabel().setText(messages.getString("status.imported"));
                } catch (Exception e) {
                    showError(messages.getString("error.import"));
                }
            }
        };
        worker.execute();
    }

    private void selectContact() {
        int selectedRow = view.getContactTable().getSelectedRow();
        if (selectedRow >= 0) {
            selectedContact = new Contact(
                    (int) view.getTableModel().getValueAt(selectedRow, 0),
                    (String) view.getTableModel().getValueAt(selectedRow, 1),
                    (String) view.getTableModel().getValueAt(selectedRow, 2),
                    (String) view.getTableModel().getValueAt(selectedRow, 3),
                    (String) view.getTableModel().getValueAt(selectedRow, 4),
                    (Boolean) view.getTableModel().getValueAt(selectedRow, 5)
            );
            view.getNameField().setText(selectedContact.getName());
            view.getEmailField().setText(selectedContact.getEmail());
            view.getPhoneField().setText(selectedContact.getPhone());
            view.getContactTypeComboBox().setSelectedItem(selectedContact.getContactType());
            updateFavoriteButton();
        } else {
            selectedContact = null;
            view.getFavoriteButton().setIcon(scaleFavoriteIcon("/icons/not_favorite.png"));
        }
    }

    private void clearForm() {
        view.getNameField().setText("");
        view.getEmailField().setText("");
        view.getPhoneField().setText("");
        view.getContactTypeComboBox().setSelectedIndex(0);
        selectedContact = null;
        view.getFavoriteButton().setIcon(scaleFavoriteIcon("/icons/not_favorite.png"));
        view.getStatusLabel().setText(messages.getString("status.ready"));
    }

    private void changeLanguage() {
        String selected = (String) view.getLanguageComboBox().getSelectedItem();
        switch (selected) {
            case "English":
                locale = new Locale("en");
                break;
            case "Español":
                locale = new Locale("es");
                break;
            case "Français":
                locale = new Locale("fr");
                break;
        }
        messages = ResourceBundle.getBundle("messages", locale);
        view.updateMessages(messages);
        loadContacts();
    }

    private void updateFavoriteButton() {
        System.out.println("Updating favorite button. Selected contact: " + (selectedContact != null ? selectedContact.getName() : "null"));
        ImageIcon icon;
        if (selectedContact != null && selectedContact.isFavorite()) {
            icon = scaleFavoriteIcon("/icons/favorite.png");
        } else {
            icon = scaleFavoriteIcon("/icons/not_favorite.png");
        }
        view.getFavoriteButton().setIcon(icon);
        System.out.println("Icon set: " + icon.getIconWidth() + "x" + icon.getIconHeight());
        view.getFavoriteButton().revalidate();
        view.getFavoriteButton().repaint();
    }

    private ImageIcon scaleFavoriteIcon(String iconPath) {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            if (icon.getImage() == null) {
                System.err.println("Icono no encontrado: " + iconPath);
                return new ImageIcon(new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB));
            }
            BufferedImage bufferedImage = new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = bufferedImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(icon.getImage(), 0, 0, 24, 24, null);
            g2d.dispose();
            ImageIcon scaledIcon = new ImageIcon(bufferedImage);
            System.out.println("Scaled icon " + iconPath + ": " + scaledIcon.getIconWidth() + "x" + scaledIcon.getIconHeight());
            return scaledIcon;
        } catch (Exception e) {
            System.err.println("Error al cargar el icono " + iconPath + ": " + e.getMessage());
            return new ImageIcon(new BufferedImage(24, 24, BufferedImage.TYPE_INT_ARGB));
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return Pattern.compile(emailRegex).matcher(email).matches();
    }

    private String escapeCSV(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(view, message, messages.getString("error.title"), JOptionPane.ERROR_MESSAGE);
    }
}