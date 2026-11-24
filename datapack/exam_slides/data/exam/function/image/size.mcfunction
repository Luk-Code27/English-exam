# Size of the picture closest to you. Example: /function exam:image/size {s:4}
$execute at @s as @e[type=minecraft:item_display,tag=exam.image,sort=nearest,limit=1,distance=..8] run data modify entity @s transformation.scale set value [$(s).0f,$(s).0f,$(s).0f]
