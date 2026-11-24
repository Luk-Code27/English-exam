# Remove the picture closest to you (max. 8 blocks away)
execute at @s run kill @e[type=minecraft:item_display,tag=exam.image,sort=nearest,limit=1,distance=..8]
