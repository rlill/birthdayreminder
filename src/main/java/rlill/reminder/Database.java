package rlill.reminder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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


/*
select * from Employees
where DATEADD (year, DatePart(year, getdate()) - DatePart(year, Birthday), Birthday)
      between convert(datetime, getdate(), 101)
              and convert(datetime, DateAdd(day, 5, getdate()), 101)
 */


/*
	Today:

	my ($t_mday,$t_mon,$t_year) = (localtime)[3,4,5];

	my $query = "select description, year(event_date) from events_ann "
		. "where month(event_date) = " . ($t_mon + 1)
		. " and dayofmonth(event_date) = $t_mday "
		. "order by description asc";

*/



/*
	Future:

	my $now = time;
	my ($t0,$t0,$t0,$t_day1,$t_mon1,$t_year1,$t0,$t0,$t0) = localtime $now;

#	my $then = $now + 367 * 24 * 60 * 60;
	my $then = $now + 30 * 24 * 60 * 60;
	my ($to,$to,$to,$t_day2,$t_mon2,$t_year2,$to,$to,$to) = localtime $then;

	my $extraline = 1;
	if ($t_year2 == $t_year1)
	{
		my $query = sprintf "select description, %d - year(event_date), "
					   . "dayofmonth(event_date), month(event_date) from events_ann "
					   . "where date_format(event_date, '%4d-%%m-%%d') > '%4d-%02d-%02d' "
					   . "and date_format(event_date, '%4d-%%m-%%d') < '%4d-%02d-%02d' "
					   . "and flags > 0 "
					   . "order by month(event_date), dayofmonth(event_date) asc",
					   $t_year1 + 1900,
					   $t_year1 + 1900, $t_year1 + 1900, $t_mon1 + 1, $t_day1,
					   $t_year2 + 1900, $t_year2 + 1900, $t_mon2 + 1, $t_day2;

		my $sth = $dbh->prepare($query)
		    or giveup ("Can't prepare query: " . $dbh->errstr, $dbh);

		my $rv = $sth->execute
		    or giveup ("can't execute the query: " . $sth->errstr, $dbh);

		my @row;
		while(@row = $sth->fetchrow_array)
		{
			printf "<tr><td>%2d.%02d.</td><td>%s (%d)</td></tr>\n",
				$row[2], $row[3], $row[0], $row[1];
		}

		$sth->finish;
	}
	else
	{
		# remainder of the current year
		my $query = sprintf "select description, %d - year(event_date), "
					   . "dayofmonth(event_date), month(event_date) from events_ann "
					   . "where date_format(event_date, '%4d-%%m-%%d') > '%4d-%02d-%02d' "
					   . "and flags > 0 "
					   . "order by month(event_date), dayofmonth(event_date) asc",
					   $t_year1 + 1900,
					   $t_year1 + 1900, $t_year1 + 1900, $t_mon1 + 1, $t_day1;

		my $sth = $dbh->prepare($query)
		    or giveup ("Can't prepare query: " . $dbh->errstr, $dbh);

		my $rv = $sth->execute
		    or giveup ("can't execute the query: " . $sth->errstr, $dbh);

		my @row;
		while(@row = $sth->fetchrow_array)
		{
			printf "<tr><td>%2d.%02d.</td><td>%s (%d)</td></tr>\n",
				$row[2], $row[3], $row[0], $row[1];
		}
		$sth->finish;

		# beginning of next year
		$query = sprintf "select description, %d - year(event_date), "
					   . "dayofmonth(event_date), month(event_date) from events_ann "
					   . "where date_format(event_date, '%4d-%%m-%%d') < '%4d-%02d-%02d' "
					   . "and flags > 0 "
					   . "order by month(event_date), dayofmonth(event_date) asc",
					   $t_year2 + 1900, $t_year2 + 1900, $t_year2 + 1900, $t_mon2 + 1, $t_day2;

		my $sth = $dbh->prepare($query)
		    or giveup ("Can't prepare query: " . $dbh->errstr, $dbh);

		my $rv = $sth->execute
		    or giveup ("can't execute the query: " . $sth->errstr, $dbh);

		while(@row = $sth->fetchrow_array)
		{
			printf "<tr><td>%2d.%02d.</td><td>%s (%d)</td></tr>\n",
				$row[2], $row[3], $row[0], $row[1];
		}
		$sth->finish;
	}
}


 */


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

	public List<String> nextBirthdays(int futureDays) {

		List<String> result = new ArrayList<>();

		try {

			Calendar cal = Calendar.getInstance();
			Date now = new Date();
			cal.setTime(now);
			int nowyear = cal.get(Calendar.YEAR);
			cal.add(Calendar.MONTH, 1);
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

			ResultSet rs = stmt.executeQuery();

			while (rs.next())
			{
				result.add(String.format("%s %d (%d.%d.)", rs.getString(1), rs.getInt(2),
						rs.getInt(3), rs.getInt(4)));
			}

			rs.close();

		}
		catch (Exception e) {
            LOG.error(e.getClass().getName() + ": " + e.getMessage(), e);
		}

		return result;
	}

}
