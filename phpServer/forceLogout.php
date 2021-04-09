<!DOCTYPE html>
<html>
<head>
  <title>Force Logout</title>
</head>
<body>

<form method="POST">
  Please enter the root password: <input type="password" name="root_password">
  <input type="submit" name="submit">
</form>

</body>
</html>

<?php
 session_start();
 require "conn.php";
 
 $listMentorsURL = "http://kmit.in/mentoringApp/listMentors.php";
 $_SESSION['username']=$_GET["username"];
 

 if($_SERVER['REQUEST_METHOD'] === 'POST')
 {
  if($_POST['root_password']=="magicKey")
  {

   $qry="select status from Login where username like '".$_SESSION['username']."';";
   $result=mysqli_query($conn,$qry);
   if(mysqli_num_rows($result)>0)
   {
     $row=mysqli_fetch_assoc($result);
     if((strcmp($row['status'],'0')!=0))
     {
        $cur_status = 0;
        $qry="update Login set status='$cur_status' where username like '".$_SESSION['username']."';";
        mysqli_query($conn,$qry);
        header("Location: $listMentorsURL");

     }
     else
           echo $_SESSION['username']." already logged out";

     
   }
   else
   {
     echo "No such username exists";
   }
  // mysqli_close($conn);
  }
   else
   {
      echo "Incorrect root credentials";
   }
 }
?>

