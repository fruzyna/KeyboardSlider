from plugin import Plugin
from utils import runCmd

class FirefoxScroll(Plugin):
    name = 'scroller'
    program = 'Mozilla Firefox'
    def onMove(self, position, old):
        scrolls = old - position
        if scrolls != 0:
            keys = ''
            for i in range(abs(scrolls)):
                if scrolls > 0:
                    keys += 'Up '
                else:
                    keys += 'Down '
            runCmd('xdotool key ' + keys)
    def update(self, pos):
        return 50
    def canBeActive(self, program, keys):
        return program.endswith(self.program) or (len(keys) == 1 and keys[0] == 'RIGHTCTRL')