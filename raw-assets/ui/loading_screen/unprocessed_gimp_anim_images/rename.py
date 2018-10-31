import os
for file in os.listdir("../unprocessed_gimp_anim_images/"):
    file_id = file.replace("Frame ", "").replace(" (40ms) (replace).png", "")

    file = open("../unprocessed_gimp_anim_images/" + file, "rb")
    data = file.read()

    file2 = open("../images/" + file_id + ".png", "wb")
    file2.write(data)

    file.close()
    file2.close()

