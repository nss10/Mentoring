<?php
session_start();
require "conn.php";

/*
$_SESSION['username']="14bd1a052x";
$_SESSION['password']="nvas";
$_SESSION['IMEI']="862628033023180";
*/



$_SESSION['username']=$_POST["user_name"];
$_SESSION['password']=$_POST["password"];
$_SESSION['IMEI']=$_POST["IMEI"];


if(strlen($_SESSION['username'])==8)
{
  $qry="select fac_no from Mentors where fac_no like '".$_SESSION['username']."';";
  $result=mysqli_query($conn,$qry);
  if(mysqli_num_rows($result)==0) 
    die("You're not a mentor");
}




$qry="select password,status from Login where username like '".$_SESSION['username']."'";

$result=mysqli_query($conn,$qry);

if(mysqli_num_rows($result)>0)
{
  $row=mysqli_fetch_assoc($result);
//  echo " PHP version ".phpversion();
  
  if(password_verify($_SESSION['password'],$row['password']))
  {
      
    if(((strlen($_SESSION['username'])==10 && substr_count($row['status'],",")<3))|| strlen($row['status'])==1)
    {
      if(strlen($row['status'])==1)
      $cur_status = $_SESSION['IMEI'].",";
      else
      $cur_status = $row['status'].$_SESSION['IMEI'].",";

      $qry="update Login set status='".$cur_status."' where username like '".$_SESSION['username']."';";

      mysqli_query($conn,$qry);
      echo "1";

    }

    else
      echo "Already Logged in";

  }
  else{
      echo "0";
  } 
}
else
{
  echo "0";
}


//  mysqli_close($conn);

?>