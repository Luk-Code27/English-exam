# Run this yourself: stand where you want to present, look at the wall, type /function exam:screen/place
# The screen appears 8 blocks in front of you and faces you.
kill @e[type=minecraft:item_display,tag=exam.screen]
execute at @s rotated ~ 0 positioned ^ ^ ^8 run summon minecraft:item_display ~ ~3 ~ {Tags:["exam.screen"],billboard:"fixed",brightness:{sky:15,block:15},view_range:4.0f,transformation:{left_rotation:[0.0f,0.0f,0.0f,1.0f],right_rotation:[0.0f,0.0f,0.0f,1.0f],translation:[0.0f,0.0f,0.0f],scale:[8.0f,8.0f,8.0f]},item:{id:"minecraft:paper",count:1,components:{"minecraft:item_model":"exam:slide_1"}}}
execute as @e[type=minecraft:item_display,tag=exam.screen] at @s facing entity @p feet run tp @s ~ ~ ~ ~ 0
scoreboard players set #current exam.slide 1
function exam:internal/apply
