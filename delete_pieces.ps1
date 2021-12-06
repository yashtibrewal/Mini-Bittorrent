#Run the script by using .\delete_pieces.ps1 in the root directory of the project.

$numbers=1001, 1002, 1003, 1004, 1005
foreach ($num in $numbers) {
  Remove-Item .\$num\piece_*
 }
