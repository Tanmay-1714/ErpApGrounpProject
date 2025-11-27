package edu.univ.erp.util;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.Component;

public class CsvUtils {
    public static void exportTableToCSV(JTable table, Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export to CSV");
        fileChooser.setSelectedFile(new File("export.csv"));

        if (fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
            }

            try (FileWriter fw = new FileWriter(file)) {
                TableModel model = table.getModel();
                // Headers
                for (int i = 0; i < model.getColumnCount(); i++) {
                    fw.write(model.getColumnName(i) + (i == model.getColumnCount() - 1 ? "" : ","));
                }
                fw.write("\n");
                // Data
                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        Object val = model.getValueAt(i, j);
                        String s = val == null ? "" : val.toString();
                        if (s.contains(",")) s = "\"" + s + "\"";
                        fw.write(s + (j == model.getColumnCount() - 1 ? "" : ","));
                    }
                    fw.write("\n");
                }
                JOptionPane.showMessageDialog(parent, "Export Successful!");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(parent, "Export Failed: " + e.getMessage());
            }
        }
    }
}
