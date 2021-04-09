<?php
 session_start();
 require "conn.php";
 
 
 $_SESSION['username']=$_POST["user_name"];
 $password=$_POST["cur_pwd"];
 $_SESSION['cur_pwd']=password_hash($password, PASSWORD_DEFAULT);
 $password=$_POST["new_pwd"];
 $_SESSION['new_pwd']=password_hash($password, PASSWORD_DEFAULT);
 


$qry="update Login set password='".$_SESSION['new_pwd']."' where username = '".$_SESSION['username']."'";

mysqli_query($conn,$qry);

if(mysqli_affected_rows($conn)>=0)
{
	
echo "password changed";
}
else
{
echo "password change Failed";
}
//mysqli_close($conn);



?>

