<?php
	$name = $_POST["name"];
	$image = $_POST["image"];
	$decodedImage = base64_decode("$image");
	file_put_contents("/home/sainote/pictures/" . $name , $decodedImage);
	system("python3 /home/sainote/backend/run.py");
?>
