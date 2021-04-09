<?php
 session_start();
 require "conn.php";
 

 $_SESSION['username']=$_POST["user_name"];
 $qry="select username from Login where username='".$_SESSION['username']."';";
$result=mysqli_query($conn,$qry);
if(mysqli_num_rows($result)>0)
{
	echo "you have already registered!!";
}
else
{
	$qry="select name,email,class_id,division from Student where ht_no='".$_SESSION['username']."';";
	$result=mysqli_query($conn,$qry);
	if(mysqli_num_rows($result)==0)
	{
		echo "Invalid hall ticket number!!";
	}
	else
	{		
		$row=mysqli_fetch_assoc($result);
		$name = $row['name'];
		$email = trim($row['email']); 	// Just in case if there are any extra spaces in the email id mentioned in the database
		
		// Collecting the info from mentor. #temporary
		$otp=rand(1001,9999);  //since the boundaries are inclusive in php
		$insert="0," . $otp;
		$qry="update Student set OTP='".$insert."' where ht_no='".$_SESSION['username']."';";
		mysqli_query($conn,$qry);
		$qry="select name from faculty where roll_no in (select fac_no from Mentors where class_id='".$row['class_id']."' and division='".$row['division']."');";
		$result1=mysqli_query($conn,$qry);
		$row1=mysqli_fetch_assoc($result1);
		echo "OTP got generated and sent to your mentor. Please take it from your Mentor: " . $row1['name'];
		// #Temporary code ends here
		/*if(strcasecmp($email,"null")==0)	//otp will be displayed to the mentor.
		{
			$otp=rand(0001,9999);  //since the boundaries are inclusive in php
		$insert="0," . $otp;
		$qry="update Student set OTP='".$insert."' where ht_no='".$_SESSION['username']."';";
		mysqli_query($conn,$qry);
		$qry="select name from faculty where roll_no in (select fac_no from Mentors where class_id='".$row['class_id']."' and division='".$row['division']."');";
		$result1=mysqli_query($conn,$qry);
		$row1=mysqli_fetch_assoc($result1);
		echo "OTP got generated and sent to your mentor. Please take it from your Mentor: " . $row1['name'];
		}
		else
		{
			$otp=rand(0001,10000);
			$qry="select OTP from Student where ht_no='".$_SESSION['username']."';";
			$result2=mysqli_query($conn,$qry);
			$str=mysqli_fetch_assoc($result2);
			$insert=$str['OTP'] .",". $otp;
			$qry="update Student set OTP='".$insert."' where ht_no='".$_SESSION['username']."';";

			// generate email here
			$from = "Student Mentoring <mail.studentmentoring@gmail.com>";
		 	$to = "$name <$email>";
			$subject = "Student Mentoring One Time Password";
			$body = "Hello $name,\n\n Your one time password: $otp.\n\n NOTE:Your registration process will be completed only if you create your own password. We recommend you choose a strong password and keep it confidential. I hope you find our app to be productive.\n\n Regards \nStudent Mentoring team";
			 
			$host = "smtp.gmail.com";
			$username = "mail.studentmentoring";
			$password = "kmit123$";
			 
			$headers = array ('From' => $from,
			   'To' => $to,
			   'Subject' => $subject);
			$smtp = Mail::factory('smtp',
			   array ('host' => $host,
			     'auth' => true,
			     'username' => $username,
			     'password' => $password));
			 
			$mail = $smtp->send($to, $headers, $body);
			 
			if (PEAR::isError($mail)) {
			  echo("<p>" . $mail->getMessage() . "</p>");
			} else {
				  echo("<p>Message successfully sent! to $to with subject $subject</p><br>");
			}

  // Email code Ends here


			echo "OTP has been sent to " . $row['email'];
		}*/
	}
}

 ?>