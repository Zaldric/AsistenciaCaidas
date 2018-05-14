<?php
//Login
if (!isset($_POST['username']) || !isset($_POST['password'])){
  echo "-1";
} else {
  $link = mysqli_connect('localhost','root','','asistenciacaidas');

  $sql = sprintf("SELECT count(*) FROM cuidadores WHERE email=\"%s\" AND pass=\"%s\"",$_POST['username'],MD5($_POST['password']));
  $res = mysqli_query($link,$sql) or die($sql);
  $result = mysqli_fetch_array($res)[0];

  if ($result == 1){ //Success
    echo "1";
  } else { //Failure
    echo "0";
  }

}

 ?>
