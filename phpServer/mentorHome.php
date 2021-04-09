<?php
 session_start();
 require "conn.php";
 $_SESSION['username']=$_POST["user_name"];
// $_SESSION['username']="BDCS1001";
 $qry="select name from faculty where roll_no = '".$_SESSION['username']."';";
$result=mysqli_query($conn,$qry);
 if(mysqli_num_rows($result)>0)
 {
    while($row=mysqli_fetch_assoc($result))
	{
		$mentorName = $row["name"];
		echo $mentorName."#";

	}
 }
 else
 {
   echo "Error.mentorHome.php1";
 }
 $qry="select class_id,Division from Mentors where fac_no = '".$_SESSION['username']."';";
 $result=mysqli_query($conn,$qry);
 $row=mysqli_fetch_assoc($result);
 $class_id = $row["class_id"];
 $Division = $row["Division"];
 //echo $class_id." ".$Division;
 $qry="select * from Student where class_id='$class_id' and Division='$Division';";
 $result=mysqli_query($conn,$qry);
 
 if(mysqli_num_rows($result)>0)
 {
    while($row=mysqli_fetch_assoc($result))
	{
		echo $row["ht_no"]."<br>".$row["name"]."&&".$row["parent_name"]."&&".$row['OTP']."&&".$row['phone']."<br>".$row["class_id"]."&&".$row["sem"]."<br>".$row["attendance1"]."<br>".$row["Academics"]."<br>".$row["isStudentFlagged"]."<br>";
		$name = $row["ht_no"];
		$class_id=$row["class_id"];
		$Division=$row["division"];
		
		echo $mentorName."<br>";

		$qry="select * from Remarks where ht_no = '".$name."';";
		$result3=mysqli_query($conn,$qry);
		
		
		if(mysqli_num_rows($result3)>0)
         {				
         	while($row3=mysqli_fetch_assoc($result3))
         	{
         		$remarkString = $remarkString.$row3["remarks"]."~";
         		$dateString = $dateString.$row3."~";
         	}
         	echo $remarkString."<br>".$dateString."<br>" ;
         }
 		else
 			echo "No "."<br>". "remarks"."<br>";
 			
 			
 			
 			
 		$qry="select * from Mentor_Fields where ht_no = '".$name."';";
		$result4=mysqli_query($conn,$qry);
		if(mysqli_num_rows($result4)>0)
 		{				
 			$row4=mysqli_fetch_assoc($result4);
 			$aggr="0";
 			if(strlen($row4["Aggr"])==0)	
 				$aggr = "0,0,0,0,0,0,0,";
 			else
 				$aggr = $row4["Aggr"];

 			echo $row4["GD"]."~".$row4["CS"]."~".$row4["Grooming"]."~".$row4["Behavior_peers"]."~".$row4["Behavior_Fac"]."~".$row4["CCA"]."~".$row4["ECA"]."~".$aggr."~"."<br>";
 		}
 		else
 			echo "Not graded"."<br>";
		
		echo "STUDENT SEPERATOR SPLIT";

	}
echo "#E.O.F";
 }
 else
 {
   echo "Error.mentorHome.php2";
 }
//  mysqli_close($conn);
 

?>