<?php
 session_start();
 require "conn.php";
 
 
 $_SESSION['username']=$_POST["user_name"];
 // $_SESSION['username']="16bd1a1225";

 $password=$_POST["new_pwd"];
 // $password="mrunal";

 $_SESSION['new_pwd'] = password_hash($password, PASSWORD_DEFAULT);
 


$qry="insert into Login values ('".$_SESSION['username']."','".$_SESSION['new_pwd']."',0);";
//echo $qry;
if(mysqli_query($conn,$qry))
	echo "1";
else
	echo "0";
 //echo "display this...";

$qry = "update Student set otp = '1' where ht_no = '". $_SESSION['username']."';";
mysqli_query($conn,$qry);

 


?>