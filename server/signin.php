<?php
//Login
if (!isset($_POST['username']) || !isset($_POST['password'])){
  echo "-1";
} else {
  $link = mysqli_connect('localhost','root','','asistenciacaidas');

  $sql = sprintf("INSERT INTO cuidadores SET email=\"%s\",pass=\"%s\"",$_POST['username'],MD5($_POST['password']));
  $res = mysqli_query($link,$sql) or die($sql);
}

 ?>
