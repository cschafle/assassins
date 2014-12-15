# This imports all the layers for "logo" into logoLayers
logoLayers = Framer.Importer.load "imported/logo"

# This imports all the layers for "AssassinsWatch" into assassinswatchLayers
assassinswatchLayers = Framer.Importer.load "imported/AssassinsWatch"

# Welcome to Framer

# Learn how to prototype: http://framerjs.com/learn
# Drop an image on the device, or import a design from Sketch or Photoshop


###########################################
#		Load Items
###########################################
appleWatchScreen = assassinswatchLayers["AppleWatchScreen"]
appleWatchScreen.scale = 1.5
appleWatchScreen.x = 40
appleWatchScreen.y = 60

logoImage = logoLayers["Logo"]
logoImage.scale = 0.065
logoImage.x = -372
logoImage.y = -155
logoImage.bringToFront()

assassinatedText = assassinswatchLayers["AssassinatedText"]
assassinatedText.scale = 1.2
assassinatedText.x = 50
assassinatedText.y = 50 - 300

denyButton = assassinswatchLayers["DenyButton"]
denyButton.scale = 1.2
denyButton.x = 190 + 200
denyButton.y = 220

confirmButton = assassinswatchLayers["ConfirmButton"]
confirmButton.scale = 1.2
confirmButton.x = 40 - 200
confirmButton.y = 220

assassinText = assassinswatchLayers["AssassinText"]
assassinText.scale = 1.3
assassinText.x = 52
assassinText.y = 70 + 500


###########################################
#		HOME SCREEN
###########################################
logoImage.animate({
	properties: {
		x: -70
		y: -70
		scale: 5
	}
	time: 0.5
	delay: 2
})

logoImage.on Events.AnimationEnd, ->
	confirmButton.animate({
		properties: {
			x: 40
		}
		time: 0.5
	})
	confirmButton.bringToFront()
	denyButton.animate({
		properties: {
			x: 190
		}
		time: 0.5
	})
	denyButton.bringToFront()
	assassinatedText.animate({
		properties: {
			y: 50
		}
		time: 0.5
	})
	assassinatedText.bringToFront()
	
	

###########################################
#		NOTIFICATION SCREEN
###########################################
animated = false
confirmButton.on Events.AnimationEnd, ->
	confirmButton.animate({
		properties: {
			x: 40 - 200
		}
		time: 0.4
		delay: 1
	})
	denyButton.animate({
		properties: {
			x: 190 + 200
		}
		time: 0.4
		delay: 1
	})
	assassinatedText.animate({
		properties: {
			y: 50 - 200
		}
		time: 0.4
		delay: 1
	})
	if animated == false
		assassinText.animate({
			properties: {
				y: 75
			}
			time: 0.4
			delay: 1.5
		})
	assassinText.bringToFront()
	animated = true




