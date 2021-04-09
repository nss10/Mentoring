<?php
 session_start();
 require "conn.php";
 
 
 $_SESSION['username']=$_POST["user_name"];
 $_SESSION['IMEI']=$_POST["IMEI"];
 
 $qry="select status from Login where username like '".$_SESSION['username']."';";
 //echo $_SESSION['username'];
 /*if(strlen($_SESSION['username'])==10)
    $qry="select * from Student where ht_no like '".$_SESSION['username']."' and password like '".$_SESSION['password']."';";   
 else
	$qry="select * from mentor where fac_no like '".$_SESSION['username']."' and password like '".$_SESSION['password']."';" ;*/
 //echo $qry;
 //echo "hello see this..";
 $result=mysqli_query($conn,$qry);
 //echo "display this...";
 if(mysqli_num_rows($result)>0)
 {
   $row=mysqli_fetch_assoc($result);
   //echo strcmp($row['status'],'0'); 
   if((strcmp($row['status'],'0')!=0))
   {
   		
               if(strlen($_SESSION['IMEI'])==0)
                            $cur_status = 0;
                else
                  $cur_status = str_replace($_SESSION['IMEI']."," ,  "" , $row['status'] );

                $qry="update Login set status='$cur_status' where username like '".$_SESSION['username']."';";
   		mysqli_query($conn,$qry);
   		echo "1";

   }
   else
         echo "0";

   
 }
 else
 {
   echo "0";
 }
//  mysqli_close($conn);

?>