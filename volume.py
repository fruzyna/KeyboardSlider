from plugin import Plugin
from utils import runCmd, within

class Volume(Plugin):
    name = 'volume control'
    program = 'system'
    def onMove(self, position, old):
        runCmd('amixer -D pulse sset Master ' + str(position) + '%')
    def update(self, pos):
        output = runCmd('amixer get Master')
        start = output.index('[') + 1
        end = output.index('%', start)
        return int(output[start:end])
    def canBeActive(self, program, keys):
        return len(keys) == 1 and keys[0].endswith('LEFTCTRL')