package rlill.reminder;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;


public class App implements ActionListener, FocusListener {

	private static Logger LOG = Logger.getLogger(App.class);

	private JFrame mainFrame;

	private JTable table;
	private DefaultTableModel tableModel;

	private OptionsDialog optionsDlg;

	private final static String PROPERTIES = "birthdayreminder.properties";
	private final static String REFRESH = "Refresh";
	private final static String OPTIONS = "Options";
	private final static String EXIT = "Exit";

	public static void main(String[] argv) {

		System.setProperty("apple.awt.application.name", "Birthday Reminder");

		PatternLayout layout = new PatternLayout();
		layout.setConversionPattern("%d{HH:mm:ss,SSS} %-5p %c{1}::%M (%F:%L) - %m%n");

		ConsoleAppender appender = new ConsoleAppender(layout);
		appender.setName("A1");
		BasicConfigurator.configure(appender);

		Logger.getRootLogger().setLevel(Level.DEBUG);

		App main = new App();

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				main.init();
				main.createAndShowGUI();
			}
		});
	}

	private void init() {

        try (InputStream input = new FileInputStream(PROPERTIES)) {

            Properties prop = new Properties();
            prop.load(input);

			optionsDlg = new OptionsDialog(mainFrame, this);

        } catch (IOException e) {
            LOG.info(e.getClass().getName() + ": " + e.getMessage(), e);
        }

	}

	private void createAndShowGUI() {

		// create the Frame
		mainFrame = new JFrame("Birthday Reminder");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(600, 400);

        tableModel = new AppTableModel(new String [] {}, 0);
        tableModel.addColumn("Date");
        tableModel.addColumn("Name");
        tableModel.addColumn("Age");
    	table = new JTable(tableModel);
    	table.setFont(new Font("SansSerif", Font.PLAIN, 16));
    	table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 16));
    	table.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(table);
        mainFrame.add(scrollPane, BorderLayout.CENTER);

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - mainFrame.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - mainFrame.getHeight()) / 2);
		mainFrame.setLocation(x, y);

        // create the MenuBar and add components
    	JMenuBar mb = new JMenuBar();

        JMenuItem menuItemOptions = new JMenuItem(REFRESH);
        menuItemOptions.setActionCommand(REFRESH);
        menuItemOptions.addActionListener(this);
        mb.add(menuItemOptions);

        menuItemOptions = new JMenuItem(OPTIONS);
        menuItemOptions.setActionCommand(OPTIONS);
        menuItemOptions.addActionListener(this);
        mb.add(menuItemOptions);

        menuItemOptions = new JMenuItem(EXIT);
        menuItemOptions.setActionCommand(EXIT);
        menuItemOptions.addActionListener(this);
        mb.add(menuItemOptions);

        mainFrame.setJMenuBar(mb);

		refreshList();

        mainFrame.setVisible(true);
	}


    @Override
	public void actionPerformed(ActionEvent e) {

		String cmd = e.getActionCommand();
		LOG.info("Action command: " + cmd);

		if (cmd.equals(REFRESH))
			refreshList();

		if (cmd.equals(OPTIONS)) {
			optionsDlg.run();
		}

		if (cmd.equals(EXIT)) {
			System.exit(0);
		}
    }

	@Override
	public void focusGained(FocusEvent e) {
	}

	@Override
	public void focusLost(FocusEvent event) {
	}

	public void refreshList() {
		Database db = new Database();

		boolean conn = db.init(optionsDlg.getDbUrl(), optionsDlg.getDbUser(), optionsDlg.getDbPass());
		if (!conn) {
			Vector<String> row = new Vector<>();
			tableModel.addRow(row);

			row = new Vector<>();
			row.add("");
			row.add("DB connection failure");
			row.add("");
			tableModel.addRow(row);

			return;
		}

		tableModel.setNumRows(0);
		db.nextBirthdays(optionsDlg.getLookahead(), optionsDlg.getFlags(), tableModel);

	}

    private static class AppTableModel extends DefaultTableModel {
    	private static final long serialVersionUID = -4307863734765833642L;

		public AppTableModel(String[] strings, int i) {
			super(strings, i);
		}

		@Override
    	public boolean isCellEditable(int row, int column) {
    		return false;
    	}
    }

}
