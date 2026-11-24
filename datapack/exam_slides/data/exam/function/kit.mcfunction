# Gives you the remote controls and one ready command block per slide. Needs cheats on.
give @s minecraft:carrot_on_a_stick[minecraft:custom_data={exam:"next"},minecraft:item_name="Next slide",minecraft:item_model="minecraft:lime_dye"]
give @s minecraft:carrot_on_a_stick[minecraft:custom_data={exam:"prev"},minecraft:item_name="Previous slide",minecraft:item_model="minecraft:red_dye"]
give @s minecraft:command_block[minecraft:block_entity_data={id:"minecraft:command_block",Command:"function exam:goto {n:1}"},minecraft:item_name="Slide 1 (command block)"]
give @s minecraft:command_block[minecraft:block_entity_data={id:"minecraft:command_block",Command:"function exam:goto {n:2}"},minecraft:item_name="Slide 2 (command block)"]
give @s minecraft:stone_button 8
