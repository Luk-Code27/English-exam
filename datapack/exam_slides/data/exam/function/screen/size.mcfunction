# Change the screen size. Example: /function exam:screen/size {s:10}   (8 = 8 blocks wide)
$execute as @e[type=minecraft:item_display,tag=exam.screen] run data modify entity @s transformation.scale set value [$(s).0f,$(s).0f,$(s).0f]
