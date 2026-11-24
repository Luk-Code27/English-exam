# Move the screen by whole blocks. Example: /function exam:screen/move {x:0,y:1,z:0}
$execute as @e[type=minecraft:item_display,tag=exam.screen] at @s run tp @s ~$(x) ~$(y) ~$(z)
