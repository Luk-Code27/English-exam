# Remote control: right-click with "Next slide" / "Previous slide"
execute as @a[scores={exam.use=1..}] if items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data={exam:"next"}] run function exam:next
execute as @a[scores={exam.use=1..}] if items entity @s weapon.mainhand minecraft:carrot_on_a_stick[minecraft:custom_data={exam:"prev"}] run function exam:prev
scoreboard players reset @a exam.use
