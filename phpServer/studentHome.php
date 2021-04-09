<?php
 session_start();
 require "conn.php";
 $_SESSION['username']=/*"14BD1A052X";//*/$_POST["user_name"];
// $_SESSION['mentor_class_id']=$_POST["mentor_class_id"];
 //$_SESSION['mentor_division']=$_POST["mentor_division"];


 //$_SESSION['username']="14BD1A052X";
 $qry="select * from Student where ht_no = '".$_SESSION['username']."';";  
// $qry="select * from Student where class_id = '".$_SESSION['mentor_class_id']."' && division = '".$_SESSION['mentor_division']."'";  

 //We need to change this query to where class_id and division!
 $result=mysqli_query($conn,$qry);
$class_id="";
$Division="";
 if(mysqli_num_rows($result)>0)
 {
    while($row=mysqli_fetch_assoc($result))		//This loop is already in 'while' so no issue
	{
		
		echo $row["ht_no"]."<br>".$row["name"]."&&".$row["parent_name"]."&&".$row['OTP']."&&".$row['phone']."<br>".$row["class_id"]."&&".$row["sem"]."<br>".$row["attendance1"]."<br>".$row["Academics"]."<br>".$row["isStudentFlagged"]."<br>";
		$class_id=$row["class_id"];
		$Division=$row["division"];
		
		// echo base64_decode($row['image']);
		$qry="select fac_no from Mentors where class_id = '$class_id' and division='$Division';";
 		$result=mysqli_query($conn,$qry);
 		$row=mysqli_fetch_assoc($result);
 		$mentor_id=$row["fac_no"];
 		$qry="select name from faculty where roll_no='$mentor_id';";
 		$result=mysqli_query($conn,$qry);
 		$row=mysqli_fetch_assoc($result);
 		echo $row["name"]."<br>";
		$qry="select * from Remarks where ht_no = '".$_SESSION['username']."';";
		$result=mysqli_query($conn,$qry);
		if(mysqli_num_rows($result)>0)
 		{				
 			$row=mysqli_fetch_assoc($result);
 			echo $row["remarks"]."<br>".$row["mentored_date"]."<br>" ;
 		}
 		else
 			echo "No "."<br>". "remarks"."<br>";
 		$qry="select * from Mentor_Fields where ht_no = '".$_SESSION['username']."';";
		$result=mysqli_query($conn,$qry);
		
        if(mysqli_num_rows($result)>0)
         {				
         	while($row3=mysqli_fetch_assoc($result))
         	{
         		$remarkString = $remarkString.$row3["remarks"];
         		$dateString = $dateString.$row3["mentored_date"];
         	}
         	echo $remarkString."<br>".$dateString."<br>" ;
         }



		/* // commented on Sep 5th 2017
		if(mysqli_num_rows($result)>0)
 		{				
 			$row=mysqli_fetch_assoc($result);
 			echo $row["GD"]."~".$row["CS"]."~".$row["Grooming"]."~".$row["Behavior_peers"]."~".$row["Behavior_Fac"]."~".$row["CCA"]."~".$row["ECA"]."~".$row["Aggr"]."~"."<br>";
 		}
 		else
 			echo "Not graded"."<br>;";
 			*/
	}
 }
 else
 {
   echo "Error_studentHome_php";
 }

 
//  mysqli_close($conn);

?>	