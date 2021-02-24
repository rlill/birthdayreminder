package rlill.reminder;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import org.apache.log4j.Logger;


public class OptionsDialog extends JDialog implements ActionListener, FocusListener {

	private static final long serialVersionUID = -8440864629511879579L;

	private static Logger LOG = Logger.getLogger(OptionsDialog.class);

	private JLabel labelLoad;
	private JLabel labelSave;
	private JLabel labelUrl;
	private JLabel labelUser;
	private JLabel labelPass;
	private JLabel labelName;
	private JLabel labelShort;
	private JLabel labelTpn;

	private JTextField textUrl;
	private JTextField textUser;
	private JTextField textPass;
	private JTextField textName;
	private JTextField textShort;
	private JTextField textTpn;

	private JButton buttonLoad;
	private JButton buttonSave;

	private String dbUrl;
	private String dbUser;
	private String dbPass;
	private String tsbName;
	private String tsbShort;
	private String partner;

	private final static String ACTION_LOAD = "load";
	private final static String ACTION_SAVE = "save";

	private final static String PROPERTIES = "birthdayreminder.properties";

	public OptionsDialog(Frame pf) {
		super(pf, "Options", true);

		labelLoad = new JLabel("Properties file");
		labelSave = new JLabel("Run query");
		labelUrl = new JLabel("DB URL");
		labelUser = new JLabel("Username");
		labelPass = new JLabel("Password");
		labelName = new JLabel("TSB Name");
		labelShort = new JLabel("TSB Shortname");
		labelTpn = new JLabel("Trading Partner Name");

		textUrl = new JTextField();
		textUrl.addFocusListener(this);
		textUser = new JTextField();
		textUser.addFocusListener(this);
		textPass = new JTextField();
		textPass.addFocusListener(this);
		textName = new JTextField();
		textName.addFocusListener(this);
		textShort = new JTextField();
		textShort.addFocusListener(this);
		textTpn = new JTextField();
		textTpn.addFocusListener(this);

        textUrl.setText(dbUrl);
        textUser.setText(dbUser);
        textPass.setText(dbPass);
        textName.setText(tsbName);
        textShort.setText(tsbShort);
        textTpn.setText(partner);

		buttonLoad = new JButton("load ...");
		buttonLoad.setActionCommand(ACTION_LOAD);
		buttonLoad.addActionListener(this);

		buttonSave = new JButton("save ...");
		buttonSave.setActionCommand(ACTION_SAVE);
		buttonSave.addActionListener(this);


		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);

		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(
		   layout.createSequentialGroup()
		      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		           .addComponent(labelLoad)
		           .addComponent(labelUrl)
		           .addComponent(labelUser)
		           .addComponent(labelPass)
		           .addComponent(labelName)
		           .addComponent(labelShort)
		           .addComponent(labelTpn)
		           .addComponent(labelSave)
		      )
		      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		           .addComponent(buttonLoad)
		           .addComponent(textUrl)
		           .addComponent(textUser)
		           .addComponent(textPass)
		           .addComponent(textName)
		           .addComponent(textShort)
		           .addComponent(textTpn)
		           .addComponent(buttonSave)
		      )
		);
		layout.setVerticalGroup(
		   layout.createSequentialGroup()
		      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		           .addComponent(labelLoad)
		           .addComponent(buttonLoad)
		      )
		      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
			           .addComponent(labelUrl)
			           .addComponent(textUrl)
			      )
		      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		           .addComponent(labelUser)
		           .addComponent(textUser)
		      )
		      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		           .addComponent(labelPass)
		           .addComponent(textPass)
		      )
		      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	    		  .addComponent(labelName)
	    		  .addComponent(textName)
    		  )
		      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	    		  .addComponent(labelShort)
	    		  .addComponent(textShort)
    		  )
		      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		           .addComponent(labelTpn)
		           .addComponent(textTpn)
		      )
		      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		           .addComponent(labelSave)
		           .addComponent(buttonSave)
		      )
		);

		pack();
	}

	public void run() {
		this.setVisible(true);
	}


    @Override
	public void actionPerformed(ActionEvent e) {

		String cmd = e.getActionCommand();
		LOG.info("Action command: " + cmd);

		switch (cmd) {

		case ACTION_LOAD:
			LOG.info("Load properties");
			loadProperties();
			break;

		case ACTION_SAVE:
			LOG.info("Save dump");
			break;

		}
    }

	private void loadProperties() {

		JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

        jfc.setDialogTitle("Select an image");
        jfc.setAcceptAllFileFilterUsed(true);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Properties files", "properties");
        jfc.addChoosableFileFilter(filter);
        jfc.setMultiSelectionEnabled(true);
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int returnValue = jfc.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = jfc.getSelectedFile();
            readProperties(selectedFile.getAbsolutePath());
        }

	}

	private void readProperties(String propFileName) {

        try (InputStream input = new FileInputStream(propFileName)) {

            Properties prop = new Properties();
            prop.load(input);

            // get the property values
            dbUrl = prop.getProperty("ev.jdbc.url");
            dbUser = prop.getProperty("ev.jdbc.user");
            dbPass = prop.getProperty("ev.jdbc.password");

            if (dbPass == null || dbPass.isEmpty()) {
            	String encpw = prop.getProperty("ev.jdbc.encrypted");
            }

            textUrl.setText(dbUrl);
            textUser.setText(dbUser);
            textPass.setText(dbPass);

        } catch (IOException e) {
            LOG.error(e.getClass().getName() + ": " + e.getMessage(), e);
        }

	}

	@Override
	public void focusGained(FocusEvent e) {
	}

	@Override
	public void focusLost(FocusEvent event) {
		if (event.getComponent() == textUrl
				|| event.getComponent() == textUser
				|| event.getComponent() == textPass
				|| event.getComponent() == textName
				|| event.getComponent() == textShort
				|| event.getComponent() == textTpn) {
	        saveProperties();
		}
	}

	private void saveProperties() {

		try (OutputStream output = new FileOutputStream(PROPERTIES)) {

		    dbUrl = textUrl.getText();
		    dbUser = textUser.getText();
		    dbPass = textPass.getText();

		    // set the properties values
		    Properties prop = new Properties();
		    prop.setProperty("db.url", dbUrl);
		    prop.setProperty("db.user", dbUser);
		    prop.setProperty("db.password", dbPass);

		    prop.store(output, null);

		} catch (IOException e) {
		    LOG.error(e.getClass().getName() + ": " + e.getMessage(), e);
		}
	}

}
