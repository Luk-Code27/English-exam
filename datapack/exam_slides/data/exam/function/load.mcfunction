# Runs when the world loads or after /reload
scoreboard objectives add exam.slide dummy
scoreboard objectives add exam.use minecraft.used:minecraft.carrot_on_a_stick
# Number of slides (the Exam Slides mod sets this when you press "Übernehmen")
execute unless score #max exam.slide matches 1.. run scoreboard players set #max exam.slide 2
execute unless score #current exam.slide matches 1.. run scoreboard players set #current exam.slide 1
