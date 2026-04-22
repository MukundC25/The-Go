import re
with open("Online_Food_Delivery_System_Test_Report.tex", "r") as f:
    text = f.read()

lines = text.split('\n')
for i, line in enumerate(lines):
    # ignore comments starting with %
    no_comment = line.split('%')[0] if '%' in line and not '\%' in line else line
    
    # check for unescaped underscores outside of math or environments
    # rough heuristic since this is text
    # Actually, let's just find unescaped _ first.
    # We will find all `_` that are not preceded by `\`
    # and print them
    unescaped_us = re.findall(r'(?<!\\)_', no_comment)
    if unescaped_us:
        print(f"Line {i+1} has unescaped _: {line}")
