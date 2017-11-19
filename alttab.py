from plugin import Plugin
from utils import runCmd, within

lastMove = None

class AltTab(Plugin):
    name = 'scroller'
    program = 'any'
    def onMove(self, position, old):
        global lastMove
        if lastMove == None:
            lastMove = position
        elif not within(lastMove, 5, position): 
            if lastMove < position:
                runCmd('xdotool key Tab')
            elif lastMove > position:
                runCmd('xdotool key Shift+Tab')
            lastMove = position
    def update(self, pos):
        return 50
    def canBeActive(self, program, keys):
        return len(keys) == 1 and keys[0] == 'LEFTALT'