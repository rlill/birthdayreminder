package rlill.reminder;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;
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
	private JButton button;

	private String dbUrl;
	private String dbUser;
	private String dbPass;

	private final static String PROPERTIES = "birthdayreminder.properties";

	public static void main(String[] argv) {

		System.setProperty("apple.laf.useScreenMenuBar", "true");

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

            // get the property values
            dbUrl = prop.getProperty("db.url");
            dbUser = prop.getProperty("db.user");
            dbPass = prop.getProperty("db.password");

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

		button = new JButton("refresh");
		button.addActionListener(this);
		mainFrame.add(button, BorderLayout.PAGE_END);

        mainFrame.setVisible(true);
	}


    @Override
	public void actionPerformed(ActionEvent e) {

		String cmd = e.getActionCommand();
		LOG.info("Action command: " + cmd);

		dumpDatabase();

//		switch (cmd) {
//
//		case ACTION_LOAD:
//			LOG.info("Load properties");
//			loadProperties();
//			break;
//
//		case ACTION_SAVE:
//			LOG.info("Save dump");
//			dumpDatabase();
//			break;
//
//		}
    }

	@Override
	public void focusGained(FocusEvent e) {
	}

	@Override
	public void focusLost(FocusEvent event) {
	}

	private void dumpDatabase() {
		Database db = new Database();

		boolean conn = db.init(dbUrl, dbUser, dbPass);
		if (!conn) {
			// message
			return;
		}

		db.nextBirthdays(30, tableModel);

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
