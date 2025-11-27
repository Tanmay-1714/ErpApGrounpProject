package edu.univ.erp.util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class ThemeUtils {

    // --- BUMBLEBEE PALETTE ---
    public static final Color BG_COLOR = new Color(255, 252, 230); // Very Light Yellow
    public static final Color PANEL_BG = new Color(255, 255, 240); // Ivory
    public static final Color TEXT_COLOR = new Color(30, 30, 30);  // Charcoal
    public static final Color ACCENT_BLACK = new Color(20, 20, 20); // Black
    public static final Color ACCENT_YELLOW = new Color(255, 215, 0); // Gold
    public static final Color HOVER_YELLOW = new Color(255, 230, 100); // Lighter Gold
    public static final Color ERROR_RED = new Color(220, 53, 69);

    // --- FONTS ---
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 13);

    public static void applyTheme(Container container) {
        container.setBackground(BG_COLOR);

        for (Component c : container.getComponents()) {
            if (c instanceof JPanel) {
                c.setBackground(PANEL_BG);
                applyTheme((Container) c);
            } else if (c instanceof JLabel) {
                c.setFont(REGULAR_FONT);
                c.setForeground(TEXT_COLOR);
            } else if (c instanceof JButton) {
                styleButton((JButton) c);
            } else if (c instanceof JCheckBox) {
                c.setBackground(BG_COLOR);
                c.setFont(REGULAR_FONT);
                c.setForeground(TEXT_COLOR);
            } else if (c instanceof JTextField || c instanceof JPasswordField) {
                styleField((JTextField) c);
            } else if (c instanceof JScrollPane) {
                ((JScrollPane) c).getViewport().setBackground(PANEL_BG);
                applyTheme((Container) c);
            } else if (c instanceof JTable) {
                styleTable((JTable) c);
            } else if (c instanceof JTabbedPane) {
                c.setFont(HEADER_FONT);
                c.setForeground(ACCENT_BLACK);
                c.setBackground(BG_COLOR);
            } else if (c instanceof JComboBox) {
                c.setFont(REGULAR_FONT);
                c.setBackground(Color.WHITE);
            }
        }
    }

    public static void styleButton(JButton btn) {
        btn.setFont(BUTTON_FONT);
        btn.setBackground(ACCENT_BLACK);
        btn.setForeground(ACCENT_YELLOW);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(ACCENT_YELLOW, 1, true), // Rounded border
                new EmptyBorder(8, 20, 8, 20)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Simple Hover Effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setForeground(Color.WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setForeground(ACCENT_YELLOW);
            }
        });
    }

    public static void styleField(JTextField field) {
        field.setFont(REGULAR_FONT);
        field.setForeground(TEXT_COLOR);
        field.setCaretColor(TEXT_COLOR);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.GRAY, 1),
                new EmptyBorder(5, 8, 5, 8)
        ));
    }

    public static void styleTable(JTable table) {
        table.setFont(REGULAR_FONT);
        table.setRowHeight(32);
        table.setSelectionBackground(ACCENT_YELLOW);
        table.setSelectionForeground(ACCENT_BLACK);
        table.setGridColor(new Color(220, 220, 220));
        table.setShowVerticalLines(false);
        table.setAutoCreateRowSorter(true); // Enables Sorting!

        JTableHeader header = table.getTableHeader();
        header.setFont(HEADER_FONT);
        header.setBackground(ACCENT_BLACK);
        header.setForeground(ACCENT_YELLOW);
        header.setReorderingAllowed(false);
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
    }
}
