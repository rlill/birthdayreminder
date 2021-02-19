package rlill.reminder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

public class Database {

	private static Logger LOG = Logger.getLogger(Database.class);

	private Connection connection;

	public final static String bbq = "select * from events_ann "
			+ "where date_format(event_date,'%m%d') >= date_format(now(),'%m%d') "
			+ "  and date_format(event_date,'%m%d') < date_format(DATE_ADD(NOW(), INTERVAL 5 DAY),'%m%d')";


	public final static String bbq1 = "select description, ? - year(event_date) as years, "
		   + "dayofmonth(event_date) as month, month(event_date) as day from events_ann "
		   + "where date_format(event_date, ?) between ? and ? "
		   + "   or date_format(event_date, ?) between ? and ? "
//		   + "and flags > 0 "
		   + "order by month(event_date), dayofmonth(event_date) asc";

	public boolean init(String url, String user, String password) {

		try {
			// load the DBdriver
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();

			// start the DBdriver
			connection = DriverManager.getConnection(url, user, password);
		}
		catch (Exception e) {
            LOG.error(e.getClass().getName() + ": " + e.getMessage(), e);
            return false;
		}
		return true;
	}

	public void nextBirthdays(int futureDays, DefaultTableModel tableModel) {

		try {

			Calendar cal = Calendar.getInstance();
			Date now = new Date();
			cal.setTime(now);
			cal.add(Calendar.DAY_OF_MONTH, -1);
			int nowyear = cal.get(Calendar.YEAR);
			now = cal.getTime();

			cal.add(Calendar.DAY_OF_MONTH, futureDays);
			Date then = cal.getTime();

			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

			PreparedStatement stmt = connection.prepareStatement(bbq1);

//	select description, ? - year(event_date) as years,
//	dayofmonth(event_date) as month, month(event_date) as day from events_ann
//	where date_format(event_date, ?) between ? and ?
//	   or date_format(event_date, ?) between ? and ?
//	and flags > 0
//	order by month(event_date), dayofmonth(event_date) asc

			stmt.setInt(1, nowyear);
			stmt.setString(2, String.format("%4d-%%m-%%d", nowyear));
			stmt.setString(3, df.format(now));
			stmt.setString(4, df.format(then));

			stmt.setString(5, String.format("%4d-%%m-%%d", nowyear+1));
			stmt.setString(6, df.format(now));
			stmt.setString(7, df.format(then));

			LOG.debug(String.format("Query %d days in the future from %s to %s",
					futureDays, now.toString(), then.toString()));
			ResultSet rs = stmt.executeQuery();

			while (rs.next())
			{
				Vector<String> row = new Vector<>();
				row.add(String.format("%02d.%02d.", rs.getInt(3), rs.getInt(4)));
				row.add(rs.getString(1));
				row.add(String.format("%d", rs.getInt(2)));
				tableModel.addRow(row);
			}

			rs.close();

		}
		catch (Exception e) {
            LOG.error(e.getClass().getName() + ": " + e.getMessage(), e);
		}
	}

}
