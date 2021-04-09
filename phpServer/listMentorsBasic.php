<?php
 session_start();
 require "conn.php";
 
 $qry="SELECT * FROM `Mentors`,`Login` where`Mentors`.`fac_no`=`Login`.`username`  Order by class_id,Division ASC;";
 
 $result=mysqli_query($conn,$qry);
 //echo "display this...";
 echo "<table cellpadding='10' border=1>";
$count=0;
 if(mysqli_num_rows($result)>0)
 {
 	      echo "<tr><td>class</td><td>Division</td><td>Mentor name </td><td>Login ID</td></tr>";
   while($row=mysqli_fetch_assoc($result))
   {
	  $count=$count+1;
   	  $qry1="SELECT name FROM `faculty` where roll_no like '".$row['fac_no']."';";//" where class_id like 'IT';";
 
 	  $result1=mysqli_query($conn,$qry1);
 	  $row2 = mysqli_fetch_assoc($result1);
         

      echo "<tr><td>".$row['class_id']."</td><td>".$row['division']."</td><td>".$row2['name']."</td><td>".$row['fac_no']."</td></tr>";
   }
 }
 echo "</table>";
 
?>
