$execute as @e[type=minecraft:item_display,tag=exam.screen] run data modify entity @s item set value {id:"minecraft:paper",count:1,components:{"minecraft:item_model":"exam:slide_$(n)"}}
