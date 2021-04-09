<?php
 
 require "conn.php";
 if($_SERVER['REQUEST_METHOD']=='GET'){
 $id = $_GET['id'];
 $sql = "select image from Student where ht_no = '$id'";
 
 
 $r = mysqli_query($conn,$sql);
 
 $result = mysqli_fetch_array($r);
 $image_data = $result['image'];
 
 header('content-type: image/jpeg');
 /*echo '<img src="data:image/jpg;base64,<?php echo base64_encode( $image_data ); ?>" />';*/

echo $image_data;


 //echo base64_decode($result['image']);
 /*
 // Format the image SRC:  data:{mime};base64,{data};
$src = 'data: '.mime_content_type($img_data).';base64,'.$imgData;

// Echo out a sample image
echo '<img src="'.$src.'">';*/
 //mysqli_close($conn);
 
 }else{
 echo "Error";
 }