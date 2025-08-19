@Table(name="course")
public class Course
{
@Column(name="code")
@PrimaryKey
@AutoIncrement
public int code;

@Column(name="title")
public String title;

}