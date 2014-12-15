# This imports all the layers for "AssassinsPhoneScreen" into assassinsphonescreenLayers1
assassinsphonescreenLayers1 = Framer.Importer.load "imported/AssassinsPhoneScreen"

# This imports all the layers for "AssassinsPhoneToast" into assassinsphonetoastLayers
assassinsphonetoastLayers = Framer.Importer.load "imported/AssassinsPhoneToast"

# Welcome to Framer

# Learn how to prototype: http://framerjs.com/learn
# Drop an image on the device, or import a design from Sketch or Photoshop

toast = assassinsphonetoastLayers["Toast"]
toast.centerX()
toast.y = 1215 + 200

screen = assassinsphonescreenLayers1["Screen"]
screen.height = 1350
screen.center()

toast.animate({
	properties: {
		y: 1215
	}
	delay: 2
})
