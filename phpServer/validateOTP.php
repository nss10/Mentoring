<?php
 session_start();
 require "conn.php";
 $_SESSION['username']=$_POST["user_name"];
 $_SESSION['OTP']=$_POST["OTP"];
 $qry="select OTP from Student where ht_no='".$_SESSION['username']."';";
 $result=mysqli_query($conn,$qry);
 $row=mysqli_fetch_assoc($result);

 if(strpos($row['OTP'],$_SESSION['OTP'])!== false)
 {
 	echo "1";
 }
 else
 {
 	echo "0";
 }
 ?>