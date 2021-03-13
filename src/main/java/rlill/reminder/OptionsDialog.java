package rlill.reminder;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;


public class OptionsDialog extends JDialog implements FocusListener {

	private static final long serialVersionUID = -8440864629511879579L;

	private static Logger LOG = Logger.getLogger(OptionsDialog.class);

	private String dbUrl;
	private String dbUser;
	private String dbPass;
	private int flags;
	private int lookahead;

	private JTextField textUrl;
	private JTextField textUser;
	private JTextField textPass;
	private JTextField textLookahead;

	private JCheckBox flag1;
	private JCheckBox flag2;
	private JCheckBox flag3;
	private JCheckBox flag4;

	private final static String PROPERTIES = "birthdayreminder.properties";

	public OptionsDialog(Frame pf) {
		super(pf, "Options", true);

		JLabel labelUrl = new JLabel("DB URL");
		JLabel labelUser = new JLabel("Username");
		JLabel labelPass = new JLabel("Password");
		JLabel labelLookahead = new JLabel("Check days in future");
		JLabel labelFlag1 = new JLabel("Flag 1");
		JLabel labelFlag2 = new JLabel("Flag 2");
		JLabel labelFlag3 = new JLabel("Flag 3");
		JLabel labelFlag4 = new JLabel("Flag 4");

		textUrl = new JTextField(32);
		textUrl.addFocusListener(this);
		textUser = new JTextField(32);
		textUser.addFocusListener(this);
		textPass = new JTextField(32);
		textPass.addFocusListener(this);
		textLookahead = new JTextField(32);
		textLookahead.addFocusListener(this);

		flag1 = new JCheckBox();
		flag2 = new JCheckBox();
		flag3 = new JCheckBox();
		flag4 = new JCheckBox();

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);

		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(
		   layout.createSequentialGroup()
		      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		           .addComponent(labelUrl)
		           .addComponent(labelUser)
		           .addComponent(labelPass)
		           .addComponent(labelLookahead)
		           .addComponent(labelFlag1)
		           .addComponent(labelFlag2)
		           .addComponent(labelFlag3)
		           .addComponent(labelFlag4)
		      )
		      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		           .addComponent(textUrl)
		           .addComponent(textUser)
		           .addComponent(textPass)
		           .addComponent(textLookahead)
		           .addComponent(flag1)
		           .addComponent(flag2)
		           .addComponent(flag3)
		           .addComponent(flag4)
		      )
		);
		layout.setVerticalGroup(
		   layout.createSequentialGroup()
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
		           .addComponent(labelLookahead)
		           .addComponent(textLookahead)
		      )
		      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	    		  .addComponent(labelFlag1)
	    		  .addComponent(flag1)
    		  )
		      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	    		  .addComponent(labelFlag2)
	    		  .addComponent(flag2)
    		  )
		      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		           .addComponent(labelFlag3)
		           .addComponent(flag3)
		      )
		      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
		           .addComponent(labelFlag4)
		           .addComponent(flag4)
		      )
		);

	    ActionListener escListener = new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            dispose();
	        }
	    };

	    getRootPane().registerKeyboardAction(escListener,
	            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
	            JComponent.WHEN_IN_FOCUSED_WINDOW);

	    getRootPane().registerKeyboardAction(escListener,
	    		KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
	    		JComponent.WHEN_IN_FOCUSED_WINDOW);

	    readProperties();

		pack();
	}

	public void run() {
		this.setVisible(true);
	}

	private void readProperties() {

        try (InputStream input = new FileInputStream(PROPERTIES)) {

            Properties prop = new Properties();
            prop.load(input);

            // get the property values
            dbUrl = prop.getProperty("db.url");
            dbUser = prop.getProperty("db.user");
            dbPass = prop.getProperty("db.password");
            flags = atoi(prop.getProperty("flags"));
            lookahead = atoi(prop.getProperty("lookahead"));

            textUrl.setText(dbUrl);
            textUser.setText(dbUser);
            textPass.setText(dbPass);
            textLookahead.setText(Integer.toString(lookahead));

            flag1.setSelected((flags & 0x01) != 0);
            flag2.setSelected((flags & 0x02) != 0);
            flag3.setSelected((flags & 0x04) != 0);
            flag4.setSelected((flags & 0x08) != 0);

        } catch (IOException e) {
            LOG.error(e.getClass().getName() + ": " + e.getMessage(), e);
        }

	}

	@Override
	public void focusGained(FocusEvent e) {
	}

	@Override
	public void focusLost(FocusEvent event) {
        saveProperties();
	}

	private void saveProperties() {

		try (OutputStream output = new FileOutputStream(PROPERTIES)) {

		    dbUrl = textUrl.getText();
		    dbUser = textUser.getText();
		    dbPass = textPass.getText();
		    lookahead = atoi(textLookahead.getText());

		    flags =
		    		(flag1.isSelected() ? 0x01 : 0x00)
    			  | (flag2.isSelected() ? 0x02 : 0x00)
				  | (flag3.isSelected() ? 0x04 : 0x00)
				  | (flag4.isSelected() ? 0x08 : 0x00);

		    // set the properties values
		    Properties prop = new Properties();
		    prop.setProperty("db.url", dbUrl);
		    prop.setProperty("db.user", dbUser);
		    prop.setProperty("db.password", dbPass);
		    prop.setProperty("flags", Integer.toString(flags));
		    prop.setProperty("lookahead", Integer.toString(lookahead));

		    prop.store(output, null);

		} catch (IOException e) {
		    LOG.error(e.getClass().getName() + ": " + e.getMessage(), e);
		}
	}

	private static int atoi(String s) {
		try {
			return Integer.parseInt(s);
		}
		catch (Exception e) {
			return 0;
		}
	}

	public String getDbUrl() {
		return dbUrl;
	}
	public String getDbUser() {
		return dbUser;
	}
	public String getDbPass() {
		return dbPass;
	}
	public int getFlags() {
		return flags;
	}
	public int getLookahead() {
		return lookahead;
	}

}
