<?php
 session_start();
 require "conn.php";
/*
Bdcs1001
14BD1A043KSPLITTER14BD1A052XSPLITTER14BD1A053KSPLITTER14BD1A0612SPLITTER nullSPLITTERnullSPLITTERnullSPLITTERnullSPLITTER
nullSPLITTERnullSPLITTERnullSPLITTERnullSPLITTER 0~0~0~0~0~0~0~SPLITTER3~4~5~5~4~5~5~SPLITTER2~5~4~1~3~4~1~SPLITTER0~0~0~0~0~0~0~SPLITTER 0SPLITTER1SPLITTER1SPLITTER0SPLITTER 6


*/
 $_SESSION['fac_username']=/*"Bdcs1001";//*/$_POST["fac_user_name"];
 $_SESSION['std_username']=/*"14BD1A043KSPLITTER14BD1A052XSPLITTER14BD1A053KSPLITTER14BD1A0612SPLITTER";//*/$_POST["std_user_name"];
 $_SESSION['remarks']=/*"nullSPLITTERnullSPLITTERnullSPLITTERnullSPLITTER";//*/$_POST["remarks"];
 $_SESSION['date']=/*"nullSPLITTERnullSPLITTERnullSPLITTERnullSPLITTER";//*/$_POST["date"];
 $_SESSION['Mentor_fields']=/*"0~0~0~0~0~0~0~SPLITTER3~4~5~5~4~5~5~SPLITTER2~5~4~1~3~4~1~SPLITTER0~0~0~0~0~0~0~SPLITTER";//*/$_POST['rating'];
 $_SESSION['isUpdatable']=/*"0SPLITTER1SPLITTER1SPLITTER0SPLITTER";//*/$_POST['isRatingSubmittable'];
 $_SESSION['sem']=/*intval("7");//*/intval($_POST['studentSem']);
 $_SESSION['isStudentFlagged']=/*"0SPLITTER1SPLITTER1SPLITTER0SPLITTER";//*/$_POST['isStudentFlagged'];
 $_SESSION['conn']=$conn;
 $std=explode("SPLITTER",$_SESSION['std_username']);
 $isStudentFlagged=explode("SPLITTER",$_SESSION['isStudentFlagged']);



	$Remarks=explode("SPLITTER",$_SESSION['remarks']);
	$date=explode("SPLITTER",$_SESSION['date']);

	$n=count($std)-1;
	for($i=0;$i<$n;$i++)
	{
		$qry="update Student set isStudentFlagged=".$isStudentFlagged[$i]."  where ht_no='".$std[$i]."';";
			 mysqli_query($conn,$qry);
			 /*if(mysqli_query($conn,$qry))
			 	echo $qry."isStudentFlagged is inserted with val = $isStudentFlagged[$i] ";
			 else	
			 	echo "isStudentFlagged is not inserted";*/

	}
	for($i=0;$i<$n;$i++)
	{
		if(strcmp($Remarks[$i],"null")==0)
			continue;
		$qry="select remarks,mentored_date from Remarks where fac_no='".$_SESSION['fac_username']."' and ht_no='".$std[$i]."';";
		$result=mysqli_query($conn,$qry);
		//echo mysqli_num_rows($result);

		if(mysqli_num_rows($result)>0)
		{
			$row=mysqli_fetch_assoc($result);
			$str=$row["remarks"] . "~" . $Remarks[$i];
			$date_str=$row["mentored_date"] . "~" . $date[$i]; 
			$qry="update Remarks set remarks='".$str."',mentored_date='".$date_str."'  where fac_no='".$_SESSION['fac_username']."' and ht_no='".$std[$i]."';";
			 mysqli_query($conn,$qry);
			// echo $_SESSION['remarks']; 
		}
		else
		{
			//$qry="insert into Remarks values('".$_SESSION['fac_username']."','".$std[$i]."','".$Remarks[$i]."','".$date[$i]."'); ";
			$qry="insert into Remarks values('".$_SESSION['fac_username']."','".$std[$i]."','".$Remarks[$i]."','".$date[$i]."','".date("Y-m-d")."'); ";
			mysqli_query($conn,$qry);
			 /*if(mysqli_query($conn,$qry))
			 	echo "inserted";
			 else
			 	echo "not inserted";*/
		}
	}


	
	$allStdfields=explode("SPLITTER",$_SESSION['Mentor_fields']);
	$isUpdatableToDB=explode("SPLITTER",$_SESSION['isUpdatable']);
	$arr=array("GD","CS","Grooming","Behavior_peers","Behavior_Fac","CCA","ECA");
	$_SESSION['arr']=$arr;
	for($i=0;$i<count($allStdfields);$i++)
	{
		$z=explode("~",$allStdfields[$i]);
		//echo $allStdfields[$i]."<br>";
	  	for($k=0;$k<count($z);$k++)
	    {
	      	$Mentor_fields[$i][$k]=$z[$k];
	    }
	}
	for($i=0;$i<count($std)-1;$i++)
	{
		
		if($isUpdatableToDB[$i]=="0")
		{
			continue;
		}
		//echo "<br>".$std[$i];
		
		$qry="select * from Mentor_Fields where ht_no='".$std[$i]."';";
		$result=mysqli_query($conn,$qry);
		
		if(mysqli_num_rows($result)>0)
		{

			$row=mysqli_fetch_assoc($result);
			
			for($j=0;$j<count($arr);$j++)
			{
				//$x=$row[$arr[$j+1]] . $Mentor_fields[$i][$j];
				// echo $row[$arr[$j]]."<br>";
				while(strlen($row[$arr[$j]])<2*($_SESSION['sem']-1))
				{
					$row[$arr[$j]] = $row[$arr[$j]]."0,";
				}
				// echo $row[$arr[$j]]."<br>";
				$x=substr($row[$arr[$j]],0,(2*($_SESSION['sem']-1))) . $Mentor_fields[$i][$j].",";
				
				$qry1="update Mentor_Fields set ".$arr[$j]." = '".$x."' where ht_no='".$std[$i]."';";
				
				mysqli_query($conn,$qry1);
							
				$qry1="update Mentor_Fields set Aggr = '".UpdateAggr($std[$i])."' where ht_no='".$std[$i]."';";
				mysqli_query($conn,$qry1);
					
			}
			
		}
		else
		{
			$cur_aggr = str_replace("~", ",", $allStdfields[$i]);
			//echo  $cur_aggr;
			$prev_sem="";
			for($j=0;$j<$_SESSION['sem']-1;$j++)
					$prev_sem=$prev_sem."0,";
			//echo  "prev_sem= ". $prev_sem;
			//$qry2="insert into Mentor_Fields values('".$std[$i]."','".$prev_sem.$Mentor_fields[$i][0].",','".$prev_sem.$Mentor_fields[$i][1].",','".$prev_sem.$Mentor_fields[$i][2].",','".$prev_sem.$Mentor_fields[$i][3].",','".$prev_sem.$Mentor_fields[$i][4].",','".$prev_sem.$Mentor_fields[$i][5].",','".$prev_sem.$Mentor_fields[$i][6].",','".$cur_aggr."');";
			$qry2="insert into Mentor_Fields values('".$std[$i]."','".$prev_sem.$Mentor_fields[$i][0].",','".$prev_sem.$Mentor_fields[$i][1].",','".$prev_sem.$Mentor_fields[$i][2].",','".$prev_sem.$Mentor_fields[$i][3].",','".$prev_sem.$Mentor_fields[$i][4].",','".$prev_sem.$Mentor_fields[$i][5].",','".$prev_sem.$Mentor_fields[$i][6].",','".$cur_aggr."','".date("Y-m-d")."');";
			//echo $qry2;
			mysqli_query($conn,$qry2);
			 /*if(mysqli_query($conn,$qry2))
			 	echo "inserted";
			 else
			 	echo "not inserted";*/
		}
	}
	echo "1";


