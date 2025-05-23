package com.juliandev.view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ResourceBundle;

public class ContactView extends JFrame {
    private JTextField nameField, emailField, phoneField, searchField;
    private JComboBox<String> contactTypeComboBox;
    private JButton saveButton, clearButton, editButton, deleteButton, favoriteButton;
    private JTable contactTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JComboBox<String> languageComboBox;
    private ResourceBundle messages;
    private JMenuItem importJsonItem;

    public ContactView(ResourceBundle messages) {
        this.messages = messages;
        initializeUI();
    }

    private void initializeUI() {
        setTitle(messages.getString("app.title"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(245, 247, 250)); // #F5F7FA

        // Configurar fuente global
        UIManager.put("Label.font", new Font("Roboto", Font.PLAIN, 14));
        UIManager.put("Button.font", new Font("Roboto", Font.BOLD, 14));
        UIManager.put("TextField.font", new Font("Roboto", Font.PLAIN, 14));
        UIManager.put("Table.font", new Font("Roboto", Font.PLAIN, 13));

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(245, 247, 250));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel = new JLabel(messages.getString("label.name"));
        nameLabel.setForeground(new Color(45, 55, 72)); // #2D3748
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        nameField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        formPanel.add(nameField, gbc);

        JLabel emailLabel = new JLabel(messages.getString("label.email"));
        emailLabel.setForeground(new Color(45, 55, 72));
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(emailLabel, gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        emailField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        formPanel.add(emailField, gbc);

        JLabel phoneLabel = new JLabel(messages.getString("label.phone"));
        phoneLabel.setForeground(new Color(45, 55, 72));
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(phoneLabel, gbc);
        gbc.gridx = 1;
        phoneField = new JTextField(20);
        phoneField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        formPanel.add(phoneField, gbc);

        JLabel typeLabel = new JLabel(messages.getString("label.type"));
        typeLabel.setForeground(new Color(45, 55, 72));
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(typeLabel, gbc);
        gbc.gridx = 1;
        contactTypeComboBox = new JComboBox<>(new String[]{
                messages.getString("type.work"),
                messages.getString("type.family"),
                messages.getString("type.personal")
        });
        contactTypeComboBox.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        formPanel.add(contactTypeComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(Color.WHITE);

        saveButton = createStyledButton(messages.getString("button.save"), "/icons/save.png", 24, 24);
        editButton = createStyledButton(messages.getString("button.edit"), "/icons/edit.png", 24, 24);
        deleteButton = createStyledButton(messages.getString("button.delete"), "/icons/delete.png", 24, 24);
        clearButton = createStyledButton(messages.getString("button.clear"), "/icons/clear.png", 24, 24);
        favoriteButton = createStyledButton("", "/icons/not_favorite.png", 24, 24);
        favoriteButton.setToolTipText(messages.getString("button.favorite"));

        buttonPanel.add(saveButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(favoriteButton);
        formPanel.add(buttonPanel, gbc);

        // Tabla
        String[] columns = {
                messages.getString("column.id"),
                messages.getString("column.name"),
                messages.getString("column.email"),
                messages.getString("column.phone"),
                messages.getString("column.type"),
                messages.getString("column.favorite")
        };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        contactTable = new JTable(tableModel);
        contactTable.setRowHeight(30);
        contactTable.setGridColor(new Color(200, 200, 200));
        contactTable.setBackground(Color.WHITE);
        contactTable.setForeground(new Color(45, 55, 72));
        contactTable.setSelectionBackground(new Color(74, 144, 226, 50));
        contactTable.setSelectionForeground(Color.BLACK);
        contactTable.setShowGrid(true);

        // Renderizar ícono de favorito
        contactTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof Boolean && (Boolean) value) {
                    label.setIcon(scaleIcon("/icons/favorite.png", 24, 24));
                    label.setText("");
                } else {
                    label.setIcon(scaleIcon("/icons/not_favorite.png", 24, 24));
                    label.setText("");
                }
                label.setHorizontalAlignment(CENTER);
                return label;
            }
        });

