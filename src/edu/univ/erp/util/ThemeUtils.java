package edu.univ.erp.util;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class ThemeUtils {

    // --- PALETTE (Consolidated for Compatibility) ---
    
    // Backgrounds
    public static final Color BG_COLOR = new Color(250, 250, 245);     // Off-White (Used by LoginWindow)
    public static final Color MAIN_BG = BG_COLOR;                      // Alias for Dashboard
    public static final Color PANEL_BG = new Color(255, 255, 255);     // White Panels
    public static final Color CARD_BG = new Color(255, 255, 255);      // Pure White Cards
    public static final Color SIDEBAR_BG = new Color(30, 30, 30);      // Dark Charcoal
    
    // Text & Accents
    public static final Color TEXT_COLOR = new Color(50, 50, 50);      // Standard Text
    public static final Color TEXT_MAIN = TEXT_COLOR;                  // Alias
    public static final Color TEXT_SIDEBAR = new Color(220, 220, 220); // Light Text for Dark Sidebar
    
    public static final Color ACCENT_BLACK = new Color(35, 35, 35);    // Soft Black
    public static final Color ACCENT_YELLOW = new Color(255, 193, 7);  // Warm Amber/Gold
    public static final Color HOVER_YELLOW = new Color(255, 215, 0);   // Lighter Gold
    public static final Color ERROR_RED = new Color(220, 53, 69);      // Error Red

    // --- FONTS ---
    public static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 24);
    public static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 16);
    public static final Font CARD_NUMBER_FONT = new Font("SansSerif", Font.BOLD, 36);
    public static final Font REGULAR_FONT = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font BUTTON_FONT = new Font("SansSerif", Font.BOLD, 13);

    // --- HELPERS ---

    public static void applyTheme(Container container) {
        container.setBackground(BG_COLOR);

        for (Component c : container.getComponents()) {
            if (c instanceof JPanel) {
                // Preserve specific background colors if set, otherwise default
                if (c.getBackground() == null || c.getBackground().equals(new JPanel().getBackground())) {
                    c.setBackground(PANEL_BG);
                }
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
            } else if (c instanceof JTextField) {
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
            }
        }
    }

    public static void styleButton(JButton btn) {
        btn.setFont(BUTTON_FONT);
        btn.setBackground(ACCENT_BLACK);
        btn.setForeground(ACCENT_YELLOW);
        btn.setFocusPainted(false);
        btn.setBorder(new CompoundBorder(
            new LineBorder(ACCENT_BLACK, 1, true), 
            new EmptyBorder(10, 20, 10, 20)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(ACCENT_YELLOW);
                btn.setForeground(ACCENT_BLACK);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(ACCENT_BLACK);
                btn.setForeground(ACCENT_YELLOW);
            }
        });
    }

    // Helper for Sidebar Buttons (Admin/Instructor)
    public static JButton createSidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(BUTTON_FONT);
        btn.setBackground(SIDEBAR_BG);
        btn.setForeground(TEXT_SIDEBAR);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(15, 20, 15, 20));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(ACCENT_YELLOW);
                btn.setForeground(ACCENT_BLACK);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(SIDEBAR_BG);
                btn.setForeground(TEXT_SIDEBAR);
            }
        });
        return btn;
    }

    // Helper for Dashboard Cards
    public static JPanel createStatCard(String title, String value, Color barColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(new CompoundBorder(
            new LineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(REGULAR_FONT);
        lblTitle.setForeground(Color.GRAY);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(CARD_NUMBER_FONT);
        lblValue.setForeground(TEXT_COLOR);

        // Colored bar on left
        JPanel bar = new JPanel();
        bar.setBackground(barColor);
        bar.setPreferredSize(new Dimension(5, 0));

        card.add(bar, BorderLayout.WEST);
        
        JPanel content = new JPanel(new GridLayout(2, 1));
        content.setBackground(CARD_BG);
        content.setBorder(new EmptyBorder(0, 10, 0, 0));
        content.add(lblTitle);
        content.add(lblValue);
        
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    public static void styleField(JTextField field) {
        field.setFont(REGULAR_FONT);
        field.setForeground(TEXT_COLOR);
        field.setBackground(Color.WHITE);
        field.setCaretColor(ACCENT_BLACK);
        field.setBorder(new CompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1), 
            new EmptyBorder(8, 10, 8, 10)
        ));
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(35);
        table.setFont(REGULAR_FONT);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(230, 230, 230));
        table.setSelectionBackground(ACCENT_YELLOW);
        table.setSelectionForeground(ACCENT_BLACK);
        table.setAutoCreateRowSorter(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(HEADER_FONT);
        header.setBackground(ACCENT_BLACK);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 40));
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
    }
}
