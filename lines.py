import os
dir = os.listdir()
filters = ('.idea', '.git', 'out', 'lines.py')
dir = [i for i in dir if i not in filters]
dir = [i for i in dir if not i.endswith("iml")]
print(dir)

global count
count = 0

def lines(file):
    if os.path.isdir(file):
        for i in os.listdir(file):
            lines(file + os.sep + i)
    if os.path.isfile(file):
        try:
            with open(file) as f:
                global count
                count += len(f.readlines())
        except:
            print("error")

for i in dir:
    lines(i)

print(count)
input()