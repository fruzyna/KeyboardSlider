class Plugin:
    name = 'generic plugin'
    program = 'none'
    def onMove(self, position, old):
        pass
    def update(self, pos):
        return pos
    def canBeActive(self, program, keys):
        return True