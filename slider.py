from serial import Serial
from subprocess import Popen, PIPE
from os import listdir
from threading import Thread
from time import sleep
from evdev import InputDevice

def runCmd(bashCmd):
    process = Popen(bashCmd.split(), stdout=PIPE)
    output, error = process.communicate()
    return output

def getPos(arduino):
    read = arduino.readline()
    if '@' in read and '%' in read:
        start = read.index('@') + 1
        end = read.index('%', start)
        return int(read[start:end])
    return -1

def moveTo(arduino, pos):
    arduino.write(str(pos))

def within(value, bounds, of):
    return value >= of - bounds and value <= of + bounds

#setup plugin system
class Plugin:
    name = 'generic plugin'
    program = 'none'
    def onMove(self, position):
        pass
    def update(self):
        return pos
    def canBeActive(self, program, keys):
        return True

class Volume(Plugin):
    name = 'volume control'
    program = 'system'
    def onMove(self, position):
        runCmd('amixer -D pulse sset Master ' + str(position) + '%')
    def update(self):
        output = runCmd('amixer get Master')
        start = output.index('[') + 1
        end = output.index('%', start)
        return int(output[start:end])
    def canBeActive(self, program, keys):
        return len(keys) == 1 and keys[0].endswith('CTRL')

class FirefoxScroll(Plugin):
    name = 'scroller'
    program = 'Mozilla Firefox'
    def onMove(self, position):
        global pos
        scrolls = pos - position
        if scrolls != 0:
            keys = ''
            for i in range(abs(scrolls)):
                if scrolls > 0:
                    keys += 'Up '
                else:
                    keys += 'Down '
            runCmd('xdotool key ' + keys)
    def update(self):
        return 50
    def canBeActive(self, program, keys):
        return program.endswith(self.program)

plugins = [Plugin(), Volume(), FirefoxScroll()]

#setup arduino communication
arduino = Serial('/dev/ttyUSB0', 9600)

#start program
plgn = None
pos = -1
running = True
program = ''
keys = []

class UpdateThread(Thread):
    def run(self):
        global plgn
        global pos
        global running
        toSend = plgn.update()
        while running:
            newPos = plgn.update()
            if newPos == toSend:
                #print('Moving to: ' + str(newPos))
                moveTo(arduino, newPos)
                pos = newPos
                toSend = -1
            if not within(newPos, 2, pos):
                toSend = newPos
            sleep(0.5)
        print('Updater thread complete.')

class ReadThread(Thread):
    def run(self):
        global plgn
        global pos
        global running
        sleep(1)
        while running:
            newPos = getPos(arduino)
            if newPos != pos:
                #print('Moved to: ' + str(newPos))
                plgn.onMove(newPos)
                pos = newPos
        print('Reader thread complete.')

class ProgramThread(Thread):
    def run(self):
        global program
        global plugins
        global plgn
        global keys
        device = InputDevice('/dev/input/event5')
        while running:
            program = runCmd('xdotool getwindowfocus getwindowname').rstrip()
            if program == 'bash: xdotool: command not found':
                print('xdotool must be installed')
            keys = [k[0].replace('KEY_', '') for k in device.active_keys(verbose=True)]
            newPlgn = plgn
            for plugin in plugins:
                if plugin.canBeActive(program, keys):
                    newPlgn = plugin
            plgn = newPlgn
        print('Program thread complete.')

check = ProgramThread()
check.start()

while plgn == None: pass
update = UpdateThread()
update.start()
read = ReadThread()
read.start()

#debugger
while running:
    raw = raw_input(':')
    if raw == 'quit':
        running = False
        print('Adjust slider to allow program to finish.')
    elif raw == 'pos':
        print('Position: ' + str(pos) + "%")
    elif raw == 'plugin':
        print('Current Plugin: ' + plgn.name)