        // Alternar colores en filas
        contactTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(232, 236, 239));
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(contactTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        // Búsqueda
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBackground(new Color(245, 247, 250));
        JLabel searchLabel = new JLabel(messages.getString("label.search"));
        searchLabel.setForeground(new Color(45, 55, 72));
        searchField = new JTextField(20);
        searchField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);

        // Barra de estado
        statusLabel = new JLabel(messages.getString("status.ready"));
        statusLabel.setForeground(Color.WHITE);
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(new Color(45, 55, 72));
        statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusPanel.add(statusLabel);

        // Barra de menú
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(245, 247, 250));
        menuBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JMenu fileMenu = new JMenu(messages.getString("menu.file"));
        fileMenu.setForeground(new Color(45, 55, 72));
        importJsonItem = new JMenuItem(messages.getString("menu.import.json"),
                scaleIcon("/icons/import.png", 20, 20));
        importJsonItem.setForeground(new Color(45, 55, 72));
        JMenuItem exportItem = new JMenuItem(messages.getString("menu.export"),
                scaleIcon("/icons/export.png", 20, 20));
        exportItem.setForeground(new Color(45, 55, 72));
        fileMenu.add(importJsonItem);
        fileMenu.add(exportItem);
        menuBar.add(fileMenu);

        JMenu languageMenu = new JMenu(messages.getString("menu.language"));
        languageMenu.setForeground(new Color(45, 55, 72));
        languageComboBox = new JComboBox<>(new String[]{"English", "Español", "Français"});
        languageComboBox.setMaximumSize(new Dimension(150, 30));
        languageMenu.add(languageComboBox);
        menuBar.add(languageMenu);

        setJMenuBar(menuBar);

        // Agregar componentes
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(searchPanel, BorderLayout.SOUTH);
        add(mainPanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text, String iconPath, int iconWidth, int iconHeight) {
        JButton button = new JButton(text, scaleIcon(iconPath, iconWidth, iconHeight));
        button.setBackground(new Color(74, 144, 226));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 40));
        button.setMinimumSize(new Dimension(120, 40));
        button.setMaximumSize(new Dimension(120, 40));
        button.setHorizontalTextPosition(SwingConstants.RIGHT);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setIconTextGap(5);
        if (text.isEmpty()) {
            button.setHorizontalAlignment(SwingConstants.CENTER);
        }
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(53, 122, 189));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(74, 144, 226));
            }
        });
        return button;
    }

    private ImageIcon scaleIcon(String iconPath, int width, int height) {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            if (icon.getImage() == null) {
                System.err.println("Icono no encontrado: " + iconPath);
            }
            Image image = icon.getImage();
            Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            System.err.println("Error al cargar el icono " + iconPath + ": " + e.getMessage());
            return new ImageIcon(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB));
        }
    }

    // Getters
    public JTextField getNameField() { return nameField; }
    public JTextField getEmailField() { return emailField; }
    public JTextField getPhoneField() { return phoneField; }
    public JTextField getSearchField() { return searchField; }
    public JComboBox<String> getContactTypeComboBox() { return contactTypeComboBox; }
    public JButton getSaveButton() { return saveButton; }
    public JButton getEditButton() { return editButton; }
    public JButton getDeleteButton() { return deleteButton; }
    public JButton getClearButton() { return clearButton; }
    public JButton getFavoriteButton() { return favoriteButton; }
    public JTable getContactTable() { return contactTable; }
    public DefaultTableModel getTableModel() { return tableModel; }
    public JLabel getStatusLabel() { return statusLabel; }
    public JComboBox<String> getLanguageComboBox() { return languageComboBox; }
    public JMenuItem getExportMenuItem() {
        return ((JMenu) getJMenuBar().getMenu(0)).getItem(1);
    }
    public JMenuItem getImportJsonMenuItem() {
        return ((JMenu) getJMenuBar().getMenu(0)).getItem(0);
    }

    public void updateMessages(ResourceBundle messages) {
        this.messages = messages;
        setTitle(messages.getString("app.title"));
        updateUILanguage();
    }

    private void updateUILanguage() {
        saveButton.setText(messages.getString("button.save"));
        editButton.setText(messages.getString("button.edit"));
        deleteButton.setText(messages.getString("button.delete"));
        clearButton.setText(messages.getString("button.clear"));
        favoriteButton.setToolTipText(messages.getString("button.favorite"));
        getJMenuBar().getMenu(0).setText(messages.getString("menu.file"));
        getImportJsonMenuItem().setText(messages.getString("menu.import.json"));
        getExportMenuItem().setText(messages.getString("menu.export"));
        getJMenuBar().getMenu(1).setText(messages.getString("menu.language"));
        String[] columns = {
                messages.getString("column.id"),
                messages.getString("column.name"),
                messages.getString("column.email"),
                messages.getString("column.phone"),
                messages.getString("column.type"),
                messages.getString("column.favorite")
        };
        tableModel.setColumnIdentifiers(columns);
        contactTypeComboBox.setModel(new DefaultComboBoxModel<>(new String[]{
                messages.getString("type.work"),
                messages.getString("type.family"),
                messages.getString("type.personal")
        }));
    }
}