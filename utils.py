from subprocess import Popen, PIPE

def runCmd(bashCmd):
    process = Popen(bashCmd.split(), stdout=PIPE)
    output, error = process.communicate()
    return output

def within(value, bounds, of):
    return value >= of - bounds and value <= of + bounds