/*function getNewAggr($id,$allStdfields) {
	$arr=$_SESSION['arr'];
    $qry="select * from Mentor_Fields where ht_no='".$id."';";
	$result=mysqli_query($_SESSION['conn'],$qry);
	$row=mysqli_fetch_assoc($result);
	$newAggr="";
	for($i=0;$i<count($arr);$i++)
	{
		$Cur_List = explode("~",$row[$arr[$i]]);
		$newAggr = $newAggr.round(array_sum($Cur_List)/$_SESSION['sem']).",";
		
	}
	
	return $newAggr;
}
*/

function UpdateAggr($id) {
	$arr=$_SESSION['arr'];
    $qry="select * from Mentor_Fields where ht_no='".$id."';";
	$result=mysqli_query($_SESSION['conn'],$qry);
	$row=mysqli_fetch_assoc($result);
	$newAggr="";
	for($i=0;$i<count($arr);$i++)
	{
		$count=0;
		$Cur_List = explode(",",$row[$arr[$i]]);
		for($j=0;$j<count($Cur_List);$j++)
			if($Cur_List[$j]!=0)
				$count++;

		if($count!=0)
			$newAggr = $newAggr.round(array_sum($Cur_List)/$count).",";

		
	}
	return $newAggr;
}
	




/*function UpdateAggr($id) {
	$arr=$_SESSION['arr'];
    $qry="select * from Mentor_Fields where ht_no='".$id."';";
	$result=mysqli_query($_SESSION['conn'],$qry);
	$row=mysqli_fetch_assoc($result);
	$newAggr="";
	for($i=0;$i<count($arr);$i++)
	{		
		$Cur_List = explode(",",$row[$arr[$i]]);
		$newAggr = $newAggr.round(array_sum($Cur_List)/$_SESSION['sem']).",";
		
	}
	return $newAggr;
}
*/