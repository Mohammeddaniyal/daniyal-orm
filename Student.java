import java.util.Date;
@Table(name="student")
public class Student
{
@Column(name="roll_number")
@PrimaryKey
public int rollNumber;

@Column(name="first_name")
public String firstName;

@Column(name="last_name")
public String lastName;

@Column(name="aadhar_card_number")
public String aadharCardNumber;

@Column(name="course_code")
@ForeignKey(parent="course",column="code")
public int courseCode;

@Column(name="gender")
public char gender;

@Column(name="date_of_birth")
public Date dateOfBirth;

}