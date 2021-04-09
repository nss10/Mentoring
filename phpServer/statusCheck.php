<?php
 session_start();
 require "conn.php";
 
 
 $_SESSION['username']=/*"14bd1a053k";//*/$_POST["user_name"];
 
 
 $qry="select status from Login where username like '".$_SESSION['username']."';";
 $result=mysqli_query($conn,$qry);
 if(mysqli_num_rows($result)>0)
 {
   $row=mysqli_fetch_assoc($result);
    echo $row['status']; 
 }
 else
 {
   echo "0";
 }
//  mysqli_close($conn);

?>