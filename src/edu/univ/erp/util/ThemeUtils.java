package edu.univ.erp.util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.Enumeration;

public class ThemeUtils {

    // --- COLOR PALETTE (Light Yellow & Black) ---
    public static final Color BG_COLOR = new Color(255, 253, 208); // Cream/Light Yellow
    public static final Color PANEL_BG = new Color(255, 255, 224); // Lighter Yellow
    public static final Color TEXT_COLOR = new Color(20, 20, 20);  // Almost Black
    public static final Color ACCENT_BLACK = new Color(0, 0, 0);   // Pure Black
    public static final Color ACCENT_YELLOW = new Color(255, 215, 0); // Gold
    public static final Color ERROR_RED = new Color(220, 53, 69);

    // --- FONTS ---
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 13);

    /**
     * recursively applies the theme to a container and all its children.
     */
    public static void applyTheme(Container container) {
        container.setBackground(BG_COLOR);

        for (Component c : container.getComponents()) {
            if (c instanceof JPanel) {
                c.setBackground(PANEL_BG);
                applyTheme((Container) c); // Recursive
            } else if (c instanceof JLabel) {
                c.setFont(REGULAR_FONT);
                c.setForeground(TEXT_COLOR);
            } else if (c instanceof JButton) {
                styleButton((JButton) c);
            } else if (c instanceof JCheckBox) {
                c.setBackground(BG_COLOR);
                c.setFont(REGULAR_FONT);
                c.setForeground(TEXT_COLOR);
            } else if (c instanceof JTextField) {
                c.setFont(REGULAR_FONT);
                c.setForeground(TEXT_COLOR);
                ((JTextField) c).setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(ACCENT_BLACK, 1),
                        new EmptyBorder(5, 5, 5, 5)
                ));
            } else if (c instanceof JScrollPane) {
                ((JScrollPane) c).getViewport().setBackground(PANEL_BG);
                applyTheme((Container) c);
            } else if (c instanceof JTable) {
                styleTable((JTable) c);
            } else if (c instanceof JTabbedPane) {
                c.setFont(HEADER_FONT);
                c.setForeground(ACCENT_BLACK);
                // TabbedPane content needs specific handling usually, but recursion helps
            }
        }
    }

    public static void styleButton(JButton btn) {
        btn.setFont(BUTTON_FONT);
        btn.setBackground(ACCENT_BLACK);
        btn.setForeground(ACCENT_YELLOW); // Yellow text on Black button
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(ACCENT_YELLOW, 1),
                new EmptyBorder(8, 15, 8, 15)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void styleTable(JTable table) {
        table.setFont(REGULAR_FONT);
        table.setRowHeight(30);
        table.setSelectionBackground(ACCENT_YELLOW);
        table.setSelectionForeground(ACCENT_BLACK);
        table.setGridColor(new Color(230, 230, 230));
        table.setShowVerticalLines(false);

        // Header Styling
        JTableHeader header = table.getTableHeader();
        header.setFont(HEADER_FONT);
        header.setBackground(ACCENT_BLACK);
        header.setForeground(ACCENT_YELLOW);
        header.setReorderingAllowed(false);
        
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
    }
}
