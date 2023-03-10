package main.CapaPresentacio.Vista;

import main.CapaDomini.Models.Document;
import main.CapaDomini.Models.Frase;
import main.CapaPresentacio.Controllers.CtrlPresentacio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

public class VistaKdoc extends JPanel {

    private CtrlPresentacio ctrl;

    private JTextField k;

    private JComboBox document;

    private JButton search;

    private GridBagConstraints c;

    private JTable table;
    public VistaKdoc(CtrlPresentacio ctrl) {
        this.ctrl = ctrl;
        initVistaKdoc();
    }

    private void initVistaKdoc(){
        c = new GridBagConstraints();
        setLayout(new GridBagLayout());
        initKTextField();
        initDocumentComboBox();
        initButton();
        initTable();
    }

    private void initDocumentComboBox() {
        c.gridx = 2;
        c.ipadx = 120;
        JPanel documentPanel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Document:");
        documentPanel.add(label, BorderLayout.WEST);
        this.document = new JComboBox();
        documentPanel.add(this.document, BorderLayout.CENTER);
        this.add(documentPanel, c);
    }

    private void initKTextField() {
        c.gridx = 0;
        c.gridy = 0;
        c.ipadx = 70;
        c.insets = new Insets(4, 4,4 ,10);
        JPanel kText = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Nº documents:");
        kText.add(label, BorderLayout.WEST);
        k = new JTextField(6);
        kText.add(k, BorderLayout.CENTER);
        this.add(kText, c);
    }


    private void initButton(){
        c.gridx = 1;
        c.gridy = 1;
        c.insets = new Insets(8, 0,8 ,0);
        search = new JButton("Search");
        search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (Integer.parseInt(k.getText()) <= 0) {
                        throw new NumberFormatException("nope");
                    } else {
                        int number_doc = Integer.parseInt(k.getText());
                        /*try {
                            if (autor.getSelectedItem() == null || document.getSelectedItem() == null) {
                                throw new Exception("invalid input");
                            } else {*/
                        Document to_search = ctrl.GetDocForK(
                                document.getSelectedItem().toString()
                        );
                        HashMap<Document, Double> docs = ctrl.getMostSimilarDocuments(to_search, number_doc);
                        updateTableValues(docs);
                        /*    }
                        } catch (Exception E) {

                        }*/
                    }
                } catch (NumberFormatException E) {
                    ctrl.errorManagement("K is not a valid integer.");
                }
            }
        });
        this.add(search, c);
    }

    private void initTable() {
        String[] columnsNames = {"Document Title", "Author"};
        Object [][] data = ctrl.getDocumentosData();
        MultiLineTableCellRenderer renderer = new MultiLineTableCellRenderer(ctrl);
        table = new JTable(new VistaKdoc.MyTableModel(data, columnsNames));
        table.setDefaultRenderer(String[].class,renderer);
        table.setDefaultEditor( String[].class, renderer);

        JPanel panel = new JPanel(new BorderLayout());
        table.getTableHeader().setReorderingAllowed(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.setRowHeight(table.getRowHeight()+9);
        table.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
        table.getColumnModel().getColumn(1).setCellRenderer(new MultiLineTableCellRenderer(ctrl));
        table.getColumnModel().getColumn(1).setCellEditor(new MultiLineTableCellRenderer(ctrl));
        table.setShowGrid(true);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        panel.add(table.getTableHeader(), BorderLayout.NORTH);
        panel.add(table, BorderLayout.CENTER);
        c.gridx = 0;
        c.gridy = 2;
        c.ipady = 40;
        c.fill = c.HORIZONTAL;
        c.weightx = 0.0;
        c.gridwidth = 3;
        c.insets = new Insets(6, 6, 6, 6);
        this.add(panel, c);
        panel.setVisible(true);
        updateTable();
    }

    public class MyTableModel extends DefaultTableModel {
        private MyTableModel(Object[][] tableData, Object[] columnsNames){
            super(tableData, columnsNames);
        }

        public boolean isCellEditable(int row, int column) { return false;}
    }

    public void updateTable(){
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        Object[][] auxi = ctrl.getDocumentosData();
        for (Object[] anAuxi : auxi) { model.addRow(anAuxi); }
        table.setModel(model);
        updateRowHeights();
    }

    public void updateTableValues(HashMap<Document, Double> docs) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        for(HashMap.Entry<Document, Double> entry: docs.entrySet()){
            Document d = entry.getKey();
            Double similarity = entry.getValue();
            String[] input = new String[3];
            input[0] = d.getTitol().toString();
            input[1] = d.getAutor().toString();
            model.addRow(input);
        }
        table.setModel(model);
        updateRowHeights();
    }

    private void updateRowHeights() {
        for (int row = 0; row < table.getRowCount(); row++) {
            int rowHeight = table.getRowHeight();
            for (int column = 0; column < table.getColumnCount(); column++) {
                Component comp = table.prepareRenderer(table.getCellRenderer(row, column), row, column);
                rowHeight = Math.max(rowHeight, comp.getPreferredSize().height);
            }
            table.setRowHeight(row, rowHeight);
        }
    }

    public void updateDocumentList() {

            ArrayList<Document> documents = ctrl.getDocuments();
            String[] documentName = new String[documents.size()];
            for (int i = 0; i < documents.size(); ++i) {
                documentName[i] = documents.get(i).toString();
            }
            document.setModel(new SortedComboBoxModel<String>(documentName));

    }

}
