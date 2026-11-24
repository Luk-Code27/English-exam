# Place picture n 3 blocks in front of you. Example: /function exam:image/place {n:1}
$execute at @s anchored eyes positioned ^ ^ ^3 run summon minecraft:item_display ~ ~ ~ {Tags:["exam.image","exam.new"],billboard:"fixed",brightness:{sky:15,block:15},view_range:4.0f,transformation:{left_rotation:[0.0f,0.0f,0.0f,1.0f],right_rotation:[0.0f,0.0f,0.0f,1.0f],translation:[0.0f,0.0f,0.0f],scale:[2.0f,2.0f,2.0f]},item:{id:"minecraft:paper",count:1,components:{"minecraft:item_model":"exam:image_$(n)"}}}
execute as @e[type=minecraft:item_display,tag=exam.new] at @s facing entity @p eyes run tp @s ~ ~ ~ ~ 0
tag @e[type=minecraft:item_display,tag=exam.new] remove exam.new
