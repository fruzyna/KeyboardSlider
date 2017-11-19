from serial import Serial
from os import listdir
from threading import Thread
from time import sleep
from evdev import InputDevice
from utils import runCmd, within

#plugins
from plugin import Plugin
from volume import Volume
from firefoxscroll import FirefoxScroll
from alttab import AltTab

def getPos(arduino):
    read = arduino.readline()
    if '@' in read and '%' in read:
        start = read.index('@') + 1
        end = read.index('%', start)
        return int(read[start:end])
    return -1

def moveTo(arduino, pos):
    arduino.write(str(pos))

plugins = [Plugin(), Volume(), FirefoxScroll(), AltTab()]

#setup arduino communication
arduino = Serial('/dev/ttyUSB0', 9600)

#start program
plgn = None
pos = 50
running = True
program = ''
keys = []

#threads
class UpdateThread(Thread):
    def run(self):
        global pos
        toSend = 50
        newPos = 50
        while running:
            newPos = plgn.update(pos)
            if newPos == toSend:
                #print('Moving to: ' + str(newPos))
                moveTo(arduino, newPos)
                pos = newPos
                toSend = -1
            if not within(newPos, 2, pos):
                toSend = newPos
            sleep(0.25)
        print('Updater thread complete.')

class ReadThread(Thread):
    def run(self):
        global pos
        sleep(1)
        while running:
            newPos = getPos(arduino)
            if not within(newPos, 1, pos):
                #print('Moved to: ' + str(newPos))
                plgn.onMove(newPos, pos)
                pos = newPos
        print('Reader thread complete.')

class ProgramThread(Thread):
    def run(self):
        global program
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
    elif raw == 'status':
        print('Status will be displayed in 3 seconds...')
        sleep(3)
        print('Current Plugin: ' + plgn.name)
        print('Open Program: ' + program)
        print('Keyboard Keys: ' + ''.join(map(str, keys)))